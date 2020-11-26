package tholin.planet9.gens;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

public class StartGeneratorPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5821652537152647096L;
	private JTextField textField;
	private JButton btnNewButton;
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private GenThread genThread;
	private Thread genThreadThread = null;
	private SystemGenerator gen;
	private GenPanel parent;
	
	public StartGeneratorPanel(SystemGenerator gen, GenPanel parent) {
		super();
		this.gen = gen;
		this.parent = parent;
		this.genThread = new GenThread();
		setPreferredSize(new Dimension(616, 308));
		setLayout(null);
		
		JLabel lblStartGenerator = new JLabel("Generator control");
		lblStartGenerator.setFont(new Font("Dialog", Font.BOLD, 15));
		lblStartGenerator.setBounds(12, 12, 156, 15);
		add(lblStartGenerator);
		
		JLabel lblGamedataFolderLocation = new JLabel("PluginData folder location:");
		lblGamedataFolderLocation.setBounds(12, 49, 210, 15);
		add(lblGamedataFolderLocation);
		
		textField = new JTextField();
		textField.setText(SystemGenerator.plugindataPath);
		textField.setBounds(240, 47, 210, 19);
		add(textField);
		textField.setColumns(10);
		
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(462, 44, 117, 25);
		add(btnBrowse);
		
		btnNewButton = new JButton("START THE GENERATOR");
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 18));
		btnNewButton.setBounds(12, 96, 592, 62);
		add(btnNewButton);
		
		lblStatus = new JLabel("Status: Idle");
		lblStatus.setBounds(12, 200, 592, 15);
		add(lblStatus);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(12, 170, 592, 18);
		add(progressBar);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 227, 592, 2);
		add(separator);
		btnBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int o = fc.showOpenDialog(GenMain.frame);
				if(o == JFileChooser.APPROVE_OPTION) {
					File folder = fc.getSelectedFile();
					if(!folder.exists()) {
						JOptionPane.showMessageDialog(GenMain.frame, "The selected file does not exist", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(!folder.isDirectory()) {
						JOptionPane.showMessageDialog(GenMain.frame, "The selected file is not a directory", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					textField.setText(folder.getPath());
				}
			}
		});
		
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(GenMain.frame, "Warning! Running the generator will overwrite existing system. If you want to use the already existing system again in the future, back it up now!\nProceed?", "Overwrite warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(res != 0) return;
				SystemGenerator.plugindataPath = textField.getText();
				btnNewButton.setEnabled(false);
				parent.setTabbedPaneEnabled(false);
				genThreadThread = new Thread(genThread);
				genThreadThread.start();
			}
		});
	}
	
	private class GenThread implements Runnable {
		
		public GenThread() {
			
		}
		
		@Override
		public void run() {
			try {
				final int totalCelestialBodies = 1 + 5 + gen.getMinorMoonCount();
				lblStatus.setText("Status: Deleting Kopernicus cache");
				progressBar.setValue(0);
				File cacheFolder = new File(new File(SystemGenerator.plugindataPath).getParentFile().getPath() + "/cache");
				if(cacheFolder.exists() && cacheFolder.isDirectory()) {
					for(File f:cacheFolder.listFiles()) {
						System.out.println(f.getName());
						f.delete();
					}
				}
				File plugindataFolder = new File(SystemGenerator.plugindataPath);
				if(plugindataFolder.exists() && plugindataFolder.isDirectory()) {
					for(File f:plugindataFolder.listFiles()) {
						f.delete();
					}
				}
				lblStatus.setText("Status: Generating Planet 9");
				gen.genP9();
				double progress = Math.ceil(1.0 / (double)totalCelestialBodies * 100.0);
				progressBar.setValue((int)progress);
				for(int i = 0; i < 5; i++) {
					lblStatus.setText("Status: Generating major moon " + Integer.toString(i + 1) + " out of 5");
					gen.genMajorMoonMaps(i);
					progress = Math.ceil((double)(i + 1 + 1) / (double)totalCelestialBodies * 100.0);
					progressBar.setValue((int)progress);
				}
				File kopernicusFolder = new File(new File(SystemGenerator.plugindataPath).getParentFile().getPath() + "/KopernicusConfigs/minor_moons");
				if(kopernicusFolder.exists() && kopernicusFolder.isDirectory()) {
					for(File f:kopernicusFolder.listFiles()) {
						f.delete();
					}
				}
				for(int i = 0; i < gen.getMinorMoonCount(); i++) {
					lblStatus.setText("Status: Generating minor moon " + Integer.toString(i + 1) + " out of " + Integer.toString(gen.getMinorMoonCount()));
					gen.genMinorMoonMaps(i);
					lblStatus.setText("Status: Generating Kopernicus config for minor moon " + Integer.toString(i + 1) + " out of " + Integer.toString(gen.getMinorMoonCount()));
					gen.buildMinorMoonKopConfig(i);
					Thread.sleep(1000);
					progress = Math.ceil((double)(i + 1 + 5 + 1) / (double)totalCelestialBodies * 100.0);
					progressBar.setValue((int)progress);
				}
				lblStatus.setText("Status: Idle");
				parent.setTabbedPaneEnabled(true);
				btnNewButton.setEnabled(true);
			}catch(Exception e) {
				JOptionPane.showMessageDialog(GenMain.frame, "Error running the generators: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				System.exit(1);
			}
		}
		
	}
	
}