package ventanas;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JTextField;

import controlador.Controlador;
import modelo.Usuario;
import observador.Observable;
import observador.Observer;

import java.awt.BorderLayout;
import javax.swing.JList;
import java.awt.Font;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import java.awt.Insets;
import javax.swing.JScrollPane;
import java.awt.Window.Type;
import java.awt.Toolkit;

public abstract class VentanaLista implements Observer /*extends Thread*/ {

	private JFrame frame; 
	private JScrollPane scrollPane;
//	protected JTextArea textArea;
	private JList<String> lista;
	
	public void mostrarVentana() {
		frame.setVisible(true);
	}
	
	public boolean isActive() {
		return frame.isEnabled();
	}
	
	public void setVisible(boolean visibilidad) {
		frame.setVisible(visibilidad);
	}
	
	public JList<String> getLista()
	{
		return lista;
	}
	
//	protected abstract void actualizar(char[] contenido);
	
	@Override
	public void update(Observable o, Object arg) {
//		if(arg instanceof String) {
//			char[] contenido = ((String) arg).toCharArray();
//			actualizar(contenido);
//			textArea.setVisible(true);
//		}
		if(arg == null || !(arg instanceof List))
			return;
		
		List<Object> argumento = (List<Object>) arg;
		if(argumento.size() < 1)
			return;
		
		Object primerElemento = argumento.get(0);
		if(primerElemento instanceof String)
		{
			if(((String)primerElemento).equals(palabraFiltro))
			{
				List<Object> contenido = new LinkedList<Object>(argumento);
				contenido.remove(0);
				actualizarContenido(contenido);
			}
		}
	}
	
	public void actualizarContenido(List<Object> elementos)
	{
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		for(int i = 0; i < elementos.size(); i++)
		{
			modelo.addElement((String) elementos.get(i));
		}
		getLista().setModel(modelo);
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
//	protected abstract void actualizar();
	
//	@Override
//	public void run() {
//		 // CADA 2 SEGUNDOS QUE ACTUALICE LOS USUARIOS
//		for(;;) {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////				textArea.setText(controlador.listaUsuarios());
//			actualizar();
//			textArea.setVisible(true);
//		}
//	}
	
	/**
	 * Create the application.
	 */
	
	private String palabraFiltro;
	
	public VentanaLista(String palabraFiltro, List<Object> elementos) 
	{
		this.palabraFiltro = palabraFiltro;
		initialize(elementos);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(List<Object> elementos) {
//		controlador = Controlador.getUnicaInstancia();
		frame = new JFrame();
		this.setVisible(true);
//		frame.setLocationByPlatform(true);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaLista.class.getResource("/recursos/NADA.png")));
		frame.setBounds(100, 100, 182, 300);
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
//		textArea = new JTextArea();
//		textArea.setEditable(false);
//		textArea.setFont(new Font("Verdana", Font.BOLD, 10));
//		char[] texto = contenido.toCharArray();
//		texto[0] = '\n';
//		textArea.setText(new String(texto));
		lista = new JList<String>();
		lista.setFont(new Font("Tahoma", Font.PLAIN, 14));
		actualizarContenido(elementos);
		scrollPane.setViewportView(lista);
	}

}
