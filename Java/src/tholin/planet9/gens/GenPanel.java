package tholin.planet9.gens;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import theGhastModding.planetGen.generators.GeneratorResult;

public class GenPanel extends JPanel {
	
	private static final long serialVersionUID = 4983719830087326537L;
	private SystemGenerator gen = null;
	private PreviewDialog previewDialog = new PreviewDialog();
	private int currentSelectedPane = -1;
	private PreviewGenThread genThread;
	private Thread genThreadThread;
	private JTabbedPane tabbedPane;
	private MajorMoonPanel p9Panel;
	private MajorMoonPanel[] majorMoonPanels;
	private MinorMoonsPanel minorMoonsPanel;
	
	public GenPanel() {
		super();
		this.gen = new SystemGenerator(6);
		this.gen.prepareInitialConfigs();
		this.genThread = new PreviewGenThread(this.gen);
		setPreferredSize(new Dimension(664, 664));
		setLayout(null);
		
		JLabel lblPlanet = new JLabel("Planet 9 - System generator tool");
		lblPlanet.setFont(new Font("Dialog", Font.BOLD, 15));
		lblPlanet.setBounds(17, 13, 280, 28);
		add(lblPlanet);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 53, 640, 599);
		add(tabbedPane);
		
		this.p9Panel = new MajorMoonPanel("Planet 9", gen.getP9Seed(), 1068000, gen.getP9Settings(), this, -1);
		JScrollPane scroller = new JScrollPane(this.p9Panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(620, 599));
		tabbedPane.addTab("Planet 9", scroller);
		
		this.majorMoonPanels = new MajorMoonPanel[5];
		MajorMoonPanel mmPanel3118 = new MajorMoonPanel("Temi", gen.getConfigSeed(0), gen.getMajorMoonRadius(0), gen.getConfig(0), this, 0);
		scroller = new JScrollPane(mmPanel3118, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(620, 599));
		tabbedPane.addTab("Temi", scroller);
		majorMoonPanels[0] = mmPanel3118;
		
		MajorMoonPanel mmPanel2352 = new MajorMoonPanel("Fol", gen.getConfigSeed(1), gen.getMajorMoonRadius(1), gen.getConfig(1), this, 1);
		scroller = new JScrollPane(mmPanel2352, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(620, 599));
		tabbedPane.addTab("Fol", scroller);
		majorMoonPanels[1] = mmPanel2352;
		
		MajorMoonPanel mmPanel4001 = new MajorMoonPanel("Pandunia", gen.getConfigSeed(2), gen.getMajorMoonRadius(2), gen.getConfig(2), this, 2);
		scroller = new JScrollPane(mmPanel4001, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(620, 599));
		tabbedPane.addTab("Pandunia", scroller);
		majorMoonPanels[2] = mmPanel4001;
		
		MajorMoonPanel mmPanel463 = new MajorMoonPanel("Спутник", gen.getConfigSeed(3), gen.getMajorMoonRadius(3), gen.getConfig(3), this, 3);
		scroller = new JScrollPane(mmPanel463, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(620, 599));
		tabbedPane.addTab("Спутник", scroller);
		majorMoonPanels[3] = mmPanel463;
		
		MajorMoonPanel mmPanel124 = new MajorMoonPanel("Goda", gen.getConfigSeed(4), gen.getMajorMoonRadius(4), gen.getConfig(4), this, 4);
		scroller = new JScrollPane(mmPanel124, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(620, 599));
		tabbedPane.addTab("Goda", scroller);
		majorMoonPanels[4] = mmPanel124;
		
		minorMoonsPanel = new MinorMoonsPanel(this.gen, this);
		tabbedPane.addTab("Minor moons", minorMoonsPanel);
		tabbedPane.addTab("Generator control", new StartGeneratorPanel(this.gen, this));
		
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(tabbedPane.getSelectedIndex() != currentSelectedPane) {
					currentSelectedPane = tabbedPane.getSelectedIndex();
					if(currentSelectedPane - 1 >= 5) return;
					if((currentSelectedPane == 0 && !p9Panel.hasPreview()) || (currentSelectedPane > 0 && !majorMoonPanels[currentSelectedPane - 1].hasPreview())) {
						triggerPreviewRegen(currentSelectedPane - 1, false);
					}
				}
			}
		});
	}
	
	synchronized void setTabbedPaneEnabled(boolean b) {
		tabbedPane.setEnabled(b);
	}
	
	synchronized void triggerPreviewRegen(int idx, boolean minorMoons) {
		MajorMoonPanel panel = idx == -1 ? p9Panel : majorMoonPanels[idx];
		if(genThreadThread != null && genThreadThread.isAlive()) return;
		if(minorMoons) minorMoonsPanel.setApplyAndRenderEnabled(false);
		else {
			if(idx == -1) {
				if(panel.getSeed() != gen.getP9Seed()) {
					gen.changeP9Seed(panel.getSeed());
					panel.updateAdvancedSettings(gen.getP9Settings());
				}
			}else {
				if(panel.getSeed() != gen.getConfigSeed(idx)) {
					gen.changeConfigSeed(idx, panel.getSeed());
					panel.updateAdvancedSettings(gen.getConfig(idx));
				}
			}
			panel.setApplyEnabled(false);
		}
		tabbedPane.setEnabled(false);
		previewDialog.setVisible(true);
		previewDialog.setLocationRelativeTo(GenMain.frame);
		genThread.setIdx(idx, minorMoons);
		genThreadThread = new Thread(genThread);
		genThreadThread.start();
	}
	
	private class PreviewGenThread implements Runnable {
		
		private int idx;
		private boolean minorMoons;
		private SystemGenerator gen;
		
		public PreviewGenThread(SystemGenerator gen) {
			this.gen = gen;
		}
		
		public void setIdx(int idx, boolean minorMoons) {
			this.idx = idx;
			this.minorMoons = minorMoons;
		}
		
		@Override
		public void run() {
			try {
				if(minorMoons) {
					for(int i = 0; i < gen.getMinorMoonCount(); i++) {
						GeneratorResult res = gen.genMinorMoonPreviewMaps(i);
						minorMoonsPanel.updatePreviews(i, res);
					}
				}else {
					if(idx == -1) {
						GeneratorResult res = gen.genP9PreviewMaps();
						p9Panel.updatePreviews(res);
					}else {
						GeneratorResult res = gen.genMajorMoonPreviewMaps(this.idx);
						majorMoonPanels[idx].updatePreviews(res);
					}
				}
			}catch(Exception e) {
				previewDialog.setVisible(false);
				JOptionPane.showMessageDialog(GenMain.frame, "Error generating previews: " + e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				System.exit(1);
			}
			previewDialog.setVisible(false);
			tabbedPane.setEnabled(true);
			if(minorMoons) minorMoonsPanel.setApplyAndRenderEnabled(true);
			else if(idx == -1) p9Panel.setApplyEnabled(true); else majorMoonPanels[idx].setApplyEnabled(true);
		}
	}
	
}