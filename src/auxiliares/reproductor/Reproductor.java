package auxiliares.reproductor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import controlador.Controlador;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import modelo.Cancion;
import observador.Observable;
import observador.Observer;
import persistencia.AdaptadorCancion;

public class Reproductor extends Observable {
	
	private static final int MAX_RECIENTES_POR_DEFECTO = 10; 
	
	// Singleton.
	private static Reproductor unicaInstancia = null;
	
	// Atributos.
	private String tempPath = System.getProperty("user.dir") + "/temp";
	private MediaPlayer mediaPlayer = null;
//	private ListaConIndice<Cancion> recientes;
	private ArrayList<Cancion> recientes;
	private int maxRecientes;
	
	private Controlador controlador;
	
	// Esto será lo que se está reproduciendo.
	private ListaConIndice<Cancion> reproduciendo;
	
	// Si este atributo es true, entonces intentará seguir con la siguiente canción.
	private boolean seguirLista;
	
	// Canción actual.
	private Cancion cancionActual;
	
	// Método getEstado.
	public MediaPlayer.Status getEstado(){
		if(mediaPlayer == null)
			return MediaPlayer.Status.READY;
		return mediaPlayer.getStatus();
	}
	
	// Pone unicaInstancia a null para que, la próxima vez que se llame al reproductor, se reinicie el mismo.
	public static void reiniciar()
	{
		if(unicaInstancia != null)
		{
			unicaInstancia.stopCancion();
			unicaInstancia.mediaPlayer = null;
			unicaInstancia = null;
		}
	}
	
	// Constructor.
	private Reproductor() 
	{
		this(MAX_RECIENTES_POR_DEFECTO);
	}
	
	private Reproductor(int maxRecientes)
	{
		try {
//			recientes = new ListaConIndice<Cancion>(new ArrayList<Cancion>());
			reproduciendo = null;
			seguirLista = false;
			this.maxRecientes = maxRecientes;
			recientes = new ArrayList<Cancion>();
			cancionActual = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	// Singleton.
	public static Reproductor getUnicaInstancia() {
		if(unicaInstancia == null)
			unicaInstancia = new Reproductor();
		return unicaInstancia;
	}
	
	public Collection<Cancion> getRecientes()
	{
		LinkedList<Cancion> recientes = new LinkedList<Cancion>();
		for(Cancion cancion: this.recientes)
			recientes.add(cancion);
		return recientes;
	}
	
	public Cancion getCancionActual() {
//		return cancionActual;
//		return recientes.getActual();
		return cancionActual;
	}
	
	/**
	 * Método que inicia una canción y no la inserta en recientes..
	 * @param cancion: la canción a iniciar.
	 */
	public void iniciarCancionNoInsertando(Cancion cancion)
	{			
		stopCancion();
		String url = cancion.getRutaFichero();
		URL uri = null;
		try 
		{
			com.sun.javafx.application.PlatformImpl.startup(() -> {
			});

			uri = new URL(url);

			System.setProperty("java.io.tmpdir", tempPath);
			Path mp3 = Files.createTempFile("now-playing", ".mp3");

			System.out.println(mp3.getFileName());
			try (InputStream stream = uri.openStream()) {
				Files.copy(stream, mp3, StandardCopyOption.REPLACE_EXISTING);
			}
			System.out.println("finished-copy: " + mp3.getFileName());

			Media media = new Media(mp3.toFile().toURI().toString());
			mediaPlayer = new MediaPlayer(media);
//				cancionActual = cancion;
			mediaPlayer.play();
			cancion.incrementarNumReproducciones();
//			controlador.modificarCancion(cancion);
			for(Observer ob: obs)
			{
				ob.update(this, cancion);
			}
			System.out.println("Reproducciones: " + cancion.getNumReproducciones());
		} 
		catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	/**
	 * Método que inicia una canción y la inserta en recientes.
	 * @param cancion: la canción a iniciar e insertar.
	 */
	public void iniciarCancionInsertando(Cancion cancion)
	{
		iniciarCancionNoInsertando(cancion);
		insertarEnRecientes(cancion);
	}
	
	/**
	 * Método que reproduce UNA ÚNICA CANCIÓN y se para.
	 * @param cancion: la Cancion a reproducir.
	 */
	public void reproducirCancion(Cancion cancion)
	{
		// Si la canción es null, salgo (no es válido).
		if(cancion == null)
			return;
		// Si la canción es la misma que se está reproduciendo, la reanudo.
		if(cancionActual != null && cancion.equals(cancionActual))
		{
			reanudarCancion();
			return;
		}
		reproduciendo = null;
		iniciarCancionInsertando(cancion);
		mediaPlayer.setOnEndOfMedia(
			new Runnable()
			{
				@Override
				public void run() {
					// Cuando se acaba la canción, pone la actual a null.
					cancionActual = null;
				}
			});
	}
	
	/**
	 * Método para insertar una canción en la lista de recientes.
	 * @param c: la canción a insertar.
	 */
	private void insertarEnRecientes(Cancion c)
	{
		// Defino recientes como aquellas canciones DIFERENTES reproducidas.
		if(recientes.contains(c))
			return;
		
		if(recientes.size() == maxRecientes)
		{
			recientes.remove(0);
		}
		recientes.add(c);
	}
	
	/**
	 * Método para reproducir, secuencialmente, una lista de canciones.
	 * @param canciones: canciones a reproducir.
	 */
	public void reproducirListaSecuencialmente(List<Cancion> canciones)
	{
		try 
		{
			ListaConIndice<Cancion> nueva = new ListaConIndice<Cancion>(canciones);
			if(nueva.equals(reproduciendo))
			{
				if(reproduciendo.getIndice() == 0)
				{
					if(!mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
						mediaPlayer.play();
				}
			}
			else
			{
				reproduciendo = nueva;
				reproducirLista();
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Método para reproducir, aleatoriamente, una lista de canciones.
	 * @param canciones: canciones a reproducir.
	 */
	public void reproducirListaAleatoriamente(List<Cancion> canciones)
	{
		List<Cancion> cancionesDesordenadas = desordenarLista(canciones);
		reproducirListaSecuencialmente(cancionesDesordenadas);
	}
	
	/**
	 * Método que inicializa una lista de índices con números aleatorios entre 0 y el tamaño pasado por argumento.
	 * @param tamano: el tamaño de la lista e índice máximo.
	 * @return una lista de enteros aleatorios.
	 */
	private static List<Integer> inicializarIndices(int tamano)
	{
		List<Integer> indicesDisponibles = new LinkedList<Integer>();
		for(int i = 0; i < tamano; i++)
			indicesDisponibles.add(i);
		return indicesDisponibles;
	}
	
	/**
	 * Método que desordena una lista de canciones.
	 * @param canciones: las canciones a desordenar.
	 * @return la lista de canciones desordenadas.
	 */
	private static List<Cancion> desordenarLista(List<Cancion> canciones)
	{
		LinkedList<Cancion> cancionesDesordenadas = new LinkedList<Cancion>();
		List<Integer> indicesDisponibles = inicializarIndices(canciones.size());
		for(int i = 0; i < canciones.size(); i++)
		{
			Random r = new Random();
			int indice = r.nextInt(indicesDisponibles.size());
			cancionesDesordenadas.add(canciones.get(indicesDisponibles.get(indice)));
			indicesDisponibles.remove(indice);
		}
		return cancionesDesordenadas;
	}
	
	public void reproducirRecientesAleatoriamente()
	{
		if(recientes.size() == 0)
			return;
		reproducirListaAleatoriamente(recientes);
	}
	
	public void reproducirRecientesSecuencialmente()
	{
		if(recientes.size() == 0)
			return;
		reproducirListaSecuencialmente(recientes);
	}
	
	/**
	 * Pone la canción actual a null y empieza a reproducir la lista.
	 */
	private void reproducirLista()
	{
		cancionActual = null;
		if(reproduciendo == null)
			return;
		
		Cancion c = reproduciendo.getPrimero();
		iniciarCancionInsertando(c);
		mediaPlayer.setOnEndOfMedia(
				new Runnable() {
					
					@Override
					public void run() {
						reproducirSiguienteLista();
					}
				});
	}
	
	/**
	 * Toma el elemento actual de "reproduciendo" y lo reproduce.
	 */
	private void reproducirSiguienteLista()
	{
		Cancion c = reproduciendo.getSiguienteElemento();
		iniciarCancionInsertando(c);
		mediaPlayer.setOnEndOfMedia(
				new Runnable() {
					
					@Override
					public void run() {
						reproducirSiguienteLista();
					}
				});
	}
	
	/**
	 * Método para reproducir el anterior de la lista.
	 */
	private void reproducirAnteriorLista()
	{
		Cancion c = reproduciendo.getElementoAnterior();
		iniciarCancionInsertando(c);
		mediaPlayer.setOnEndOfMedia(
				new Runnable() {
					
					@Override
					public void run() {
						reproducirSiguienteLista();
					}
				});
	}
	
	/**
	 * Método que reanuda una canción. En caso de que el mediaPlayer esté pausado, la continúa. Si estaba parado, la reinicia.
	 */
	public void reanudarCancion()
	{
		if(cancionActual == null)
			return;
		if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)
			|| mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED))
			mediaPlayer.play();	
	} 
	
	/**
	 * Método que para la Canción actual.
	 */
	public void stopCancion() {
		if(mediaPlayer != null)
			mediaPlayer.stop();
		File directorio = new File(tempPath);
		String[] files = directorio.list();
		for (String archivo : files) {
			File fichero = new File(tempPath + File.separator + archivo);
			fichero.delete();
		}
	}
	
	/**
	 * Método para pausar el reproductor.
	 */
	public void pausar()
	{
		if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING))
			mediaPlayer.pause();
	}
	
	/**
	 * Método para parar el reproductor.
	 */
	public void parar()
	{
		stopCancion();
	}
	
	/**
	 * Método para reproducir el siguiente.
	 */
	public void siguiente()
	{
		if(reproduciendo != null)
		{
			parar();
			reproducirSiguienteLista();
		}
	}
	
	/**
	 * Método para reproducir el anterior.
	 */
	public void anterior()
	{
		if(reproduciendo != null)
		{
			parar();
			reproducirAnteriorLista();
		}
	}
	
//	// Singleton.
//	public static Reproductor getUnicaInstancia() {
//		if(unicaInstancia == null)
//			unicaInstancia = new Reproductor();
//		return unicaInstancia;
//	}
//	
//	public Collection<Cancion> getRecientes()
//	{
//		return new LinkedList<Cancion>(recientes.getLista());
//	}
//	
//	public Cancion getCancionActual() {
////		return cancionActual;
//		return recientes.getActual();
//	}
//	
//	/*
//	 * Método para cambiar la lista que se está reproduciendo.
//	 */
//	private void cambiarListaReproduccion(List<Cancion> canciones)
//	{
//		try {
//			reproduciendo = new ListaConIndice<Cancion>(canciones);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
////	/*
////	 * Play para cada uno de los casos posibles a la hora de reproducir una canción. Únicamente usado para reproducir UNA canción que manda el controlador.
////	 */
////	public void playCancion(Cancion cancion) 
////	{
////		reproduciendo = recientes;
////		// Caso especial: cuando se ha enviado la misma canción.
////		if(cancion.equals(getCancionActual()))
////			reproducirActual();
////		// Si no es la misma, se reproduce de forma normal.
////		else
////		{
////			reproducirCancion(cancion);
////		}
////	}
//	
//	/*
//	 * Método para reproducir la Canción que ya estaba sonando.
//	 */
//	private void reproducirActual() 
//	{
//		switch(getEstado())
//		{
//		// En caso de estar pausado, la canción se continua.
//		case PAUSED:
//			continuarCancion();
//			break;
//		// En caso de estar parado, la canción se reinicia.
//		case STOPPED:
//			continuarDesdeParado();
//			break;
//		// En otro caso no se hace nada.
//		default:
//			break;
//		}
//	}
//	
//	/*
//	 * Continua la canción que se estaba reproduciendo.
//	 */
//	public void continuarCancion() 
//	{
//		MediaPlayer.Status estado = getEstado();
//
//		// Si no hay reproductor, no hace nada.
//		if(estado == null)
//			return;
//		// Si no estaba reproduciendo una canción antes, no hace nada.
//		if(!estado.equals(MediaPlayer.Status.PAUSED))
//			return;
//		mediaPlayer.play();
//	}
//	
//	/*
//	 * Reproducir cuando está parado.
//	 */
//	public void continuarDesdeParado()
//	{
//		Cancion cancionActual = getCancionActual();
//		if(cancionActual == null)
//			return;
////		reproducirCancionSinInsertar(cancionActual);
//		mediaPlayer.setOnEndOfMedia(
//				new Runnable()
//				{
//					@Override
//					public void run() 
//					{
//						reproducirSiguientes();
//					}
//				});
//	}
//	
//	/*
//	 * Reproducir una canción seleccionada, no de una lista.
//	 */
//	private void reproducirCancion(Cancion c)
//	{
//		if(recientes.getTamano() == maxRecientes)
//			recientes.eliminarPrimero();
//		
//		if(reproduciendo != recientes)
//			recientes.addElemento(c);
//		mediaPlayer.setOnEndOfMedia(
//				new Runnable()
//				{
//					@Override
//					public void run() 
//					{
//						reproducirSiguientes();
//					}
//				});
////		reproducirCancionSinInsertar(c);
//	}
//	
////	/*
////	 * Reproducir una canción PERO sin añadirla a recientes.
////	 */
////	private void reproducirCancionSinInsertar(Cancion c)
////	{
////		iniciarReproduccion(c);
////		// La idea de esto es que, si hay más canciones posteriores, se reproduzcan.
////		mediaPlayer.setOnEndOfMedia(
////				new Runnable()
////				{
////					@Override
////					public void run() 
////					{
////						reproducirSiguientes();
////					}
////				});
////	}
//	
//	/*
//	 * Reproduce una canción desde el principio. Únicamente tomará la ruta de la Canción y la reproducirá.
//	 */
//	private void iniciarReproduccion(Cancion cancion)
//	{
//		stopCancion();
//		String url = cancion.getRutaFichero();
//		URL uri = null;
//		try 
//		{
//			com.sun.javafx.application.PlatformImpl.startup(() -> {
//			});
//
//			uri = new URL(url);
//
//			System.setProperty("java.io.tmpdir", tempPath);
//			Path mp3 = Files.createTempFile("now-playing", ".mp3");
//
//			System.out.println(mp3.getFileName());
//			try (InputStream stream = uri.openStream()) {
//				Files.copy(stream, mp3, StandardCopyOption.REPLACE_EXISTING);
//			}
//			System.out.println("finished-copy: " + mp3.getFileName());
//
//			Media media = new Media(mp3.toFile().toURI().toString());
//			mediaPlayer = new MediaPlayer(media);
////			cancionActual = cancion;
//			mediaPlayer.play();
//		} 
//		catch (MalformedURLException e1) 
//		{
//			e1.printStackTrace();
//		} 
//		catch (IOException e1) 
//		{
//			e1.printStackTrace();
//		}
//	}
//	
//	/*
//	 * Reproducir la siguiente canción.
//	 */
//	public boolean siguiente()
//	{
//		Cancion c = reproduciendo.getSiguienteElemento();
//		if(c != null)
//		{
////			reproducirCancionSinInsertar(c);
////			if(reproduciendo == recientes)
////				reproducirCancionSinInsertar(c);
////			else
////				reproducirCancion(c);
//			reproducirCancion(c);
//			return true;
//		}
//		return false;
//	}
//	
//	/*
//	 * Reproducir la anterior canción.
//	 */
//	public boolean anterior() 
//	{
//		Cancion c = reproduciendo.getElementoAnterior();
//		if(c != null)
//		{
////			if(reproduciendo == recientes)
////				reproducirCancionSinInsertar(c);
////			else
////				reproducirCancion(c);
//			reproducirCancion(c);
//			return true;
//		}
//		return false;
//	}
//	
//	
//	public void stopCancion() {
//		if(mediaPlayer != null)
//			mediaPlayer.stop();
//		File directorio = new File(tempPath);
//		String[] files = directorio.list();
//		for (String archivo : files) {
//			File fichero = new File(tempPath + File.separator + archivo);
//			fichero.delete();
//		}
//	}
//	
//	public void pauseCancion() {
//		mediaPlayer.pause();
//	}
//	
//	/*
//	 * Método para reproducir las siguientes canciones que haya en "reproduciendo".
//	 */
//	private void reproducirSiguientes()
//	{
//		Cancion c = reproduciendo.getSiguienteElemento();
//		if(c == null)
//			return;
//		else
//		{
//			reproducirCancion(c);
////			mediaPlayer.setOnEndOfMedia(
////					new Runnable()
////					{
////						@Override
////						public void run() 
////						{
////							reproducirSiguientes();
////						}
////					});
//		}
//	}
//	
//	public void reproducirRecientes()
//	{
//		reproduciendo = recientes;
//		Cancion cancion = reproduciendo.getPrimero();
//		if(cancion == null)
//			return;
//		reproducirCancion(cancion);
//	}
//	
//	/*
//	 * Método para reproducir secuencialmente una lista de canciones.
//	 */
//	public void reproducirSecuencialmente(List<Cancion> canciones)
//	{
//		cambiarListaReproduccion(canciones);
//		
//		Cancion c = reproduciendo.getPrimero();
//		if(c == null)
//			return;
//		reproducirCancion(c);
//	}
//	
//	private LinkedList<Integer> indicesDisponibles;
//	
//	private void inicializarIndices(int tamano)
//	{
//		indicesDisponibles = new LinkedList<Integer>();
//		for(int i = 0; i < tamano; i++)
//			indicesDisponibles.add(i);
//	}
//	
//	private List<Cancion> desordenarLista(List<Cancion> canciones)
//	{
//		LinkedList<Cancion> cancionesDesordenadas = new LinkedList<Cancion>();
//		
//		for(int i = 0; i < canciones.size(); i++)
//		{
//			Random r = new Random();
//			int indice = r.nextInt(indicesDisponibles.size());
//			cancionesDesordenadas.add(canciones.get(indicesDisponibles.get(indice)));
//			indicesDisponibles.remove(indice);
//		}
//		return cancionesDesordenadas;
//	}
//	
//	/*
//	 * Método para reproducir aleatoriamente una lista de canciones.
//	 */
//	public void reproducirAleatoriamente(List<Cancion> canciones)
//	{
//		inicializarIndices(canciones.size());
//		List<Cancion> cancionesDesordenadas = desordenarLista(canciones);
//		reproducirSecuencialmente(cancionesDesordenadas);
//	}
}
