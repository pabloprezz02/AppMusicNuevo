package modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EstiloMusical {
	// ATRIBUTOS
	private String nombre;
	private int codigo;
	private List<Cancion> canciones;
	
	// CONSTRUCTOR
	public EstiloMusical(String nombre) {
		this.nombre=nombre;
		codigo=0;
		canciones=new LinkedList<>();
	}
	
	// MÉTODOS GET
	public String getNombre() {
		return new String(nombre);
	}
	
	public int getCodigo() {
		return codigo;
	}
	
	public List<Cancion> getCanciones() {
		return new LinkedList<>(canciones);
	}

	// SET CÓDIGO
	public void setCodigo(int codigo) {
		this.codigo=codigo;
	}
	
	// MÉTODOS
	public void addCancion(Cancion cancion) {
		canciones.add(cancion);
	}
}
