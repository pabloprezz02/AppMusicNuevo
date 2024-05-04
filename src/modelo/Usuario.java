package modelo;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Usuario {
	// ATRIBUTOS PRIMITIVOS
	private String password;
	private String nombre;
	private boolean premium;
	private int codigo;
	private String email;
	private String fechaNacimiento;
	
	// ATRIBUTOS QUE CONTIENEN CLASES
//	private String fechaNacimiento;
	private Set<Playlist> playlists;
	private List<Cancion> recientes;
	
	// CONSTRUCTORES
	public Usuario(String nombre, String password, String email, String fechaNacimiento) {
		this.password=password;
		this.nombre=nombre;
		this.email=email;
		this.fechaNacimiento=fechaNacimiento;
		premium=false;
		codigo=0;
		playlists=new HashSet<>();
		recientes=new LinkedList<>();
		//descuento=null;
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
		recientes.add(cancion);
	}
	
	public void setPlaylist(Collection<Playlist> playlists) {
		this.playlists = new HashSet<Playlist>(playlists);
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
