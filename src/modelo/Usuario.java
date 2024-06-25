package modelo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import descuento.Descuento;

public class Usuario {
	
	// El número máximo de canciones recientes que puede tener el Usuario. 100 para no sobrecargar mucho.
	private static final int MAX_RECIENTES = 100;
	// El índice para la canción reciente.
	private int indice;
	
	// ATRIBUTOS PRIMITIVOS
	private String password;
	private String nombre;
	private boolean premium;
	private int codigo;
	private String email;
	private String fechaNacimiento;
	
	// ATRIBUTOS QUE CONTIENEN CLASES
	private Descuento descuento;
	private Set<Playlist> playlists;
	private LinkedList<Cancion> recientes;
	
	// CONSTRUCTORES
	public Usuario(String nombre, String password, String email, String fechaNacimiento) {
		this.password=password;
		this.nombre=nombre;
		this.email=email;
		this.fechaNacimiento=fechaNacimiento;
		premium=false;
		codigo=0;
		playlists=new HashSet<>();
		recientes=new LinkedList<Cancion>();
		indice = 0;
		descuento=null;
	}
	
//	public Usuario(String nombre, String password, Descuento descuento) {
//		this(nombre, password);
//		this.descuento=descuento;
//	}
	
	// MÉTODOS GET
	public String getNombre() {
		return new String(nombre);
	}
	
	public int getCodigo() {
		return codigo;
	}

	public String getPassword() {
		return new String(password);
	}

	public boolean isPremium() {
		return premium;
	}
	
	public String getEmail() {
		return new String(email);
	}

	public String getFechaNacimiento() {
		return new String(fechaNacimiento);
	}
	
	public Set<Playlist> getPlaylists() {
		return new HashSet<Playlist>(playlists);
	}
	
	public List<Cancion> getRecientes(){
		return new LinkedList<Cancion>(recientes);
	}

	public void realizarPago() {
		premium = true;
	}

	// MÉTODO SET codigo
	public void setCodigo(int id) {
		codigo=id;
	}
	
	public void setPremium(boolean premium) {
		this.premium=premium;
	}
	
	public void addPlaylist(Playlist playlist) {
		playlists.add(playlist);
	}
	
	public void addCancionReciente(Cancion cancion) {
		LinkedList<Cancion> recientesSinCancion = new LinkedList<Cancion>(recientes);
		recientesSinCancion.remove(cancion);
		recientes = recientesSinCancion;
		
		if(recientes.contains(cancion))
			return;
		
		if(indice == MAX_RECIENTES)
			indice = 0;
		
		if(recientes.size() == MAX_RECIENTES)
			recientes.remove(indice);
		recientes.add(cancion);
		indice++;
	}
	
	public void setPlaylist(Collection<Playlist> playlists) {
		this.playlists = new HashSet<Playlist>(playlists);
	}
	
	public void setDescuento(Descuento descuento)
	{
		this.descuento = descuento;
	}
	
	public Descuento getDescuento()
	{
		return descuento;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if(this == obj)
			return true;
		
		if(obj.getClass() != this.getClass())
			return false;
		
		Usuario objeto = (Usuario)obj;
		return this.nombre.equals(objeto.getNombre());
	}
}
