package persistencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.persistence.internal.sessions.ExclusiveIsolatedClientSession;

import beans.Entidad;
import beans.Propiedad;
import modelo.Cancion;
//import modelo.Descuento;
//import modelo.DescuentoFijo;
//import modelo.DescuentoJovenes;
import modelo.Playlist;
import modelo.Usuario;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;

public class AdaptadorUsuario implements IAdaptadorUsuarioDAO {
	private static ServicioPersistencia servicioPersistencia;
	private static AdaptadorUsuario unicaInstancia=null;
	
	private AdaptadorUsuario() {
		servicioPersistencia=FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}
	
	public static AdaptadorUsuario getUnicaInstancia() {
		if(unicaInstancia==null) {
			unicaInstancia=new AdaptadorUsuario();
		}
		return unicaInstancia;
	}
	
	@Override
	public void registrarUsuario(Usuario usuario) {
		Entidad eUsuario=null;
		try {
			eUsuario=servicioPersistencia.recuperarEntidad(usuario.getCodigo());
		}catch(Exception e) {}
		if(eUsuario!=null) return;
		
		// REGISTRO DE PROPIEDADES OBJETO
		AdaptadorPlaylist adaptadorPlaylist=AdaptadorPlaylist.getUnicaInstancia();
		for(Playlist playlist:usuario.getPlaylists()) {
			adaptadorPlaylist.registrarPlaylist(playlist);
		}
		
		AdaptadorCancion adaptadorCancion=AdaptadorCancion.getUnicaInstancia();
		for(Cancion cancion:usuario.getRecientes()) {
			adaptadorCancion.registrarCancion(cancion);
		}
		// DESCUENTO
		
		// CREAR ENTIDAD USUARIO
		eUsuario=new Entidad();
		eUsuario.setNombre("usuario");
		List<Propiedad> propiedades=new ArrayList<>();
		propiedades.add(new Propiedad("nombre", usuario.getNombre()));
		propiedades.add(new Propiedad("password", usuario.getPassword()));
		propiedades.add(new Propiedad("premium", Boolean.toString(usuario.isPremium())));
		propiedades.add(new Propiedad("playlists", getCodigosPlaylists(usuario.getPlaylists())));
		propiedades.add(new Propiedad("fechaNacimiento", usuario.getFechaNacimiento()));
		propiedades.add(new Propiedad("email", usuario.getEmail()));
		propiedades.add(new Propiedad("recientes", getCodigosRecientes(usuario.getRecientes())));
//		propiedades.add(new Propiedad("descuento", usuario.getTipoDescuento()));
		eUsuario.setPropiedades(propiedades);
		
		// REGISTRAR ENTIDAD USUARIO
		eUsuario=servicioPersistencia.registrarEntidad(eUsuario);
		// APROVECHO EL CÓDIGO
		usuario.setCodigo(eUsuario.getId());
	}
	
	@Override
	public Usuario recuperarUsuario(int codigo) {
		// NO ES NECESARIO UN POOL, PUES NO TIENE NINGÚN ATRIBUTO CUYA CLASE REFERENCIE A USUARIOS
		Set<Playlist> playlists;
		List<Cancion> recientes;
		String password;
		String nombre;
		boolean premium;
		String fechaNacimiento;
		String email;
//		Descuento descuento;
		
		// RECUPERACIÓN DE LA ENTIDAD
		Entidad eUsuario=servicioPersistencia.recuperarEntidad(codigo);
		
		// RECUPERACIÓN DE LAS PROPIEDADES
		password=servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "password");
		nombre=servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "nombre");
		email = servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "email");
		fechaNacimiento=servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "fechaNacimiento");
		
		//Comprobar con profesor
//		String tipoDescuento=servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "descuento");
//        if(tipoDescuento.equals("DescuentoFijo")) {
//            descuento=new DescuentoFijo();
//        }else {
//            descuento=new DescuentoJovenes();
//        }
		// PREGUNTAR ESTO
		if(servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "premium").equals("true")) {
			premium=true;
		}else {
			premium=false;
		}
		
		// CREACIÓN DEL OBJETO
		Usuario usuario=new Usuario(nombre, password.toString(), email, fechaNacimiento);
		usuario.setPremium(true);
		usuario.setCodigo(codigo);
//		usuario.setDescuento(descuento);
		
		// RECUPERACIÓN DE PROPIEDADES QUE SON OBJETOS
		playlists=getPlaylistsDesdeCodigos(servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "playlists"));
		for(Playlist playlist:playlists) {
			usuario.addPlaylist(playlist);
		}
		
		recientes=getRecientesDesdeCodigos(servicioPersistencia.recuperarPropiedadEntidad(eUsuario, "recientes"));
		for(Cancion reciente:recientes) {
			usuario.addCancionReciente(reciente);
		}
		
		// RETORNO DEL OBJETO
		return usuario;
	}
	
	@Override
	public List<Usuario> recuperarTodosUsuarios(){
		List<Entidad> eUsuarios=servicioPersistencia.recuperarEntidades("usuario");
		List<Usuario> usuarios=new LinkedList<>();
		
		for(Entidad eUsuario:eUsuarios) {
			usuarios.add(recuperarUsuario(eUsuario.getId()));
		}
		return usuarios;
	}
	
	@Override
	public void eliminarUsuario(Usuario usuario) {
		// SE RECUPERA LA ENTIDAD
		Entidad eUsuario=servicioPersistencia.recuperarEntidad(usuario.getCodigo());
		
		// SE ELIMINAN SUS ENTIDADES AGREGADAS (PLAYLISTS)
		AdaptadorPlaylist adaptadorPlaylist=AdaptadorPlaylist.getUnicaInstancia();
		for(Playlist playlist:usuario.getPlaylists()) {
			adaptadorPlaylist.borrarPlaylist(playlist);
		}
		
		// SE ELIMINA LA ENTIDAD
		servicioPersistencia.borrarEntidad(eUsuario);
	}
	
	@Override
	public void modificarUsuario(Usuario usuario) {
		// SE RECUPERA LA ENTIDAD
		Entidad eUsuario=servicioPersistencia.recuperarEntidad(usuario.getCodigo());
		
		// SE RECORREN SUS PROPIEDADES PARA ACTUALIZARLAS
		for(Propiedad propiedad:eUsuario.getPropiedades()) {
			if(propiedad.getNombre().equals("nombre")) {
				propiedad.setValor(usuario.getNombre());
			}else if(propiedad.getNombre().equals("password")) {
				propiedad.setValor(usuario.getPassword().toString());
			}else if(propiedad.getNombre().equals("premium")) {
				propiedad.setValor(Boolean.toString(usuario.isPremium()));
			}else if(propiedad.getNombre().equals("playlists")) {
				propiedad.setValor(getCodigosPlaylists(usuario.getPlaylists()));
			}else if(propiedad.getNombre().equals("recientes")) {
				propiedad.setValor(getCodigosRecientes(usuario.getRecientes()));
			}else if(propiedad.getNombre().equals("email")) {
				propiedad.setValor(usuario.getEmail());
			}else if(propiedad.getNombre().equals("fechaNacimiento")) {
				propiedad.setValor(usuario.getFechaNacimiento());
			}
//			}else if(propiedad.getNombre().equals("descuento")) {
//				propiedad.setValor(usuario.getTipoDescuento());
//			}
			servicioPersistencia.modificarPropiedad(propiedad);
		}
	}
	
	// ******************** FUNCIONES AUXILIARES ********************
	private String getCodigosPlaylists(Set<Playlist> playlists) {
		String cadena="";
		for(Playlist playlist:playlists) {
			cadena+=playlist.getCodigo()+" ";
		}
		return cadena.trim();
	}
	
	private Set<Playlist> getPlaylistsDesdeCodigos(String cadena){
		Set<Playlist> playlists=new HashSet<>();
		StringTokenizer strtok=new StringTokenizer(cadena, " ");
		AdaptadorPlaylist adaptadorPlaylist=AdaptadorPlaylist.getUnicaInstancia();
		while(strtok.hasMoreTokens()) {
			playlists.add(adaptadorPlaylist.recuperarPlaylist(Integer.valueOf((String) strtok.nextElement())));
		}
		return playlists;
	}
	
	private String getCodigosRecientes(List<Cancion> recientes) {
		String cadena="";
		for(Cancion reciente:recientes) {
			cadena+=reciente.getCodigo()+" ";
		}
		return cadena.trim();
	}
	
	private List<Cancion> getRecientesDesdeCodigos(String cadena){
		List<Cancion> recientes=new LinkedList<>();
		StringTokenizer strtok=new StringTokenizer(cadena, " ");
		AdaptadorCancion adaptadorCancion=AdaptadorCancion.getUnicaInstancia();
		while(strtok.hasMoreTokens()) {
			recientes.add(adaptadorCancion.recuperarCancion(Integer.valueOf((String) strtok.nextElement())));
		}
		return recientes;
	}
}
