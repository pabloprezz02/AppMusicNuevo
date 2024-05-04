package controlador;

import java.util.Date;
import java.util.EventObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import auxiliares.ComboBoxObserver;
import auxiliares.reproductor.Reproductor;
import controlador.Controlador;
import descuento.Descuento;
import descuento.FactoriaDescuento;
import javafx.scene.media.MediaPlayer;
import main.Lanzador;
import modelo.Cancion;
import modelo.CatalogoCanciones;
import modelo.CatalogoPlaylists;
import modelo.CatalogoUsuarios;
import modelo.Playlist;
import modelo.Usuario;
import observador.Observable;
import observador.Observer;
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
import ventanas.VentanaCanciones;
import ventanas.VentanaLista;
//import ventanas.VentanaPrincipal;
import ventanas.VentanaUsuarios;

public class Controlador extends Observable implements CancionesListener, Observer {
	
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
//		private CatalogoPlaylists catalogoPlaylists = null;
				
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
//		private List<Cancion> favoritas;
		
		
		// CLASES DAO
		// ...
		// REFERENCIAS A DRIVERS
		// ...
		
		// REPRODUCTOR Y CANCIÓN ACTUAL
		private Reproductor reproductor;
		
//		// EL USUARIO QUE ESTARÁ USANDO LA APLICACIÓN UNA VEZ HAYA REALIZADO EL LOGIN.
//		private Usuario usuarioAplicacion = null;
//		
//		// PLAYLISTS DEL USUARIO.
//		private Set<Playlist> listas;
		private Map<Usuario, String> cadenasPlaylists;
		
		private Controlador() {
			// Inicialización de los filtros.
			cancionesPorArtista = new HashMap<String, List<Cancion>>();
			cancionesPorEstilo = new HashMap<String, List<Cancion>>();
			cancionesPorTitulo = new HashMap<String, List<Cancion>>();
			
			// Inicialización del conjunto de Playlists.

			cadenasPlaylists = new HashMap<Usuario, String>();

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
			
//			for(Usuario u:catalogoUsuarios.getUsuarios()) {
//				catalogoUsuarios.removeUsuario(u);
//			}
//			for(Cancion c:catalogoCanciones.getCanciones())
//				catalogoCanciones.removeCancion(c);
			
			// Inicialización de las cadenas para observadores.
//			crearCadenaNombresUsuarios();
//			crearCadenaTitulosCanciones();
//			crearCadenaEstilos();
//			crearCadenaPlaylists();
			
			// Inicialización del cargador de canciones.
			cargadorCanciones = new CargadorCanciones();
			cargadorCanciones.addCancionesListener(this);
			
			// Usuario de la aplicación a null.
//			usuarioAplicacion = null;
			
//			ventanaMain = null;
//			listas = null;
		}
		
//		private void crearNombresListas(Usuario usuarioAplicacion) {
//			nombresPlaylists = "Listas\n";
//			
//			// Si ya hay un usuario usando la aplicación actualizo las listas.
//			if(usuarioAplicacion != null) {
//				usuarioAplicacion.getPlaylists().stream()
//					.forEach(lista -> nombresPlaylists += lista.getNombre());
//				
//				listas.stream()
//					.forEach(l -> nombresPlaylists += (l.getNombre() + "\n"));
//			}
//		}
		
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
			cancionesPorTitulo.put(titulo, new LinkedList());
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
//			Set<String>	interpretes = cancionesPorArtista.keySet();
////			return catalogoCanciones.getCanciones().stream()
////					.anyMatch(c -> c.equals(cancion));
//			Collection<Cancion> canciones = buscarCanciones(getCadenaInterpretes(cancion.getInterpretes()), cancion.getTitulo(), cancion.getEstilo());
//			return  canciones != null && !canciones.isEmpty();			
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
		
		/*
		 * INSERTAR UNA CANCIÓN EN EL SISTEMA (FILTROS, CATÁLOGO Y ADAPTADOR).
		 * ACTUALIZA LA CADENA QUE REPRESENTA LAS CANCIONES PARA LAS VENTANAS.
		 */
		private void insertarCancionSinNotificar(Cancion c) {
			// Si la canción existía, no hago nada.
			if(cancionExistente(c))
				return;
			
			// Si el intérprete no estaba...
			c.getInterpretes().stream()
				.filter(interprete -> !existeInterprete(interprete))
				.forEach(interprete -> crearInteprete(interprete));
			
//			if(!existeInterprete(c.getInterprete())) {
//				crearInteprete(c.getInterprete());
//			}
			
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
			
			/*
			 * Actualización de la cadena para el Observer:
			 */
//			titulosCanciones += (c.getTitulo() + " ~ " + getCadenaInterpretes(c.getInterpretes()) + "\n");
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
		
		// Si cambian las canciones.
		private void notificarCambioCanciones() 
		{
			List<String> canciones = new LinkedList<String>(catalogoCanciones.getCanciones().stream()
					.map(c -> c.getTitulo() + "~" + Cancion.getCadenaInterpretes(c.getInterpretes()))
					.collect(Collectors.toList())
					);
			canciones.add(0, "Canciones");
			setChanged();
			notifyObservers(canciones);
		}
		
		// Si cambian los estilos.
		private void notificarCambioEstilos() 
		{
			List<String> estilos = new LinkedList<String>(cancionesPorEstilo.keySet());
			estilos.add(0, "Todos");
			setChanged();
			notifyObservers(estilos);
		}
		
		// Si cambian los usuarios.
		private void notificarCambioUsuarios() 
		{
			LinkedList<String> usuarios = new LinkedList<String>(catalogoUsuarios.getUsuarios().stream()
					.map(u -> u.getNombre())
					.collect(Collectors.toList()));
			usuarios.add(0, "Usuarios");
			setChanged();
			notifyObservers(usuarios);
		}
		
		// Si cambian las playlists.
		private void notificarCambioPlaylists(Usuario usuario)
		{
			List<String> usuarioYPlaylists = new LinkedList<String>(getPlaylists(usuario).stream()
					.map(p -> p.getNombre())
					.collect(Collectors.toList()));
			
			usuarioYPlaylists.add(0, "Listas");
			usuarioYPlaylists.add(0, usuario.getNombre());
			setChanged();
			notifyObservers(usuarioYPlaylists);
//			actualizarFavoritas();
		}
		
//		private void actualizarFavoritas()
//		{
//			favoritas = new LinkedList<Cancion>(); 
//			listas.stream()
//				.map(lista -> lista.getCanciones())
//				.flatMap(listaCanciones -> listaCanciones.stream())
//				.forEach(c -> 
//				{
//					if(!favoritas.contains(c))
//						favoritas.add(c);
//				});
//		}
		
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
			}
		}
		
		/*
		 * REGISTRO DE UN USUARIO. DEVOLVERÁ UN STRING REPRESENTANDO MENSAJES O NULL EN CASO DE REGISTRAR EL USUARIO.
		 */
		public String registrarUsuario(String nombre, char[] contrasena, String email, Date fecha) {
			
			String password = new String(contrasena);
			
			// ***************** MENSAJES DE ERROR ***************** //
			
			// El nombre es vacío --> no válido.
			if(nombre.equals("") || nombre==null)
				return NOMBRE_VACIO;
			
			// La contraseña es vacía --> no válida.
			if(password.equals("") || password==null)
				return CONTRASENA_VACIA;
			
			// Email vacío --> no válido.
			if(email.equals("") || email==null)
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
			
			if((usuario = catalogoUsuarios.getUsuario(nombre)) != null)
				return usuario.getPassword().equals(password);
			
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
		 * @return
		 */
		public boolean isLogueadoConGitHub(String nombreUsuario)
		{
			Usuario usuario = getUsuario(nombreUsuario);
			
			if(usuario == null)
				return false;
			
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
		
		public Collection<Cancion> getRecientes()
		{
			return reproductor.getRecientes();
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
			reproductor.pausar();
		}
		
		public void reproducirRecientesSec()
		{
			reproductor.reproducirRecientesSecuencialmente();
		}
		
		public void reproducirRecientesAleat()
		{
			reproductor.reproducirRecientesAleatoriamente();
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
				reproducirRecientesSec();
				return;
			}
			Playlist p = null;
			try {
				p = buscarPlaylist(usuario, playlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reproducirSecuencialmente(p);
		}
		
		public void reproducirSecuencialmente(Playlist playlist)
		{
			if(playlist == null)
				return;
			reproducirSecuencialmente(playlist.getCanciones());
		}
		
		public void reproducirSecuencialmente(List<Cancion> canciones)
		{
			if(canciones == null)
				return;
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
				reproducirRecientesAleat();
				return;
			}
			
			Playlist p = null;
			try {
				p = buscarPlaylist(usuario, playlist);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reproducirAleatoriamente(p);
		}
		
		public void reproducirAleatoriamente(Playlist playlist)
		{
			if(playlist == null)
				return;
			reproducirAleatoriamente(playlist.getCanciones());
		}
		
		public void reproducirAleatoriamente(List<Cancion> canciones)
		{
			if(canciones == null)
				return;
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
		
//		/*
//		 * Método para crear la cadena con los nombres de las Playlists.
//		 */
//		private void crearCadenaPlaylists() 
//		{
//			nombresPlaylists = "Listas\n";
//			listas.stream()
//				.forEach(lista -> nombresPlaylists += (lista.getNombre() + "\n"));
//		}
		
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
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
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
		
		/**
		 * Método que actualiza la Playlist con nombre pasado por argumento.
		 * @param nombreLista: el nombre de la Playlist.
		 * @param canciones: la nueva lista de Canciones de la Playlist.
		 */
		private void actualizarPlaylist(Usuario usuario, String nombreLista, List<Cancion> canciones) 
		{
			Playlist p = null;
			try {
				p = buscarPlaylist(usuario, nombreLista);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			actualizarPlaylist(p, canciones);
		}
		
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
		
		private void anadirPlaylist(Usuario usuarioAplicacion, Playlist playlist)
		{
			adaptadorPlaylist.registrarPlaylist(playlist);
			usuarioAplicacion.addPlaylist(playlist);
			adaptadorUsuario.modificarUsuario(usuarioAplicacion);
//			nombresPlaylists += (playlist.getNombre() + "\n");
			String cadena = cadenasPlaylists.get(usuarioAplicacion) + (playlist.getNombre() + "\n");
			cadenasPlaylists.put(usuarioAplicacion, cadena);
			notificarCambioPlaylists(usuarioAplicacion);
		}
		
		/*
		 * Método para crear una playlist a partir de un nombre y una lista de canciones.
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
		
		
		/*
		 * REALIZA EL LOGIN DEL USUARIO CON NOMBRE nombre Y CONTRASEÑA contrasena.
		 * EL USUARIO QUE UTILIZA LA APLICACIÓN QUEDA REGISTRADO EN EL ATRIBUTO usuarioAplicacion.
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
			
			Usuario usuarioAplicacion = catalogoUsuarios.getUsuario(nombre);
//			listas = usuarioAplicacion.getPlaylists();
//			favoritas = new LinkedList<Cancion>();
//			for(Playlist p:listas) {
//				nombresPlaylists += p.getNombre() + "\n";
//				for(Cancion c:p.getCanciones()) {
//					if(!favoritas.contains(c)) {
//						favoritas.add(c);
//					}
//				}
//			}
			
//			listas.stream()
//				.forEach(lista -> {
//					nombresPlaylists += (lista.getNombre() + "\n");
//					lista.getCanciones().stream()
//						.filter(cancion -> !favoritas.contains(cancion))
//						.forEach(cancion -> favoritas.add(cancion));
//				});
			
			NewVentanaPrincipal ventanaMain = new NewVentanaPrincipal(nombre, usuarioAplicacion.isPremium());
			// Inicialización del reproductor de canciones.
			reproductor = Reproductor.getUnicaInstancia();
			reproductor.addObserver(this);
//			obs.add(ventanaMain);
			return null;	// Si la ventana recibe null, entonces se loguea.
		}
		
		private String formatearFecha(Date fecha) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(fecha);
		}
		
		// COMO NO SABEMOS SI LAS VENTANAS SIGUEN ABIERTAS O ACTIVAS, SE COMPRUEBA QUE EL OBSERVADOR SIGA ACTIVO.
//		@Override
//		public boolean comprobar(Observer o) {
//			if(o instanceof VentanaLista)
//				if(((VentanaLista)o).isActive())
//					return true;
//				else {
//					deleteObserver(o);
//					return false;
//				}
//			return true;
//		}
		
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
		
//		public String mostrarUsuarios() {
//			return nombresUsuarios;
//		}
//		
//		public String mostrarCanciones() {
//			return titulosCanciones;
//		}
		
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
				// TODO Auto-generated catch block
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
		
		/**
		 * Método para desloguearse (se pone unicaInstancia a null, de modo que se "reinicia" todo.
		 */
		public void logout()
		{
			Reproductor.reiniciar();
			this.unicaInstancia = null;
//			ventanaMain.getFrame().dispose();
			Lanzador.main(null);
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
			return aplicarDescuentos(user, parado);
		}
		
		private float aplicarDescuentos(Usuario usuario, boolean parado)
		{
			float precio = PRECIO_APPMUSIC;
			FactoriaDescuento factoria = FactoriaDescuento.getUnicaInstancia();
			if(isJoven(usuario))
			{
				Descuento descuento = factoria.crearDescuento("DescuentoJovenes");
				precio = descuento.aplicarDescuento(precio);
			}
			if(parado)
			{
				Descuento descuento = factoria.crearDescuento("DescuentoParados");
				precio = descuento.aplicarDescuento(precio);
			}
			return precio;
		}
		
		private final static int EDAD_JOVEN = 25;
		
		/**
		 * Método que devuelve si un Usuario es joven (menor de 25 años).
		 * @param usuario: Usuario a comprobar si es joven.
		 * @return true si es menor de 25 años, false si no.
		 */
		private boolean isJoven(Usuario usuario)
		{
			String fechaNacimiento = usuario.getFechaNacimiento();
			int actualYear = LocalDate.now().getYear();
			int mesActual = LocalDate.now().getMonthValue();
			int diaActual = LocalDate.now().getDayOfMonth();
			
			String[] elementosFechaNacimiento = fechaNacimiento.split("/");
			int yearNacimiento = Integer.parseInt(elementosFechaNacimiento[2]);
			int mesNacimiento = Integer.parseInt(elementosFechaNacimiento[1]);
			int diaNacimiento = Integer.parseInt(elementosFechaNacimiento[0]);
			
			if(EDAD_JOVEN <= actualYear - yearNacimiento)
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
}
