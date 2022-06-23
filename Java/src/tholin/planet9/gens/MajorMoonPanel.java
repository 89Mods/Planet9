package tholin.planet9.gens;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tholin.planetGen.generators.GeneratorResult;
import tholin.planetGen.generators.GraymoonGen;
import tholin.planetGen.utils.MapUtils;

import java.awt.Color;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;

public class MajorMoonPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7213743980514472403L;
	private GraymoonGen.GraymoonGenSettings settings;
	private boolean hasPreview = false;
	private QaDPlanetRenderer pRenderer;
	private BufferedImage img;
	private long seed;
	
	private JTextField textField;
	private JTextField textField_1;
	private JButton btnApply;
	private JPanel previewPanel;
	private JCheckBox chckbxHasMaria;
	private JLabel lblMariaCraterCount;
	private JSpinner spinner,spinner_1,spinner_2,spinner_3,spinner_4,spinner_5,spinner_6,spinner_7,spinner_8,spinner_9;
	private JPanel panel_1,panel_2,panel_3,panel_4;
	private NoiseConfigDialog[] nConfigDialogs;
	private CraterConfigDialog[] craterConfigDialogs;
	
	@SuppressWarnings("serial")
	public MajorMoonPanel(String name, long seed1, int radius, GraymoonGen.GraymoonGenSettings settingsIn, GenPanel parent, int idx) {
		super();
		this.seed = seed1;
		this.settings = settingsIn;
		setPreferredSize(new Dimension(616, 1000));
		setLayout(null);
		pRenderer = new QaDPlanetRenderer(radius / 1000.0);
		
		img = new BufferedImage(221, 221, BufferedImage.TYPE_INT_RGB);
		previewPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
			}
		};
		previewPanel.setBorder(new LineBorder(new Color(99, 130, 191), 1));
		previewPanel.setBounds(383, 34, 221, 221);
		add(previewPanel);
		
		JLabel lblPreview = new JLabel("Preview");
		lblPreview.setFont(new Font("Dialog", Font.BOLD, 15));
		lblPreview.setBounds(383, 12, 96, 18);
		add(lblPreview);
		
		JLabel lblBodyname = new JLabel(name);
		lblBodyname.setFont(new Font("Dialog", Font.BOLD, 15));
		lblBodyname.setBounds(12, 14, 106, 17);
		add(lblBodyname);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 267, 592, 2);
		add(separator);
		
		JCheckBox chckbxAdvancedSettings = new JCheckBox("Advanced Settings");
		chckbxAdvancedSettings.setBounds(8, 243, 173, 23);
		add(chckbxAdvancedSettings);
		
		JLabel lblSeed = new JLabel("Name:");
		lblSeed.setBounds(12, 53, 70, 15);
		add(lblSeed);
		
		textField = new JTextField();
		textField.setText(name);
		textField.setBounds(100, 51, 271, 19);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblSeed_1 = new JLabel("Seed:");
		lblSeed_1.setBounds(12, 81, 70, 15);
		add(lblSeed_1);
		
		textField_1 = new JTextField();
		textField_1.setText(Long.toString(seed));
		textField_1.setBounds(100, 79, 271, 19);
		add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblTextureResolution = new JLabel("Texture resolution:");
		lblTextureResolution.setBounds(12, 108, 151, 15);
		add(lblTextureResolution);
		
		JLabel lblX = new JLabel("x " + Integer.toString(settings.width / 2));
		lblX.setBounds(236, 108, 70, 15);
		add(lblX);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(8192, 5120, 655360, 2));
		spinner.setBounds(170, 106, 58, 20);
		spinner.setValue(settings.width);
		add(spinner);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int num = (Integer)spinner.getValue();
				lblX.setText("x " + Integer.toString(num / 2));
			}
		});
		
		chckbxHasMaria = new JCheckBox("Has Maria");
		chckbxHasMaria.setSelected(settings.mariaLatitudeRange > 0);
		chckbxHasMaria.setEnabled(false);
		chckbxHasMaria.setBounds(12, 293, 129, 23);
		add(chckbxHasMaria);
		
		lblMariaCraterCount = new JLabel("Maria crater count:");
		lblMariaCraterCount.setEnabled(false);
		lblMariaCraterCount.setBounds(142, 297, 162, 15);
		add(lblMariaCraterCount);
		
		spinner_1 = new JSpinner();
		spinner_1.setEnabled(false);
		spinner_1.setModel(new SpinnerNumberModel(6, 0, 32, 1));
		spinner_1.setValue(settings.mariaCraterCount);
		spinner_1.setBounds(310, 295, 61, 20);
		add(spinner_1);
		
		JLabel lblRegularCraterCount = new JLabel("Regular crater count:");
		lblRegularCraterCount.setEnabled(false);
		lblRegularCraterCount.setBounds(12, 324, 169, 15);
		add(lblRegularCraterCount);
		
		spinner_2 = new JSpinner();
		spinner_2.setModel(new SpinnerNumberModel(8192, 1, 65536, 1));
		spinner_2.setValue(settings.smallCraterCount);
		spinner_2.setEnabled(false);
		spinner_2.setBounds(310, 322, 61, 20);
		add(spinner_2);
		
		JLabel lblHugeCraterCount = new JLabel("Huge crater count:");
		lblHugeCraterCount.setEnabled(false);
		lblHugeCraterCount.setBounds(12, 351, 220, 15);
		add(lblHugeCraterCount);
		
		spinner_3 = new JSpinner();
		spinner_3.setEnabled(false);
		spinner_3.setModel(new SpinnerNumberModel(3, 0, 32, 1));
		spinner_3.setValue(settings.hugeCraterCount);
		spinner_3.setBounds(310, 349, 61, 20);
		add(spinner_3);
		
		JLabel lblCraterMaxSize = new JLabel("Crater size range:");
		lblCraterMaxSize.setEnabled(false);
		lblCraterMaxSize.setBounds(12, 378, 151, 15);
		add(lblCraterMaxSize);
		
		spinner_4 = new JSpinner();
		spinner_4.setEnabled(false);
		spinner_4.setModel(new SpinnerNumberModel(16.0, 4.0, 8192.0, 0.1));
		spinner_4.setValue(settings.craterMaxsize);
		spinner_4.setBounds(400, 376, 61, 20);
		add(spinner_4);
		
		spinner_5 = new JSpinner();
		spinner_5.setEnabled(false);
		spinner_5.setModel(new SpinnerNumberModel(0.5, 0.1, 4096.0, 0.1));
		spinner_5.setValue(settings.craterMinsize);
		spinner_5.setBounds(310, 376, 61, 20);
		add(spinner_5);
		
		JLabel label = new JLabel("-");
		label.setEnabled(false);
		label.setBounds(383, 378, 12, 15);
		add(label);
		
		JLabel lblMariaCraterSize = new JLabel("Maria crater size range:");
		lblMariaCraterSize.setEnabled(false);
		lblMariaCraterSize.setBounds(12, 405, 203, 15);
		add(lblMariaCraterSize);
		
		spinner_6 = new JSpinner(new SpinnerNumberModel(8.0, 4.0, 8192.0, 0.1));
		spinner_6.setValue(settings.mariaCraterMaxsize);
		spinner_6.setEnabled(false);
		spinner_6.setBounds(400, 403, 61, 20);
		add(spinner_6);
		
		JLabel label_1 = new JLabel("-");
		label_1.setEnabled(false);
		label_1.setBounds(383, 405, 12, 15);
		add(label_1);
		
		spinner_7 = new JSpinner(new SpinnerNumberModel(0.5, 0.1, 4096.0, 0.1));
		spinner_7.setValue(settings.mariaCraterMinsize);
		spinner_7.setEnabled(false);
		spinner_7.setBounds(310, 403, 61, 20);
		add(spinner_7);
		
		JLabel lblFlattenedCraterFade = new JLabel("Flattened crater fade range:");
		lblFlattenedCraterFade.setEnabled(false);
		lblFlattenedCraterFade.setBounds(12, 432, 260, 15);
		add(lblFlattenedCraterFade);
		
		spinner_8 = new JSpinner();
		spinner_8.setEnabled(false);
		spinner_8.setModel(new SpinnerNumberModel(14.0, 4.0, 256.0, 1.0));
		spinner_8.setValue(settings.craterFlattenedStart);
		spinner_8.setBounds(310, 430, 61, 20);
		add(spinner_8);
		
		JLabel label_2 = new JLabel("-");
		label_2.setEnabled(false);
		label_2.setBounds(383, 432, 12, 15);
		add(label_2);
		
		spinner_9 = new JSpinner();
		spinner_9.setEnabled(false);
		spinner_9.setModel(new SpinnerNumberModel(28.0, 8.0, 256.0, 1.0));
		spinner_9.setValue(settings.craterFlattenedEnd);
		spinner_9.setBounds(400, 430, 61, 20);
		add(spinner_9);
		
		JLabel lblNoisemapConfiguration = new JLabel("Noisemap configuration (click to configure)");
		lblNoisemapConfiguration.setEnabled(false);
		lblNoisemapConfiguration.setBounds(12, 464, 324, 15);
		add(lblNoisemapConfiguration);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 485, 592, 2);
		add(separator_1);
		
		nConfigDialogs = new NoiseConfigDialog[9];
		JButton btnMariaNoise = new JButton("Maria Noise");
		btnMariaNoise.setEnabled(false);
		btnMariaNoise.setBounds(12, 491, 260, 25);
		add(btnMariaNoise);
		nConfigDialogs[0] = new NoiseConfigDialog("Maria Noise", settings.mariaNoise);
		btnMariaNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[0].setVisible(true);
				nConfigDialogs[0].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnMountainBiomeNoise = new JButton("Mountain biome noise");
		btnMountainBiomeNoise.setEnabled(false);
		btnMountainBiomeNoise.setBounds(284, 491, 260, 25);
		add(btnMountainBiomeNoise);
		nConfigDialogs[1] = new NoiseConfigDialog("Mountain biome noise", settings.mountainNoise);
		btnMountainBiomeNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[1].setVisible(true);
				nConfigDialogs[1].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnGroundNoiselarge = new JButton("Ground noise (large detail)");
		btnGroundNoiselarge.setEnabled(false);
		btnGroundNoiselarge.setBounds(12, 528, 260, 25);
		add(btnGroundNoiselarge);
		nConfigDialogs[2] = new NoiseConfigDialog("Ground noise (large detail)", settings.groundNoiseLargeDetail);
		btnGroundNoiselarge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[2].setVisible(true);
				nConfigDialogs[2].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnGroundNoisemedium = new JButton("Ground noise (medium detail)");
		btnGroundNoisemedium.setEnabled(false);
		btnGroundNoisemedium.setBounds(284, 528, 260, 25);
		add(btnGroundNoisemedium);
		nConfigDialogs[3] = new NoiseConfigDialog("Ground noise (medium detail)", settings.groundNoiseMediumDetail);
		btnGroundNoisemedium.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[3].setVisible(true);
				nConfigDialogs[3].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnGroundNoisesmall = new JButton("Ground noise (small detail)");
		btnGroundNoisesmall.setEnabled(false);
		btnGroundNoisesmall.setBounds(12, 565, 260, 25);
		add(btnGroundNoisesmall);
		nConfigDialogs[4] = new NoiseConfigDialog("Ground noise (small detail)", settings.groundNoiseSmallDetail);
		btnGroundNoisesmall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[4].setVisible(true);
				nConfigDialogs[4].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnMountainNoise = new JButton("Mountain noise");
		btnMountainNoise.setEnabled(false);
		btnMountainNoise.setBounds(284, 565, 260, 25);
		add(btnMountainNoise);
		nConfigDialogs[5] = new NoiseConfigDialog("Mountain noise", settings.mountainsNoise);
		btnMountainNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[5].setVisible(true);
				nConfigDialogs[5].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnCraterPeakNoise = new JButton("Crater peak noise");
		btnCraterPeakNoise.setEnabled(false);
		btnCraterPeakNoise.setBounds(12, 602, 260, 25);
		add(btnCraterPeakNoise);
		nConfigDialogs[6] = new NoiseConfigDialog("Crater peak noise", settings.craterMountainsNoise);
		btnCraterPeakNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[6].setVisible(true);
				nConfigDialogs[6].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnColorNoise = new JButton("Color noise");
		btnColorNoise.setEnabled(false);
		btnColorNoise.setBounds(284, 602, 260, 25);
		add(btnColorNoise);
		nConfigDialogs[7] = new NoiseConfigDialog("Color noise", settings.colorNoise);
		btnColorNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[7].setVisible(true);
				nConfigDialogs[7].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JButton btnSecondaryColorNoise = new JButton("Secondary color noise");
		btnSecondaryColorNoise.setEnabled(false);
		btnSecondaryColorNoise.setBounds(12, 639, 260, 25);
		add(btnSecondaryColorNoise);
		nConfigDialogs[8] = new NoiseConfigDialog("Secondary color noise", settings.secondColorNoise);
		btnSecondaryColorNoise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nConfigDialogs[8].setVisible(true);
				nConfigDialogs[8].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		JLabel lblColorConfiguration = new JLabel("Color configuration");
		lblColorConfiguration.setEnabled(false);
		lblColorConfiguration.setBounds(12, 797, 294, 15);
		add(lblColorConfiguration);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(12, 817, 592, 2);
		add(separator_2);
		
		panel_1 = new JPanel();
		panel_1.setBackground(RGB(settings.normalColor));
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(142, 824, 32, 32);
		add(panel_1);
		
		JLabel lblBaseColor = new JLabel("Base color:");
		lblBaseColor.setEnabled(false);
		lblBaseColor.setBounds(12, 833, 118, 15);
		add(lblBaseColor);
		
		panel_2 = new JPanel();
		panel_2.setBackground(RGB(settings.mountainsColor));
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(142, 868, 32, 32);
		add(panel_2);
		
		panel_3 = new JPanel();
		panel_3.setBackground(RGB(settings.mariasColor));
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBounds(142, 912, 32, 32);
		add(panel_3);
		
		panel_4 = new JPanel();
		panel_4.setBackground(RGB(settings.secondaryColor));
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBounds(142, 956, 32, 32);
		add(panel_4);
		
		JLabel lblMountainsColor = new JLabel("Mountains:");
		lblMountainsColor.setEnabled(false);
		lblMountainsColor.setBounds(12, 879, 118, 15);
		add(lblMountainsColor);
		
		JLabel lblMaria = new JLabel("Maria:");
		lblMaria.setEnabled(false);
		lblMaria.setBounds(12, 923, 70, 15);
		add(lblMaria);
		
		JLabel lblSecondary = new JLabel("Secondary:");
		lblSecondary.setEnabled(false);
		lblSecondary.setBounds(12, 966, 106, 15);
		add(lblSecondary);
		
		JButton btnChange = new JButton("Change");
		btnChange.setEnabled(false);
		btnChange.setBounds(186, 831, 106, 25);
		add(btnChange);
		btnChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(GenMain.frame, "Choose color", panel_1.getBackground());
				if(c != null) panel_1.setBackground(c);
			}
		});
		
		JButton button = new JButton("Change");
		button.setEnabled(false);
		button.setBounds(186, 875, 106, 25);
		add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(GenMain.frame, "Choose color", panel_2.getBackground());
				if(c != null) panel_2.setBackground(c);
			}
		});
		
		JButton button_1 = new JButton("Change");
		button_1.setEnabled(false);
		button_1.setBounds(186, 918, 106, 25);
		add(button_1);
		button_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(GenMain.frame, "Choose color", panel_3.getBackground());
				if(c != null) panel_3.setBackground(c);
			}
		});
		
		JButton button_2 = new JButton("Change");
		button_2.setEnabled(false);
		button_2.setBounds(186, 963, 106, 25);
		add(button_2);
		button_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(GenMain.frame, "Choose color", panel_4.getBackground());
				if(c != null) panel_4.setBackground(c);
			}
		});
		
		btnApply = new JButton("Apply");
		btnApply.setBounds(12, 156, 117, 25);
		add(btnApply);
		
		JLabel lblCraterConfiguration = new JLabel("Crater Configuration");
		lblCraterConfiguration.setEnabled(false);
		lblCraterConfiguration.setBounds(12, 676, 212, 15);
		add(lblCraterConfiguration);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setEnabled(false);
		separator_3.setBounds(12, 696, 592, 2);
		add(separator_3);
		
		craterConfigDialogs = new CraterConfigDialog[3];
		craterConfigDialogs[0] = new CraterConfigDialog("Bowl craters", settings.mainFeatureDist.bowlCraterConfig);
		JButton btnMainCraters = new JButton("Bowl craters");
		btnMainCraters.setEnabled(false);
		btnMainCraters.setBounds(12, 703, 260, 25);
		add(btnMainCraters);
		btnMainCraters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				craterConfigDialogs[0].setVisible(true);
				craterConfigDialogs[0].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		craterConfigDialogs[1] = new CraterConfigDialog("Flattened craters", settings.mainFeatureDist.flattenedCraterConfig);
		JButton btnFlattenedCraters = new JButton("Flattened craters");
		btnFlattenedCraters.setEnabled(false);
		btnFlattenedCraters.setBounds(284, 703, 260, 25);
		add(btnFlattenedCraters);
		btnFlattenedCraters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				craterConfigDialogs[1].setVisible(true);
				craterConfigDialogs[1].setLocationRelativeTo(GenMain.frame);
			}
		});
		
		craterConfigDialogs[2] = new CraterConfigDialog("Maria craters", settings.mariaFeatureDist.bowlCraterConfig);
		JButton btnMariaCraters = new JButton("Maria craters");
		btnMariaCraters.setEnabled(false);
		btnMariaCraters.setBounds(12, 740, 260, 25);
		add(btnMariaCraters);
		btnMariaCraters.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				craterConfigDialogs[2].setVisible(true);
				craterConfigDialogs[2].setLocationRelativeTo(GenMain.frame);
			}
		});
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int width = (Integer)spinner.getValue();
				settings.width = width;
				settings.height = width / 2;
				long newSeed = 0L;
				try {
					newSeed = Long.parseLong(textField_1.getText());
				}catch(Exception e) {
					e.printStackTrace();
					newSeed = textField_1.getText().hashCode();
				}
				if(newSeed != seed) {
					if(chckbxAdvancedSettings.isSelected()) {
						int res = JOptionPane.showConfirmDialog(GenMain.frame, "Warning: you have changed the configuration seed. Proceeding with a new seed will re-generate the configuration settings randomly, wiping any changes you may have made in the advanced settings. Are you sure you want to continue?\n(Pressing ’No’ will change the seed back and keep you changes to the advanced settings)", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if(res != 0) {
							textField_1.setText(Long.toString(seed));
							return;
						}
					}
					seed = newSeed;
					parent.triggerPreviewRegen(idx, false);
					return;
				}
				settings.normalColor = MapUtils.RGB(panel_1.getBackground());
				settings.mountainsColor = MapUtils.RGB(panel_2.getBackground());
				settings.mariasColor = MapUtils.RGB(panel_3.getBackground());
				settings.secondaryColor = MapUtils.RGB(panel_4.getBackground());
				if(chckbxHasMaria.isSelected()) {
					settings.mariaLongitudeRange = 181;
					settings.mariaLatitudeRange = 91;
					settings.mariaCraterCount = (Integer)spinner_1.getValue();
				}else {
					settings.mariaLongitudeRange = -181;
					settings.mariaLatitudeRange = -91;
					settings.mariaCraterCount = 0;
				}
				settings.smallCraterCount = (Integer)spinner_2.getValue();
				settings.hugeCraterCount = (Integer)spinner_3.getValue();
				settings.craterMinsize = (Double)spinner_5.getValue();
				settings.craterMaxsize = (Double)spinner_4.getValue();
				settings.mariaCraterMinsize = (Double)spinner_7.getValue();
				settings.mariaCraterMaxsize = (Double)spinner_6.getValue();
				settings.craterFlattenedStart = (Integer)spinner_8.getValue();
				settings.craterFlattenedEnd = (Integer)spinner_9.getValue();
				for(NoiseConfigDialog diag:nConfigDialogs) {
					diag.apply();
				}
				for(CraterConfigDialog diag:craterConfigDialogs) {
					diag.apply();
				}
				
				parent.triggerPreviewRegen(idx, false);
			}
		});
		
		chckbxAdvancedSettings.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean b = chckbxAdvancedSettings.isSelected();
				button.setEnabled(b);
				button_1.setEnabled(b);
				button_2.setEnabled(b);
				lblSecondary.setEnabled(b);
				lblMaria.setEnabled(b);
				lblMountainsColor.setEnabled(b);
				btnChange.setEnabled(b);
				lblBaseColor.setEnabled(b);
				panel_1.setEnabled(b);
				panel_2.setEnabled(b);
				panel_3.setEnabled(b);
				panel_4.setEnabled(b);
				btnMariaNoise.setEnabled(b);
				btnMountainBiomeNoise.setEnabled(b);
				btnGroundNoiselarge.setEnabled(b);
				btnGroundNoisemedium.setEnabled(b);
				btnGroundNoisesmall.setEnabled(b);
				btnMountainNoise.setEnabled(b);
				btnCraterPeakNoise.setEnabled(b);
				btnColorNoise.setEnabled(b);
				btnSecondaryColorNoise.setEnabled(b);
				lblColorConfiguration.setEnabled(b);
				lblNoisemapConfiguration.setEnabled(b);
				spinner_9.setEnabled(b);
				label_2.setEnabled(b);
				spinner_8.setEnabled(b);
				lblFlattenedCraterFade.setEnabled(b);
				spinner_7.setEnabled(b);
				label_1.setEnabled(b);
				spinner_6.setEnabled(b);
				lblMariaCraterSize.setEnabled(b);
				label.setEnabled(b);
				spinner_5.setEnabled(b);
				spinner_4.setEnabled(b);
				lblCraterMaxSize.setEnabled(b);
				spinner_3.setEnabled(b);
				lblHugeCraterCount.setEnabled(b);
				spinner_2.setEnabled(b);
				lblRegularCraterCount.setEnabled(b);
				spinner_1.setEnabled(b);
				lblMariaCraterCount.setEnabled(b);
				chckbxHasMaria.setEnabled(b);
				btnMainCraters.setEnabled(b);
				btnMariaCraters.setEnabled(b);
				btnFlattenedCraters.setEnabled(b);
				lblCraterConfiguration.setEnabled(b);
				separator_3.setEnabled(b);
				
			}
		});
	}
	
	private Color RGB(double[] d) {
		return new Color((int)(d[0] * 255.0), (int)(d[1] * 255.0), (int)(d[2] * 255.0));
	}
	
	public boolean hasPreview() {
		return this.hasPreview;
	}
	
	public void updatePreviews(GeneratorResult res) throws Exception {
		this.hasPreview = true;
		pRenderer.loadData(res, 10.0);
		pRenderer.render(img);
		Graphics2D g = (Graphics2D)previewPanel.getGraphics();
		g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
	}
	
	public void updateAdvancedSettings(GraymoonGen.GraymoonGenSettings settings) {
		this.settings = settings;
		spinner.setValue(settings.width);;
		chckbxHasMaria.setSelected(settings.mariaLatitudeRange > 0);
		lblMariaCraterCount.setEnabled(false);
		spinner_1.setValue(settings.mariaCraterCount);
		spinner_2.setValue(settings.smallCraterCount);
		spinner_3.setValue(settings.hugeCraterCount);
		spinner_5.setValue(settings.craterMinsize);
		spinner_4.setValue(settings.craterMaxsize);
		spinner_7.setValue(settings.mariaCraterMinsize);
		spinner_6.setValue(settings.mariaCraterMaxsize);
		spinner_8.setValue(settings.craterFlattenedStart);
		spinner_9.setValue(settings.craterFlattenedEnd);
		panel_1.setBackground(RGB(settings.normalColor));
		panel_2.setBackground(RGB(settings.mountainsColor));
		panel_3.setBackground(RGB(settings.mariasColor));
		panel_4.setBackground(RGB(settings.secondaryColor));
		craterConfigDialogs[0].updateValues(settings.mainFeatureDist.bowlCraterConfig);
		craterConfigDialogs[1].updateValues(settings.mainFeatureDist.flattenedCraterConfig);
		craterConfigDialogs[2].updateValues(settings.mariaFeatureDist.bowlCraterConfig);
		nConfigDialogs[0].updateValues(settings.mariaNoise);
		nConfigDialogs[1].updateValues(settings.mountainNoise);
		nConfigDialogs[2].updateValues(settings.groundNoiseLargeDetail);
		nConfigDialogs[3].updateValues(settings.groundNoiseMediumDetail);
		nConfigDialogs[4].updateValues(settings.groundNoiseSmallDetail);
		nConfigDialogs[5].updateValues(settings.mountainsNoise);
		nConfigDialogs[6].updateValues(settings.craterMountainsNoise);
		nConfigDialogs[7].updateValues(settings.colorNoise);
		nConfigDialogs[8].updateValues(settings.secondColorNoise);
	}
	
	public void setApplyEnabled(boolean b) {
		btnApply.setEnabled(b);
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public String getChangedName() {
		return textField.getText();
	}
	
	public GraymoonGen.GraymoonGenSettings getChangedConfig(){
		return this.settings;
	}
}