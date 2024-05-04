package modelo;

public class Interprete {
	// ATRIBUTOS
	private String nombre;
	
	// CONSTRUCTOR
	public Interprete(String nombre) {
		this.nombre=nombre;
	}
	
	// MÉTODO GET NOMBRE
	public String getNombre() {
		return new String(nombre);
	}
}
