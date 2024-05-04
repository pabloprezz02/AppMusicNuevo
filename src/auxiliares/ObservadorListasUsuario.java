package auxiliares;

import java.util.List;

import javax.swing.JList;

import observador.Observable;

public class ObservadorListasUsuario extends ListObserver {
	private String usuario;
	
	public String getUsuario()
	{
		return new String(usuario);
	}
	
	public ObservadorListasUsuario(JList<String> lista, String palabra, String usuario)
	{
		super(lista, palabra);
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
			super.update(o, argumento);
		}
//		if(!argumento.contains("\n"))
//		{
//			String[] elementos = argumento.split("\n");
//			if(!elementos[0].equals(usuario))
//				return;
//			
//		}
	}
}
