package graph;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import speedFunction.SpeedFunction;



@SuppressWarnings("serial")
public class SFparamChoice extends JFrame implements ActionListener{

	int l;
	JTextField[] jTextField;
	SpeedFunction sf;
	
	public SFparamChoice(SpeedFunction _sf) {
		super("Parameters choice");
		sf = _sf;
		
		double[] oldParam = sf.parameters;
		String[] paramDescription = sf.paramDescription;
		
		l = oldParam.length;

		jTextField = new JTextField[l];

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panel = (JPanel) getContentPane();
		panel.setLayout(new GridLayout(0,2));
		for (int i=0; i<l; i++) {
			jTextField[i] = new JTextField(Double.toString(oldParam[i]));
			add(new JLabel(paramDescription[i]+" ="));
			add(jTextField[i]);
		}
		
		JButton ok = new JButton("ok");
		ok.addActionListener(this);
		ok.setActionCommand("ok");
		add(ok);
		
		JButton cancel = new JButton("cancel");
		cancel.addActionListener(this);
		cancel.setActionCommand("cancel");
		add(cancel);
		
		setLocation(450, 80);
		pack();
		setVisible(true);

		
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getActionCommand()=="ok") {
			double[] newParam = new double[l]; 
			try{
				for (int i=0; i<l; i++) {
					newParam[i] = Double.parseDouble(jTextField[i].getText());
				}
				sf.setParamDefault(newParam);
				dispose();
			}
			catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Wrong format");
			}
		}
		if (actionEvent.getActionCommand()=="cancel") dispose();
	}
	
	
}

