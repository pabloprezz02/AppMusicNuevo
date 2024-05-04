package persistencia;

import java.util.List;

import modelo.Playlist;

public interface IAdaptadorPlaylist {
	public void registrarPlaylist(Playlist playlist);
	public void borrarPlaylist(Playlist playlist);
	public void modificarPlaylist(Playlist playlist);
	public Playlist recuperarPlaylist(int codigo);
	public List<Playlist> recuperarTodasPlaylists();
}
