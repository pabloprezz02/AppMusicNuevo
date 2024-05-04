package ventanas;

import java.util.List;
import java.util.stream.Collectors;

import observador.Observable;

public class VentanaUsuarios extends VentanaLista {
	
	private final static String PALABRA_PARA_FILTRAR = "Usuarios";
	
	public VentanaUsuarios(List<String> usuarios) {
		super(PALABRA_PARA_FILTRAR, usuarios.stream().map(usuario -> (Object)usuario).collect(Collectors.toList()));
	}
	
//	@Override
//	protected void actualizar(char[] contenido) {
//		
//		
////		if(contenido[0] == 'U') {
////			contenido[0] = '\n';
////			textArea.setText(new String(contenido));
////		}
//	}
}
