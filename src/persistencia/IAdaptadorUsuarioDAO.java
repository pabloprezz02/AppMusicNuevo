package persistencia;

import java.util.List;

import modelo.Usuario;

public interface IAdaptadorUsuarioDAO extends Adaptador {
	public void registrarUsuario(Usuario usuario);
	public void modificarUsuario(Usuario usuario);
	// BORRAR
	public void eliminarUsuario(Usuario usuario);
	//
	public Usuario recuperarUsuario(int codigo);
	public List<Usuario> recuperarTodosUsuarios();
}
