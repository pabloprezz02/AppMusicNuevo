package observador.componenteObservador;

import java.util.List;

import javax.swing.JList;

import observador.Observable;
import observador.Observer;

//public class ObservadorListasUsuario extends ListObserver {
//	private String usuario;
//	
//	public String getUsuario()
//	{
//		return new String(usuario);
//	}
//	
//	public ObservadorListasUsuario(JList<String> lista, String usuario)
//	{
//		super(lista);
//		this.usuario = usuario;
//	}
//	
//	/**
//	 * Se actualizará el componente 
//	 */
//	@Override
//	public void update(Observable o, Object arg) {
//		if(arg == null || !(arg instanceof List))
//			return;
//		
//		List<String> argumento = (List<String>) arg;
//		
//		if(argumento.size() < 1)
//			return;
//		
//		if(argumento.get(0).equals(usuario))
//		{
//			argumento.remove(0);
//			super.update(o, argumento);
//		}
//	}
//}

/**
 * Observador de listas de Usuario --> patrón Composite.
 */
public class ObservadorListasUsuario implements Observer {
	private String usuario;
	private ListObserver lista;
	
	public String getUsuario()
	{
		return new String(usuario);
	}
	
	public ObservadorListasUsuario(JList<String> lista, String usuario)
	{
//		super(lista);
		this.lista = new ListObserver(lista);
		this.usuario = usuario;
	}
	
	/**
	 * Se actualizará el componente 
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(arg == null || !(arg instanceof List))
			return;
		
		List<String> argumento = (List<String>) arg;
		
		if(argumento.size() < 1)
			return;
		
		if(argumento.get(0).equals(usuario))
		{
			argumento.remove(0);
			lista.update(o, argumento);
		}
	}
}