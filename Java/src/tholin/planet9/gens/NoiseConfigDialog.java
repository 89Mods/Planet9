package tholin.planet9.gens;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tholin.planetGen.noise.NoiseConfig;
import tholin.planetGen.noise.NoiseFunction;
import tholin.planetGen.noise.OctaveNoise2D;
import tholin.planetGen.noise.OctaveNoise3D;
import tholin.planetGen.noise.OctaveWorley;
import tholin.planetGen.noise.PerlinNoise2D;
import tholin.planetGen.noise.PerlinNoise3D;
import tholin.planetGen.noise.WorleyNoise;

import javax.swing.JButton;

public class NoiseConfigDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8096373473521014059L;
	private JCheckBox chckbxIsRidged;
	private JSpinner spinner,spinner_1,spinner_2,spinner_3,spinner_4,spinner_5,spinner_6,spinner_7;
	private JCheckBox chckbxLock;
	private NoiseConfig noise;
	
	public NoiseConfigDialog(String name, NoiseConfig noise) {
		super(GenMain.frame, name);
		this.noise = noise;
		setModal(false);
		setPreferredSize(new Dimension(450, 335));
		getContentPane().setLayout(null);
		
		JLabel lblName = new JLabel(name);
		lblName.setBounds(12, 12, 396, 15);
		getContentPane().add(lblName);
		
		chckbxIsRidged = new JCheckBox("Is ridged");
		chckbxIsRidged.setBounds(12, 134, 99, 23);
		chckbxIsRidged.setSelected(noise.ridged);
		getContentPane().add(chckbxIsRidged);
		
		JLabel lblItIsRecommended = new JLabel("It is recommended to lower the");
		lblItIsRecommended.setForeground(Color.RED);
		lblItIsRecommended.setBounds(139, 126, 293, 15);
		getContentPane().add(lblItIsRecommended);
		lblItIsRecommended.setVisible(false);
		
		JLabel lblNoiseOffsetOn = new JLabel("noise offset on ridged noise.");
		lblNoiseOffsetOn.setForeground(Color.RED);
		lblNoiseOffsetOn.setBounds(139, 142, 226, 15);
		getContentPane().add(lblNoiseOffsetOn);
		lblNoiseOffsetOn.setVisible(false);
		
		JLabel lblNoiseStrength = new JLabel("Noise strength");
		lblNoiseStrength.setBounds(16, 176, 126, 15);
		getContentPane().add(lblNoiseStrength);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(noise.noiseStrength, 0.0, 10.0, 0.1));
		spinner.setBounds(171, 174, 58, 20);
		getContentPane().add(spinner);
		
		chckbxLock = new JCheckBox("Lock");
		chckbxLock.setSelected(Math.abs(noise.noiseLatitudeScale - noise.noiseLongitudeScale) < 0.0001);
		chckbxLock.setBounds(363, 203, 69, 23);
		getContentPane().add(chckbxLock);
		chckbxLock.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				spinner_2.setEnabled(!chckbxLock.isSelected());
				if(chckbxLock.isSelected()) {
					spinner_2.setValue(spinner_1.getValue());
				}
			}
		});
		
		spinner_1 = new JSpinner();
		spinner_1.setModel(new SpinnerNumberModel(noise.noiseLatitudeScale, 0.0001, Double.MAX_VALUE, 0.1));
		spinner_1.setBounds(171, 205, 58, 20);
		getContentPane().add(spinner_1);
		spinner_1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(chckbxLock.isSelected()) {
					spinner_2.setValue(spinner_1.getValue());
				}
			}
		});
		
		JLabel lblNosieScale = new JLabel("Noise scale");
		lblNosieScale.setBounds(16, 207, 95, 15);
		getContentPane().add(lblNosieScale);
		
		JLabel lblNewLabel = new JLabel("Noise offset");
		lblNewLabel.setBounds(16, 239, 95, 15);
		getContentPane().add(lblNewLabel);
		
		JLabel lblLat = new JLabel("Lat:");
		lblLat.setBounds(137, 207, 39, 15);
		getContentPane().add(lblLat);
		
		JLabel lblLon = new JLabel("Lon:");
		lblLon.setBounds(247, 207, 39, 15);
		getContentPane().add(lblLon);
		
		spinner_2 = new JSpinner();//Crater size range broke?
		spinner_2.setModel(new SpinnerNumberModel(noise.noiseLongitudeScale, 0.0001, Double.MAX_VALUE, 0.1));
		spinner_2.setBounds(288, 205, 58, 20);
		getContentPane().add(spinner_2);
		
		spinner_3 = new JSpinner();
		spinner_3.setModel(new SpinnerNumberModel(noise.noiseOffset, -16.0, 16.0, 0.1));
		spinner_3.setBounds(171, 237, 58, 20);
		getContentPane().add(spinner_3);
		chckbxIsRidged.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(chckbxIsRidged.isSelected() && (Double)spinner_3.getValue() >= 0.2) {
					lblItIsRecommended.setVisible(true);
					lblNoiseOffsetOn.setVisible(true);
				}else {
					lblItIsRecommended.setVisible(false);
					lblNoiseOffsetOn.setVisible(false);
				}
			}
		});
		spinner_3.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(chckbxIsRidged.isSelected() && (Double)spinner_3.getValue() >= 0.2) {
					lblItIsRecommended.setVisible(true);
					lblNoiseOffsetOn.setVisible(true);
				}else {
					lblItIsRecommended.setVisible(false);
					lblNoiseOffsetOn.setVisible(false);
				}
			}
		});
		
		spinner_4 = new JSpinner();
		spinner_4.setModel(new SpinnerNumberModel(noise.distortStrength, 0.0, 1.0, 0.1));
		spinner_4.setBounds(171, 269, 58, 20);
		getContentPane().add(spinner_4);
		
		JLabel lblDistortStrength = new JLabel("Distort strength");
		lblDistortStrength.setBounds(16, 271, 126, 15);
		getContentPane().add(lblDistortStrength);
		
		JButton btnClose = new JButton("Close");
		btnClose.setBounds(16, 298, 117, 25);
		getContentPane().add(btnClose);
		
		JLabel lblOctaves = new JLabel("Octaves");
		lblOctaves.setBounds(12, 38, 70, 15);
		getContentPane().add(lblOctaves);
		
		spinner_5 = new JSpinner();
		spinner_5.setModel(new SpinnerNumberModel(getOctaves(noise.noise), 1, 64, 1));
		spinner_5.setBounds(171, 36, 58, 20);
		getContentPane().add(spinner_5);
		
		spinner_6 = new JSpinner();
		spinner_6.setModel(new SpinnerNumberModel(getPersistence(noise.noise), 0.1, 16.0, 0.1));
		spinner_6.setBounds(171, 68, 58, 20);
		getContentPane().add(spinner_6);
		
		JLabel lblPersistence = new JLabel("Persistence");
		lblPersistence.setBounds(12, 70, 95, 15);
		getContentPane().add(lblPersistence);
		
		spinner_7 = new JSpinner();
		spinner_7.setModel(new SpinnerNumberModel(getLacunarity(noise.noise), 0.1, 16.0, 0.1));
		spinner_7.setBounds(171, 100, 58, 20);
		getContentPane().add(spinner_7);
		
		JLabel lblLacunarity = new JLabel("Lacunarity");
		lblLacunarity.setBounds(12, 102, 99, 15);
		getContentPane().add(lblLacunarity);
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setVisible(false);
	}
	
	public void updateValues(NoiseConfig noise) {
		this.noise = noise;
		chckbxIsRidged.setSelected(noise.ridged);
		spinner.setValue(noise.noiseStrength);
		spinner_1.setValue(noise.noiseLatitudeScale);
		spinner_2.setValue(noise.noiseLatitudeScale);
		spinner_3.setValue(noise.noiseOffset);
		spinner_4.setValue(noise.distortStrength);
		chckbxLock.setSelected(Math.abs(noise.noiseLatitudeScale - noise.noiseLongitudeScale) < 0.0001);
		spinner_5.setValue(getOctaves(noise.noise));
		spinner_6.setValue(getPersistence(noise.noise));
		spinner_7.setValue(getLacunarity(noise.noise));
	}
	
	public void apply() {
		noise.ridged = chckbxIsRidged.isSelected();
		noise.noiseStrength = (Double)spinner.getValue();
		noise.noiseLatitudeScale = noise.noiseLongitudeScale = (Double)spinner_1.getValue();
		if(!chckbxLock.isSelected()) noise.noiseLongitudeScale = (Double)spinner_2.getValue();
		noise.noiseOffset = (Double)spinner_3.getValue();
		noise.distortStrength = (Double)spinner_4.getValue();
		if(((Integer)spinner_5.getValue()).intValue() != getOctaves(noise.noise) || Math.abs((Double)spinner_6.getValue() - getPersistence(noise.noise)) >= 0.0001 || Math.abs((Double)spinner_7.getValue() - getLacunarity(noise.noise)) >= 0.0001) {
			if((Integer)spinner_5.getValue() == 1) {
				if(noise.noise instanceof WorleyNoise || noise.noise instanceof OctaveWorley) {
					noise.noise = new WorleyNoise(noise.noise.getWidth(), noise.noise.getHeight(), noise.noise.getDepth());
				}else if(noise.noise instanceof PerlinNoise2D || noise.noise instanceof OctaveNoise2D) {
						noise.noise = new PerlinNoise2D(noise.noise.getWidth(), noise.noise.getHeight());
				}else {
					noise.noise = new PerlinNoise3D(noise.noise.getWidth(), noise.noise.getHeight(), noise.noise.getDepth());
				}
			}else {//Prevent changing noise function from changing literally the entire celestial body
				int octaves = (Integer)spinner_5.getValue();
				double persistence = (Double)spinner_6.getValue();
				double lacunarity = (Double)spinner_7.getValue();
				if(noise.noise instanceof WorleyNoise || noise.noise instanceof OctaveWorley) {
					noise.noise = new OctaveWorley(noise.noise.getWidth(), noise.noise.getHeight(), noise.noise.getDepth(), octaves, lacunarity, persistence);
				}else if(noise.noise instanceof PerlinNoise2D || noise.noise instanceof OctaveNoise2D) {
					noise.noise = new OctaveNoise2D(noise.noise.getWidth(), noise.noise.getHeight(), octaves, lacunarity, persistence);
				}else {
					noise.noise = new OctaveNoise3D(noise.noise.getWidth(), noise.noise.getHeight(), noise.noise.getDepth(), octaves, lacunarity, persistence);
				}
			}
		}
	}
	
	private int getOctaves(NoiseFunction noise) {
		if(noise instanceof PerlinNoise2D || noise instanceof PerlinNoise3D) return 1;
		if(noise instanceof OctaveNoise2D) return ((OctaveNoise2D)noise).getOctaves();
		if(noise instanceof OctaveNoise3D) return ((OctaveNoise3D)noise).getOctaves();
		if(noise instanceof OctaveWorley) return ((OctaveWorley)noise).getOctaves();
		return 0;
	}
	
	private double getLacunarity(NoiseFunction noise) {
		if(noise instanceof PerlinNoise2D || noise instanceof PerlinNoise3D) return 2.0;
		if(noise instanceof OctaveNoise2D) return ((OctaveNoise2D)noise).getLacunarity();
		if(noise instanceof OctaveNoise3D) return ((OctaveNoise3D)noise).getLacunarity();
		if(noise instanceof OctaveWorley) return ((OctaveWorley)noise).getLacunarity();
		return 0;
	}
	
	private double getPersistence(NoiseFunction noise) {
		if(noise instanceof PerlinNoise2D || noise instanceof PerlinNoise3D) return 0.5;
		if(noise instanceof OctaveNoise2D) return ((OctaveNoise2D)noise).getPersistence();
		if(noise instanceof OctaveNoise3D) return ((OctaveNoise3D)noise).getPersistence();
		if(noise instanceof OctaveWorley) return ((OctaveWorley)noise).getPersistence();
		return 0;
	}
}