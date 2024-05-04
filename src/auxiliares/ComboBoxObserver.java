package auxiliares;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import observador.Observable;

public class ComboBoxObserver extends ComponenteObservador {
	
	public ComboBoxObserver(JComboBox<String> comboBox) {
//		componente = comboBox;
		super(comboBox);
	}
	
	public void actualizarContenido(List<String> contenido)
	{
		JComboBox componente = (JComboBox) getComponente();
		DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<String>();
		contenido.stream()
			.forEach(c -> modelo.addElement(c));
		componente.setModel(modelo);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(arg == null || !(arg instanceof List))
			return;
		
		List<Object> objetos = (List<Object>)arg;
		if(objetos.stream()
				.anyMatch(ob -> !(ob instanceof String)))
			return;
		
		List<String> argumento = (List<String>) arg;
		
		if(argumento.size() < 1)
			return;
		
		if(argumento.get(0).equals("Todos"))
		{
			actualizarContenido(argumento);
		}
//		if(arg == null || !(arg instanceof String))
//			return;
//		
//		if(!((String)arg).contains("\n"))
//			return;
//		
//		String[] elementos = ((String)arg).split("\n");
//		if(elementos[0].equals("Todos")) {			
//			((JComboBox<String>)getComponente()).setModel(new DefaultComboBoxModel(elementos));
//		}
	}
}
