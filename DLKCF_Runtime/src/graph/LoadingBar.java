package graph;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

@SuppressWarnings("serial")
public class LoadingBar extends JFrame{

	JPanel loadingPanel;
	JLabel[] loadingFields;
	
	public LoadingBar(int numEstimations) {
		super("Loading time");

		loadingPanel = new JPanel(new GridLayout(0,3));
		loadingFields = new JLabel[numEstimations];
		for (int i = 0; i<numEstimations; i++) {
			loadingPanel.add(new JLabel("Filter "+Integer.toString(i)+"  "));
			loadingFields[i] = new JLabel("0");
			loadingPanel.add(loadingFields[i]);
			loadingPanel.add(new JLabel(" %"));
		}
		loadingPanel.setPreferredSize(new java.awt.Dimension(200, 65*numEstimations));
		setContentPane(loadingPanel);
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
	}

	public void update(int pos, int num) {
		loadingFields[pos].setText(Double.toString(num));
		update(getGraphics());
	}
	
	public void reset(int numEstimations) {
		loadingPanel.removeAll();
		loadingFields = new JLabel[numEstimations];
		for (int i = 0; i<numEstimations; i++) {
			loadingPanel.add(new JLabel("Filter "+Integer.toString(i)+"  "));
			loadingFields[i] = new JLabel("0");
			loadingPanel.add(loadingFields[i]);
			loadingPanel.add(new JLabel(" %"));
		}
		
	}
	
}
