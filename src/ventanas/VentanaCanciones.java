package ventanas;

import java.util.List;
import java.util.stream.Collectors;

import observador.Observable;

public class VentanaCanciones extends VentanaLista {

	private final static String PALABRA_FILTRAR = "Canciones";
	
	public VentanaCanciones(List<String> contenido) {
		super(PALABRA_FILTRAR, contenido.stream().map(elemento -> (Object) elemento).collect(Collectors.toList()));
	}
}
