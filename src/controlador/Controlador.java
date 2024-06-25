package controlador;

import java.util.Date;
import java.util.EventObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import com.itextpdf.text.DocumentException;

import auxiliares.generadorPDF.GeneradorPDF;
import auxiliares.reproductor.Reproductor;
import controlador.Controlador;
import descuento.Descuento;
import descuento.FactoriaDescuento;
import main.Lanzador;
import modelo.Cancion;
import modelo.CatalogoCanciones;
import modelo.CatalogoUsuarios;
import modelo.Playlist;
import modelo.Usuario;
import observador.Observable;
import observador.Observer;
import observador.tiposObservadores.ObservadorReproduccion;
import persistencia.DAOException;
import persistencia.FactoriaDAO;
import persistencia.IAdaptadorCancion;
import persistencia.IAdaptadorPlaylist;
import persistencia.IAdaptadorUsuarioDAO;
import umu.tds.componente.Canciones;
import umu.tds.componente.CancionesEvent;
import umu.tds.componente.CancionesListener;
import umu.tds.componente.CargadorCanciones;
import ventanas.NewVentanaPrincipal;

public class Controlador extends Observable implements CancionesListener, Observer, ObservadorReproduccion {
	
		// Precio de hacerse premium.
		private static final int PRECIO_APPMUSIC = 5;
	
		// PATRÓN SINGLETON
		private static Controlador unicaInstancia = null;
		
		// ADAPTADORES
		private IAdaptadorUsuarioDAO adaptadorUsuario = null;
		private IAdaptadorPlaylist adaptadorPlaylist = null;
		private IAdaptadorCancion adaptadorCancion = null;
		
		// CARGADOR DE CANCIONES
		private CargadorCanciones cargadorCanciones;
		
		// CATÁLOGOS
		private CatalogoUsuarios catalogoUsuarios = null;
		private CatalogoCanciones catalogoCanciones = null;
				
		// VARIABLES GLOBALES (PRIVADAS AL CONTROLADOR)
		private final static String NOMBRE_VACIO = "Tienes que introducir un nombre.";
		private final static String CONTRASENA_VACIA = "Tienes que introducir una contraseña.";
		private final static String EMAIL_VACIO = "Tienes que introducir un email.";
		private final static String FECHA_INCORRECTA = "La fecha que has introducido está por llegar.";
		private final static String FECHA_VACIA = "Tienes que introducir una fecha.";
		private final static String USUARIO_DUPLICADO = "¡Ese nombre de usuario ya existe! Introduce otro...";
		private final static String EMAIL_DUPLICADO = "Ya hay una cuenta registrada con ese correo electrónico. Introduce otro...";
		private final static String USUARIO_NO_REGISTRADO = "No hay ningún usuario con ese nombre.";
		private final static String CONTRASENA_INCORRECTA = "La contraseña que has introducido no es correcta.";
		
		// ATRIBUTOS PARA LOS FILTROS.
		private Map<String, List<Cancion>> cancionesPorArtista;
		private Map<String, List<Cancion>> cancionesPorEstilo;
		private Map<String, List<Cancion>> cancionesPorTitulo;		
		
		// REPRODUCTOR Y CANCIÓN ACTUAL
		private Reproductor reproductor;
		private Usuario usuarioReproduciendo;
		
		// LOS OBSERVADORES DE CADA TIPO QUE VA A HABER.
		private Map<TipoObservadorControlador, List<Observer>> observadores;
		
		private Controlador() {
			// Inicialización de los filtros.
			cancionesPorArtista = new HashMap<String, List<Cancion>>();
			cancionesPorEstilo = new HashMap<String, List<Cancion>>();
			cancionesPorTitulo = new HashMap<String, List<Cancion>>();
			
			// Inicialización de los observadores.
			observadores = new HashMap<TipoObservadorControlador, List<Observer>>();
			for(TipoObservadorControlador tipo: TipoObservadorControlador.values())
				observadores.put(tipo, new LinkedList<Observer>());

			// Inicialización de adaptadores y catálogos.
			inicializarAdaptadores();
			
//			for(Playlist p:adaptadorPlaylist.recuperarTodasPlaylists()) {
//				adaptadorPlaylist.borrarPlaylist(p);
//			}
//			for(Usuario u:adaptadorUsuario.recuperarTodosUsuarios())
//				adaptadorUsuario.eliminarUsuario(u);
//			for(Cancion c:adaptadorCancion.recuperarTodasCanciones())
//				adaptadorCancion.eliminarCancion(c);
			
			inicializarCatalogos();
			
			// Inicialización del cargador de canciones.
			cargadorCanciones = new CargadorCanciones();
			cargadorCanciones.addCancionesListener(this);
		}
		
		/*
		 * Singleton.
		 */
		public static Controlador getUnicaInstancia() {
			if(unicaInstancia==null) {
				unicaInstancia = new Controlador();
			}
			return unicaInstancia;
		}
		
		/*
		 * Método que devuelve si dos cadenas son iguales sin tener en cuenta mayúsculas ni blancos al principio y al final.
		 */
		private static boolean sonIgualesSinMayus(String s1, String s2) {
			return s1.trim().toLowerCase().equals(s2.trim().toLowerCase());
		}
		
		/*
		 * Métodos que devuelven si un intérprete, un estilo o un título estaban en el sistema.
		 */
		private boolean existeInterprete(String interprete) {
			return cancionesPorArtista.keySet().stream()
					.anyMatch(artista -> sonIgualesSinMayus(artista, interprete));
		}
		
		private boolean existeEstilo(String estilo) {
			return cancionesPorEstilo.keySet().stream()
					.anyMatch(e -> sonIgualesSinMayus(e, estilo));
		}
		
		private boolean existeTitulo(String titulo) {
			return cancionesPorTitulo.keySet().stream()
					.anyMatch(t -> sonIgualesSinMayus(t, titulo));
		}
		
		/*
		 * Métodos que devuelven el intérprete, título o estilo con las mayúsculas aunque se le pase sin estas:
		 */
		private String getInterprete(String interprete){
			List<String> interpretes = Cancion.getInterpretesDesdeCadena(interprete);
			
			for(String artista:cancionesPorArtista.keySet()) {
				if(sonIgualesSinMayus(artista, interpretes.get(0))) {
					return artista;
				}
			}
			return null;
		}
		
		private String getTitulo(String titulo){
			for(String t:cancionesPorTitulo.keySet()) {
				if(sonIgualesSinMayus(t, titulo))
					return t;
			}
			return null;
		}
		
		private String getEstilo(String estilo){
			for(String e:cancionesPorEstilo.keySet())
				if(sonIgualesSinMayus(e, estilo))
					return e;
			return null;
		}
		
		/*
		 * Métodos que hace lo necesario para introducir un nuevo intérprete, estilo o título en el sistema.
		 */
		private void crearInteprete(String interprete) {
			cancionesPorArtista.put(interprete, new LinkedList<Cancion>());
		}
		
		private void crearEstilo(String estilo) {
			cancionesPorEstilo.put(estilo, new LinkedList<Cancion>());
//			todosLosEstilos += (estilo + "\n");
			notificarCambioEstilos();
		}
		
		private void crearTitulo(String titulo) {
			cancionesPorTitulo.put(titulo, new LinkedList<Cancion>());
		}
		
		/*
		 * Método que devuelve si una canción estaba ya en el sistema.
		 */
		public boolean cancionExistente(Cancion cancion) {			
			if(!existeTitulo(cancion.getTitulo()))
				return false;
			if(!existeEstilo(cancion.getEstilo()))
				return false;
			if(!cancion.getInterpretes().stream()
					.allMatch(interprete -> existeInterprete(interprete)))
				return false;
			
			return cancionesPorArtista.get(cancion.getInterpretes().get(0)).stream()
					.anyMatch(c -> c.equals(cancion));
		}
		
		/*
		 * Método que devuelve si un usuario estaba ya en el sistema.
		 */
		public boolean usuarioExistente(Usuario usuario) 
		{
			return catalogoUsuarios.getUsuarios().contains(usuario);
		}
		
		/**
		 * Método que devuelve verdadero si había algún Usuario con el nombre pasado por argumento en el Sistema.
		 * @param nombreUsuario: el nombre de Usuario.
		 * @return verdadero si había un Usuario con ese nombre, falso en caso contrario.
		 */
		public boolean usuarioExistente(String nombreUsuario)
		{
			return catalogoUsuarios.getUsuarios()
					.stream()
					.anyMatch(usuario -> usuario.getNombre().equals(nombreUsuario));
		}
		
		/*
		 * Busca las canciones de un artista, por un título o por un estilo sin tener en cuenta mayúsculas:
		 */
		private List<Cancion> getCancionesInterprete(String interprete){
			for(String artista:cancionesPorArtista.keySet()) {
				if(sonIgualesSinMayus(artista, interprete)) {
					return cancionesPorArtista.get(artista);
				}
			}
			return null;
		}
		
		private List<Cancion> getCancionesTitulo(String titulo){
			for(String t:cancionesPorTitulo.keySet()) {
				if(sonIgualesSinMayus(t, titulo))
					return cancionesPorTitulo.get(t);
			}
			return null;
		}
		
		private List<Cancion> getCancionesEstilo(String estilo){
			for(String e:cancionesPorEstilo.keySet())
				if(sonIgualesSinMayus(e, estilo))
					return cancionesPorEstilo.get(e);
			return null;
		}
		
		/**
		 * Inserta una Canción en el sistema. Si algún intérprete no estaba, lo inserta. Si el estilo no estaba, lo añade.
		 * @param c: la Cancion a insertar.
		 */
		private void insertarCancionSinNotificar(Cancion c) 
		{
			// Si la canción existía, no hago nada.
			if(cancionExistente(c))
				return;
			
			// Si el intérprete no estaba...
			c.getInterpretes().stream()
				.filter(interprete -> !existeInterprete(interprete))
				.forEach(interprete -> crearInteprete(interprete));
			
			// Si el estilo no estaba...
			if(!existeEstilo(c.getEstilo())) {
				crearEstilo(c.getEstilo());
			}
			
			// Si el título no estaba...
			if(!existeTitulo(c.getTitulo())) {
				crearTitulo(c.getTitulo());
			}
			
			/*
			 * Inserción en los filtros:
			 */
			c.getInterpretes().stream()
				.forEach(interprete -> {
					List<Cancion> cancionesDelArtista = getCancionesInterprete(interprete);
					cancionesDelArtista.add(c);
					cancionesPorArtista.put(interprete, cancionesDelArtista);
				});
			
			List<Cancion> cancionesDelTitulo = getCancionesTitulo(c.getTitulo());
			List<Cancion> cancionesDelEstilo = getCancionesEstilo(c.getEstilo());
			
			cancionesDelTitulo.add(c);
			cancionesDelEstilo.add(c);
			
			cancionesPorTitulo.put(getTitulo(c.getTitulo()), cancionesDelTitulo);
			cancionesPorEstilo.put(getEstilo(c.getEstilo()), cancionesDelEstilo);
			
			/*
			 * Inserción en el adaptador:
			 */
			adaptadorCancion.registrarCancion(c);
			
			/*
			 * Inserción en el catálogo:
			 */
			catalogoCanciones.addCancion(c);
		}
		
		/*
		 * Método que inserta una canción en el sistema y, además, notifica del cambio.
		 */
		private void insertarCancionNotificando(Cancion c) {
			insertarCancionSinNotificar(c);
			notificarCambioCanciones();
		}
		
		/*
		 * Métodos para notificar a los observadores de los cambios:
		 */
		
		/**
		 * Nuevo método para notificar a los observadores. Dependiendo del tipo, se notificará a unos o a otros.
		 * @param tipo: Tipo de Observador.
		 * @param arg: objeto a notificar.
		 */
		public void notifyObservers(TipoObservadorControlador tipo, Object arg) 
		{
			Object[] arrayLocal;
			synchronized(this) {
				if(!getChanged())
					return;
				arrayLocal = observadores.get(tipo).toArray();
				clearChanged();
			}
			
			for(int i=0; i<arrayLocal.length; i++) {
				Observer o = (Observer) arrayLocal[i];
				o.update(this, arg);
			}
		}
		
		/**
		 * Método para añadir un Observador de un tipo concreto.
		 * @param tipo: Tipo del Observador.
		 * @param observador: el Observador concreto.
		 */
		public void addObserver(TipoObservadorControlador tipo, Observer observador)
		{
			List<Observer> observadoresTipo = observadores.get(tipo);
			observadoresTipo.add(observador);
			observadores.put(tipo, observadoresTipo);
		}
		
		/**
		 * Método para eliminar un Observador de un tipo concreto.
		 * @param tipo: Tipo del Observador.
		 * @param observador: el Observador concreto.
		 */
		public void deleteObserver(TipoObservadorControlador tipo, Observer observador)
		{
			List<Observer> observadoresTipo = observadores.get(tipo);
			observadoresTipo.remove(observador);
			observadores.put(tipo, observadoresTipo);
		}
		
		// Si cambian las canciones.
		private void notificarCambioCanciones() 
		{
			List<String> canciones = new LinkedList<String>(catalogoCanciones.getCanciones().stream()
					.map(c -> c.getTitulo() + "~" + Cancion.getCadenaInterpretes(c.getInterpretes()))
					.collect(Collectors.toList())
					);
			setChanged();
			notifyObservers(TipoObservadorControlador.CANCION, canciones);
		}
		
		// Si cambian los estilos.
		private void notificarCambioEstilos() 
		{
			List<String> estilos = new LinkedList<String>(cancionesPorEstilo.keySet());
			estilos.add(0, "Todos");
			setChanged();
			notifyObservers(TipoObservadorControlador.ESTILO, estilos);
		}
		
		// Si cambian los usuarios.
		private void notificarCambioUsuarios() 
		{
			LinkedList<String> usuarios = new LinkedList<String>(catalogoUsuarios.getUsuarios().stream()
					.map(u -> u.getNombre())
					.collect(Collectors.toList()));
			setChanged();
			notifyObservers(TipoObservadorControlador.USUARIO, usuarios);
		}
		
		// Si cambian las playlists.
		private void notificarCambioPlaylists(Usuario usuario)
		{
			List<String> usuarioYPlaylists = new LinkedList<String>(getPlaylists(usuario).stream()
					.map(p -> p.getNombre())
					.collect(Collectors.toList()));
			
			usuarioYPlaylists.add(0, usuario.getNombre());
			setChanged();
			notifyObservers(TipoObservadorControlador.PLAYLIST, usuarioYPlaylists);
		}
		
		private void notificarCambioMasReproducidas()
		{
			List<Cancion> masReproducidas = getMasReproducidas();
			setChanged();
			notifyObservers(TipoObservadorControlador.MAS_REPRODUCIDAS, masReproducidas);
		}
		
		/*
		 *	CUANDO SE HAN AÑADIDO NUEVAS CANCIONES, SE AVISA AL CONTROLADOR MEDIANTE EL USO DE ESTE MÉTODO.
		 *	EL CONTROLADOR CONVERTIRÁ LAS CANCIONES (QUE SERÁN DE TIPO "umu.tds.componente.Cancion") EN CANCIONES DE ESTE SISTEMA (CLASE Cancion).
		 */
		@Override
		public void enteradoCambioCanciones(EventObject arg0) {
			Canciones canciones = ((CancionesEvent)arg0).getCancionesAhora();
			
			for(umu.tds.componente.Cancion cancion:canciones.getCancion()) {
				Cancion cancionConvertida = convertirCancion(cancion);
				insertarCancionSinNotificar(cancionConvertida);
			}
			notificarCambioCanciones();
		}
		
		@Override
		public void update(Observable o, Object arg) 
		{
			if(arg instanceof Cancion)
			{
				modificarCancion((Cancion)arg);
				notificarCambioMasReproducidas();
			}
		}
		
		@Override
		public void updateRepro(Observable o, Object arg) 
		{
			if(!(arg instanceof Cancion))
				return;
			if(o != reproductor)
				return;
			
			Cancion argumento = (Cancion)arg;
			usuarioReproduciendo.addCancionReciente(argumento);
			actualizarUsuario(usuarioReproduciendo);
		}
		
		
		/*
		 * REGISTRO DE UN USUARIO. DEVOLVERÁ UN STRING REPRESENTANDO MENSAJES O NULL EN CASO DE REGISTRAR EL USUARIO.
		 */
		public String registrarUsuario(String nombre, char[] contrasena, String email, Date fecha) {
			
			String password = new String(contrasena);
			
			// ***************** MENSAJES DE ERROR ***************** //
			
			// El nombre es vacío --> no válido.
			if(nombre==null || nombre.equals(""))
				return NOMBRE_VACIO;
			
			// La contraseña es vacía --> no válida.
			if(password==null || password.equals(""))
				return CONTRASENA_VACIA;
			
			// Email vacío --> no válido.
			if(email==null || email.equals(""))
				return EMAIL_VACIO;
			
			// Fecha vacía --> no válida.
			if(fecha==null)
				return FECHA_VACIA;
			
			// La fecha es posterior a la actual --> no válida.
			if(fecha.after(new Date()))
				return FECHA_INCORRECTA;
			
			// El nombre de Usuario ya estaba en el sistema.
			if(consultarUsuario(nombre))
				return USUARIO_DUPLICADO;
			
			// El email ya estaba en el sistema.
			if(consultarEmail(email))
				return EMAIL_DUPLICADO;
			
			Usuario newUsr = new Usuario(nombre, password, email, formatearFecha(fecha));
			
			// Se añade el Usuario al adaptador y al catálogo.
			adaptadorUsuario.registrarUsuario(newUsr);
			catalogoUsuarios.addUsuario(newUsr);
			
			// Se actualizan los nombres de Usuario.
//			nombresUsuarios += (nombre + "\n");

			// Se notifica a los observadores.
			notificarCambioUsuarios();
				
			return null;	// Si la ventana recibe un null, entonces se habrá registrado el usuario.
		}		
		
		/*
		 * BÚSQUEDA DE CANCIONES EN EL SISTEMA.
		 */
		
		public Collection<Cancion> buscarCanciones(String interprete, String titulo, String estilo, String nombreUsuario, boolean favoritas)
		{
			if(!favoritas)
				return buscarCanciones(interprete, titulo, estilo);
			
			Usuario usuario = getUsuario(nombreUsuario);
			
			return buscarCanciones(interprete, titulo, estilo).stream()
					.filter(c -> usuario.getPlaylists().stream()
							.anyMatch(lista -> lista.getCanciones().contains(c)))
					.collect(Collectors.toList());
		}
				
		public Collection<Cancion> buscarCanciones(String interprete, String titulo, String estilo)
		{			
			String tituloM = titulo.trim().toLowerCase();
			String interpreteM = interprete.trim().toLowerCase();
			
			boolean todosTitulos = tituloM.equals("");
			boolean todosEstilos = estilo.equals("Todos");
			boolean todosInterpretes = interpreteM.equals("");
			
			// Se busca todo
			if(todosTitulos && todosEstilos && todosInterpretes) 
			{
//				cancionesFiltradas = catalogoCanciones.getCanciones();
				return catalogoCanciones.getCanciones();
			}
			// 1. Todos los estilos:
			else if(todosEstilos) 
			{
				// 1.1. Todos los estilos y todos los interpretes:
				if(todosInterpretes) 
				{
					// Ya sé que no se quieren todos los títulos -> filtro por título.
					return cancionesPorTitulo.keySet()
						.stream()
						.filter(tituloCancion -> tituloCancion.toLowerCase().startsWith(tituloM))
						.map(tituloCancion -> cancionesPorTitulo.get(tituloCancion))
						.flatMap(listaCanciones -> listaCanciones.stream())
						.collect(Collectors.toList());
				}
				// 1.2. Todos los estilos y todos los titulos:
				if(todosTitulos) 
				{
					// Ya sé que no se quieren todos los intérpretes -> filtro por intérprete.
					return cancionesPorArtista.keySet()
						.stream()
						.filter(interpreteCancion -> interpreteCancion.toLowerCase().startsWith(interpreteM))
						.map(interpreteCancion -> cancionesPorArtista.get(interpreteCancion))
						.flatMap(listaCanciones -> listaCanciones.stream())
						.collect(Collectors.toList());
				}
				// 1.3. Todos los estilos, pero filtrando por intérprete y título:
				return cancionesPorArtista.keySet()
						.stream()
						.filter(interpreteCancion -> interpreteCancion.toLowerCase().startsWith(interpreteM))
						.map(interpreteCancion -> cancionesPorArtista.get(interpreteCancion))
						.flatMap(listaCanciones -> listaCanciones.stream())
						.filter(cancion -> cancion.getTitulo().toLowerCase().trim().startsWith(tituloM))
						.collect(Collectors.toList());
			}
			
			// Ya sabemos que podemos filtrar por estilo.
			
			// 2. Todos los intérpretes:
			if(todosInterpretes) 
			{
				// 2.1. Todos los intérpretes y todos los títulos:
				if(todosTitulos) {
					return cancionesPorEstilo.get(estilo);
				}
				
				// 2.2. Todos los intérpretes, pero filtrando por estilo y título:
				return cancionesPorEstilo.get(estilo)
						.stream()
						.filter(cancion -> cancion.getTitulo().toLowerCase().trim().startsWith(tituloM))
						.collect(Collectors.toList());
			}
			// Sabemos que podemos filtrar por estilo e intérprete.
			
			// 3. Todos los títulos:
			if(todosTitulos) {
				return cancionesPorEstilo.get(estilo)
						.stream()
						.filter(c -> c.getInterpretes().stream()
								.anyMatch(interp -> interp.toLowerCase().startsWith(interpreteM))) 
						.collect(Collectors.toList());
			}
			
			// 4. Filtramos por estilo, intérprete y título:
			return cancionesPorEstilo.get(estilo)
					.stream()
					.filter(c -> c.getInterpretes().stream().anyMatch(inter -> inter.toLowerCase().startsWith(interpreteM)))
					.filter(c -> c.getEstilo().toLowerCase().startsWith(titulo))
					.collect(Collectors.toList());
		}
		
		/*
		 * CONSULTA SI HAY ALGÚN USUARIO CON nombre NOMBRE DE USUARIO.
		 * PUEDE HABER CONDICIONES DE CARRERA CON EJECUCIONES SIMULTÁNEAS DE LA APLICACIÓN.
		 */
		private boolean consultarUsuario(String nombre) {
			return catalogoUsuarios.getUsuarios().stream()
					.anyMatch(u -> u.getNombre().equals(nombre));
		}
		
		/*
		 * CONSULTA SI HAY ALGÚN USUARIO CON EL email PASADO POR ARGUMENTO.
		 * PUEDE HABER CONDICIONES DE CARRERA CON EJECUCIONES SIMULTÁNEAS DE LA APLICACIÓN.
		 */
		private boolean consultarEmail(String email) {			
			return catalogoUsuarios.getUsuarios().stream()
					.anyMatch(u -> u.getEmail().equals(email));
		}
		
		/*
		 * CONSULTA SI EL USUARIO CON NOMBRE nombre COMO CONTRASEÑA password.
		 * PUEDE HABER CONDICIONES DE CARRERA CON EJECUCIONES SIMULTÁNEAS DE LA APLICACIÓN.
		 */
		private boolean consultarContrasena(String nombre, String password) {
			Usuario usuario;
			
			try {
				if((usuario = catalogoUsuarios.getUsuario(nombre)) != null)
					return usuario.getPassword().equals(password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return false;
		}
		
		public void eliminarUsuario(Usuario usuario) {
			adaptadorUsuario.eliminarUsuario(usuario);
			catalogoUsuarios.removeUsuario(usuario);
		}
		
		/*
		 * Método que verifica una cuenta de GitHub.
		 */
		public boolean verificarGitHub(String usuario, String githubPropertiesPath) {
			try {
				GitHub github = GitHubBuilder.fromPropertyFile(githubPropertiesPath).build();

				if (github.isCredentialValid()) {
					GHUser ghuser = github.getMyself();
					System.out.println("Validado! " + ghuser.getLogin());
					System.out.println("¿Login válido?: true");

					return (ghuser.getLogin().equals(usuario) && github.isCredentialValid());
				}
				return false;

			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;

		}
		
		/**
		 * Método que devuelve si el Usuario que usa la aplicación se ha logueado con GitHub.
		 * @param nombreUsuario: el nombre del Usuario. 
		 * @return true si está logueado con GitHub, false si no.
		 */
		public boolean isLogueadoConGitHub(String nombreUsuario)
		{
			Usuario usuario = getUsuario(nombreUsuario);
			
			if(usuario == null)
				return false;
			
			return isLogueadoConGitHub(usuario);
		}
		
		/**
		 * Método que devuelve si un Usuario está logueado con GitHub.
		 * @param usuario: el Usuario a comprobar.
		 * @return true si está logueado con GitHub, false si no.
		 */
		public boolean isLogueadoConGitHub(Usuario usuario)
		{
			return usuario instanceof UsuarioGitHub;
		}
		
		/**
		 * Login con GitHub. Por simplicidad se crea un usuario temporal.
		 * @param usuario
		 * @param contrasena
		 */
		public void loguearConGitHub(String usuario, String contrasena)
		{			
			if(usuarioExistente(usuario))
			{
				int i = 1;
				usuario = usuario + "(" + i + ")";
				// Mientras que el sistema tenga usuarios, le cambio el nombre (usuario temporal por simplicidad por GitHub).
				while(usuarioExistente(usuario))
				{
					i++;
					usuario = usuario + "(" + i + ")";
				}
			}
			
			Usuario newUsr = new UsuarioGitHub(usuario, contrasena, "", null);
			
			// Se añade el Usuario al adaptador y al catálogo.
			catalogoUsuarios.addUsuario(newUsr);
			
			// Se actualizan los nombres de Usuario.
//			nombresUsuarios += (usuario + "\n");

			// Se notifica a los observadores.
			notificarCambioUsuarios();
			login(usuario, contrasena.toCharArray());
		}
		
		// ***************** FUNCIONES AUXILIARES *****************
		
		public boolean cargarCanciones(String fichero) {
			return cargadorCanciones.setArchivoCanciones(fichero);
		}
		
		/*
		 * Pasar de una Canción de umu.tds.componente a modelo.Cancion.
		 */
		private Cancion convertirCancion(umu.tds.componente.Cancion cancion) {
			String titulo = cancion.getTitulo();
			String ruta = cancion.getURL();
			String estilo = cancion.getEstilo();
			String interprete = cancion.getInterprete();
			List<String> interpretes = Cancion.getInterpretesDesdeCadena(interprete);
			return new Cancion(titulo, ruta, estilo, interpretes);
		}
		
		/*
		 * Método que inicializa los adaptadores.
		 */
		private void inicializarAdaptadores() {
			FactoriaDAO factoria=null;
			try {
				factoria=FactoriaDAO.getUnicaInstancia(FactoriaDAO.DAO_APPMUSIC);
			}catch(DAOException e) {
				e.printStackTrace();
			}
			adaptadorUsuario=factoria.getAdaptadorUsuario();
			adaptadorPlaylist=factoria.getAdaptadorPlaylist();
			adaptadorCancion=factoria.getAdaptadorCancion();
		}
		
		/*
		 * FUNCIÓN QUE DEVUELVE UNA CANCIÓN DADO SU TÍTULO E INTÉRPRETE (UNICIDAD).
		 */
		public Cancion getCancion(String titulo, String interprete) 
		{
			// Toma el intérprete con las mayúsculas puestas.
			String interpreteMayus = getInterprete(interprete);
			
			// De entre las canciones del artista, busca aquella con ese título.
			List<Cancion> canciones = cancionesPorArtista.get(interpreteMayus).stream()
				.filter(cancion -> sonIgualesSinMayus(cancion.getTitulo(), titulo))
				.collect(Collectors.toList());
			
			// Teniendo en cuenta que las Canciones son únicas por título y artista, el stream anterior devolverá una única canción, por eso solo se toma una.
			if(canciones != null && !canciones.isEmpty())
			{
				return canciones.get(0);
			}
			
			return null;
		}
		
		public void anteriorCancion() 
		{
			reproductor.anterior();
		}
		
		public void siguienteCancion() 
		{
			reproductor.siguiente();
		}
		
		public void reanudarCancion() 
		{
			reproductor.reanudarCancion();
		}
		
		/**
		 * Método para conseguir las Canciones recientes de un usuario.
		 * @param usuario: el Usuario cuyas recientes queremos conocer.
		 * @return: una colección con las canciones recientes.
		 */
		public List<Cancion> getRecientes(Usuario usuario)
		{
			return usuario.getRecientes();
		}
		
		/*
		 * FUNCIÓN QUE PARA (PAUSE) UNA CANCIÓN.
		 */
		public void pararCancion() {
			reproductor.pausar();
		}
		
		/*
		 * FUNCIÓN QUE DETIENE UNA CANCIÓN.
		 */
		public void detenerCancion() {
			reproductor.parar();
		}
		
		public void reproducirRecientesSec(Usuario usuario)
		{
			usuarioReproduciendo = usuario;
			reproductor.reproducirListaSecuencialmente(getRecientes(usuario));
		}
		
		public void reproducirRecientesAleat(Usuario usuario)
		{
			usuarioReproduciendo = usuario;
			reproductor.reproducirListaAleatoriamente(getRecientes(usuario));
		}
		
		/**
		 * Método que reproduce secuencialmente una Playlist con nombre pasado por argumento.
		 * @param playlist el nombre de la Playlist a reproducir.
		 */
		public void reproducirSecuencialmente(Usuario usuario, String playlist)
		{
//			if(playlist == null)
//			{
//				reproducirSecuencialmente((List<Cancion>)getRecientes());
//				return;
//			}
			if(playlist == null)
			{
				reproducirRecientesSec(usuario);
				return;
			}
			Playlist p = null;
			try {
				p = buscarPlaylist(usuario, playlist);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reproducirSecuencialmente(p, usuario);
		}
		
		public void reproducirSecuencialmente(Playlist playlist, Usuario usuario)
		{
			if(playlist == null)
				return;
			reproducirSecuencialmente(playlist.getCanciones(), usuario);
		}
		
		public void reproducirSecuencialmente(List<Cancion> canciones, Usuario usuario)
		{
			if(canciones == null)
				return;
			usuarioReproduciendo = usuario;
			reproductor.reproducirListaSecuencialmente(canciones);
		}
		
		/*
		 * Método para reproducir aleatoriamente una Playlist.
		 */
		public void reproducirAleatoriamente(Usuario usuario, String playlist)
		{
//			if(playlist == null)
//			{
//				reproducirAleatoriamente((List<Cancion>)getRecientes());
//				return;
//			}
			
			if(playlist == null)
			{
				reproducirRecientesAleat(usuario);
				return;
			}
			
			Playlist p = null;
			try {
				p = buscarPlaylist(usuario, playlist);
			} catch (Exception e) {
				e.printStackTrace();
			}
			reproducirAleatoriamente(p, usuario);
		}
		
		public void reproducirAleatoriamente(Playlist playlist, Usuario usuario)
		{
			if(playlist == null)
				return;
			reproducirAleatoriamente(playlist.getCanciones(), usuario);
		}
		
		public void reproducirAleatoriamente(List<Cancion> canciones, Usuario usuario)
		{
			if(canciones == null)
				return;
			usuarioReproduciendo = usuario;
			reproductor.reproducirListaAleatoriamente(canciones);
		}
		
		/**
		 * Modificar una canción.
		 */
		public void modificarCancion(Cancion cancion)
		{
			adaptadorCancion.modificarCancion(cancion);
			catalogoCanciones.reemplazarCanciones(adaptadorCancion.recuperarTodasCanciones());
			notificarCambioCanciones();
		}
				
		private void inicializarCatalogos()
		{
			// INICIALIZACIÓN DEL CATÁLOGO DE USUARIOS
			catalogoUsuarios=CatalogoUsuarios.getUnicaInstancia();
			adaptadorUsuario.recuperarTodosUsuarios().stream()
				.forEach(usuario -> 
				{
					catalogoUsuarios.addUsuario(usuario);
					notificarCambioUsuarios();
				});
			
			// INICIALIZACIÓN DEL CATÁLOGO DE CANCIONES
			catalogoCanciones=CatalogoCanciones.getUnicaInstancia();
			adaptadorCancion.recuperarTodasCanciones().stream()
				.forEach(cancion -> 
				{
					catalogoCanciones.addCancion(cancion);
					insertarCancionNotificando(cancion);
				});
		}
		
		/**
		 * Método para eliminar la playlist con nombre pasado por argumento para el Usuario con nombre pasado por argumento.
		 * @param nombreUsuario: el nombre del Usuario cuya Playlist queremos eliminar.
		 * @param nombrePlaylist: el nombre de la lista a eliminar.
		 */
		public void eliminarPlaylist(String nombreUsuario, String nombrePlaylist)
		{
			Usuario usuario = getUsuario(nombreUsuario);
			if(usuario == null)
				return;
			eliminarPlaylist(usuario, nombrePlaylist);
		}
		
		/**
		 * Elimina todas las listas del Usuario que utiliza la aplicación con el nombre pasado como argumento (unicidad de listas por nombre --> solo se eliminará una).
		 * @param usuarioAplicacion: el Usuario al que pertenece la lista.
		 * @param nombre: el nombre de la Playlist a eliminar.
		 */
		public void eliminarPlaylist(Usuario usuarioAplicacion, String nombre)
		{
			Set<Playlist> listas = usuarioAplicacion.getPlaylists();
			Set<Playlist> nuevasListas = new HashSet<Playlist>(listas);
			
			listas.stream()
				.filter(lista -> sonIgualesSinMayus(nombre, lista.getNombre()))
				.forEach(lista -> 
					{
						adaptadorPlaylist.borrarPlaylist(lista);
						nuevasListas.remove(lista);
					}
				);
			usuarioAplicacion.setPlaylist(nuevasListas);
			adaptadorUsuario.modificarUsuario(usuarioAplicacion);
			listas = nuevasListas;
//			crearCadenaPlaylists();
			notificarCambioPlaylists(usuarioAplicacion);
		}
		
		/*
		 * Método que devuelve los nombres de Playlists del usuario.
		 */
		public Set<Playlist> getPlaylists(Usuario usuario) {
			return new HashSet<Playlist>(usuario.getPlaylists());
		}
		
		/**
		 * Método que añade canciones a una Playlist con nombre pasado por argumento.
		 * @param nombrePlaylist: el nombre de la Playlist.
		 * @param canciones: las canciones a añadirle.
		 */
		public void anadirAPlaylist(Usuario usuario, String nombrePlaylist, List<Cancion> canciones)
		{
			try {
				Playlist p = buscarPlaylist(usuario, nombrePlaylist);
				anadirAPlaylist(p, canciones);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Método que añade canciones a una Playlist pasada por argumento.
		 * @param playlist: la Playlist.
		 * @param canciones: las canciones a añadirle.
		 */
		public void anadirAPlaylist(Playlist playlist, List<Cancion> canciones)
		{
			List<Cancion> cancionesPlaylist = playlist.getCanciones();
			canciones.stream()
				.forEach(cancion -> cancionesPlaylist.add(cancion));
			actualizarPlaylist(playlist, cancionesPlaylist);
		}
		
		/**
		 * Método que elimina las canciones de una Playlist con el nombre pasado por argumento.
		 * @param nombrePlaylist: el nombre de la Playlist.
		 * @param canciones: la lista de canciones a eliminar.
		 */
		public void eliminarDePlaylist(Usuario usuario, String nombrePlaylist, List<Cancion> canciones)
		{
			try {
				Playlist p = buscarPlaylist(usuario, nombrePlaylist);
				if(p == null)
					return;
				eliminarDePlaylist(p, canciones);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Método que elimina las canciones de una Playlist pasada por argumento.
		 * @param playlist: la Playlist de la que eliminar las canciones.
		 * @param canciones: las canciones a eliminar de la Playlist.
		 */
		private void eliminarDePlaylist(Playlist playlist, List<Cancion> canciones)
		{
			if(playlist == null)
				return;
			
			if(canciones == null)
				return;
			
			List<Cancion> nuevasCanciones = playlist.getCanciones().stream()
				.filter(cancion -> !canciones.contains(cancion))
				.collect(Collectors.toList());
			
			actualizarPlaylist(playlist, nuevasCanciones);			
		}
		
//		/**
//		 * Método que actualiza la Playlist con nombre pasado por argumento.
//		 * @param nombreLista: el nombre de la Playlist.
//		 * @param canciones: la nueva lista de Canciones de la Playlist.
//		 */
//		private void actualizarPlaylist(Usuario usuario, String nombreLista, List<Cancion> canciones) 
//		{
//			Playlist p = null;
//			try {
//				p = buscarPlaylist(usuario, nombreLista);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			actualizarPlaylist(p, canciones);
//		}
		
		/**
		 * Método que actualiza la Playlist pasada por argumento con las canciones pasadas por argumento.
		 * @param playlist: la Playlist a actualizar.
		 * @param canciones: la lista de canciones que vamos a asignarle a la Playlist.
		 */
		private void actualizarPlaylist(Playlist playlist, List<Cancion> canciones) 
		{
			if(playlist == null)
				return;
			if(canciones == null)
				return;
			
			playlist.setCanciones(canciones);
			adaptadorPlaylist.modificarPlaylist(playlist);
		} 
		
		/**
		 * Constantes para mensajes de error al crear una playlist.
		 */
		private static final String NOMBRE_LISTA_DUPLICADO = "Nombre de lista duplicado.";
		private static final String NOMBRE_VACIO_LISTA = "¡La lista debe tener un nombre!";
		
		/**
		 * Crea una Playlist sin canciones a partir de un nombre.
		 * @param nombre: nombre de la lista.
		 * @return un mensaje de error o "" si se ha conseguido crear con éxito.
		 */
		public String crearPlaylist(Usuario usuario, String nombre)
		{			
			return crearPlaylist(usuario, nombre, new LinkedList<Cancion>());
		}
		
		/**
		 * Método que añade una Playlist a un Usuario en el Sistema (modifica la persistencia de la Playlist y del Usuario).
		 * @param usuarioAplicacion: Usuario al que pertenecerá la Playlist.
		 * @param playlist: la Playlista a añadir.
		 */
		private void anadirPlaylist(Usuario usuarioAplicacion, Playlist playlist)
		{
			adaptadorPlaylist.registrarPlaylist(playlist);
			usuarioAplicacion.addPlaylist(playlist);
			adaptadorUsuario.modificarUsuario(usuarioAplicacion);
			notificarCambioPlaylists(usuarioAplicacion);
		}
		
		/**
		 * Método que crea una Playlist a partir de un Usuario, un nombre y una lista de canciones.
		 * @param usuario: Usuario al que pertenece la Playlist.
		 * @param nombre: nombre de la Playlist.
		 * @param canciones: lista de Canciones que contendrá la Playlist.
		 * @return "" si todo ha ido bien; String de error en otro caso.
		 */
		public String crearPlaylist(Usuario usuario, String nombre, List<Cancion> canciones) 
		{			
			if(nombre == null || nombre.trim().equals(""))
				return NOMBRE_VACIO_LISTA;
			
			if(existePlaylistConNombre(usuario, nombre))
				return NOMBRE_LISTA_DUPLICADO;
			
			Playlist nueva = new Playlist(nombre);
			if(canciones == null)
			{
				nueva.setCanciones(new LinkedList<Cancion>());
			}
			else
			{
				nueva.setCanciones(new LinkedList<Cancion>(canciones));
			}
			
			anadirPlaylist(usuario, nueva);
			return "";
		}
		
		
		/**
		 * Método que realiza el Login (comprueba que exista y le crea una VentanaMain) de un Usuario con nombre y contraseña pasados por argumento.
		 * @param nombre: nombre del Usuario.
		 * @param contrasena: contraseña del Usuario.
		 * @return null si todo ha salido bien o un String de error en otro caso.
		 */
		public String login(String nombre, char[] contrasena)
		{
			if(nombre.equals("") || nombre == null )
				return NOMBRE_VACIO;
			
			String password = new String(contrasena);
			if(password.equals("")  || password == null)
				return CONTRASENA_VACIA;
			
			if(catalogoUsuarios.getUsuarios().isEmpty() || !consultarUsuario(nombre))
				return USUARIO_NO_REGISTRADO;
			
			if(!consultarContrasena(nombre, password))
				return CONTRASENA_INCORRECTA;
			
			Usuario usuarioAplicacion = null;
			try {
				usuarioAplicacion = catalogoUsuarios.getUsuario(nombre);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			crearVentanaMain(nombre, usuarioAplicacion.isPremium());
			
			// Inicialización del reproductor de canciones.
			reproductor = Reproductor.getUnicaInstancia();
			reproductor.addObservadorReproduccion(this);
			reproductor.addObserver(this);
			
			return null;	// Si la ventana recibe null, entonces se loguea.
		}
		
		/**
		 * Método que devuelve si un Usuario es premium.
		 * @param usuario: el Usuario a comprobar.
		 * @return: true si es Premium, false si no.
		 */
		public boolean esPremium(Usuario usuario)
		{
			return usuario.isPremium();
		}
		
		/**
		 * Método que crea una VentanaMain para un Usuario con nombre pasado por argumento.
		 * @param usuario: el nombre del Usuario.
		 * @param premium: si el Usuario es premium. En caso de serlo, la ventana se inicializará con dos botones extra.
		 */
		private void crearVentanaMain(String usuario, boolean premium)
		{
			new NewVentanaPrincipal(usuario, premium);
		}
		
		private String formatearFecha(Date fecha) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(fecha);
		}
		
		/**
		 * Método que devuelve una lista con los nombres de Usuario del sistema.
		 * @return la lista con todos los nombres.
		 */
		public List<String> getNombresUsuarios()
		{
			return catalogoUsuarios.getUsuarios().stream()
					.map(u -> u.getNombre())
					.collect(Collectors.toList());
		}
		
		/*
		 * FUNCIÓN QUE DEVUELVE TODOS LOS ESTILOS MUSICALES DEL SISTEMA.
		 */
		public List<String> getEstilosMusicales(){
			List<String> estilos = new LinkedList<String>();
			estilos.add("Todos");
			cancionesPorEstilo.keySet().stream()
				.forEach(estilo -> estilos.add(estilo));
			return estilos;
		}
		
		
		/**
		 * Método que devuelve si un Usuario tiene una Playlist con el nombre pasado por argumento.
		 * @param usuario
		 * @param nombreLista
		 * @return
		 */
		public boolean existePlaylistConNombre(Usuario usuario, String nombreLista)
		{
			if(usuario.getPlaylists() == null || usuario.getPlaylists().isEmpty())
				return false;
			
			if(nombreLista == null)
				return false;
			
			return usuario.getPlaylists().stream()
					.anyMatch(lista -> sonIgualesSinMayus(nombreLista, lista.getNombre()));
		}		
		
		/**
		 * Método que devuelve la lista de canciones contenidas en la Playlist con nombre pasado por argumento.
		 * @param nombreLista: el nombre de la Playlist de la que queremos extraer las canciones.
		 * @return la Lista de Cancion.
		 */
		public List<Cancion> cargarDatosPlaylist(Usuario usuario, String nombreLista) 
		{
			Playlist playlist = null;
			try {
				playlist = buscarPlaylist(usuario, nombreLista);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return cargarDatosPlaylist(playlist);
		}
		
		/**
		 * Método que devuelve la lista de canciones contenidas en la Playlist pasada como argumento.
		 * @param playlist: la Playlist de la que cargar las canciones.
		 * @return la lista de canciones contenida en la Playlist.
		 */
		private List<Cancion> cargarDatosPlaylist(Playlist playlist)
		{
			if(playlist == null)
				return null;
			return playlist.getCanciones();
		}
		
		/**
		 * Busca una Playlist con el nombre pasado por argumento.
		 * @param nombre: nombre de la lista.
		 * @param usuario: Usuario al que le debe pertenecer la lista.
		 * @return la Playlist con ese nombre (sin tener en cuenta mayúsculas).
		 * @throws Exception: si hay más de una Playlist para el Usuario de la Aplicación con el mismo nombre, lanza una excepción.
		 */
		public Playlist buscarPlaylist(Usuario usuario, String nombre) throws Exception
		{
			List<Playlist> listasConNombre = usuario.getPlaylists().stream()
				.filter(l -> sonIgualesSinMayus(l.getNombre(), nombre))
				.collect(Collectors.toList());
			
			if(listasConNombre.size() > 1)
			{
				Exception e = new Exception("Algo falló: alguna lista del Usuario tiene el nombre duplicado.");
				throw e;
			}
			
			if(listasConNombre.size() == 0)
				return null;
			
			return listasConNombre.get(0);
		}
		
		public String getTituloCancion(Cancion c)
		{
			return c.getTitulo();
		}
		
		public String getEstiloCancion(Cancion c)
		{
			return c.getEstilo();
		}
		
		public String getInterpreteCancion(Cancion c)
		{
			return Cancion.getCadenaInterpretes(c.getInterpretes());
		}
		
		public int getNumReproducciones(Cancion c)
		{
			return c.getNumReproducciones();
		}
		
		/**
		 * Método que devuelve las Canciones del Sistema en orden de reproducciones.
		 * @return la Lista de canciones ordenada por número de reproducciones.
		 */
		public List<Cancion> getMasReproducidas()
		{
			return catalogoCanciones.getCanciones().stream()
				.sorted((c1, c2) -> c2.getNumReproducciones() - c1.getNumReproducciones())
				.collect(Collectors.toList());
		}
		
		/**
		 * Método para desloguearse (se pone unicaInstancia a null, de modo que se "reinicia" todo.
		 */
		public void logout()
		{
			Reproductor.reiniciar();
//			Controlador.unicaInstancia = null;
//			ventanaMain.getFrame().dispose();
			Lanzador.main(null);
		}
		
		public void shutdown()
		{
			unicaInstancia = null;
		}
		
		/**
		 * Método para hacer premium a un Usuario.
		 * @param usuario: nombre del Usuario a hacer premium.
		 * @param parado: indica si dicho Usuario ha marcado si está en paro.
		 * @return: el precio a pagar.
		 * @throws Exception
		 */
		public float hacerPremium(String usuario, Boolean parado) throws Exception
		{
			Usuario user = getUsuario(usuario);
			
			if(user == null)
				throw new Exception("Intentando hacer premium un Usuario no existente en el Sistema.");
			
			user.setPremium(true);
			adaptadorUsuario.modificarUsuario(user);
			return aplicarDescuentos(user, parado);
		}
		
		private float aplicarDescuentos(Usuario usuario, boolean parado)
		{
			float precio = PRECIO_APPMUSIC;
			FactoriaDescuento factoria = FactoriaDescuento.getUnicaInstancia();
			Descuento descuento = null; 
			if(isJoven(usuario))
			{
				descuento = factoria.crearDescuento("descuento.DescuentoJovenes");
				precio = descuento.aplicarDescuento(precio);
			}
			else if(parado)
			{
				descuento = factoria.crearDescuento("descuento.DescuentoParados");
				precio = descuento.aplicarDescuento(precio);
			}
			usuario.setDescuento(descuento);
			adaptadorUsuario.modificarUsuario(usuario);
			return precio;
		}
		
		private final static int EDAD_JOVEN = 25;
		
		/**
		 * Método que devuelve si un Usuario es joven (menor de 25 años).
		 * @param usuario: Usuario a comprobar si es joven.
		 * @return true si es menor de 25 años, false si no.
		 */
		private static boolean isJoven(Usuario usuario)
		{
			String fechaNacimiento = usuario.getFechaNacimiento();
			int actualYear = LocalDate.now().getYear();
			int mesActual = LocalDate.now().getMonthValue();
			int diaActual = LocalDate.now().getDayOfMonth();
			
			String[] elementosFechaNacimiento = fechaNacimiento.split("/");
			int yearNacimiento = Integer.parseInt(elementosFechaNacimiento[2]);
			int mesNacimiento = Integer.parseInt(elementosFechaNacimiento[1]);
			int diaNacimiento = Integer.parseInt(elementosFechaNacimiento[0]);
			
			if(EDAD_JOVEN >= actualYear - yearNacimiento)
				return true;
			if(EDAD_JOVEN == actualYear - yearNacimiento + 1)
			{
				if(mesActual < mesNacimiento)
					return true;
				if(mesActual == mesNacimiento)
					return diaActual < diaNacimiento;
			}
			return false;
		}
		
		/**
		 * Método que busca un Usuario en el sistema dado su nombre.
		 * @param nombre: nombre del Usuario.
		 * @return el Usuario o null si no había ningún Usuario con ese nombre.
		 */
		public Usuario getUsuario(String nombre)
		{
			for(Usuario u: catalogoUsuarios.getUsuarios())
			{
				if(u.getNombre().equals(nombre))
					return u;
			}
			return null;
		}
		
		
		/**
		 * Clase anidada porque únicamente el Controlador la usará. Simplemente, se utilizará como subclase de Usuario para comprobar si este ha sido logueado con GitHub.
		 */
		class UsuarioGitHub extends Usuario {
			public UsuarioGitHub(String nombre, String password, String email, String fechaNacimiento) {
				super(nombre, password, email, fechaNacimiento);
			}
		}
		
		/**
		 *	Método para generar el PDF con los datos del Usuario.
		 */
		public void generarPDF(String ubicacion, Usuario usuario)
		{
			String contenido = "";
			List<String> datosListas = new LinkedList<String>();
			for(Playlist p: usuario.getPlaylists())
			{
				String datosLista = "Nombre de la lista: " + p.getNombre() + "\n";
				for(Cancion c: p.getCanciones())
				{
					datosLista +=
							"título: " + c.getTitulo()
							+ ", intérpretes: " + Cancion.getCadenaInterpretes(c.getInterpretes())
							+ ", estilo: " + c.getEstilo()
							+ "\n";
				}
				contenido += datosLista + "\n";
			}
			try {
				GeneradorPDF.generarPDF(usuario.getNombre() + ".pdf", ubicacion, contenido);
			} catch (FileNotFoundException | DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Método para actualizar un Usuario.
		 * @param usuario: el Usuario a actualizar.
		 */
		public void actualizarUsuario(Usuario usuario)
		{
			adaptadorUsuario.modificarUsuario(usuario);
			catalogoUsuarios.reemplazarUsuarios(adaptadorUsuario.recuperarTodosUsuarios());
		}
}
