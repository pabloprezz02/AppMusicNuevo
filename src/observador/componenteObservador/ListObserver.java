package observador.componenteObservador;

import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import observador.Observable;

public class ListObserver extends ComponenteObservador {
	
	public ListObserver(JList<String> lista) 
	{
		super(lista);
	}
	
	public void actualizarComponente(Object[] elementos)
	{
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		for(Object elemento: elementos)
			modelo.addElement((String) elemento);
		
		((JList<String>)getComponente()).setModel(modelo);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(arg == null || !(arg instanceof List))
			return;
		
		List<String> argumento = (List<String>) arg;
		
		if(argumento.size() < 0)
			return;
		
		List<String> elementos = new LinkedList<String>(argumento);
		actualizarComponente(elementos.toArray());
	}
	
	public LinkedList<String> getContenido()
	{
		LinkedList<String> contenido = new LinkedList<String>();
		for(int i = 0; i < ((JList<String>)getComponente()).getModel().getSize(); i++)
		{
			contenido.add(((JList<String>)getComponente()).getModel().getElementAt(0));
		}
		return contenido;
	}
}
