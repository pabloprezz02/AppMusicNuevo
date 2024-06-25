package observador.tiposObservadores;

import auxiliares.reproductor.ListaConIndice;
import controlador.Controlador;
import modelo.Cancion;
import observador.Observable;
import observador.Observer;

public interface ObservadorReproduccion {	
	public abstract void updateRepro(Observable o, Object arg);
}
