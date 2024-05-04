package modelo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import persistencia.FactoriaDAO;
import persistencia.IAdaptadorCancion;

public class CatalogoCanciones {
	// ATRIBUTOS
	private Map<Integer, Cancion> canciones;
	private FactoriaDAO dao;
	private IAdaptadorCancion adaptadorCancion;
	
	// SINGLETON
	private static CatalogoCanciones unicaInstancia=null;
	
	// CONSTRUCTOR
	private CatalogoCanciones() {
		try {
			dao=FactoriaDAO.getUnicaInstancia();
			adaptadorCancion=dao.getAdaptadorCancion();
			canciones=new HashMap<>();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// MÉTODO GET ÚNICA INSTANCIA (SINGLETON)
	public static CatalogoCanciones getUnicaInstancia() {
		if(unicaInstancia==null) {
			unicaInstancia=new CatalogoCanciones();
		}
		return unicaInstancia;
	}
	
	// MÉTODOS
	public List<Cancion> getCanciones(){
		return new LinkedList<>(canciones.values());
	}
	
	public Cancion getCancion(int codigo) {
		return canciones.get(codigo);
	}
	
	public void addCancion(Cancion cancion) {
		canciones.put(cancion.getCodigo(), cancion);
	}
	
	public void removeCancion(Cancion cancion) {
		canciones.remove(cancion.getCodigo());
	}
	
	public void reemplazarCanciones(List<Cancion> canciones) {
		this.canciones = new HashMap<Integer, Cancion>();
		canciones.stream()
			.forEach(cancion -> addCancion(cancion));
	}
}
