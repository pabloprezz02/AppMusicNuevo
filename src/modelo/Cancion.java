package modelo;

import java.util.LinkedList;
import java.util.List;

import observador.Observable;

public class Cancion {
	// ATRIBUTOS PRIMITIVOS
	private String titulo;
	private String rutaFichero;
	private int numReproducciones;
	private int codigo;
	private List<String> interpretes;
	private String estilo;
	
	// ATRIBUTOS QUE CONTIENEN CLASES
	
	// CONSTRUCTOR
	public Cancion(String titulo, String rutaFichero, String estilo, List<String> interpretes) {
		this.titulo=titulo;
		this.rutaFichero=rutaFichero;
		this.interpretes = new LinkedList<String>(interpretes);
		this.estilo=estilo;
		numReproducciones=0;
		codigo=0;
	}
	
	// MÉTODOS GET
	public String getTitulo() {
		return titulo;
	}

	public String getRutaFichero() {
		return rutaFichero;
	}

	public int getNumReproducciones() {
		return numReproducciones;
	}

	public int getCodigo() {
		return codigo;
	}

	public String getEstilo() {
		return estilo;
	}

	public List<String> getInterpretes() {
		return interpretes;
	}

	// MÉTODOS SET
	public void setCodigo(int codigo) {
		this.codigo=codigo;
	}
	
	public void setEstiloMusical(String estilo) {
		this.estilo=estilo;
	}
	
	public void setNumReproducciones(int numReproducciones) {
		this.numReproducciones=numReproducciones;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(obj == null)
			return false;
		
		if(getClass() != obj.getClass())
			return false;
		
		Cancion objeto = (Cancion) obj;
		boolean mismosInterpretes = objeto.getInterpretes().stream()
				.allMatch(interprete -> this.interpretes.contains(interprete))
				&&
				this.getInterpretes().stream()
				.allMatch(interprete -> objeto.getInterpretes().contains(interprete));
		return mismosInterpretes && objeto.getTitulo().equals(this.titulo);
	}
	
	public void incrementarNumReproducciones()
	{
		setNumReproducciones(getNumReproducciones() + 1);
	}
	
	public static String getCadenaInterpretes(List<String> interpretes)
	{
		String cadena = "";
		int i = 0;
		for(String interprete: interpretes)
		{
			cadena += interprete; 
			if(i != interpretes.size() - 1)
				cadena += "&";
			i++;
		}
		return cadena;
	}
	
	public static List<String> getInterpretesDesdeCadena(String cadena)
	{
		List<String> interpretes = new LinkedList<String>();
		String interprete = "";
		for(int i = 0; i < cadena.length(); i++)
		{
			if(cadena.charAt(i) == '&')
			{
				interpretes.add(interprete);
				interprete = "";
			}
			else
			{
				interprete += cadena.charAt(i);
			}
		}
		interpretes.add(interprete);
		return interpretes;
	}
}
