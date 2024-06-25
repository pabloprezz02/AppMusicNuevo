package modelo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CatalogoUsuarios {
	private Map<Integer, Usuario> usuarios;
	private static CatalogoUsuarios unicaInstancia=null;
	
//	private FactoriaDAO dao;
//	private IAdaptadorUsuarioDAO adaptadorUsuario;
	
	// CONSTRUCTOR
	private CatalogoUsuarios() {
//		try {
//			dao=FactoriaDAO.getUnicaInstancia(FactoriaDAO.DAO_APPMUSIC);
//			adaptadorUsuario=dao.getAdaptadorUsuario();
		usuarios=new HashMap<>();
//		}catch(DAOException e) {
//			e.printStackTrace();
//		}
	}
	
	// PATRÓN SINGLETON
	public static CatalogoUsuarios getUnicaInstancia() {
		if(unicaInstancia==null) {
			unicaInstancia=new CatalogoUsuarios();
		}
		return unicaInstancia;
	}
	
	// MÉTODOS
	public List<Usuario> getUsuarios(){
		return new LinkedList<>(usuarios.values());
	}
	
	public Usuario getUsuario(int codigo) {
		for(Usuario usuario:usuarios.values()) {
			if(usuario.getCodigo()==codigo) {
				return usuario;
			}
		}
		return null;
	}
	
	public Usuario getUsuario(String nombre) throws Exception {
		List<Usuario> usuariosConNombre = usuarios.values().stream()
				.filter(u -> u.getNombre().equals(nombre))
				.collect(Collectors.toList());
		if(usuariosConNombre.size() > 1)
		{
			Exception e = new Exception("Hay usuarios con nombres duplicados.");
			throw e;
		}
		if(usuariosConNombre.isEmpty())
			return null;
		return usuariosConNombre.get(0);
	}
	
	public void addUsuario(Usuario usuario) {
		usuarios.put(usuario.getCodigo(), usuario);
	}
	
	public void removeUsuario(Usuario usuario) {
		usuarios.remove(usuario.getNombre());
	}
	
	public void removeAllUsusarios() {
		usuarios = new HashMap<Integer, Usuario>();
	}
	
	public void reemplazarUsuarios(List<Usuario> usuarios) {
		this.usuarios = new HashMap<Integer, Usuario>();
		usuarios.stream()
			.forEach(usuario -> addUsuario(usuario));
	}
}
