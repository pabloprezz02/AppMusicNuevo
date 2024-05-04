package ventanas;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.List;
import java.awt.Font;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import controlador.Controlador;

import java.awt.Dimension;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.border.MatteBorder;

import com.toedter.calendar.JCalendar;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.CardLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import pulsador.Luz;
import javax.swing.border.SoftBevelBorder;

public class NewVentanaLogin {

	private JFrame frame;
	private Controlador controlador;
	private JTextField textFieldUsrLogin;
	private JPasswordField passwordFieldLogin;
	private JTextField textFieldUsuarioRegistro;
	private JPasswordField passwordFieldRegistro;
	private JTextField textFieldEmailRegistro;
	
	private VentanaUsuarios ventanaUsuarios = null;
	
	public void mostrarVentana() {
		frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public NewVentanaLogin() {
		controlador = Controlador.getUnicaInstancia();
//		ventanaUsuarios = new VentanaUsuarios(controlador.mostrarUsuarios());
//		ventanaUsuarios.getFrame().addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				controlador.
//			}
//		});
//		controlador.addObserver(ventanaUsuarios);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
//		try {
//			UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//				| UnsupportedLookAndFeelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		frame = new JFrame();
		frame.setResizable(false);
		frame.setMinimumSize(new Dimension(600, 420));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		
		JPanel panelLogin = new JPanel();
		frame.getContentPane().add(panelLogin, "panelLogin");
		GridBagLayout gbl_panelLogin = new GridBagLayout();
		gbl_panelLogin.columnWidths = new int[]{50, 400, 50, 0};
		gbl_panelLogin.rowHeights = new int[]{0, 0, 0, 20, 0, 0};
		gbl_panelLogin.columnWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelLogin.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelLogin.setLayout(gbl_panelLogin);
		
		JLabel labelAppMusic = new JLabel("AppMusic");
		labelAppMusic.setForeground(new Color(255, 255, 255));
		labelAppMusic.setFont(new Font("Verdana", Font.BOLD, 24));
		GridBagConstraints gbc_labelAppMusic = new GridBagConstraints();
		gbc_labelAppMusic.insets = new Insets(0, 0, 5, 5);
		gbc_labelAppMusic.gridx = 1;
		gbc_labelAppMusic.gridy = 1;
		panelLogin.add(labelAppMusic, gbc_labelAppMusic);
		
		JLabel iconoAppMusic = new JLabel("");
		iconoAppMusic.setIcon(new ImageIcon(NewVentanaLogin.class.getResource("/recursos/iconoAppMusicGrand.png")));
		GridBagConstraints gbc_iconoAppMusic = new GridBagConstraints();
		gbc_iconoAppMusic.insets = new Insets(0, 0, 5, 5);
		gbc_iconoAppMusic.gridx = 1;
		gbc_iconoAppMusic.gridy = 2;
		panelLogin.add(iconoAppMusic, gbc_iconoAppMusic);
		
		JPanel panelCamposLogin = new JPanel();
		GridBagConstraints gbc_panelCamposLogin = new GridBagConstraints();
		gbc_panelCamposLogin.insets = new Insets(0, 0, 0, 5);
		gbc_panelCamposLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelCamposLogin.gridx = 1;
		gbc_panelCamposLogin.gridy = 4;
		panelLogin.add(panelCamposLogin, gbc_panelCamposLogin);
		GridBagLayout gbl_panelCamposLogin = new GridBagLayout();
		gbl_panelCamposLogin.columnWidths = new int[]{0, 0};
		gbl_panelCamposLogin.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panelCamposLogin.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelCamposLogin.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelCamposLogin.setLayout(gbl_panelCamposLogin);
		
		JPanel campoUsuarioLogin = new JPanel();
		campoUsuarioLogin.setOpaque(false);
		campoUsuarioLogin.setBorder(new SoftBevelBorder(BevelBorder.RAISED, new Color(0, 255, 0), null, null, null));
		campoUsuarioLogin.setBackground(new Color(0, 0, 0));
		campoUsuarioLogin.setForeground(new Color(0, 0, 0));
		campoUsuarioLogin.setPreferredSize(new Dimension(10, 20));
		GridBagConstraints gbc_campoUsuarioLogin = new GridBagConstraints();
		gbc_campoUsuarioLogin.insets = new Insets(0, 0, 5, 0);
		gbc_campoUsuarioLogin.fill = GridBagConstraints.BOTH;
		gbc_campoUsuarioLogin.gridx = 0;
		gbc_campoUsuarioLogin.gridy = 0;
		panelCamposLogin.add(campoUsuarioLogin, gbc_campoUsuarioLogin);
		campoUsuarioLogin.setLayout(new BoxLayout(campoUsuarioLogin, BoxLayout.X_AXIS));
		
		JPanel panelMargen1 = new JPanel();
		panelMargen1.setPreferredSize(new Dimension(5, 10));
		panelMargen1.setOpaque(false);
		panelMargen1.setBackground(new Color(0, 0, 0));
		panelMargen1.setBorder(null);
		campoUsuarioLogin.add(panelMargen1);
		
		JLabel labelUsuarioLogin = new JLabel("Usuario:");
		labelUsuarioLogin.setBackground(new Color(0, 0, 0));
		labelUsuarioLogin.setForeground(new Color(255, 255, 255));
		labelUsuarioLogin.setHorizontalAlignment(SwingConstants.LEFT);
		labelUsuarioLogin.setPreferredSize(new Dimension(80, 14));
		labelUsuarioLogin.setFont(new Font("Verdana", Font.BOLD, 10));
		campoUsuarioLogin.add(labelUsuarioLogin);
		
		textFieldUsrLogin = new JTextField();
		textFieldUsrLogin.setOpaque(false);
		textFieldUsrLogin.setFont(new Font("Tahoma", Font.BOLD, 11));
		textFieldUsrLogin.setColumns(10);
		textFieldUsrLogin.setBorder(null);
		textFieldUsrLogin.setBackground(new Color(0, 0, 0));
		campoUsuarioLogin.add(textFieldUsrLogin);
		
		JPanel campoContrasenaLogin = new JPanel();
		campoContrasenaLogin.setOpaque(false);
		campoContrasenaLogin.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, new Color(0, 255, 0), null, null, null));
		campoContrasenaLogin.setBackground(new Color(0, 0, 0));
		campoContrasenaLogin.setPreferredSize(new Dimension(10, 20));
		GridBagConstraints gbc_campoContrasenaLogin = new GridBagConstraints();
		gbc_campoContrasenaLogin.insets = new Insets(0, 0, 5, 0);
		gbc_campoContrasenaLogin.fill = GridBagConstraints.BOTH;
		gbc_campoContrasenaLogin.gridx = 0;
		gbc_campoContrasenaLogin.gridy = 1;
		panelCamposLogin.add(campoContrasenaLogin, gbc_campoContrasenaLogin);
		campoContrasenaLogin.setLayout(new BoxLayout(campoContrasenaLogin, BoxLayout.X_AXIS));
		
		JPanel panelMargen2 = new JPanel();
		panelMargen2.setPreferredSize(new Dimension(5, 10));
		panelMargen2.setOpaque(false);
		campoContrasenaLogin.add(panelMargen2);
		
		JLabel labelContrasenaLogin = new JLabel("Contraseña:\r\n\r\n");
		labelContrasenaLogin.setForeground(new Color(255, 255, 255));
		labelContrasenaLogin.setHorizontalAlignment(SwingConstants.LEFT);
		labelContrasenaLogin.setPreferredSize(new Dimension(80, 14));
		labelContrasenaLogin.setFont(new Font("Verdana", Font.BOLD, 10));
		campoContrasenaLogin.add(labelContrasenaLogin);
		
		passwordFieldLogin = new JPasswordField();
		passwordFieldLogin.setEchoChar('*');
		passwordFieldLogin.setFont(new Font("Tahoma", Font.BOLD, 11));
		passwordFieldLogin.setBorder(null);
		passwordFieldLogin.setOpaque(false);
		campoContrasenaLogin.add(passwordFieldLogin);
		
		JPanel botoneraLogin = new JPanel();
		GridBagConstraints gbc_botoneraLogin = new GridBagConstraints();
		gbc_botoneraLogin.anchor = GridBagConstraints.EAST;
		gbc_botoneraLogin.insets = new Insets(0, 0, 5, 0);
		gbc_botoneraLogin.fill = GridBagConstraints.VERTICAL;
		gbc_botoneraLogin.gridx = 0;
		gbc_botoneraLogin.gridy = 2;
		panelCamposLogin.add(botoneraLogin, gbc_botoneraLogin);
		botoneraLogin.setLayout(new BoxLayout(botoneraLogin, BoxLayout.X_AXIS));
		
		JLabel labelMensajes = new JLabel();
		labelMensajes.setForeground(new Color(255, 0, 0));
		labelMensajes.setFont(new Font("Verdana", Font.BOLD, 10));
		GridBagConstraints gbc_labelMensajes = new GridBagConstraints();
		gbc_labelMensajes.anchor = GridBagConstraints.SOUTH;
		gbc_labelMensajes.insets = new Insets(0, 0, 5, 5);
		gbc_labelMensajes.gridx = 1;
		gbc_labelMensajes.gridy = 3;
		labelMensajes.setVisible(false);
		panelLogin.add(labelMensajes, gbc_labelMensajes);
		
		JButton botonLogin = new JButton("Loguearse");
		botonLogin.setBackground(new Color(0, 0, 0));
		botonLogin.setOpaque(false);
		botonLogin.setForeground(new Color(0, 255, 64));
		botonLogin.setFont(new Font("Verdana", Font.BOLD, 10));
		botonLogin.addActionListener(e -> {
			String usuario = textFieldUsrLogin.getText();
			char[] contrasena = passwordFieldLogin.getPassword();
			
			String mensaje = controlador.login(usuario, contrasena);
			if(mensaje == null) {
//				VentanaPrincipal main = new VentanaPrincipal(usuario);
				frame.setVisible(false);
				if(ventanaUsuarios != null)
					ventanaUsuarios.setVisible(false);
				controlador.deleteObserver(ventanaUsuarios);
			}else {
				labelMensajes.setText(mensaje);
				labelMensajes.setVisible(true);
			}
		});
		botoneraLogin.add(botonLogin);
		
		JButton botonLoginGitHub = new JButton("Login Con GitHub");
		botonLoginGitHub.setBackground(new Color(0, 0, 0));
		botonLoginGitHub.setForeground(new Color(0, 255, 64));
		botonLoginGitHub.setFont(new Font("Verdana", Font.BOLD, 10));
		botonLoginGitHub.addActionListener(e->{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public String getDescription() {
					return "GitHub Properties File (*.properties)";
				}

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					} else {
						return f.getName().toLowerCase().endsWith(".properties");
					}
				}
			});
			fileChooser.setAcceptAllFileFilterUsed(false);
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileChooser.setCurrentDirectory(workingDirectory);
			int result = fileChooser.showOpenDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				System.out.println("Selected file: " + selectedFile.getAbsolutePath());
				if (controlador.verificarGitHub(textFieldUsrLogin.getText(), selectedFile.getAbsolutePath())) {
					JOptionPane.showMessageDialog(frame, "Login Correcto", "Login",
							JOptionPane.INFORMATION_MESSAGE);
					controlador.loguearConGitHub(textFieldUsrLogin.getText(), new String(passwordFieldLogin.getPassword()));
//					NewVentanaPrincipal main=new NewVentanaPrincipal(textFieldUsrLogin.getText(), true);
					frame.setVisible(false);
					if(ventanaUsuarios != null)
						ventanaUsuarios.setVisible(false);
					controlador.deleteObserver(ventanaUsuarios);
				} else {
					JOptionPane.showMessageDialog(frame, "Login Fallido", "Login", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		botoneraLogin.add(botonLoginGitHub);
		
		JPanel panelRegistro = new JPanel();
		frame.getContentPane().add(panelRegistro, "panelRegistro");
		GridBagLayout gbl_panelRegistro = new GridBagLayout();
		gbl_panelRegistro.columnWidths = new int[]{50, 0, 50, 0};
		gbl_panelRegistro.rowHeights = new int[]{20, 20, 0, 0, 0, 0, 0, 0, 0, 20, 0};
		gbl_panelRegistro.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panelRegistro.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelRegistro.setLayout(gbl_panelRegistro);
		
		JButton botonRegistrarseLogin = new JButton("Registrarse");
		botonRegistrarseLogin.setBackground(new Color(0, 0, 0));
		botonRegistrarseLogin.addActionListener(e -> {
			CardLayout c1 = (CardLayout) frame.getContentPane().getLayout();
			c1.show(frame.getContentPane(), "panelRegistro");
		});
		botonRegistrarseLogin.setForeground(new Color(0, 255, 64));
		botonRegistrarseLogin.setFont(new Font("Verdana", Font.BOLD, 10));
		GridBagConstraints gbc_botonRegistrarseLogin = new GridBagConstraints();
		gbc_botonRegistrarseLogin.anchor = GridBagConstraints.SOUTHEAST;
		gbc_botonRegistrarseLogin.gridx = 0;
		gbc_botonRegistrarseLogin.gridy = 3;
		panelCamposLogin.add(botonRegistrarseLogin, gbc_botonRegistrarseLogin);
		
		JLabel labelRegistro = new JLabel("Registro");
		labelRegistro.setFont(new Font("Verdana", Font.BOLD, 16));
		GridBagConstraints gbc_labelRegistro = new GridBagConstraints();
		gbc_labelRegistro.anchor = GridBagConstraints.NORTH;
		gbc_labelRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_labelRegistro.gridx = 1;
		gbc_labelRegistro.gridy = 0;
		panelRegistro.add(labelRegistro, gbc_labelRegistro);
		
		JPanel CampoUsuarioRegistro = new JPanel();
		CampoUsuarioRegistro.setBorder(new SoftBevelBorder(BevelBorder.RAISED, new Color(0, 255, 0), null, null, null));
		GridBagConstraints gbc_CampoUsuarioRegistro = new GridBagConstraints();
		gbc_CampoUsuarioRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_CampoUsuarioRegistro.fill = GridBagConstraints.BOTH;
		gbc_CampoUsuarioRegistro.gridx = 1;
		gbc_CampoUsuarioRegistro.gridy = 2;
		panelRegistro.add(CampoUsuarioRegistro, gbc_CampoUsuarioRegistro);
		CampoUsuarioRegistro.setLayout(new BoxLayout(CampoUsuarioRegistro, BoxLayout.X_AXIS));
		
		JLabel labelUsuarioRegistro = new JLabel("Usuario:");
		labelUsuarioRegistro.setPreferredSize(new Dimension(80, 14));
		labelUsuarioRegistro.setFont(new Font("Verdana", Font.BOLD, 10));
		CampoUsuarioRegistro.add(labelUsuarioRegistro);
		
		textFieldUsuarioRegistro = new JTextField();
		textFieldUsuarioRegistro.setBorder(null);
		textFieldUsuarioRegistro.setOpaque(false);
		CampoUsuarioRegistro.add(textFieldUsuarioRegistro);
		textFieldUsuarioRegistro.setColumns(10);
		
		JPanel panelContrasenaRegistro = new JPanel();
		panelContrasenaRegistro.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_panelContrasenaRegistro = new GridBagConstraints();
		gbc_panelContrasenaRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_panelContrasenaRegistro.fill = GridBagConstraints.BOTH;
		gbc_panelContrasenaRegistro.gridx = 1;
		gbc_panelContrasenaRegistro.gridy = 3;
		panelRegistro.add(panelContrasenaRegistro, gbc_panelContrasenaRegistro);
		panelContrasenaRegistro.setLayout(new BoxLayout(panelContrasenaRegistro, BoxLayout.X_AXIS));
		
		JLabel labelContrasenaRegistro = new JLabel("Contraseña:");
		labelContrasenaRegistro.setFont(new Font("Verdana", Font.BOLD, 10));
		labelContrasenaRegistro.setPreferredSize(new Dimension(80, 14));
		panelContrasenaRegistro.add(labelContrasenaRegistro);
		
		passwordFieldRegistro = new JPasswordField();
		passwordFieldRegistro.setBorder(null);
		passwordFieldRegistro.setOpaque(false);
		panelContrasenaRegistro.add(passwordFieldRegistro);
		
		JPanel panelEmailRegistro = new JPanel();
		panelEmailRegistro.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, new Color(0, 255, 0), null, null, null));
		GridBagConstraints gbc_panelEmailRegistro = new GridBagConstraints();
		gbc_panelEmailRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_panelEmailRegistro.fill = GridBagConstraints.BOTH;
		gbc_panelEmailRegistro.gridx = 1;
		gbc_panelEmailRegistro.gridy = 4;
		panelRegistro.add(panelEmailRegistro, gbc_panelEmailRegistro);
		panelEmailRegistro.setLayout(new BoxLayout(panelEmailRegistro, BoxLayout.X_AXIS));
		
		JLabel labelEmailRegistro = new JLabel("Email:");
		labelEmailRegistro.setFont(new Font("Verdana", Font.BOLD, 10));
		labelEmailRegistro.setPreferredSize(new Dimension(80, 14));
		panelEmailRegistro.add(labelEmailRegistro);
		
		textFieldEmailRegistro = new JTextField();
		textFieldEmailRegistro.setOpaque(false);
		textFieldEmailRegistro.setBorder(null);
		panelEmailRegistro.add(textFieldEmailRegistro);
		textFieldEmailRegistro.setColumns(10);
		
		JLabel labelIntroduceFNRegistro = new JLabel("Introduce tu fecha de nacimiento:");
		labelIntroduceFNRegistro.setFont(new Font("Verdana", Font.BOLD, 10));
		GridBagConstraints gbc_labelIntroduceFNRegistro = new GridBagConstraints();
		gbc_labelIntroduceFNRegistro.anchor = GridBagConstraints.WEST;
		gbc_labelIntroduceFNRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_labelIntroduceFNRegistro.gridx = 1;
		gbc_labelIntroduceFNRegistro.gridy = 5;
		panelRegistro.add(labelIntroduceFNRegistro, gbc_labelIntroduceFNRegistro);
		
		JCalendar calendario = new JCalendar();
		calendario.getYearChooser().getSpinner().setFont(new Font("Verdana", Font.BOLD, 10));
		calendario.getYearChooser().getSpinner().setBackground(Color.LIGHT_GRAY);
		calendario.getMonthChooser().getComboBox().setFont(new Font("Verdana", Font.BOLD, 10));
		GridBagConstraints gbc_calendario = new GridBagConstraints();
		gbc_calendario.insets = new Insets(0, 0, 5, 5);
		gbc_calendario.fill = GridBagConstraints.BOTH;
		gbc_calendario.gridx = 1;
		gbc_calendario.gridy = 6;
		panelRegistro.add(calendario, gbc_calendario);
		
		JPanel botoneraRegistro = new JPanel();
		GridBagConstraints gbc_botoneraRegistro = new GridBagConstraints();
		gbc_botoneraRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_botoneraRegistro.gridx = 1;
		gbc_botoneraRegistro.gridy = 7;
		panelRegistro.add(botoneraRegistro, gbc_botoneraRegistro);
		botoneraRegistro.setLayout(new BoxLayout(botoneraRegistro, BoxLayout.X_AXIS));
		
		JButton botonVolverALogin = new JButton("Volver a Login");
		botonVolverALogin.setBackground(new Color(0, 0, 0));
		botonVolverALogin.setForeground(new Color(0, 255, 64));
		botonVolverALogin.addActionListener(e -> {
			CardLayout c1 = (CardLayout) frame.getContentPane().getLayout();
			c1.show(frame.getContentPane(), "panelLogin");
		});
		botonVolverALogin.setFont(new Font("Verdana", Font.BOLD, 10));
		botoneraRegistro.add(botonVolverALogin);
		
		JLabel labelMensajesRegistro = new JLabel();
		labelMensajesRegistro.setFont(new Font("Verdana", Font.BOLD, 10));
		GridBagConstraints gbc_labelMensajesRegistro = new GridBagConstraints();
		gbc_labelMensajesRegistro.anchor = GridBagConstraints.SOUTH;
		gbc_labelMensajesRegistro.insets = new Insets(0, 0, 5, 5);
		gbc_labelMensajesRegistro.gridx = 1;
		gbc_labelMensajesRegistro.gridy = 1;
		labelMensajesRegistro.setVisible(false);
		panelRegistro.add(labelMensajesRegistro, gbc_labelMensajesRegistro);
		
		JButton botonRegistrarseRegistro = new JButton("Registro");
		botonRegistrarseRegistro.setBackground(new Color(0, 0, 0));
		botonRegistrarseRegistro.addActionListener(e -> {
			String nombre = textFieldUsuarioRegistro.getText();
			char[] password = passwordFieldRegistro.getPassword();
			String email = textFieldEmailRegistro.getText();
			Date fecha = calendario.getDate();
			
			String mensaje = controlador.registrarUsuario(nombre, password, email, fecha);
			if(mensaje == null) {
				labelMensajesRegistro.setForeground(Color.GREEN);
				labelMensajesRegistro.setText("Usuario registrado con éxito.");
			}else {
				labelMensajesRegistro.setForeground(Color.RED);
				labelMensajesRegistro.setText(mensaje);
			}
			labelMensajesRegistro.setVisible(true);
		});
		botonRegistrarseRegistro.setForeground(new Color(0, 255, 64));
		botonRegistrarseRegistro.setFont(new Font("Verdana", Font.BOLD, 10));
		botoneraRegistro.add(botonRegistrarseRegistro);
		
		JButton botonVerUsuarios = new JButton("Ver usuarios");
		botonVerUsuarios.setBackground(new Color(0, 0, 0));
		botonVerUsuarios.addActionListener(e -> {
//			controlador.mostrarUsuarios();
//			ventanaUsuarios.mostrarVentana();
			List<String> usuarios = controlador.getNombresUsuarios();
			VentanaUsuarios ventana = new VentanaUsuarios(usuarios);
			controlador.addObserver(ventana);
			ventana.getFrame().addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					controlador.deleteObserver(ventana);
				}
			});
		});
		botonVerUsuarios.setForeground(new Color(0, 255, 64));
		botonVerUsuarios.setFont(new Font("Verdana", Font.BOLD, 10));
		GridBagConstraints gbc_botonVerUsuarios = new GridBagConstraints();
		gbc_botonVerUsuarios.insets = new Insets(0, 0, 5, 5);
		gbc_botonVerUsuarios.gridx = 1;
		gbc_botonVerUsuarios.gridy = 8;
		panelRegistro.add(botonVerUsuarios, gbc_botonVerUsuarios); 
	}

}
