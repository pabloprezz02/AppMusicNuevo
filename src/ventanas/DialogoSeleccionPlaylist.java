package ventanas;

import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Color;

public class DialogoSeleccionPlaylist extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldNombrePlaylist;
	private boolean aceptado = false;
	private JComboBox desplegablePlaylists;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		try {
//			DialogoSeleccionPlaylist dialog = new DialogoSeleccionPlaylist();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public String getTexto()
	{
		if(textFieldNombrePlaylist.getText() == null)
			return null;
		
		return textFieldNombrePlaylist.getText();
	}
	
	public String getSeleccionada()
	{
		if((String) desplegablePlaylists.getSelectedItem() == "")
		{
			return null;
		}
		return (String) desplegablePlaylists.getSelectedItem();
	}

	/**
	 * Create the dialog.
	 */
	public DialogoSeleccionPlaylist(JFrame padre, List<String> opcionesDesplegable) {
		super(padre, true);
		setBounds(100, 100, 450, 300);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{436, 0};
		gridBagLayout.rowHeights = new int[]{232, 31, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.fill = GridBagConstraints.BOTH;
		gbc_contentPanel.insets = new Insets(0, 0, 5, 0);
		gbc_contentPanel.gridx = 0;
		gbc_contentPanel.gridy = 0;
		getContentPane().add(contentPanel, gbc_contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{5, 0, 5, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			String[] opciones = new String[opcionesDesplegable.size() + 1];
			opciones[0] = "";
			for(int i = 1; i < opciones.length; i++)
			{
				opciones[i] = opcionesDesplegable.get(i - 1);
			}
			DefaultComboBoxModel<String> modelo = new DefaultComboBoxModel<String>(opciones);
			desplegablePlaylists = new JComboBox(modelo);
			desplegablePlaylists.setForeground(new Color(0, 255, 0));
			desplegablePlaylists.setBackground(new Color(0, 0, 0));
			desplegablePlaylists.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_desplegablePlaylists = new GridBagConstraints();
			gbc_desplegablePlaylists.insets = new Insets(0, 0, 5, 5);
			gbc_desplegablePlaylists.fill = GridBagConstraints.HORIZONTAL;
			gbc_desplegablePlaylists.gridx = 1;
			gbc_desplegablePlaylists.gridy = 0;
			contentPanel.add(desplegablePlaylists, gbc_desplegablePlaylists);
		}
		{
			textFieldNombrePlaylist = new JTextField();
			textFieldNombrePlaylist.setFont(new Font("Tahoma", Font.PLAIN, 14));
			GridBagConstraints gbc_textFieldNombrePlaylist = new GridBagConstraints();
			gbc_textFieldNombrePlaylist.insets = new Insets(0, 0, 0, 5);
			gbc_textFieldNombrePlaylist.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldNombrePlaylist.gridx = 1;
			gbc_textFieldNombrePlaylist.gridy = 1;
			contentPanel.add(textFieldNombrePlaylist, gbc_textFieldNombrePlaylist);
			textFieldNombrePlaylist.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 0;
			gbc_buttonPane.gridy = 1;
			getContentPane().add(buttonPane, gbc_buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.setBackground(new Color(0, 0, 0));
				okButton.setForeground(new Color(0, 255, 0));
				okButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
//				okButton.setActionCommand("OK");
				okButton.addActionListener(e -> {
					aceptado = true;
					dispose();
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
				cancelButton.setForeground(new Color(0, 255, 0));
				cancelButton.setBackground(new Color(0, 0, 0));
//				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(e -> {
					aceptado = false;
					dispose();
				});
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public boolean showDialog() {
		this.setVisible(true);
		return aceptado;
	}

}
