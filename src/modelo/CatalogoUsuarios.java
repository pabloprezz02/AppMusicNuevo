package modelo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import persistencia.DAOException;
import persistencia.FactoriaDAO;
import persistencia.IAdaptadorUsuarioDAO;
import tds.driver.ServicioPersistencia;

public class CatalogoUsuarios {
	private Map<String, Usuario> usuarios;
	private static CatalogoUsuarios unicaInstancia=null;
	
	private FactoriaDAO dao;
	private IAdaptadorUsuarioDAO adaptadorUsuario;
	
	// CONSTRUCTOR
	private CatalogoUsuarios() {
		try {
			dao=FactoriaDAO.getUnicaInstancia(FactoriaDAO.DAO_APPMUSIC);
			adaptadorUsuario=dao.getAdaptadorUsuario();
			usuarios=new HashMap<>();
		}catch(DAOException e) {
			e.printStackTrace();
		}
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
	
	public Usuario getUsuario(String nombre) {
		return usuarios.get(nombre);
	}
	
	public void addUsuario(Usuario usuario) {
		usuarios.put(usuario.getNombre(), usuario);
	}
	
	public void removeUsuario(Usuario usuario) {
		usuarios.remove(usuario.getNombre());
	}
	
	public void removeAllUsusarios() {
		usuarios = new HashMap<String, Usuario>();
	}
	
	public void reemplazarUsuarios(List<Usuario> usuarios) {
		this.usuarios = new HashMap();
		usuarios.stream()
			.forEach(usuario -> addUsuario(usuario));
	}
}
