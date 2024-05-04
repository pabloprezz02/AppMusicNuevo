package ventanas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;

public class DialogoParado extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private boolean enParo;

	/**
	 * Create the dialog.
	 */
	public DialogoParado(JFrame frame) {
		super(frame, true);
		enParo = false;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblNewLabel = new JLabel("¿Se encuentra ahora mismo en Paro?");
			lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
			contentPanel.add(lblNewLabel);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			GridBagLayout gbl_buttonPane = new GridBagLayout();
			gbl_buttonPane.columnWidths = new int[]{45, 63, 0};
			gbl_buttonPane.rowHeights = new int[]{21, 0};
			gbl_buttonPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
			gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			buttonPane.setLayout(gbl_buttonPane);
			{
				JButton botonSi = new JButton("Sí");
				botonSi.addActionListener(e ->
				{
					enParo = true;
					dispose();
				});
				botonSi.setBackground(new Color(0, 0, 0));
				botonSi.setForeground(new Color(255, 255, 255));
				botonSi.setFont(new Font("Tahoma", Font.PLAIN, 14));
				GridBagConstraints gbc_botonSi = new GridBagConstraints();
				gbc_botonSi.anchor = GridBagConstraints.EAST;
				gbc_botonSi.insets = new Insets(0, 0, 0, 5);
				gbc_botonSi.gridx = 0;
				gbc_botonSi.gridy = 0;
				buttonPane.add(botonSi, gbc_botonSi);
				getRootPane().setDefaultButton(botonSi);
			}
			{
				JButton botonNo = new JButton("No");
				botonNo.addActionListener(e -> 
				{
					dispose();
				});
				botonNo.setForeground(new Color(255, 255, 255));
				botonNo.setFont(new Font("Tahoma", Font.PLAIN, 14));
				botonNo.setBackground(new Color(0, 0, 0));
				GridBagConstraints gbc_botonNo = new GridBagConstraints();
				gbc_botonNo.anchor = GridBagConstraints.WEST;
				gbc_botonNo.gridx = 1;
				gbc_botonNo.gridy = 0;
				buttonPane.add(botonNo, gbc_botonNo);
			}
		}
	}
	
	public boolean showDialog()
	{
		this.setVisible(true);
		return enParo;
	}
}
