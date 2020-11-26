package tholin.planet9.gens;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class PreviewDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6460880478440914884L;
	
	public PreviewDialog() {
		super(GenMain.frame, ".");
		setPreferredSize(new Dimension(300, 80));
		getContentPane().setLayout(null);
		
		JLabel lblGeneratingPreviewPlease = new JLabel("Generating preview, please wait");
		lblGeneratingPreviewPlease.setBounds(12, 12, 266, 15);
		getContentPane().add(lblGeneratingPreviewPlease);
		setModal(false);
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(false);
	}
	
}