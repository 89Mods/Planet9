package tholin.planet9.gens;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.border.LineBorder;

import tholin.planetGen.generators.GeneratorResult;

import javax.swing.UIManager;

public class MinorMoonsPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2407957298219840897L;
	private JTextField textField;
	private JTextField textField_1;
	private JPanel[] previewPanels;
	private BufferedImage[] previewImgs;
	private QaDPlanetRenderer[] renderers;
	private JButton btnApply,btnGeneratePreviews;
	private long orbitsSeed,shapeSeed;
	
	public MinorMoonsPanel(SystemGenerator gen, GenPanel parent) {
		super();
		this.orbitsSeed = gen.getMinorMoonOrbitsSeed();
		this.shapeSeed = gen.getMinorMoonShapeSeed();
		setPreferredSize(new Dimension(616, 450));
		setLayout(null);
		
		JLabel lblMinorMoons = new JLabel("Minor moons");
		lblMinorMoons.setFont(new Font("Dialog", Font.BOLD, 15));
		lblMinorMoons.setBounds(12, 12, 121, 15);
		add(lblMinorMoons);
		
		JLabel lblMinorMoonsCurrently = new JLabel("Minor moons currently cannot be configured individually.");
		lblMinorMoonsCurrently.setBounds(12, 49, 592, 15);
		add(lblMinorMoonsCurrently);
		
		JLabel lblOnlySomeBasic = new JLabel("Only some basic options are available for now.");
		lblOnlySomeBasic.setBounds(12, 66, 389, 15);
		add(lblOnlySomeBasic);
		
		JLabel lblMinorMoonOrbits = new JLabel("Minor moon orbits seed:");
		lblMinorMoonOrbits.setBounds(12, 103, 195, 15);
		add(lblMinorMoonOrbits);
		
		textField = new JTextField();
		textField.setText(Long.toString(gen.getMinorMoonOrbitsSeed()));
		textField.setColumns(10);
		textField.setBounds(225, 101, 271, 19);
		add(textField);
		
		JLabel lblMinorMoonShapes = new JLabel("Minor moon shapes seed:");
		lblMinorMoonShapes.setBounds(12, 130, 195, 15);
		add(lblMinorMoonShapes);
		
		textField_1 = new JTextField();
		textField_1.setText(Long.toString(gen.getMinorMoonShapeSeed()));
		textField_1.setColumns(10);
		textField_1.setBounds(225, 128, 271, 19);
		add(textField_1);
		
		btnApply = new JButton("Apply");
		btnApply.setBounds(12, 157, 117, 25);
		add(btnApply);
		
		JLabel lblPreviews = new JLabel("Previews");
		lblPreviews.setBounds(12, 210, 70, 15);
		add(lblPreviews);
		
		btnGeneratePreviews = new JButton("Generate previews");
		btnGeneratePreviews.setBounds(100, 205, 169, 25);
		add(btnGeneratePreviews);
		btnGeneratePreviews.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.triggerPreviewRegen(0, true);
			}
		});
		
		JLabel lblDangerThisCould = new JLabel("Warning: this could take some time!");
		lblDangerThisCould.setForeground(Color.RED);
		lblDangerThisCould.setBounds(283, 210, 271, 15);
		add(lblDangerThisCould);
		
		int moonCount = gen.getMinorMoonCount();
		renderers = new QaDPlanetRenderer[moonCount];
		for(int i = 0; i < moonCount; i++) {
			renderers[i] = new QaDPlanetRenderer(gen.getMinorMoonKopConfig(i).radius / 1000.0);
		}
		previewImgs = new BufferedImage[moonCount];
		previewPanels = new JPanel[moonCount];
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(48 + moonCount * (150 + 24), 180));
		panel.setLayout(null);
		JScrollPane scroller = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setBounds(12, 237, 592, 200);
		scroller.setBorder(new LineBorder(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground")));
		add(scroller);
		for(int i = 0; i < moonCount; i++) {
			BufferedImage img = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
			previewImgs[i] = img;
			@SuppressWarnings("serial")
			JPanel previewPanel = new JPanel() {
				@Override
				public void paint(Graphics g) {
					g.drawImage(img, 0, 0, 150, 150, this);
				}
			};
			previewPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			previewPanel.setBounds(24 + i * (150 + 24), 15, 150, 150);
			panel.add(previewPanel);
			previewPanels[i] = previewPanel;
		}
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				long newSeed = 0L;
				try {
					newSeed = Long.parseLong(textField.getText());
				}catch(Exception e) {
					e.printStackTrace();
					newSeed = textField.getText().hashCode();
				}
				if(newSeed != orbitsSeed) {
					orbitsSeed = newSeed;
					gen.changeMinorMoonOrbitsSeed(orbitsSeed);
				}//Taurus
				newSeed = 0L;
				try {
					newSeed = Long.parseLong(textField_1.getText());
				}catch(Exception e) {
					e.printStackTrace();
					newSeed = textField_1.getText().hashCode();
				}
				if(newSeed != shapeSeed) {
					shapeSeed = newSeed;
					gen.changeMinorMoonShapeSeed(shapeSeed);
				}
			}
		});
	}
	
	public void setApplyAndRenderEnabled(boolean b) {
		btnApply.setEnabled(b);
		btnGeneratePreviews.setEnabled(b);
	}
	
	public void updatePreviews(int idx, GeneratorResult res) {
		renderers[idx].loadData(res, 10.0);
		renderers[idx].render(previewImgs[idx]);
		previewPanels[idx].repaint();
	}
}