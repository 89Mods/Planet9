package tholin.planet9.gens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;

import theGhastModding.planetGen.utils.CraterGenerator.CraterConfig;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class CraterConfigDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6067826123509739532L;
	
	private CraterConfig config;
	private JSpinner spinner,spinner_1,spinner_2,spinner_3,spinner_4,spinner_5,spinner_6,spinner_7;
	
	public CraterConfigDialog(String name, CraterConfig config) {
		super(GenMain.frame, name);
		this.config = config;
		setModal(false);
		setPreferredSize(new Dimension(450, 290));
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel(name);
		label.setBounds(12, 12, 396, 15);
		getContentPane().add(label);
		
		JLabel lblPerturbStrength = new JLabel("Perturb strength:");
		lblPerturbStrength.setBounds(12, 40, 174, 15);
		getContentPane().add(lblPerturbStrength);
		
		JLabel lblPerturbScale = new JLabel("Perturb scale:");
		lblPerturbScale.setBounds(12, 67, 174, 15);
		getContentPane().add(lblPerturbScale);
		
		JLabel lblShapeP = new JLabel("Shape P1:");
		lblShapeP.setBounds(12, 94, 174, 15);
		getContentPane().add(lblShapeP);
		
		JLabel lblShapeP_1 = new JLabel("Shape P2:");
		lblShapeP_1.setBounds(12, 121, 174, 15);
		getContentPane().add(lblShapeP_1);
		
		JLabel lblPeakPerturbStrength = new JLabel("Peak perturb strength:");
		lblPeakPerturbStrength.setBounds(12, 148, 174, 15);
		getContentPane().add(lblPeakPerturbStrength);
		
		JLabel lblPeakPerturbScale = new JLabel("Peak perturb scale:");
		lblPeakPerturbScale.setBounds(12, 175, 174, 15);
		getContentPane().add(lblPeakPerturbScale);
		
		JLabel lblRingFunctMul = new JLabel("Ring funct. mul:");
		lblRingFunctMul.setBounds(12, 202, 174, 15);
		getContentPane().add(lblRingFunctMul);
		
		JLabel lblFloorHeight = new JLabel("Floor height:");
		lblFloorHeight.setBounds(12, 229, 174, 15);
		getContentPane().add(lblFloorHeight);
		
		JButton btnClose = new JButton("Close");
		btnClose.setBounds(12, 256, 117, 25);
		getContentPane().add(btnClose);
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(config.perturbStrength, 0, 1, 0.1));
		spinner.setBounds(238, 38, 58, 20);
		getContentPane().add(spinner);
		
		spinner_1 = new JSpinner();
		spinner_1.setModel(new SpinnerNumberModel(config.perturbScale, 0, 64, 0.1));
		spinner_1.setBounds(238, 65, 58, 20);
		getContentPane().add(spinner_1);
		
		spinner_2 = new JSpinner();
		spinner_2.setModel(new SpinnerNumberModel(config.p1, 0, 64, 0.1));
		spinner_2.setBounds(238, 92, 58, 20);
		getContentPane().add(spinner_2);
		
		spinner_3 = new JSpinner();
		spinner_3.setModel(new SpinnerNumberModel(config.p2, 0, 64, 0.1));
		spinner_3.setBounds(238, 119, 58, 20);
		getContentPane().add(spinner_3);
		
		spinner_4 = new JSpinner();
		spinner_4.setModel(new SpinnerNumberModel(config.ejectaPerturbStrength, 0, 1, 0.1));
		spinner_4.setBounds(238, 146, 58, 20);
		getContentPane().add(spinner_4);
		
		spinner_5 = new JSpinner();
		spinner_5.setModel(new SpinnerNumberModel(config.ejectaPerturbScale, 0, 64, 0.1));
		spinner_5.setBounds(238, 173, 58, 20);
		getContentPane().add(spinner_5);
		
		spinner_6 = new JSpinner();
		spinner_6.setModel(new SpinnerNumberModel(config.ringFunctMul, 0, 2, 0.1));
		spinner_6.setBounds(238, 200, 58, 20);
		getContentPane().add(spinner_6);
		
		spinner_7 = new JSpinner();
		spinner_7.setModel(new SpinnerNumberModel(config.floorHeight, -10, 2, 0.1));
		spinner_7.setBounds(238, 227, 58, 20);
		getContentPane().add(spinner_7);
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setVisible(false);
	}
	
	public void updateValues(CraterConfig conf) {
		this.config = conf;
		spinner.setValue(config.ejectaPerturbStrength);
		spinner_1.setValue(config.ejectaPerturbScale);
		spinner_2.setValue(conf.p1);
		spinner_3.setValue(conf.p2);
		spinner_4.setValue(conf.ejectaPerturbStrength);
		spinner_5.setValue(conf.ejectaPerturbScale);
		spinner_6.setValue(conf.ringFunctMul);
		spinner_7.setValue(conf.floorHeight);
	}
	
	public void apply() {
		config.ejectaPerturbStrength = (Double)spinner.getValue();
		config.ejectaPerturbScale = (Double)spinner_1.getValue();
		config.p1 = (Double)spinner_2.getValue();
		config.p2 = (Double)spinner_3.getValue();
		config.ejectaPerturbStrength = (Double)spinner_4.getValue();
		config.ejectaPerturbScale = (Double)spinner_5.getValue();
		config.ringFunctMul = (Double)spinner_6.getValue();
		config.floorHeight = (Double)spinner_7.getValue();
	}
	
}