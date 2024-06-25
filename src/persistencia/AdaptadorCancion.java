package persistencia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import beans.Entidad;
import beans.Propiedad;
import modelo.Cancion;
import tds.driver.FactoriaServicioPersistencia;
import tds.driver.ServicioPersistencia;

public class AdaptadorCancion implements IAdaptadorCancion {
	// SINGLETON
	private static AdaptadorCancion unicaInstancia=null;
	private static ServicioPersistencia servicioPersistencia;
	
	// CONSTRUCTOR
	private AdaptadorCancion() {
		servicioPersistencia=FactoriaServicioPersistencia.getInstance().getServicioPersistencia();
	}
	
	// SINGLETON
	public static AdaptadorCancion getUnicaInstancia() {
		if(unicaInstancia==null) {
			unicaInstancia=new AdaptadorCancion();
		}
		return unicaInstancia;
	}

	private String getCadenaInterpretes(List<String> interpretes)
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
	
	private List<String> getInterpretesDesdeCadena(String cadena)
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
	
	// MÉTODOS DE LA CLASE ABSTRACTA
	@Override
	public void registrarCancion(Cancion cancion) {
		// COMPROBAMOS SI LA ENTIDAD YA EXISTE
		Entidad eCancion=null;
		try {
			eCancion=servicioPersistencia.recuperarEntidad(cancion.getCodigo());
		}catch(Exception e) {}
		if(eCancion!=null) {		// Significa que ya existía
			return;
		}
		
		// REGISTRO DE PROPIEDADES QUE SON OBJETOS (ESTILO MUSICAL-->HACER)
//		AdaptadorEstiloMusical adaptadorEstilo=AdaptadorEstiloMusical.getUnicaInstancia();
//		adaptadorEstilo.registrarEstiloMusical(cancion.getEstilo());
		
		// CREACIÓN ENTIDAD CANCIÓN
		eCancion=new Entidad();
		eCancion.setNombre("cancion");
		List<Propiedad> propiedades=new ArrayList<>();
		propiedades.add(new Propiedad("titulo", cancion.getTitulo()));
		propiedades.add(new Propiedad("rutaFichero", cancion.getRutaFichero()));
		propiedades.add(new Propiedad("numReproducciones", Integer.toString(cancion.getNumReproducciones())));
		propiedades.add(new Propiedad("interprete", getCadenaInterpretes(cancion.getInterpretes())));
		propiedades.add(new Propiedad("estilo", cancion.getEstilo()));
		eCancion.setPropiedades(propiedades);
		
		// REGISTRO DE LA ENTIDAD
		eCancion=servicioPersistencia.registrarEntidad(eCancion);
		// APROVECHO CÓDIGO
		cancion.setCodigo(eCancion.getId());
	}

	@Override
	public void modificarCancion(Cancion cancion) {
		// RECUPERACIÓN DE LA ENTIDAD
		Entidad eCancion=servicioPersistencia.recuperarEntidad(cancion.getCodigo());
		
		// SE RECORREN LAS PROPIEDADES
		for(Propiedad p:eCancion.getPropiedades()) {
			if(p.getNombre().equals("titulo")) {
				p.setValor(cancion.getTitulo());
			}else if(p.getNombre().equals("rutaFichero")){
				p.setValor(cancion.getRutaFichero());
			}else if(p.getNombre().equals("numReproducciones")) {
				p.setValor(Integer.toString(cancion.getNumReproducciones()));
			}else if(p.getNombre().equals("interprete")) {
				p.setValor(getCadenaInterpretes(cancion.getInterpretes()));
			}else if(p.getNombre().equals("estilo")) {
				p.setValor(cancion.getEstilo());
			}
			servicioPersistencia.modificarPropiedad(p);
		}
	}

	@Override
	public void eliminarCancion(Cancion cancion) {
		// RECUPERACIÓN DE LA ENTIDAD
		Entidad eCancion=servicioPersistencia.recuperarEntidad(cancion.getCodigo());
		
//		// SE ELIMINAN LAS ENTIDADES AGREGADAS (ESTILO MUSICAL)
//		AdaptadorEstiloMusical adaptadorEstilo=AdaptadorEstiloMusical.getUnicaInstancia();
//		adaptadorEstilo.eliminarEstiloMusical(cancion.getEstilo());
		
		// SE ELIMINA LA ENTIDAD
		servicioPersistencia.borrarEntidad(eCancion);
	}

	@Override
	public Cancion recuperarCancion(int codigo) {
		// SE HACE USO DE UN POOL PARA LA BIDIRECCIONALIDAD ENTRE CANCIÓN Y ESTILO MUSICAL
//		if(PoolDAO.getUnicaInstancia().contiene(codigo)) {
//			return (Cancion) PoolDAO.getUnicaInstancia().getObjeto(codigo);
//		}
		
		// RECUPERA ENTIDAD DE LA BASE DE DATOS
		Entidad eCancion=servicioPersistencia.recuperarEntidad(codigo);
		String titulo;
		String rutaFichero;
		List<String> interprete;
		int numReproducciones;
		String estilo;
		
		// RECUPERACIÓN DE PROPIEDADES QUE NO SON OBJETOS
		titulo=servicioPersistencia.recuperarPropiedadEntidad(eCancion, "titulo");
		rutaFichero=servicioPersistencia.recuperarPropiedadEntidad(eCancion, "rutaFichero");
		interprete = getInterpretesDesdeCadena(servicioPersistencia.recuperarPropiedadEntidad(eCancion, "interprete"));
		estilo=servicioPersistencia.recuperarPropiedadEntidad(eCancion, "estilo"); 
		numReproducciones=Integer.parseInt(servicioPersistencia.recuperarPropiedadEntidad(eCancion, "numReproducciones"));
		
		// CREACIÓN DE LA ENTIDAD
		Cancion cancion=new Cancion(titulo, rutaFichero, estilo, interprete);
		cancion.setCodigo(codigo);
		cancion.setNumReproducciones(numReproducciones);
		
		// AÑADIMOS LA CANCIÓN AL POOL DAO
		// PoolDAO.getUnicaInstancia().addObjeto(codigo, cancion);
		
		// RECUPERAMOS PROPIEDADES QUE SON OBJETOS
//		AdaptadorEstiloMusical adaptadorEstilo=AdaptadorEstiloMusical.getUnicaInstancia();
//		estilo=adaptadorEstilo.recuperarEstiloMusical(Integer.parseInt(servicioPersistencia.recuperarPropiedadEntidad(eCancion, "estilo")));
//		cancion.setEstiloMusical(estilo);
		
		// RETORNO CANCIÓN
		return cancion;
	}

	@Override
	public List<Cancion> recuperarTodasCanciones() {
		List<Entidad> eCanciones=servicioPersistencia.recuperarEntidades("cancion");
		List<Cancion> canciones=new LinkedList<>();
		for(Entidad eCancion:eCanciones) {
			canciones.add(recuperarCancion(eCancion.getId()));
		}
		return canciones;
	}
}
