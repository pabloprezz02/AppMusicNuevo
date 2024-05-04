package modelo;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
	// ATRIBUTOS PRIMITIVOS
	private String nombre;
	private int codigo;
	
	// ATRIBUTOS QUE CONTIENEN CLASES
	private List<Cancion> canciones;	// Lista porque en Spotify puedes añadir una canción varias veces
	
	// CONSTRUCTORES
	public Playlist(String nombre) {
		this.nombre=nombre;
		canciones=new ArrayList<Cancion>();
	}
	
	public Playlist(String nombre, Cancion...canciones) {
		this(nombre);
		for(Cancion cancion:canciones)
			this.canciones.add(cancion);
	}
	
	// MÉTODOS GET
	public String getNombre() {
		return nombre;
	}

	public int getCodigo() {
		return codigo;
	}
	
	public List<Cancion> getCanciones(){
		return new ArrayList<Cancion>(canciones);	// Le paso una copia.
	}
	
	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones;
	}
	
	// MÉTODO SET CÓDIGO
	public void setCodigo(int codigo) {
		this.codigo=codigo;
	}

	// MÉTODOS
	public boolean addCancion(Cancion cancion) {
		return canciones.add(cancion);
	}
}
