package ventanas;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Insets;
import javax.swing.ImageIcon;
import java.awt.CardLayout;
import javax.swing.border.MatteBorder;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import pulsador.Luz;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import java.awt.ComponentOrientation;
import java.awt.Rectangle;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import java.awt.Frame;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import auxiliares.ComboBoxObserver;
import auxiliares.ListObserver;
import auxiliares.ObservadorListasUsuario;
import controlador.Controlador;
import modelo.Cancion;
import modelo.Playlist;
import modelo.Usuario;
import observador.Observable;
import observador.Observer;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import javax.swing.ListSelectionModel;

public class NewVentanaPrincipal extends JFrame {

	// CONSTANTES
	private static int COLUMNA_TITULO = 0;
	private static int COLUMNA_INTERPRETE = 1;
	private static int COLUMNA_ESTILO = 2;
	private static int COLUMNA_SELECCIONADA = 3;
	private static int NUM_COLUMNAS = 4;
	public static String[] NOMBRES_COLUMNAS = {"Título", "Intérprete", "Estilo", ""};
	
	private JFrame frmAppmusic;
	private boolean mostrandoPanelReproduccion = false;
	private JTextField textFieldIntepreteBuscar;
	private JTextField textFieldTituloBuscar;
	private String nombreUsuario;
	private Usuario usuario;
	private boolean modoOscuro = true;
	private Controlador controlador;
	private JPanel panelMisPlayLists;
	private JPanel panelVacioListas;
	private JPanel panelVacioCardGeneral;
	
	// Tablas
	private JTable tablaCancionesBuscadas = null;
	private JTable tablaGestionPlaylists = null;
	private JTable tablaPlaylists = null;

	// PROPIEDADES PARA LAS LISTAS.
	private Set<String> elementosLista;
	private JList listaPlaylists;
	private boolean mostrandoPlaylists = false;
	TableModel modeloAnteriorGestion = null;
	
	private JPanel panelCentralMostrado;
	
	// CANCIONES DEVUELTAS EN LA BÚSQUEDA ACTUAL
	private List<Cancion> cancionesBuscadas;
	// CANCIONES SELECCIONADAS EN LA BÚSQUEDA ACTUAL
	private List<Cancion> cancionesSeleccionadas;
	private JTextField textFieldTituloPlaylist;
	private boolean ponerReproducidasYPDF;
	
	
	public NewVentanaPrincipal(String nombreUsuario, boolean ponerReproducidasYPDF) {
		controlador = Controlador.getUnicaInstancia();
		this.nombreUsuario = nombreUsuario;
		this.usuario = controlador.getUsuario(nombreUsuario);
		elementosLista = new HashSet<String>();
		listaPlaylists = new JList<>();
		listaPlaylists.setOpaque(false);
		listaPlaylists.setFont(new Font("Verdana", Font.PLAIN, 10));
		cancionesBuscadas = new LinkedList<Cancion>();
		cancionesSeleccionadas = new ArrayList<Cancion>();
		this.ponerReproducidasYPDF = ponerReproducidasYPDF;
		initialize();
	}
	
	private void cambiarPanelCardCentro(JPanel panelAMostrar) {
		panelCentralMostrado.setVisible(false);
		panelCentralMostrado = panelAMostrar;
		panelAMostrar.setVisible(true);
	}
	
	public JTable getTablaCancionesBuscadas()
	{
		return tablaCancionesBuscadas;
	}
	
	public JTable getTablaGestionPlaylist()
	{
		return tablaGestionPlaylists;
	}
	
	public JTable getTablaPlaylists()
	{
		return tablaPlaylists;
	}
	
//	private JTable inicializarTablaCanciones(String nombre)
//	{
//		Object[][] data = {null, null, null, null};
//		DefaultTableModel modelo = new DefaultTableModel(data, NOMBRES_COLUMNAS);
//		JTable tabla = new JTable(modelo) {
//			@Override
//			public Class getColumnClass(int column) {
//				switch(column) {
//					case 0:
//						return String.class;
//					case 1:
//						return String.class;
//					case 2:
//						return String.class;
//					case 3:
//						return Boolean.class;
//					default:
//						return null;
//				}
//			}
//		};
//		tabla.setName(nombre);
//		tabla = darFormatoATabla(tabla);
//		return tabla;
//	}
	
	
	public JFrame getFrame()
	{
		return frmAppmusic;
	}
	
	private static List<Object[]> sacarNoSeleccionadasALista(JTable tabla)
	{
		LinkedList<Object[]> lista = new LinkedList<Object[]>();
		TableModel modelo = tabla.getModel();
		
		for(int i = 0; i < tabla.getRowCount(); i++)
		{
			Object seleccionado = modelo.getValueAt(i, 3);
			if(seleccionado != null && !(boolean) modelo.getValueAt(i, 3))
			{
				Object nombre = modelo.getValueAt(i, 0);
				Object interprete = modelo.getValueAt(i, 1);
				Object estilo = modelo.getValueAt(i, 2);
				Object seleccionada = (Object) true;	// Lo inicializo a no seleccionado.
				Object[] fila = {nombre, interprete, estilo, seleccionada};
				lista.add(fila);
			}
		}
		return lista;
	}
	
	private static List<Object[]> sacarSeleccionadasALista(JTable tabla)
	{
		LinkedList<Object[]> lista = new LinkedList<Object[]>();
		TableModel modelo = tabla.getModel();
		
		for(int i = 0; i < tabla.getRowCount(); i++)
		{
			Object seleccionado = modelo.getValueAt(i, 3);
			if(seleccionado != null && (boolean) modelo.getValueAt(i, 3))
			{
				Object nombre = modelo.getValueAt(i, 0);
				Object interprete = modelo.getValueAt(i, 1);
				Object estilo = modelo.getValueAt(i, 2);
				Object seleccionada = (Object) true;	// Lo inicializo a no seleccionado.
				Object[] fila = {nombre, interprete, estilo, seleccionada};
				lista.add(fila);
			}
		}
		return lista;
	}
	
	private Object[][] sacarSeleccionadas(JTable tabla)
	{
		List<Object[]> lista = sacarSeleccionadasALista(tabla);
		Object[][] arrayFilas = new Object[lista.size()][];
		int i = 0;
		for(Object[] fila: lista)
		{
			arrayFilas[i] = fila;
			i++;
		}
		
		return arrayFilas;
	}
	
	private static Object[][] DATOS_INICIALES_TABLAS = {null, null, null, null};
	
//	// Este método, básicamente, inicializa una tabla de columnas "Título", "Intérprete", "Estilo" y seleccionada.
//	private JTable inicializarTablaCanciones(Collection<Cancion> canciones)
//	{
//		Object[][] datos = crearFilasTablaCanciones(canciones);
//		TableModel modelo = new DefaultTableModel(datos, NOMBRES_COLUMNAS);
//		JTable tabla = new JTable(modelo) {
//			@Override
//			public Class getColumnClass(int column) {
//				switch(column) {
//					case 0:
//						return String.class;
//					case 1:
//						return String.class;
//					case 2:
//						return String.class;
//					case 3:
//						return Boolean.class;
//					default:
//						return null;
//				}
//			}
//		};
//		tabla = darFormatoATabla(tabla);
//		return tabla;
//	}
	
	private DefaultTableModel crearModelo(Collection<Cancion> canciones)
	{
		return new DefaultTableModel(crearFilasTablaCanciones(canciones), NOMBRES_COLUMNAS);
	}
	
	// Este método crea los datos de dentro de la tabla de canciones.
	private Object[][] crearFilasTablaCanciones(Collection<Cancion> canciones)
	{
		Object[][] datosTabla = new Object[canciones.size()][NUM_COLUMNAS];
		int i = 0;
		for(Cancion c: canciones)
		{
			datosTabla[i][0] = controlador.getTituloCancion(c);
			datosTabla[i][1] = controlador.getInterpreteCancion(c);
			datosTabla[i][2] = controlador.getEstiloCancion(c);
			datosTabla[i][3] = false; 
			i++;
		}
		return datosTabla;
	}
	
	
//	@Override
//	public void update(Observable o, Object arg) {
//		if(arg instanceof String) {
//			char[] contenido = ((String) arg).toCharArray();
//			if(contenido[0] == 'P') {
//				contenido[0] = '\n';
//				elementosLista.add(new String(contenido));
//			}
//		}
//	}
	
	public void inicializarListaPlaylists(List<String> playlists) {
		for(String lista:playlists) {
			listaPlaylists.setModel(new AbstractListModel() {
				String[] values = (String[]) playlists.toArray();
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
		}
	}
	
	/*
	 * Método para mandar los datos de una canción a reproducir al Controlador.
	 */
	private void mandarAReproducir(JTable tabla)
	{
		int filaSeleccionada = tabla.getSelectedRow();
		if(filaSeleccionada != -1)
		{
			List<Cancion> canciones = new LinkedList<Cancion>();
			for(int i = filaSeleccionada; i < tabla.getRowCount(); i++)
			{
				String titulo = (String)tabla.getValueAt(i, 0);
				String interprete = (String)tabla.getValueAt(i, 1);
				canciones.add(controlador.getCancion(titulo, interprete));
			}
			for(int i = filaSeleccionada - 1; i >= 0; i--)
			{
				String titulo = (String)tabla.getValueAt(i, 0);
				String interprete = (String)tabla.getValueAt(i, 1);
				canciones.add(controlador.getCancion(titulo, interprete));
			}
//			controlador.reproducirCancion(titulo, interprete, estilo);
			controlador.reproducirSecuencialmente(canciones);
		}
		else
		{
			controlador.reanudarCancion();
		}
	}

	/**
	 * Create the application.
	 */
	public NewVentanaPrincipal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAppmusic = new JFrame();
		frmAppmusic.setVisible(true);
		frmAppmusic.setIconImage(Toolkit.getDefaultToolkit().getImage(NewVentanaPrincipal.class.getResource("/recursos/iconoAppMusicGrand.png")));
		frmAppmusic.setTitle("AppMusic");
		frmAppmusic.setBounds(100, 100, 1357, 788);
		frmAppmusic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAppmusic.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panelVacioCardGeneral = new JPanel();
		panelVacioCardGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
		panelVacioCardGeneral.setLayout(new BorderLayout());
		JLabel iconoGeneral = new JLabel(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/iconoAppMusicMuyGrande.png")));
		iconoGeneral.setAlignmentY(Component.TOP_ALIGNMENT);
		iconoGeneral.setForeground(new Color(0, 255, 0));
		iconoGeneral.setHorizontalTextPosition(SwingConstants.CENTER);
		iconoGeneral.setFont(new Font("Tahoma", Font.BOLD, 89));
		iconoGeneral.setText("AppMusic");
		panelVacioCardGeneral.add(iconoGeneral);
		panelCentralMostrado = panelVacioCardGeneral;
		
		JPanel panelReproduccion = new JPanel();
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(3, 3, 3, 3));
		frmAppmusic.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panelLateralIzq = new JPanel();
		panelLateralIzq.setBorder(new LineBorder(new Color(0, 0, 0), 5, true));
		panel.add(panelLateralIzq, BorderLayout.WEST);
		GridBagLayout gbl_panelLateralIzq = new GridBagLayout();
		gbl_panelLateralIzq.columnWidths = new int[]{5, 173, 5, 0};
		gbl_panelLateralIzq.rowHeights = new int[]{20, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0};
		gbl_panelLateralIzq.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelLateralIzq.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panelLateralIzq.setLayout(gbl_panelLateralIzq);
		
		
		JPanel panelGestionPlaylists = new JPanel();
		GridBagLayout gbl_panelGestionPlaylists = new GridBagLayout();
		gbl_panelGestionPlaylists.columnWidths = new int[]{5, 0, 5, 0};
		gbl_panelGestionPlaylists.rowHeights = new int[]{5, 0, 26, 0, 0};
		gbl_panelGestionPlaylists.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelGestionPlaylists.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelGestionPlaylists.setLayout(gbl_panelGestionPlaylists);
//		JPanel panelTablaGestion = new JPanel();
//		boolean mostrandoDatosPlaylist = false;
		panelGestionPlaylists.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 3, true), "Gesti\u00F3n de Playlists", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panelTablaGestion = new JPanel();
		GridBagConstraints gbc_panelTablaGestion = new GridBagConstraints();
		gbc_panelTablaGestion.insets = new Insets(0, 0, 5, 5);
		gbc_panelTablaGestion.fill = GridBagConstraints.BOTH;
		gbc_panelTablaGestion.gridx = 1;
		gbc_panelTablaGestion.gridy = 3;
		panelGestionPlaylists.add(panelTablaGestion, gbc_panelTablaGestion);
		panelTablaGestion.setLayout(new BorderLayout(0, 0));
		
//		tablaGestionPlaylists = inicializarTablaCanciones("tablaGestionPlaylists");
		tablaGestionPlaylists = new JTable(new DefaultTableModel(DATOS_INICIALES_TABLAS, NOMBRES_COLUMNAS)) {
			@Override
			public Class getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					case 1:
						return String.class;
					case 2:
						return String.class;
					case 3:
						return Boolean.class;
					default:
						return null;
				}
			}
		};
		tablaGestionPlaylists.setDefaultEditor(Object.class, null);
		tablaGestionPlaylists.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
				{
					mandarAReproducir(tablaGestionPlaylists);
				}
			};
		});
		tablaGestionPlaylists.setShowGrid(false);
		tablaGestionPlaylists.setBackground(new Color(49, 49, 49));
		tablaGestionPlaylists.setGridColor(new Color(0, 0, 0));
		tablaGestionPlaylists.setForeground(new Color(0, 255, 128));
		tablaGestionPlaylists.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tablaGestionPlaylists.setRequestFocusEnabled(false);
		tablaGestionPlaylists.setSelectionBackground(new Color(0, 172, 0));
		
		JScrollPane scrollPaneTablaGestion = new JScrollPane(tablaGestionPlaylists);
		GridBagConstraints gbc_scrollPaneTablaGestion = new GridBagConstraints();
		gbc_scrollPaneTablaGestion.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTablaGestion.gridx = 0;
		gbc_scrollPaneTablaGestion.gridy = 0;
		panelTablaGestion.add(scrollPaneTablaGestion);
		
		JButton botonGestion_1 = new JButton("<html>Gestión<br>Playlists</html>");
		botonGestion_1.addActionListener(e -> {
			String texto = textFieldTituloPlaylist.getText();
			if(controlador.existePlaylistConNombre(usuario, texto))
			{
				Collection<Cancion> cancionesPlaylist = controlador.cargarDatosPlaylist(usuario, texto);
				tablaGestionPlaylists.setModel(crearModelo(cancionesPlaylist));
			}
			else
			{
				Object[][] filasSeleccionadasBusqueda = sacarSeleccionadas(tablaCancionesBuscadas);
				tablaGestionPlaylists.setModel(new DefaultTableModel(filasSeleccionadasBusqueda, NOMBRES_COLUMNAS));
			}
			
			scrollPaneTablaGestion.setVisible(true);
			panelReproduccion.setVisible(true);
			cambiarPanelCardCentro(panelGestionPlaylists);
			
//			panelReproduccion.setVisible(true);
//			tablaGestionPlaylists = controlador.crearTablaGestionPlaylist(textFieldTituloPlaylist.getText());
//			tablaGestionPlaylists.setGridColor(new Color(0, 0, 0));
//			tablaGestionPlaylists.setRequestFocusEnabled(false);
//			tablaGestionPlaylists.setShowGrid(false);
//			tablaGestionPlaylists.setSelectionBackground(new Color(0, 172, 0));
//			tablaGestionPlaylists.setForeground(new Color(0, 255, 0));
//			tablaGestionPlaylists.setFont(new Font("Tahoma", Font.PLAIN, 14));
//			tablaGestionPlaylists.setBackground(new Color(49, 49, 49));
//			panelTablaGestion.setLayout(new BorderLayout(0, 0));
//			
//			JScrollPane scrollPaneTablaGestion = new JScrollPane(tablaGestionPlaylists);
//			panelTablaGestion.add(scrollPaneTablaGestion);
			
//			DefaultTableModel modelo = controlador.cargarDatosPlaylist(textFieldTituloPlaylist.getText());
//			if(modelo != null) {
//				tablaGestionPlaylists.setModel(modelo);
//			}else {
//				cancionesSeleccionadas = new LinkedList<Cancion>();
//				LinkedList<Object[]> filasTablaGestion = new LinkedList<Object[]>();
//				for(int i = 0; i < cancionesBuscadas.size(); i++) {
//					if((boolean)tablaCancionesBuscadas.getModel().getValueAt(i, COLUMNA_SELECCIONADA)) {	// Si está seleccionada
//						cancionesSeleccionadas.add(cancionesBuscadas.get(i));
//						Object[] elementos = new Object[NUM_COLUMNAS];
//						for(int j = 0; j < NUM_COLUMNAS; j++) {
//							elementos[j] = tablaCancionesBuscadas.getModel().getValueAt(i, j);
//						}
//						filasTablaGestion.add(elementos);
//					}
//				}
//				Object[][] filas = new Object[filasTablaGestion.size()][];
//				for(int i = 0; i < filasTablaGestion.size(); i++) {
//					filas[i] = filasTablaGestion.get(i);
//				}
//				modelo = new DefaultTableModel(filas, NOMBRES_COLUMNAS);
//				tablaGestionPlaylists.setModel(modelo);
//			}
			
//			cambiarPanelCardCentro(panelGestionPlaylists);
			
//			panelCentralMostrado.setVisible(false);
//			panelCentralMostrado = panelGestionPlaylists;
//			panelGestionPlaylists.setVisible(true);
		});
		botonGestion_1.setBorder(null);
		botonGestion_1.setContentAreaFilled(false);
		botonGestion_1.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/biblioteca.png")));
		botonGestion_1.setIconTextGap(20);
		botonGestion_1.setHorizontalAlignment(SwingConstants.LEFT);
		botonGestion_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		botonGestion_1.setBackground(Color.BLACK);
		GridBagConstraints gbc_botonGestion_1 = new GridBagConstraints();
		gbc_botonGestion_1.anchor = GridBagConstraints.WEST;
		gbc_botonGestion_1.fill = GridBagConstraints.VERTICAL;
		gbc_botonGestion_1.insets = new Insets(0, 0, 5, 5);
		gbc_botonGestion_1.gridx = 1;
		gbc_botonGestion_1.gridy = 2;
		panelLateralIzq.add(botonGestion_1, gbc_botonGestion_1);
		
		JButton botonRecientes_1 = new JButton("Recientes");

		botonRecientes_1.setBorder(null);
		botonRecientes_1.setContentAreaFilled(false);
		botonRecientes_1.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/reloj-de-arena.png")));
		botonRecientes_1.setIconTextGap(20);
		botonRecientes_1.setHorizontalAlignment(SwingConstants.LEFT);
		botonRecientes_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		botonRecientes_1.setBackground(Color.BLACK);
		GridBagConstraints gbc_botonRecientes_1 = new GridBagConstraints();
		gbc_botonRecientes_1.anchor = GridBagConstraints.WEST;
		gbc_botonRecientes_1.fill = GridBagConstraints.VERTICAL;
		gbc_botonRecientes_1.insets = new Insets(0, 0, 5, 5);
		gbc_botonRecientes_1.gridx = 1;
		gbc_botonRecientes_1.gridy = 3;
		panelLateralIzq.add(botonRecientes_1, gbc_botonRecientes_1);
		
		JButton botonMisPlaylists_1 = new JButton("<html>Mis<br>Playlists</html>");
		botonMisPlaylists_1.addActionListener(e -> {
			if(!mostrandoPlaylists) {
				panelVacioListas.setVisible(false);
				panelMisPlayLists.setVisible(true);
				mostrandoPlaylists = true;
			}else {
				panelVacioListas.setVisible(true);
				panelMisPlayLists.setVisible(false);
				mostrandoPlaylists = false;
			}
			panelReproduccion.setVisible(true);
		});
		
		JButton botonMasReproducidas = new JButton("Más Reproducidas");
		botonMasReproducidas.setContentAreaFilled(false);
		botonMasReproducidas.setVisible(false);
		botonMasReproducidas.setBorder(null);
		botonMasReproducidas.setHorizontalAlignment(SwingConstants.LEFT);
		botonMasReproducidas.setIconTextGap(20);
		botonMasReproducidas.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/nota-musical (1).png")));
		botonMasReproducidas.setBorderPainted(false);
		botonMasReproducidas.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_botonMasReproducidas = new GridBagConstraints();
		gbc_botonMasReproducidas.fill = GridBagConstraints.BOTH;
		gbc_botonMasReproducidas.insets = new Insets(0, 0, 5, 5);
		gbc_botonMasReproducidas.gridx = 1;
		gbc_botonMasReproducidas.gridy = 4;
		panelLateralIzq.add(botonMasReproducidas, gbc_botonMasReproducidas);
		
		JButton btnNewButton = new JButton("Generar PDF");
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setIconTextGap(20);
		btnNewButton.setBorder(null);
		btnNewButton.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/archivo-pdf (1).png")));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnNewButton.setBorderPainted(false);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 5;
		panelLateralIzq.add(btnNewButton, gbc_btnNewButton);
		botonMisPlaylists_1.setBorder(null);
		botonMisPlaylists_1.setContentAreaFilled(false);
		botonMisPlaylists_1.setHorizontalAlignment(SwingConstants.LEFT);
		botonMisPlaylists_1.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/auriculares.png")));
		botonMisPlaylists_1.setIconTextGap(20);
		botonMisPlaylists_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		botonMisPlaylists_1.setBackground(Color.BLACK);
		GridBagConstraints gbc_botonMisPlaylists_1 = new GridBagConstraints();
		gbc_botonMisPlaylists_1.anchor = GridBagConstraints.WEST;
		gbc_botonMisPlaylists_1.fill = GridBagConstraints.VERTICAL;
		gbc_botonMisPlaylists_1.insets = new Insets(0, 0, 5, 5);
		gbc_botonMisPlaylists_1.gridx = 1;
		gbc_botonMisPlaylists_1.gridy = 6;
		panelLateralIzq.add(botonMisPlaylists_1, gbc_botonMisPlaylists_1);
		
		JPanel panelMostrarPlaylists = new JPanel();
		GridBagConstraints gbc_panelMostrarPlaylists = new GridBagConstraints();
		gbc_panelMostrarPlaylists.anchor = GridBagConstraints.WEST;
		gbc_panelMostrarPlaylists.fill = GridBagConstraints.VERTICAL;
		gbc_panelMostrarPlaylists.insets = new Insets(0, 0, 5, 5);
		gbc_panelMostrarPlaylists.gridx = 1;
		gbc_panelMostrarPlaylists.gridy = 8;
		panelLateralIzq.add(panelMostrarPlaylists, gbc_panelMostrarPlaylists);
		panelMostrarPlaylists.setLayout(new CardLayout(0, 0));
		
		panelVacioListas = new JPanel();
		panelMostrarPlaylists.add(panelVacioListas, "name_185652221072400");
		
		panelMisPlayLists = new JPanel();
		panelMisPlayLists.setBorder(new LineBorder(new Color(0, 0, 0), 10, true));
		panelMostrarPlaylists.add(panelMisPlayLists, "name_185661361072600");
		panelMisPlayLists.setLayout(new BorderLayout());
		
		DefaultListModel modeloLista = new DefaultListModel();
		for(Playlist p: controlador.getPlaylists(usuario)) {
			modeloLista.addElement(p.getNombre());
		}
		listaPlaylists = new JList<String>(modeloLista);
		listaPlaylists.setRequestFocusEnabled(false);
		listaPlaylists.setFont(new Font("Tahoma", Font.PLAIN, 14));
//		listaPlaylists.setModel(modeloLista);
//		ListObserver listaConLasPlaylists = new ListObserver(listaPlaylists, "Listas");
		ObservadorListasUsuario listaConLasPlaylists = new ObservadorListasUsuario(listaPlaylists, "Listas", nombreUsuario);
		controlador.addObserver(listaConLasPlaylists);
		
		JScrollPane scrollPanePlaylists = new JScrollPane(listaPlaylists);
		scrollPanePlaylists.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelMisPlayLists.add(scrollPanePlaylists, BorderLayout.CENTER);
		
		JPanel panelTituloMisPlaylists = new JPanel();
		panelTituloMisPlaylists.setBorder(new MatteBorder(0, 0, 3, 0, (Color) new Color(0, 0, 0)));
		panelTituloMisPlaylists.setFont(new Font("Verdana", Font.BOLD, 11));
		panelMisPlayLists.add(panelTituloMisPlaylists, BorderLayout.NORTH);
		
		JLabel labelMisPlaylists = new JLabel("Mis PlayLists");
		labelMisPlaylists.setHorizontalAlignment(SwingConstants.CENTER);
		labelMisPlaylists.setPreferredSize(new Dimension(200, 25));
		labelMisPlaylists.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panelTituloMisPlaylists.add(labelMisPlaylists);
		
		JPanel panelCentroPrincipal = new JPanel();
		panelCentroPrincipal.setBorder(new LineBorder(new Color(0, 0, 0), 5, true));
		panel.add(panelCentroPrincipal, BorderLayout.CENTER);
		GridBagLayout gbl_panelCentroPrincipal = new GridBagLayout();
		gbl_panelCentroPrincipal.columnWidths = new int[]{878, 0};
		gbl_panelCentroPrincipal.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelCentroPrincipal.rowWeights = new double[]{0.0, 1.0, 0.0};
		panelCentroPrincipal.setLayout(gbl_panelCentroPrincipal);
		
		JPanel panelNorteCentro = new JPanel();
		panelNorteCentro.setAlignmentY(Component.TOP_ALIGNMENT);
		GridBagConstraints gbc_panelNorteCentro = new GridBagConstraints();
		gbc_panelNorteCentro.fill = GridBagConstraints.BOTH;
		gbc_panelNorteCentro.insets = new Insets(0, 0, 5, 0);
		gbc_panelNorteCentro.gridx = 0;
		gbc_panelNorteCentro.gridy = 0;
		panelCentroPrincipal.add(panelNorteCentro, gbc_panelNorteCentro);
		GridBagLayout gbl_panelNorteCentro = new GridBagLayout();
		gbl_panelNorteCentro.columnWidths = new int[]{0, 0, 0, 50, 0};
		gbl_panelNorteCentro.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0};
		gbl_panelNorteCentro.rowWeights = new double[]{0.0};
		panelNorteCentro.setLayout(gbl_panelNorteCentro);
		
		Luz luz = new Luz();
		luz.addEncendidoListener(e -> {
			JFileChooser fileChooserFicheroCanciones = new JFileChooser();
			fileChooserFicheroCanciones.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public String getDescription() {
					return "XML songs file (*.xml)";
				}

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					} else {
						return f.getName().toLowerCase().endsWith(".xml");
					}
				}
			});
			fileChooserFicheroCanciones.setAcceptAllFileFilterUsed(false);
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooserFicheroCanciones.setCurrentDirectory(workingDirectory);
			int result = fileChooserFicheroCanciones.showOpenDialog(frmAppmusic);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooserFicheroCanciones.getSelectedFile();
//System.out.println("Nombre del fichero: " + selectedFile.getAbsolutePath());
				if(controlador.cargarCanciones(selectedFile.getAbsolutePath()))
					System.out.println("Cargadas las canciones con éxito.");
				else
					System.out.println("No se han podido cargar las canciones.");
			}
		});
		luz.setForeground(Color.BLACK);
		luz.setColor(new Color(0, 255, 64));
		GridBagConstraints gbc_luz = new GridBagConstraints();
		gbc_luz.fill = GridBagConstraints.VERTICAL;
		gbc_luz.insets = new Insets(0, 0, 0, 5);
		gbc_luz.anchor = GridBagConstraints.WEST;
		gbc_luz.gridx = 1;
		gbc_luz.gridy = 0;
		panelNorteCentro.add(luz, gbc_luz);
		
		JLabel lblNewLabel = new JLabel("Bienvenido, " + nombreUsuario);
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setForeground(new Color(0, 227, 0));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		panelNorteCentro.add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panelBotonesArriba = new JPanel();
		GridBagConstraints gbc_panelBotonesArriba = new GridBagConstraints();
		gbc_panelBotonesArriba.fill = GridBagConstraints.BOTH;
		gbc_panelBotonesArriba.gridx = 4;
		gbc_panelBotonesArriba.gridy = 0;
		panelNorteCentro.add(panelBotonesArriba, gbc_panelBotonesArriba);
		panelBotonesArriba.setLayout(new BoxLayout(panelBotonesArriba, BoxLayout.X_AXIS));
		
		// Cuando un Usuario esté logueado con GitHub no se va a dar la opción de hacerse premium.
		if(!controlador.isLogueadoConGitHub(nombreUsuario))
		{
			JButton botonConvertirPremium = new JButton("Premium");
			botonConvertirPremium.addActionListener(e ->
			{
				DialogoParado dialogo = new DialogoParado(frmAppmusic);
				boolean parado = dialogo.showDialog();
				try {
					float precio = controlador.hacerPremium(nombreUsuario, parado);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			botonConvertirPremium.setBackground(new Color(0, 0, 0));
			botonConvertirPremium.setForeground(new Color(255, 255, 255));
			botonConvertirPremium.setFont(new Font("Tahoma", Font.PLAIN, 14));
			panelBotonesArriba.add(botonConvertirPremium);
		}
		
		JButton botonLogout = new JButton("Logout");
		botonLogout.addActionListener(e -> 
		{
			controlador.logout();
			frmAppmusic.dispose();
		});
		botonLogout.setForeground(new Color(255, 255, 255));
		botonLogout.setBackground(new Color(0, 0, 0));
		botonLogout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelBotonesArriba.add(botonLogout);	
		
		JButton botonVolverInicio = new JButton("");
		botonVolverInicio.addActionListener(e -> {
			panelReproduccion.setVisible(false);
			cambiarPanelCardCentro(panelVacioCardGeneral);
		});
		botonVolverInicio.setBorder(null);
		botonVolverInicio.setContentAreaFilled(false);
		botonVolverInicio.setBorderPainted(false);
		botonVolverInicio.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/casa-icono-silueta.png")));
		panelBotonesArriba.add(botonVolverInicio);
		
		JToggleButton botonModoOscuro = new JToggleButton();
		botonModoOscuro.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/moon.png")));
		botonModoOscuro.setPreferredSize(new Dimension(25, 25));
		botonModoOscuro.setBorderPainted(false);
		panelBotonesArriba.add(botonModoOscuro);
		botonModoOscuro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(modoOscuro) {
					try {
						UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");
						SwingUtilities.updateComponentTreeUI(frmAppmusic);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					modoOscuro = false;
				}else {
					try {
						UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
						SwingUtilities.updateComponentTreeUI(frmAppmusic);
//frame.pack();
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					modoOscuro = true;
				}
			}
		});
		
		JPanel panelCardGeneral = new JPanel();
		GridBagConstraints gbc_panelCardGeneral = new GridBagConstraints();
		gbc_panelCardGeneral.fill = GridBagConstraints.BOTH;
		gbc_panelCardGeneral.insets = new Insets(0, 0, 5, 0);
		gbc_panelCardGeneral.gridx = 0;
		gbc_panelCardGeneral.gridy = 1;
		panelCentroPrincipal.add(panelCardGeneral, gbc_panelCardGeneral);
		panelCardGeneral.setLayout(new CardLayout(0, 0));
		panelCardGeneral.add(panelVacioCardGeneral, "name_256186490325500");
		
		panelCardGeneral.add(panelGestionPlaylists, "name_3097917391500");
		
		JPanel panelBuscar = new JPanel();
		panelCardGeneral.add(panelBuscar, "name_256186498265200");
		GridBagLayout gbl_panelBuscar = new GridBagLayout();
		gbl_panelBuscar.columnWidths = new int[]{5, 0, 5, 0};
		gbl_panelBuscar.rowHeights = new int[]{0, 0, 0, 50, 0};
		gbl_panelBuscar.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelBuscar.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panelBuscar.setLayout(gbl_panelBuscar);
		
		JPanel panelRealizarBusqueda = new JPanel();
		panelRealizarBusqueda.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 3, true), "Buscar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelRealizarBusqueda = new GridBagConstraints();
		gbc_panelRealizarBusqueda.anchor = GridBagConstraints.NORTH;
		gbc_panelRealizarBusqueda.insets = new Insets(0, 0, 5, 5);
		gbc_panelRealizarBusqueda.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelRealizarBusqueda.gridx = 1;
		gbc_panelRealizarBusqueda.gridy = 1;
		panelBuscar.add(panelRealizarBusqueda, gbc_panelRealizarBusqueda);
		GridBagLayout gbl_panelRealizarBusqueda = new GridBagLayout();
		gbl_panelRealizarBusqueda.columnWidths = new int[]{5, 0, 0, 0, 5};
		gbl_panelRealizarBusqueda.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0};
		gbl_panelRealizarBusqueda.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		panelRealizarBusqueda.setLayout(gbl_panelRealizarBusqueda);
		
		JLabel labelInterpreteBuscar = new JLabel("Intérprete");
		labelInterpreteBuscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_labelInterpreteBuscar = new GridBagConstraints();
		gbc_labelInterpreteBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_labelInterpreteBuscar.gridx = 1;
		gbc_labelInterpreteBuscar.gridy = 1;
		panelRealizarBusqueda.add(labelInterpreteBuscar, gbc_labelInterpreteBuscar);
		
		JLabel labelTituloBuscar = new JLabel("Título");
		labelTituloBuscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_labelTituloBuscar = new GridBagConstraints();
		gbc_labelTituloBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_labelTituloBuscar.gridx = 3;
		gbc_labelTituloBuscar.gridy = 1;
		panelRealizarBusqueda.add(labelTituloBuscar, gbc_labelTituloBuscar);
		
		textFieldIntepreteBuscar = new JTextField();
		textFieldIntepreteBuscar.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_textFieldIntepreteBuscar = new GridBagConstraints();
		gbc_textFieldIntepreteBuscar.anchor = GridBagConstraints.NORTH;
		gbc_textFieldIntepreteBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldIntepreteBuscar.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldIntepreteBuscar.gridx = 1;
		gbc_textFieldIntepreteBuscar.gridy = 2;
		panelRealizarBusqueda.add(textFieldIntepreteBuscar, gbc_textFieldIntepreteBuscar);
		textFieldIntepreteBuscar.setColumns(10);
		
		textFieldTituloBuscar = new JTextField();
		textFieldTituloBuscar.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_textFieldTituloBuscar = new GridBagConstraints();
		gbc_textFieldTituloBuscar.anchor = GridBagConstraints.NORTH;
		gbc_textFieldTituloBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldTituloBuscar.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldTituloBuscar.gridx = 3;
		gbc_textFieldTituloBuscar.gridy = 2;
		panelRealizarBusqueda.add(textFieldTituloBuscar, gbc_textFieldTituloBuscar);
		textFieldTituloBuscar.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBackground(new Color(0, 255, 0));
		comboBox.setForeground(new Color(0, 0, 0));
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		ComboBoxObserver comboBoxObserver = new ComboBoxObserver(comboBox);
		controlador.addObserver(comboBoxObserver);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Favoritas");
		chckbxNewCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 1;
		gbc_chckbxNewCheckBox.gridy = 3;
		panelRealizarBusqueda.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
		comboBox.setModel(new DefaultComboBoxModel(controlador.getEstilosMusicales().toArray()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.NORTH;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 3;
		gbc_comboBox.gridy = 3;
		panelRealizarBusqueda.add(comboBox, gbc_comboBox);	
		
		JPanel panelTablaBuscar = new JPanel();
		panelTablaBuscar.setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
		panelTablaBuscar.setVisible(false);
		GridBagConstraints gbc_panelTablaBuscar = new GridBagConstraints();
		gbc_panelTablaBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_panelTablaBuscar.fill = GridBagConstraints.BOTH;
		gbc_panelTablaBuscar.gridx = 1;
		gbc_panelTablaBuscar.gridy = 2;
		panelBuscar.add(panelTablaBuscar, gbc_panelTablaBuscar);
				
		Object[][] data = {null, null, null, null};
		DefaultTableModel model = new DefaultTableModel(data, NOMBRES_COLUMNAS);
		GridBagLayout gbl_panelTablaBuscar = new GridBagLayout();
		gbl_panelTablaBuscar.columnWidths = new int[]{831, 0, 0};
		gbl_panelTablaBuscar.rowHeights = new int[]{50, 0, 0};
		gbl_panelTablaBuscar.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panelTablaBuscar.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		panelTablaBuscar.setLayout(gbl_panelTablaBuscar);
		
		tablaCancionesBuscadas = new JTable(new DefaultTableModel(DATOS_INICIALES_TABLAS, NOMBRES_COLUMNAS)) {
			@Override
			public Class getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					case 1:
						return String.class;
					case 2:
						return String.class;
					case 3:
						return Boolean.class;
					default:
						return null;
				}
			}
		};
		
		tablaCancionesBuscadas.setDefaultEditor(Object.class, null);
		
		tablaCancionesBuscadas.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
				{
					mandarAReproducir(tablaCancionesBuscadas);
				}
			};
		});
		tablaCancionesBuscadas.setShowGrid(false);
		tablaCancionesBuscadas.setBackground(new Color(49, 49, 49));
		tablaCancionesBuscadas.setGridColor(new Color(0, 0, 0));
		tablaCancionesBuscadas.setForeground(new Color(0, 255, 128));
		tablaCancionesBuscadas.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tablaCancionesBuscadas.setRequestFocusEnabled(false);
		tablaCancionesBuscadas.setSelectionBackground(new Color(0, 172, 0));
		JScrollPane scrollPaneTablaBuscar = new JScrollPane(tablaCancionesBuscadas);
//		scrollPaneTablaBuscar.setVisible(false);
		GridBagConstraints gbc_scrollPaneTablaBuscar = new GridBagConstraints();
		gbc_scrollPaneTablaBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneTablaBuscar.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTablaBuscar.gridx = 0;
		gbc_scrollPaneTablaBuscar.gridy = 0;
		panelTablaBuscar.add(scrollPaneTablaBuscar, gbc_scrollPaneTablaBuscar);
		
		JLabel mensajeErrorAlAnadirCanciones = new JLabel("");
		mensajeErrorAlAnadirCanciones.setVisible(false);
		mensajeErrorAlAnadirCanciones.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_mensajeErrorAlAnadirCanciones = new GridBagConstraints();
		gbc_mensajeErrorAlAnadirCanciones.insets = new Insets(0, 0, 0, 5);
		gbc_mensajeErrorAlAnadirCanciones.gridx = 1;
		gbc_mensajeErrorAlAnadirCanciones.gridy = 3;
		panelBuscar.add(mensajeErrorAlAnadirCanciones, gbc_mensajeErrorAlAnadirCanciones);
		
		JButton botonAnadirALista = new JButton("Añadir a Lista");
		botonAnadirALista.addActionListener(e ->
		{
			List<Object[]> filasSeleccionadas = sacarSeleccionadasALista(tablaCancionesBuscadas);
			List<Cancion> cancionesSeleccionadas = filasSeleccionadas.stream()
					.map(fila -> controlador.getCancion((String) fila[0], (String) fila[1]))
					.collect(Collectors.toList());
			
			DialogoSeleccionPlaylist dialogoSeleccionPlaylist = new DialogoSeleccionPlaylist(frmAppmusic, controlador.getPlaylists(usuario).stream()
					.map(playlist -> playlist.getNombre())
					.collect(Collectors.toList()));
			dialogoSeleccionPlaylist.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogoSeleccionPlaylist.setLocationRelativeTo(frmAppmusic);
			boolean aceptado = dialogoSeleccionPlaylist.showDialog();
			
			String nombreLista;
			if(aceptado)
			{
				nombreLista = dialogoSeleccionPlaylist.getSeleccionada(); 
				if(nombreLista != null)
				{
					controlador.anadirAPlaylist(usuario, nombreLista, cancionesSeleccionadas);
				}
				else
				{
					nombreLista = dialogoSeleccionPlaylist.getTexto();
					if(nombreLista != null)
					{
						if(controlador.existePlaylistConNombre(usuario, nombreLista))
						{
							controlador.anadirAPlaylist(usuario, nombreLista, cancionesSeleccionadas);
						}
						else
						{
							String creada = controlador.crearPlaylist(usuario, nombreLista, cancionesSeleccionadas);
							if(creada == "")
								mensajeErrorAlAnadirCanciones.setVisible(false);
							else
								mensajeErrorAlAnadirCanciones.setText(creada);
						}
					}
				}
			}
		});
		botonAnadirALista.setBackground(new Color(0, 0, 0));
		botonAnadirALista.setForeground(new Color(0, 255, 0));
		botonAnadirALista.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_botonAnadirALista = new GridBagConstraints();
		gbc_botonAnadirALista.gridx = 1;
		gbc_botonAnadirALista.gridy = 1;
		panelTablaBuscar.add(botonAnadirALista, gbc_botonAnadirALista);
		
		JButton botonBuscarCancion = new JButton("Buscar");
		botonBuscarCancion.addActionListener(e -> 
		{
			String interprete = textFieldIntepreteBuscar.getText();
			String titulo = textFieldTituloBuscar.getText();
			String estilo = (String)comboBox.getSelectedItem();
			boolean favoritas = chckbxNewCheckBox.isSelected();
			
			Collection<Cancion> canciones = controlador.buscarCanciones(interprete, titulo, estilo, nombreUsuario, favoritas);
			tablaCancionesBuscadas.setModel(crearModelo(canciones));
			panelTablaBuscar.setVisible(true);
			
//			JScrollPane scrollPaneTablaBuscar = new JScrollPane(tablaCancionesBuscadas);
//			GridBagConstraints gbc_scrollPaneTablaBuscar = new GridBagConstraints();
//			gbc_scrollPaneTablaBuscar.fill = GridBagConstraints.BOTH;
//			gbc_scrollPaneTablaBuscar.gridx = 0;
//			gbc_scrollPaneTablaBuscar.gridy = 0;
//			panelTablaBuscar.add(scrollPaneTablaBuscar, gbc_scrollPaneTablaBuscar);
//			panelTablaBuscar.setVisible(true);
			
			
//			panelTablaBuscar.setVisible(false);
//			String interprete = textFieldIntepreteBuscar.getText();
//			String titulo = textFieldTituloBuscar.getText();
//			String estilo = (String)comboBox.getSelectedItem();
//			if(estilo == null) {
//				estilo = "Todos";
//			}
//			boolean favoritas = chckbxNewCheckBox.isSelected();
//			
//			cancionesBuscadas = controlador.buscarCanciones(interprete, titulo, estilo, favoritas).stream()
//				.collect(Collectors.toList());
//			Object[][] tabla = new Object[cancionesBuscadas.size()][4];
//			int i = 0;
//			for(Cancion c:cancionesBuscadas) {
//				tabla[i][0] = c.getTitulo();
//				tabla[i][1] = c.getInterprete();
//				tabla[i][2] = c.getEstilo();
//				tabla[i][3] = false;
//				i++;
//			}
//			filas = tabla;
//			DefaultTableModel modelo = new DefaultTableModel(tabla, NOMBRES_COLUMNAS);
//			tablaCancionesBuscadas.setModel(modelo);
//			tablaCancionesBuscadas.setSelectionBackground(new Color(0, 128, 0));
//			panelTablaBuscar.setVisible(true);
		});
		botonBuscarCancion.setContentAreaFilled(false);
		botonBuscarCancion.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/lupa32.png")));
		botonBuscarCancion.setBorder(null);
		botonBuscarCancion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_botonBuscarCancion = new GridBagConstraints();
		gbc_botonBuscarCancion.insets = new Insets(0, 0, 0, 5);
		gbc_botonBuscarCancion.anchor = GridBagConstraints.NORTHEAST;
		gbc_botonBuscarCancion.gridx = 3;
		gbc_botonBuscarCancion.gridy = 4;
		panelRealizarBusqueda.add(botonBuscarCancion, gbc_botonBuscarCancion);
		
		JPanel panelDatosPlaylist = new JPanel();
		panelDatosPlaylist.setBorder(null);
		GridBagConstraints gbc_panelDatosPlaylist = new GridBagConstraints();
		gbc_panelDatosPlaylist.insets = new Insets(0, 0, 5, 5);
		gbc_panelDatosPlaylist.fill = GridBagConstraints.BOTH;
		gbc_panelDatosPlaylist.gridx = 1;
		gbc_panelDatosPlaylist.gridy = 1;
		panelGestionPlaylists.add(panelDatosPlaylist, gbc_panelDatosPlaylist);
		GridBagLayout gbl_panelDatosPlaylist = new GridBagLayout();
		gbl_panelDatosPlaylist.columnWidths = new int[]{0, 0, 0, 0};
		gbl_panelDatosPlaylist.rowHeights = new int[]{10, 0, 0, 0, 0};
		gbl_panelDatosPlaylist.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panelDatosPlaylist.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelDatosPlaylist.setLayout(gbl_panelDatosPlaylist);
		
		JLabel labelTituloPlaylist = new JLabel("Título");
		labelTituloPlaylist.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_labelTituloPlaylist = new GridBagConstraints();
		gbc_labelTituloPlaylist.anchor = GridBagConstraints.SOUTHWEST;
		gbc_labelTituloPlaylist.insets = new Insets(0, 0, 5, 5);
		gbc_labelTituloPlaylist.gridx = 1;
		gbc_labelTituloPlaylist.gridy = 0;
		panelDatosPlaylist.add(labelTituloPlaylist, gbc_labelTituloPlaylist);
		
		textFieldTituloPlaylist =  new JTextField();
		textFieldTituloPlaylist.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String texto = textFieldTituloPlaylist.getText();
				if(controlador.existePlaylistConNombre(usuario, texto))
				{
					if(modeloAnteriorGestion == null)
						modeloAnteriorGestion = tablaGestionPlaylists.getModel();
					Collection<Cancion> canciones = controlador.cargarDatosPlaylist(usuario, texto);
					tablaGestionPlaylists.setModel(crearModelo(canciones));
				}
				else
				{
					if(modeloAnteriorGestion != null)
					{
						tablaGestionPlaylists.setModel(modeloAnteriorGestion);
						modeloAnteriorGestion = null;
					}
				}
			}
		});
		textFieldTituloPlaylist.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		textFieldTituloPlaylist.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_textFieldTituloPlaylist = new GridBagConstraints();
		gbc_textFieldTituloPlaylist.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldTituloPlaylist.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldTituloPlaylist.gridx = 1;
		gbc_textFieldTituloPlaylist.gridy = 1;
		panelDatosPlaylist.add(textFieldTituloPlaylist, gbc_textFieldTituloPlaylist);
		textFieldTituloPlaylist.setColumns(10);
		
		JPanel panelBotonesCrearEliminar = new JPanel();
		GridBagConstraints gbc_panelBotonesCrearEliminar = new GridBagConstraints();
		gbc_panelBotonesCrearEliminar.insets = new Insets(0, 0, 0, 5);
		gbc_panelBotonesCrearEliminar.fill = GridBagConstraints.VERTICAL;
		gbc_panelBotonesCrearEliminar.gridx = 1;
		gbc_panelBotonesCrearEliminar.gridy = 3;
		panelDatosPlaylist.add(panelBotonesCrearEliminar, gbc_panelBotonesCrearEliminar);
		panelBotonesCrearEliminar.setLayout(new BoxLayout(panelBotonesCrearEliminar, BoxLayout.X_AXIS));
		
		JLabel labelRespuestaCreacion = new JLabel("");
		labelRespuestaCreacion.setVisible(false);
		
		JButton botonCrearPlaylist = new JButton("Crear");
		botonCrearPlaylist.setBackground(new Color(0, 0, 0));
		botonCrearPlaylist.setForeground(new Color(0, 255, 0));
		botonCrearPlaylist.addActionListener(e -> {
			labelRespuestaCreacion.setVisible(false);
			DialogoAceptarRechazar dialogo = new DialogoAceptarRechazar(frmAppmusic, "¿Seguro que quieres crear la Playlist <<" + textFieldTituloPlaylist.getText() + ">>?");
			dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogo.setLocationRelativeTo(frmAppmusic);
			boolean aceptado = dialogo.showDialog();
			
			if(aceptado) {
				
				List<Object[]> filasSeleccionadas = sacarSeleccionadasALista(tablaGestionPlaylists);
				List<Cancion> cancionesDeLaPlaylist = filasSeleccionadas.stream()
						.map(f -> controlador.getCancion((String)f[0], (String)f[1]))
						.collect(Collectors.toList());
				
				String crearLista;
				if(cancionesDeLaPlaylist.isEmpty())
					crearLista = controlador.crearPlaylist(usuario, textFieldTituloPlaylist.getText());
				else
					crearLista = controlador.crearPlaylist(usuario, textFieldTituloPlaylist.getText(), cancionesDeLaPlaylist);
				if(!crearLista.equals("")) {
					labelRespuestaCreacion.setText(crearLista);
					labelRespuestaCreacion.setForeground(Color.RED);
				}else {
					labelRespuestaCreacion.setText("¡Lista creada con éxito!");
					labelRespuestaCreacion.setForeground(Color.GREEN);
				}
				labelRespuestaCreacion.setVisible(true);
			}
		});
		botonCrearPlaylist.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelBotonesCrearEliminar.add(botonCrearPlaylist);
		
		JButton botonEliminarPlaylist = new JButton("Eliminar");
		botonEliminarPlaylist.setForeground(new Color(0, 255, 0));
		botonEliminarPlaylist.setBackground(new Color(0, 0, 0));
		botonEliminarPlaylist.addActionListener(e -> {
			controlador.eliminarPlaylist(usuario, textFieldTituloPlaylist.getText());
		});
		botonEliminarPlaylist.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panelBotonesCrearEliminar.add(botonEliminarPlaylist);
		
		JButton botonEliminarDePlaylist = new JButton("Eliminar de Playlist");
		botonEliminarDePlaylist.addActionListener(e -> 
		{	
			List<Object[]> filasSeleccionadas = sacarSeleccionadasALista(tablaGestionPlaylists);
			String nombreLista = textFieldTituloPlaylist.getText();
			
			List<Cancion> canciones = filasSeleccionadas.stream()
					.map(fila -> controlador.getCancion((String)fila[0], (String)fila[1]))
					.collect(Collectors.toList());
			
			controlador.eliminarDePlaylist(usuario, nombreLista, canciones);
			List<Cancion> cancionesPlaylistActualizada = controlador.cargarDatosPlaylist(usuario, nombreLista);
			tablaGestionPlaylists.setModel(crearModelo(canciones));
//			TableModel modelo = tablaGestionPlaylists.getModel();
//			modelo = controlador.actualizarPlaylist(textFieldTituloPlaylist.getText(), modelo);
//			tablaGestionPlaylists.setModel(modelo);
		});
		botonEliminarDePlaylist.setBackground(new Color(0, 0, 0));
		botonEliminarDePlaylist.setForeground(new Color(0, 255, 0));
		botonEliminarDePlaylist.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_botonEliminarDePlaylist = new GridBagConstraints();
		gbc_botonEliminarDePlaylist.anchor = GridBagConstraints.EAST;
		gbc_botonEliminarDePlaylist.gridx = 2;
		gbc_botonEliminarDePlaylist.gridy = 3;
		panelDatosPlaylist.add(botonEliminarDePlaylist, gbc_botonEliminarDePlaylist);
		
		labelRespuestaCreacion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_labelRespuestaCreacion = new GridBagConstraints();
		gbc_labelRespuestaCreacion.insets = new Insets(0, 0, 5, 5);
		gbc_labelRespuestaCreacion.gridx = 1;
		gbc_labelRespuestaCreacion.gridy = 2;
		panelGestionPlaylists.add(labelRespuestaCreacion, gbc_labelRespuestaCreacion);
		
//		JPanel panelTablaGestion = new JPanel();
//		GridBagConstraints gbc_panelTablaGestion = new GridBagConstraints();
//		gbc_panelTablaGestion.insets = new Insets(0, 0, 0, 5);
//		gbc_panelTablaGestion.fill = GridBagConstraints.BOTH;
//		gbc_panelTablaGestion.gridx = 1;
//		gbc_panelTablaGestion.gridy = 3;
//		panelGestionPlaylists.add(panelTablaGestion, gbc_panelTablaGestion);
		
//		DefaultTableModel modeloGestion = new DefaultTableModel(data, NOMBRES_COLUMNAS);
//		panelTablaGestion.setLayout(new BorderLayout(0, 0));
//		
//		JScrollPane scrollPaneTablaGestion = new JScrollPane(tablaGestionPlaylists);
//		panelTablaGestion.add(scrollPaneTablaGestion);
		
		JPanel panelCancionesLista = new JPanel();
		panelCardGeneral.add(panelCancionesLista, "name_17890355854300");
		
		
		DefaultTableModel modeloTablaPlaylists = new DefaultTableModel(data, NOMBRES_COLUMNAS);
		GridBagLayout gbl_panelCancionesLista = new GridBagLayout();
		gbl_panelCancionesLista.columnWidths = new int[]{452, 0};
		gbl_panelCancionesLista.rowHeights = new int[]{0, 426, 10, 0};
		gbl_panelCancionesLista.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelCancionesLista.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		panelCancionesLista.setLayout(gbl_panelCancionesLista);
		
		JPanel panelBotonesReproPlaylist = new JPanel();
		GridBagConstraints gbc_panelBotonesReproPlaylist = new GridBagConstraints();
		gbc_panelBotonesReproPlaylist.anchor = GridBagConstraints.EAST;
		gbc_panelBotonesReproPlaylist.insets = new Insets(0, 0, 5, 0);
		gbc_panelBotonesReproPlaylist.fill = GridBagConstraints.VERTICAL;
		gbc_panelBotonesReproPlaylist.gridx = 0;
		gbc_panelBotonesReproPlaylist.gridy = 0;
		panelCancionesLista.add(panelBotonesReproPlaylist, gbc_panelBotonesReproPlaylist);
		GridBagLayout gbl_panelBotonesReproPlaylist = new GridBagLayout();
		gbl_panelBotonesReproPlaylist.columnWidths = new int[]{0, 87, 0};
		gbl_panelBotonesReproPlaylist.rowHeights = new int[]{25, 0};
		gbl_panelBotonesReproPlaylist.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panelBotonesReproPlaylist.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panelBotonesReproPlaylist.setLayout(gbl_panelBotonesReproPlaylist);
		
		JButton botonReproSecuencial = new JButton("Secuencial");
		botonReproSecuencial.addActionListener(e -> 
		{	
			String playlist = (String)listaPlaylists.getSelectedValue();
			controlador.reproducirSecuencialmente(usuario, playlist);
		});
		botonReproSecuencial.setBackground(new Color(0, 0, 0));
		botonReproSecuencial.setForeground(new Color(0, 255, 0));
		botonReproSecuencial.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_botonReproSecuencial = new GridBagConstraints();
		gbc_botonReproSecuencial.insets = new Insets(0, 0, 0, 5);
		gbc_botonReproSecuencial.gridx = 0;
		gbc_botonReproSecuencial.gridy = 0;
		panelBotonesReproPlaylist.add(botonReproSecuencial, gbc_botonReproSecuencial);
		
		JButton botonReproAleatoria = new JButton("Aleatorio");
		botonReproAleatoria.addActionListener(e -> 
		{
			String playlist = (String)listaPlaylists.getSelectedValue();
			controlador.reproducirAleatoriamente(usuario, playlist);
		});
		botonReproAleatoria.setForeground(Color.GREEN);
		botonReproAleatoria.setFont(new Font("Tahoma", Font.PLAIN, 14));
		botonReproAleatoria.setBackground(Color.BLACK);
		GridBagConstraints gbc_botonReproAleatoria = new GridBagConstraints();
		gbc_botonReproAleatoria.anchor = GridBagConstraints.WEST;
		gbc_botonReproAleatoria.gridx = 1;
		gbc_botonReproAleatoria.gridy = 0;
		panelBotonesReproPlaylist.add(botonReproAleatoria, gbc_botonReproAleatoria);

		tablaPlaylists = new JTable(modeloTablaPlaylists) {
			@Override
			public Class getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					case 1:
						return String.class;
					case 2:
						return String.class;
					case 3:
						return Boolean.class;
					default:
						return null;
				}
			}
		};
		tablaPlaylists.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
				{
					mandarAReproducir(tablaPlaylists);
				}
			};
		});
		tablaPlaylists.setDefaultEditor(Object.class, null);
		tablaPlaylists.setBackground(new Color(49, 49, 49));
		tablaPlaylists.setForeground(new Color(0, 255, 128));
		tablaPlaylists.setShowGrid(false);
		tablaPlaylists.setSelectionBackground(new Color(0, 172, 0));
		JScrollPane scrollPaneTablaPlaylist = new JScrollPane(tablaPlaylists);
		GridBagConstraints gbc_scrollPaneTablaPlaylist = new GridBagConstraints();
		gbc_scrollPaneTablaPlaylist.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneTablaPlaylist.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTablaPlaylist.gridx = 0;
		gbc_scrollPaneTablaPlaylist.gridy = 1;
		panelCancionesLista.add(scrollPaneTablaPlaylist, gbc_scrollPaneTablaPlaylist);
		
//		botonRecientes_1.addActionListener(e -> {
//			TableModel modelo = controlador.tablaRecientes();
//			tablaPlaylists.setModel(modelo);
//			panelReproduccion.setVisible(true);
//			cambiarPanelCardCentro(panelCancionesLista);
//		});
		
		GridBagConstraints gbc_panelReproduccion = new GridBagConstraints();
		gbc_panelReproduccion.fill = GridBagConstraints.BOTH;
		gbc_panelReproduccion.gridx = 0;
		gbc_panelReproduccion.gridy = 2;
		panelCentroPrincipal.add(panelReproduccion, gbc_panelReproduccion);
		panelReproduccion.setLayout(new BoxLayout(panelReproduccion, BoxLayout.Y_AXIS));
		panelReproduccion.setVisible(false);
		
		JPanel panelSliderRepro = new JPanel();
		panelReproduccion.add(panelSliderRepro);
		
		JSlider slider = new JSlider();
		slider.setPreferredSize(new Dimension(500, 22));
		slider.setForeground(new Color(0, 255, 0));
		panelSliderRepro.add(slider);
		
		JPanel panelBotonesRepro = new JPanel();
		panelReproduccion.add(panelBotonesRepro);
		GridBagLayout gbl_panelBotonesRepro = new GridBagLayout();
		gbl_panelBotonesRepro.columnWidths = new int[]{0, 25, 25, 25, 25, 25, 0, 0};
		gbl_panelBotonesRepro.rowHeights = new int[]{25, 2, 0};
		gbl_panelBotonesRepro.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelBotonesRepro.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panelBotonesRepro.setLayout(gbl_panelBotonesRepro);
		
		JButton botonIzquierda = new JButton("");
		botonIzquierda.addActionListener(e -> {
			controlador.anteriorCancion();
		});
		botonIzquierda.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/anterior (1).png")));
		botonIzquierda.setPreferredSize(new Dimension(32, 32));
		botonIzquierda.setContentAreaFilled(false);
		botonIzquierda.setBorder(null);
		GridBagConstraints gbc_botonIzquierda = new GridBagConstraints();
		gbc_botonIzquierda.anchor = GridBagConstraints.WEST;
		gbc_botonIzquierda.insets = new Insets(0, 0, 5, 5);
		gbc_botonIzquierda.gridx = 1;
		gbc_botonIzquierda.gridy = 0;
		panelBotonesRepro.add(botonIzquierda, gbc_botonIzquierda);
		
		JButton botonParar = new JButton("");
		botonParar.addActionListener(e -> {
			controlador.detenerCancion();
		});
		botonParar.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/boton-detener.png")));
		botonParar.setPreferredSize(new Dimension(32, 32));
		botonParar.setContentAreaFilled(false);
		botonParar.setBorder(null);
		GridBagConstraints gbc_botonParar = new GridBagConstraints();
		gbc_botonParar.anchor = GridBagConstraints.WEST;
		gbc_botonParar.insets = new Insets(0, 0, 5, 5);
		gbc_botonParar.gridx = 2;
		gbc_botonParar.gridy = 0;
		panelBotonesRepro.add(botonParar, gbc_botonParar);
		
		JButton botonPausa = new JButton("");
		botonPausa.addActionListener(e -> {
			controlador.pararCancion();
		});
		botonPausa.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/boton-de-pausa-de-video.png")));
		botonPausa.setPreferredSize(new Dimension(32, 32));
		botonPausa.setContentAreaFilled(false);
		botonPausa.setBorder(null);
		GridBagConstraints gbc_botonPausa = new GridBagConstraints();
		gbc_botonPausa.anchor = GridBagConstraints.WEST;
		gbc_botonPausa.insets = new Insets(0, 0, 5, 5);
		gbc_botonPausa.gridx = 3;
		gbc_botonPausa.gridy = 0;
		panelBotonesRepro.add(botonPausa, gbc_botonPausa);
		
		JButton botonPlay = new JButton("");
		botonPlay.addActionListener(e -> {
			
			JTable tabla = null;
			
			if(panelCentralMostrado == panelBuscar) 
			{
				tabla = tablaCancionesBuscadas;
			}
			else if(panelCentralMostrado == panelGestionPlaylists) 
			{
				tabla = tablaGestionPlaylists;
			}
			else if(panelCentralMostrado == panelCancionesLista) 
			{
				tabla = tablaPlaylists;
			}
			
			if(tabla != null)
				mandarAReproducir(tabla);
			else
				controlador.reanudarCancion();
		});
		botonPlay.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/boton-de-play (1).png")));
		botonPlay.setPreferredSize(new Dimension(32, 32));
		botonPlay.setContentAreaFilled(false);
		botonPlay.setBorder(null);
		GridBagConstraints gbc_botonPlay = new GridBagConstraints();
		gbc_botonPlay.anchor = GridBagConstraints.WEST;
		gbc_botonPlay.insets = new Insets(0, 0, 5, 5);
		gbc_botonPlay.gridx = 4;
		gbc_botonPlay.gridy = 0;
		panelBotonesRepro.add(botonPlay, gbc_botonPlay);
		
		JButton botonDerecha = new JButton("");
		botonDerecha.addActionListener(e -> {
			controlador.siguienteCancion();
		});
		botonDerecha.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/flecha-correcta.png")));
		botonDerecha.setPreferredSize(new Dimension(32, 32));
		botonDerecha.setContentAreaFilled(false);
		botonDerecha.setBorder(null);
		GridBagConstraints gbc_botonDerecha = new GridBagConstraints();
		gbc_botonDerecha.insets = new Insets(0, 0, 5, 5);
		gbc_botonDerecha.anchor = GridBagConstraints.WEST;
		gbc_botonDerecha.gridx = 5;
		gbc_botonDerecha.gridy = 0;
		panelBotonesRepro.add(botonDerecha, gbc_botonDerecha);
		
		JButton botonPanelBuscar = new JButton("Buscar");
		botonPanelBuscar.addActionListener(e -> {
//			panelReproduccion.setVisible(true);
			panelCentralMostrado.setVisible(false);
			panelBuscar.setVisible(true);
			panelCentralMostrado = panelBuscar;
			panelReproduccion.setVisible(true);
			mostrandoPanelReproduccion = true;
		});
		botonPanelBuscar.setContentAreaFilled(false);
		botonPanelBuscar.setBorder(null);
		botonPanelBuscar.setHorizontalAlignment(SwingConstants.LEFT);
		botonPanelBuscar.setIcon(new ImageIcon(NewVentanaPrincipal.class.getResource("/recursos/buscar (1).png")));
		botonPanelBuscar.setToolTipText("Ir al menu de buscar canciones");
		botonPanelBuscar.setIconTextGap(20);
		botonPanelBuscar.setFont(new Font("Tahoma", Font.PLAIN, 16));
		botonPanelBuscar.setBackground(new Color(25, 25, 25));
		GridBagConstraints gbc_botonPanelBuscar = new GridBagConstraints();
		gbc_botonPanelBuscar.anchor = GridBagConstraints.WEST;
		gbc_botonPanelBuscar.fill = GridBagConstraints.VERTICAL;
		gbc_botonPanelBuscar.insets = new Insets(0, 0, 5, 5);
		gbc_botonPanelBuscar.gridx = 1;
		gbc_botonPanelBuscar.gridy = 1;
		panelLateralIzq.add(botonPanelBuscar, gbc_botonPanelBuscar);
		
		botonRecientes_1.addActionListener(e -> 
		{
			listaPlaylists.clearSelection();
			Collection<Cancion> canciones = controlador.getRecientes();
			tablaPlaylists.setModel(crearModelo(canciones));
			cambiarPanelCardCentro(panelCancionesLista);
			panelReproduccion.setVisible(true);
		});
		
		listaPlaylists.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evento){
					String nombreLista = (String) listaPlaylists.getSelectedValue();
					Collection<Cancion> canciones = controlador.cargarDatosPlaylist(usuario, nombreLista);
					
					tablaPlaylists.setModel(crearModelo(canciones));
					cambiarPanelCardCentro(panelCancionesLista);
					panelReproduccion.setVisible(true);
				}
		});
	}

}
