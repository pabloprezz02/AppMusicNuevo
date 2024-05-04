package persistencia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Entidad;
import beans.Propiedad;
import modelo.Cancion;
import modelo.Playlist;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;

public class AdaptadorPlaylist implements IAdaptadorPlaylist {
	private static ServicioPersistencia servicioPersistencia;
	private static AdaptadorPlaylist unicaInstancia=null;
	
	private AdaptadorPlaylist() {
		servicioPersistencia=FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}
	
	public static AdaptadorPlaylist getUnicaInstancia() {
		if(unicaInstancia==null) {
			unicaInstancia=new AdaptadorPlaylist();
		}
		return unicaInstancia;
	}
	
	
	@Override
	public void registrarPlaylist(Playlist playlist) {
		// COMPRUEBA SI EXISTE LA ENTIDAD
		Entidad ePlaylist=null;
		try {
			ePlaylist=servicioPersistencia.recuperarEntidad(playlist.getCodigo());
		}catch(NullPointerException e) {}
		if(ePlaylist!=null) {
			return;
		}
		
		// REGISTRAR OBJETOS AGREGADOS
		AdaptadorCancion adaptadorCancion=AdaptadorCancion.getUnicaInstancia();
		for(Cancion cancion:playlist.getCanciones()) {
			adaptadorCancion.registrarCancion(cancion);
		}
		
		// CREAR ENTIDAD PLAYLIST
		ePlaylist=new Entidad();
		ePlaylist.setNombre("playlist");
		List<Propiedad> propiedades=new ArrayList<>();
		propiedades.add(new Propiedad("nombre", playlist.getNombre()));
		propiedades.add(new Propiedad("canciones", obtenerCodigosCanciones(playlist.getCanciones())));
		ePlaylist.setPropiedades(propiedades);
		
		// REGISTRAR ENTIDAD PLAYLIST
		ePlaylist=servicioPersistencia.registrarEntidad(ePlaylist);
		// APROVECHO EL CÓDIGO
		playlist.setCodigo(ePlaylist.getId());
	}
	
	@Override
	public Playlist recuperarPlaylist(int codigo) {
		// COMPROBAR SI EXISTE EN EL POOL
//		if(PoolDAO.getUnicaInstancia().contiene(codigo)) {
//			return (Playlist) PoolDAO.getUnicaInstancia().getObjeto(codigo);
//		}
		
		// RECUPERACIÓN DE LA ENTIDAD
		Entidad ePlaylist=servicioPersistencia.recuperarEntidad(codigo);
		String nombre;
		List<Cancion> canciones;
		
		// RECUPERACIÓN DE LAS PROPIEDADES
		nombre=servicioPersistencia.recuperarPropiedadEntidad(ePlaylist, "nombre");
		
		// CREACIÓN DEL OBJETO
		Playlist playlist=new Playlist(nombre);
		playlist.setCodigo(codigo);
		
		// AÑADIR AL POOL ?
		
		// RECUPERACIÓN DE PROPIEDADES QUE SON OBJETOS
		canciones=getCancionesDesdeCodigos(servicioPersistencia.recuperarPropiedadEntidad(ePlaylist, "canciones"));
		for(Cancion cancion:canciones) {
			playlist.addCancion(cancion);
		}
		
		// RETORNO EL OBJETO
		return playlist;
	}
	
	@Override
	public List<Playlist> recuperarTodasPlaylists() {
		List<Entidad> ePlaylists = servicioPersistencia.recuperarEntidades("playlist");
////		List<Playlist> playlists = new LinkedList<Playlist>();
//		
//		ePlaylists.stream()
//			.forEach(ePlaylist -> playlists.add(recuperarPlaylist(ePlaylist.getId())));
		
		List<Playlist> playlists = ePlaylists.stream()
			.map(ePlaylist -> recuperarPlaylist(ePlaylist.getId()))
			.collect(Collectors.toList());
		
		return playlists;
	}
	
	@Override
	public void modificarPlaylist(Playlist playlist) {
		// SE RECUPERA LA ENTIDAD
		Entidad ePlaylist=servicioPersistencia.recuperarEntidad(playlist.getCodigo());
		
		// SE RECORREN LAS PROPIEDADES Y SE ACTUALIZAN
		for(Propiedad p:ePlaylist.getPropiedades()) {
			if(p.getNombre().equals("nombre")) {
				p.setValor(playlist.getNombre());
			}else if(p.getNombre().equals("canciones")) {
				p.setValor(obtenerCodigosCanciones(playlist.getCanciones()));
			}
			servicioPersistencia.modificarPropiedad(p);
		}
	}
	
	@Override
	public void borrarPlaylist(Playlist playlist) {
		// SE RECUPERA LA ENTIDAD
		Entidad ePlaylist=servicioPersistencia.recuperarEntidad(playlist.getCodigo());
		
		// SE ELIMINAN SUS ENTIDADES AGREGADAS (NADA)
		
		// SE ELIMINA LA ENTIDAD
		servicioPersistencia.borrarEntidad(ePlaylist);
	}
	
	// *************** FUNCIONES AUXILIARES ***************
	private String obtenerCodigosCanciones(List<Cancion> canciones) {
		String cadena="";
		for(Cancion cancion:canciones) {
			cadena+=cancion.getCodigo()+" ";
		}
		return cadena.trim();
	}
	
	private List<Cancion> getCancionesDesdeCodigos(String cadena){
		List<Cancion> canciones=new LinkedList<>();
		if(cadena == null)
			return canciones;
		StringTokenizer strtok=new StringTokenizer(cadena, " ");
		AdaptadorCancion adaptadorCancion=AdaptadorCancion.getUnicaInstancia();
		while(strtok.hasMoreTokens()) {
			canciones.add(adaptadorCancion.recuperarCancion(Integer.valueOf((String) strtok.nextElement())));
		}
		return canciones;
	}
}
