package graph;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

@SuppressWarnings("serial")
public class VarWindow extends JFrame{
	
	JPanel panel;
	
	public VarWindow(JPanel[] varPanels) {
		super("Variance of the filters");
		
		panel = new JPanel(new GridLayout(varPanels.length,0));
		for (int i = 0; i<varPanels.length; i++) {
			panel.add(varPanels[i]);
		}
		
		setPreferredSize(new java.awt.Dimension(1200, 700));
		setVisible(true);
		setContentPane(panel);
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
	}

}
