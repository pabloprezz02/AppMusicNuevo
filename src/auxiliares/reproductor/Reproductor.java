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
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import modelo.Cancion;
import observador.Observable;
import observador.Observer;
import persistencia.AdaptadorCancion;

public class Reproductor extends Observable {
	
	private static final int MAX_RECIENTES_POR_DEFECTO = 20; 
	
	// Singleton.
	private static Reproductor unicaInstancia = null;
	
	// Atributos.
	private String tempPath = System.getProperty("user.dir") + "/temp";
	private MediaPlayer mediaPlayer = null;
//	private ListaConIndice<Cancion> recientes;
	private ArrayList<Cancion> recientes;
	private int maxRecientes;
	private Timeline timeline;
	
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
			if(unicaInstancia.timeline != null)
				unicaInstancia.timeline.stop();
			unicaInstancia.timeline = null;
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
			
			mediaPlayer.play();
//			cancionActual = cancion;
			
			 mediaPlayer.setOnReady(() -> {
		            // Obtener la duración total del medio
		            Duration totalDuration = media.getDuration();
		            
		            // Crear un Timeline para actualizar el tiempo restante cada segundo
		            if(timeline != null)
						 timeline.stop();
		            timeline = new Timeline(
		                    new KeyFrame(Duration.seconds(0.1), event -> {
		                        // Obtener el tiempo actual de reproducción
		                    	Duration currentTime = mediaPlayer.getCurrentTime();
		                        int porcentaje = (int)(1000 * currentTime.toSeconds() / totalDuration.toSeconds());
		                        setChanged();
		                        notifyObservers(porcentaje);
		                    })
		            );
		            // Repetir el Timeline indefinidamente
		            timeline.setCycleCount(Animation.INDEFINITE);
		            // Iniciar el Timeline
		            timeline.play();
		        });
	
//			mediaPlayer.setOnPlaying(new Runnable() {
//				
//				@Override
//				public void run() {
//					MediaPlayer.Status estado = mediaPlayer.getStatus();
//					while(estado == MediaPlayer.Status.PLAYING)
//					{
//						int porcentaje = (int) (1000 * (mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds()));
//						
//						setChanged();
//						notifyObservers(porcentaje);
//						try {
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						estado = mediaPlayer.getStatus();
//					}
//				}
//			});
			
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
		cancionActual = cancion;
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
		if(mediaPlayer == null)
			return;
		if(mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED))
			mediaPlayer.play();
		if(mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED))
		{
			Cancion cancion = null;
			if(cancionActual != null)
			{
				cancion = cancionActual;
			}
			else
			{
				if(reproduciendo == null)
					return;
				cancion = reproduciendo.getActual();
			}
			iniciarCancionNoInsertando(cancion);
		}
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
		if(mediaPlayer == null)
			return;
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
}
