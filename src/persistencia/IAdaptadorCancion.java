package persistencia;

import java.util.List;

import modelo.Cancion;

public interface IAdaptadorCancion extends Adaptador {
	public void registrarCancion(Cancion cancion);
	public void modificarCancion(Cancion cancion);
	// BORRAR
	public void eliminarCancion(Cancion cancion);
	// 
	public Cancion recuperarCancion(int codigo);
	public List<Cancion> recuperarTodasCanciones();
}
