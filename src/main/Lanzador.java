package main;

import java.awt.EventQueue;

import javax.swing.UIManager;

import ventanas.NewVentanaLogin;
//import ventanas.VentanaLogin;

public class Lanzador {
	public static void main(String[] args) {
		System.out.println("\n");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacDarkLaf");
					NewVentanaLogin ventana = new NewVentanaLogin();
					ventana.mostrarVentana();
				} catch (Exception e) {				
					e.printStackTrace();
				}
			}
		});
	}
}
