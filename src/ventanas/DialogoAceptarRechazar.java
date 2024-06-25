package ventanas;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;

public class DialogoAceptarRechazar extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean aceptado = false;
	
	/**
	 * Create the application.
	 */
	public DialogoAceptarRechazar(JFrame framePadre, String pregunta) {
		super(framePadre, "Crear Playlist", true);
		
		JPanel panel = new JPanel();
//		frame.getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel(pregunta);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		panel.add(panel_1, gbc_panel_1);
		
		JButton btnNewButton = new JButton("Sí");
		btnNewButton.addActionListener(e -> {
			aceptado = true;
			dispose();
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_1.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("No");
		btnNewButton_1.addActionListener(e -> {
			dispose();
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_1.add(btnNewButton_1);
		setContentPane(panel);
		pack();
	}
	
	public boolean showDialog() {
		this.setVisible(true);
		return aceptado;
	}
}
