package auxiliares;

import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListModel;

import observador.Observable;

public class ListObserver extends ComponenteObservador {
	
	private final String palabra;
	
	public ListObserver(JList<String> lista, String palabra) {
		super(lista);
		this.palabra = palabra;
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
		
		if(argumento.size() < 1)
			return;
		
		if(argumento.get(0).equals(palabra))
		{
			List<String> elementos = new LinkedList<String>(argumento);
			elementos.remove(0);
			actualizarComponente(elementos.toArray());
		}
		
//		String argumento = (String) arg;
//		
//		if(!argumento.contains("\n"))
//			return;
//		
//		String[] elementos = argumento.split("\n");
//		
//		if(!elementos[0].equals("Listas"))
//		{
//			String[] listas = 
//		}
//		
//		Object[] elementos = ((String)arg).split("\n");
////		if(((String)elementos[0]).equals("Listas")) {	
////			componente.setVisible(false);
////			DefaultListModel modelo = new DefaultListModel();
////			for(int i = 1; i < elementos.length; i++) {
////				modelo.addElement(elementos[i]);
////			}
////			((JList<String>)componente).setModel(modelo);
////			componente.setVisible(true);
////		}
//		actualizarComponente(null);
	}
	
	public LinkedList<String> getContenido()
	{
		LinkedList<String> contenido = new LinkedList<String>();
		for(int i = 0; i < ((JList<String>)getComponente()).getModel().getSize(); i++)
		{
//			System.out.println(((JList<String>)getComponente()).getModel().getElementAt(0));
			contenido.add(((JList<String>)getComponente()).getModel().getElementAt(0));
		}
		return contenido;
	}
}
