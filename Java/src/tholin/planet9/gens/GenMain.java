package tholin.planet9.gens;

import javax.swing.JFrame;

public class GenMain {
	
	public static JFrame frame;
	public static final String NAME = "Planet 9 - Moon Generator";
	public static final String VERSION = "1.1.1";
	public static boolean LOWMEM = false;
	
	public static void main(String[] args) {
		for(String s:args) {
			if(s.equalsIgnoreCase("lowmem")) {
				LOWMEM = true;
			}
		}
		/*try {
			SystemGenerator.debugColors();
		}catch(Exception e) {
			e.printStackTrace();
		}*/
		System.exit(1);
		frame = new JFrame(NAME + " - " + VERSION);
		GenPanel panel = new GenPanel();
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		/*try {
			new SystemGenerator(1L).genP9();
			System.exit(0);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}*/
	}
	
}