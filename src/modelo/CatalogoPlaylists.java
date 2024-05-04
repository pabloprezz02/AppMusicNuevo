package modelo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import persistencia.FactoriaDAO;
import persistencia.IAdaptadorPlaylist;

public class CatalogoPlaylists {
	// ATRIBUTOS
	private Map<Integer, Playlist> playlists;
	private FactoriaDAO dao;
	private IAdaptadorPlaylist adaptadorPlaylist;
	
	// SINGLETON
	private static CatalogoPlaylists unicaInstancia = null;
	
	// CONSTRUCTOR
	private CatalogoPlaylists() {
		try {
			dao = FactoriaDAO.getUnicaInstancia();
			adaptadorPlaylist = dao.getAdaptadorPlaylist();
			playlists = new HashMap<Integer, Playlist>();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// GET ÚNICA INSTANCIA --> SINGLETON
	public static CatalogoPlaylists getUnicaInstancia() {
		if(unicaInstancia == null)
			unicaInstancia = new CatalogoPlaylists();
		return unicaInstancia;
	}
	
	// MÉTODOS
	
	// GET PLAYLISTS: devuelve la lista de playlists (values del atributo playlists).
	public List<Playlist> getListas(){
		return new LinkedList<Playlist>(playlists.values());
	}
	
	// getPlaylist(int código): devuelve aquella playlist cuyo código es pasado por argumento.
	public Playlist getPlaylist(int codigo) {
		return playlists.get(codigo);
	}
	
	// addPlaylist(Playlist lista): añade a las playlists la lista "lista"
	public void addPlaylist(Playlist lista) {
		playlists.put(lista.getCodigo(), lista);
	}
	
	// removePlaylist(Playlist lista): elimina la lista del atributo playlists-
	public void removePlaylist(Playlist lista) {
		playlists.remove(lista.getCodigo());
	}
}
