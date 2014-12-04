package graph;

import filters.Estimation;

import java.awt.Color;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileOutputStream;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jblas.DoubleMatrix;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


import trueSolution.TrueSolution;

@SuppressWarnings("serial")
public class ControlPanel extends ApplicationFrame implements ActionListener, ComponentListener {

	public static Color[] color = {Color.black, Color.red, Color.blue, Color.green, Color.cyan, Color.darkGray, Color.ORANGE};
	
	JPanel panel = new JPanel();
	ChartPanel chartPanel;
	LegendTitle legend;
	
	FilterChoice filterChoice;
	
	BoxLayout layout;
	LoadingBar loadingBar;
	
	JTextField rhoLeftButton;
	JTextField rhoRightButton;
	JTextField customUpdate;
	
	boolean isDeviation = false;
	
	LinkedList<Estimation> listEstimations = new LinkedList<Estimation>();
	int numEstimations; //number of filters for this running graph
	
	double rhoLeftDefault = 0.70;
	double rhoRightDefault = 0.30;

	Plot densityPlot;
	Plot speedPlot;
	
	double spaceStep;
	int cells;
	
	double[] densityNrmse;
	double[] speedNrmse;
	double[] densityANrmse;
	double[] speedANrmse;
	
	double[] densityNrmseNoData;
	double[] speedNrmseNoData;
	double[] densityANrmseNoData;
	double[] speedANrmseNoData;
	
	TrueSolution trueSolution;
	
	XYSeriesCollection densityData = new XYSeriesCollection();
	XYSeriesCollection speedData = new XYSeriesCollection();
	
	String savingDirectory = "C:/Users/Seb/Desktop/Dropbox/Papier/Illustrations/";
	public static String absolutePath;
	
	public ControlPanel(double dt, double dx, int _cells) {
		super("");
		absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		cells = _cells;
		//trueSolution = _trueSolution;
		spaceStep = dx;

		panel = new JPanel();
		
		layout = new BoxLayout(panel,BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		filterChoice = new FilterChoice(rhoLeftDefault, rhoRightDefault, dt, dx, cells);
		filterChoice.addComponentListener(this);		
		listEstimations = filterChoice.listEstimations;
		numEstimations = listEstimations.size();
		trueSolution = filterChoice.trueSolution;

		panel.add(filterChoice);
		
		densityNrmse = new double[numEstimations];
		speedNrmse = new double[numEstimations];
		densityANrmse = new double[numEstimations];
		speedANrmse = new double[numEstimations];

		densityNrmseNoData = new double[numEstimations];
		speedNrmseNoData = new double[numEstimations];
		densityANrmseNoData = new double[numEstimations];
		speedANrmseNoData = new double[numEstimations];
		
		JPanel somePanel = new JPanel(new GridLayout(1,0));
		JButton start = new JButton("Start");
		start.setActionCommand("start");
		start.addActionListener(this);
		
		somePanel.add(start);
		panel.add(somePanel);
		
		JPanel rhoPanel = new JPanel(new GridLayout(2,2));
		JLabel tempLabel = new JLabel("rho left = ");
		tempLabel.setHorizontalAlignment(JLabel.CENTER);
		rhoPanel.add(tempLabel);
		rhoLeftButton = new JTextField(2);
		rhoLeftButton.setText(Double.toString(rhoLeftDefault));
		rhoLeftButton.addActionListener(this);
		rhoPanel.add(rhoLeftButton);
	
		tempLabel = new JLabel("rho right = ");
		tempLabel.setHorizontalAlignment(JLabel.CENTER);
		rhoPanel.add(tempLabel);
		rhoRightButton = new JTextField(2);
		rhoRightButton.setText(Double.toString(rhoRightDefault));
		rhoRightButton.addActionListener(this);
		rhoPanel.add(rhoRightButton);

		panel.add(rhoPanel);
		
		JPanel timePanel = new JPanel(new GridLayout(2,2)); 
		JButton b0 = new JButton("+1 time-step");
		JButton b1 = new JButton("+10 time-steps");
		JButton b2 = new JButton("+100 time-steps");
		JButton b3 = new JButton("+ ... time-steps");
		b0.setActionCommand("plusOne");
		b1.setActionCommand("plusTen");
		b2.setActionCommand("plusHundred");
		b3.setActionCommand("plusCustom");
		b0.addActionListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		timePanel.add(b0);
		timePanel.add(b1);
		timePanel.add(b2);
		customUpdate = new JTextField(3);
		customUpdate.setText("200");
		JPanel jpa = new JPanel(); jpa.add(customUpdate); jpa.add(b3);
		timePanel.add(jpa);
		
		panel.add(timePanel);
		
		JPanel anotherPanel = new JPanel(new GridLayout(1,0));
		JButton covar =  new JButton("Covar");
		covar.setActionCommand("covar");
		covar.addActionListener(this);
		
		JButton showSpeed = new JButton("Speed");
		showSpeed.setActionCommand("speed");
		showSpeed.addActionListener(this);
		
		JCheckBox showDeviation = new JCheckBox("Deviations");
		showDeviation.setActionCommand("showDeviation");
		showDeviation.addActionListener(this);
		
		anotherPanel.add(covar);
		anotherPanel.add(showSpeed);
		anotherPanel.add(showDeviation);
		
		panel.add(anotherPanel);
	
		loadingBar = new LoadingBar(numEstimations);
		
		JPanel savePanel = new JPanel();

		
		JButton save = new JButton("Save");
		save.setActionCommand("save");
		save.addActionListener(this);
		savePanel.add(save);
		
		JButton setSave = new JButton("Set save directory");
		setSave.setActionCommand("setSave");
		setSave.addActionListener(this);
		savePanel.add(setSave);
		
		panel.add(savePanel);
		
		setLocation(10, 0);
		//panel.setPreferredSize(new Dimension(300, 800));
		setContentPane(panel);
		pack();
		
		setVisible(true);

		speedPlot = new Plot("Velocities",numEstimations);		
		densityPlot = new Plot("Densities",numEstimations);
		printValues();
		toFront();

	}
	

	public void printValues() {
		XYSeries densityExact = new XYSeries("Exact density");
		densityExact.setDescription("densityExact");
		XYSeries speedExact = new XYSeries("Exact speed");
		speedExact.setDescription("speedExact");
		
		for (int i=0; i<cells ; i++) {
			double pos = ((double)i)*spaceStep;

			double de = trueSolution.getDensityValueFromPos(pos);
			densityExact.add(pos, de);	
			
			double se = trueSolution.getSpeedValueFromPos(pos);
			speedExact.add(pos, se);	
		}
		
		densityData.removeAllSeries();
		densityData = new XYSeriesCollection(densityExact);
		speedData.removeAllSeries();
		speedData = new XYSeriesCollection(speedExact);
		
		densityData.addSeries(trueSolution.getDensityXYSeries()); //Density measure points
		speedData.addSeries(trueSolution.getSpeedXYSeries()); //Speed measure points
		
		double maxDensityVar = getDensityMaxVariance()*10;
		double maxSpeedVar = getSpeedMaxVariance()*10;
		if (maxDensityVar==0) maxDensityVar=0.1;
		if (maxSpeedVar==0) maxSpeedVar=0.1;
		
		for (int i =0; i<numEstimations; i++) {
			Estimation estim = listEstimations.get(i);
			densityNrmse[i]=0;
			speedNrmse[i]=0;

			String S = "Filter "+Integer.toString(i);

			XYSeries densityMean = new XYSeries(S+"  density");
			densityMean.setDescription("densityMean");
			XYSeries densityVariance = new XYSeries(S+" density variance");
			densityVariance.setDescription("densityVariance");
			XYSeries densityDownStd = new XYSeries(S+" density downStd");
			densityDownStd.setDescription("densityDownStd");
			XYSeries densityUpStd = new XYSeries(S+" density upStd");
			densityUpStd.setDescription("densityUpStd");
			
			XYSeries speedMean = new XYSeries(S+" speed");
			speedMean.setDescription("speedMean");
			XYSeries speedVariance = new XYSeries(S+" speed variance");
			speedVariance.setDescription("speedVariance");
			XYSeries speedDownStd = new XYSeries(S+" speed downStd");
			speedDownStd.setDescription("speedDownStd");
			XYSeries speedUpStd = new XYSeries(S+" speed upStd");
			speedUpStd.setDescription("speedUpStd");
			
			DoubleMatrix dm = estim.getDensityMean();
			DoubleMatrix dv = estim.getDensityVar();
			DoubleMatrix sm = estim.getSpeedMean();
			DoubleMatrix sv = estim.getSpeedVar();
			
			for (int j=0; j<cells ; j++) {
				double pos = ((double)j)*spaceStep;

				double m = dm.get(j);
				double v = dv.get(j,j);
				densityMean.add(pos, m);
				densityVariance.add(pos, v/maxDensityVar);
				densityDownStd.add(pos,m-2*Math.sqrt(v));
				densityUpStd.add(pos,m+2*Math.sqrt(v));
				
				m = sm.get(j);
				v = sv.get(j,j);
				speedMean.add(pos, m);
				speedVariance.add(pos, v/maxSpeedVar);
				speedDownStd.add(pos,m-2*Math.sqrt(v));
				speedUpStd.add(pos,m+2*Math.sqrt(v));

			}
			

			densityData.addSeries(densityMean);
			if (isDeviation) {
				densityData.addSeries(densityDownStd);
				densityData.addSeries(densityUpStd);
			}
			else densityData.addSeries(densityVariance); //does not show variances when deviations are on
			
			speedData.addSeries(speedMean);
			if (isDeviation) {
				speedData.addSeries(speedDownStd);
				speedData.addSeries(speedUpStd);
			}
			else speedData.addSeries(speedVariance); //does not show variances when deviations are on
			
		}
		
		makeNrmse();
		
		densityPlot.updateGraph("Density after "+Integer.toString(trueSolution.numUp)+" step(s)",
				densityData,densityNrmse,densityANrmse, densityNrmseNoData, densityANrmseNoData);
		speedPlot.updateGraph("Speed after "+Integer.toString(trueSolution.numUp)+" step(s)",
				speedData,speedNrmse,speedANrmse, speedNrmseNoData, speedANrmseNoData);
		

	}
	

	public void update(int numUpdates) {
		loadingBar.setVisible(true);
		for(int i=0; i<numUpdates; i++) {
	    	trueSolution.update();
    		makeNrmse();
			for (int j = 0; j<numEstimations; j++) {
				listEstimations.get(j).nextStepWithoutUpdateTrue();
	    		loadingBar.update(j,100*(i+1)/numUpdates);
	    	}

	    }
		loadingBar.setVisible(false);
		printValues();	
	}
	
	private double getDensityMaxVariance(){ 
		double max = 0;
		for (Estimation f : listEstimations) {
			max = Math.max(max, f.getDensityVar().getRange(0, cells, 0, f.getDensityVar().columns).max());
		}
		return max;
	}

	private double getSpeedMaxVariance(){ 
		double max = 0;
		for (Estimation f : listEstimations) {
			max = Math.max(max, f.getSpeedVar().getRange(0, cells, 0, f.getSpeedVar().columns).max());
		}
		return max;
	}
	

	public void actionPerformed(ActionEvent e) {
		String Action = e.getActionCommand ();
		if (Action.equals ("plusOne")) update(1);
		else if (Action.equals ("plusTen")) update(10);
		else if (Action.equals ("plusHundred")) update(100);
		else if (Action.equals ("plusCustom")) update(Integer.valueOf(customUpdate.getText()));		
		else if (Action.equals ("start")) start();
		else if (Action.equals ("showDeviation")) {
			isDeviation = !(isDeviation);
			densityPlot.setIsDeviation(isDeviation);
			speedPlot.setIsDeviation(isDeviation);
			printValues();
		}
		
		
		else if (Action.equals("covar")) {;
			JPanel[] j = new JPanel[numEstimations];
			for (int i=0; i<numEstimations; i++) {
				Printer p = new Printer(listEstimations.get(i).getVar(),"Filter "+Integer.toString(i));
				j[i] = p.createPanel();
			}
			
			new VarWindow(j);
		}
		
		
		else if (Action.equals("speed")) {
			XYSeriesCollection data = new XYSeriesCollection();
			XYSeries points = trueSolution.createXYSeries();
			points.setKey(points.getKey()+"   ");
			data.addSeries(points);
			String title = "";
			for (int i = 0; i<numEstimations; i++) {
				points = listEstimations.get(i).createSpeedXYSeries();
				points.setKey("Filter "+Integer.toString(i)+" = "+points.getKey()+"   ");
				data.addSeries(points);
				title+= listEstimations.get(i).getSpeedFunction().describeParameters()+"\n";
			}
			new GraphWindow(data, title, "Density", "Speed", color);
		}
		else if (Action.equals ("save")) save();
		else if (Action.equals ("setSave")) setSave();
		
	}
	
	
	private void start() {
		System.out.println();
		
		String rhol = rhoLeftButton.getText(); 
		String rhor = rhoRightButton.getText(); 
		if (!rhol.equals("")) rhoLeftDefault = Double.valueOf(rhol);
		if (!rhor.equals("")) rhoRightDefault = Double.valueOf(rhor); 	
		
		trueSolution.newBoundaries(rhoLeftDefault,rhoRightDefault);
		filterChoice.newBoundaries(rhoLeftDefault,rhoRightDefault);
		
		listEstimations = filterChoice.listEstimations;
		trueSolution = filterChoice.trueSolution;
		numEstimations = listEstimations.size();
		
		loadingBar.reset(numEstimations);
		
		speedPlot.dispose();
		speedPlot = new Plot("Velocities", numEstimations);		
		
		densityPlot.dispose();
		densityPlot = new Plot("Densities", numEstimations);		
		
		densityNrmse = new double[numEstimations];
		speedNrmse = new double[numEstimations];
		densityANrmse = new double[numEstimations];
		speedANrmse = new double[numEstimations];
		
		densityNrmseNoData = new double[numEstimations];
		speedNrmseNoData = new double[numEstimations];
		densityANrmseNoData = new double[numEstimations];
		speedANrmseNoData = new double[numEstimations];
		
	    toFront();
		printValues();
	}

	private void makeNrmse() {
		double maxDensity = trueSolution.rhoMax;
		double maxSpeed = trueSolution.speedMax;
				
		for (int i =0; i<numEstimations; i++) {
			Estimation estim = listEstimations.get(i);
			densityNrmse[i]=0;
			speedNrmse[i]=0;
			densityNrmseNoData[i]=0;
			speedNrmseNoData[i]=0;
			
			DoubleMatrix dm = estim.getDensityMean();
			DoubleMatrix dmNoData = estim.getDensityMeanWithNoData();
			
			DoubleMatrix sm = estim.getSpeedMean();
			DoubleMatrix smNoData = estim.getSpeedMeanWithNoData();
			
			for (int j=0; j<cells ; j++) {
				double pos = ((double)j)*spaceStep;

				double m = dm.get(j);
				double de = trueSolution.getDensityValueFromPos(pos);
				densityNrmse[i]+=(m-de)*(m-de);
				
				m = sm.get(j);
				double se = trueSolution.getSpeedValueFromPos(pos);
				speedNrmse[i]+=(m-se)*(m-se);
				
				double mNoData = dmNoData.get(j);
				densityNrmseNoData[i]+=(mNoData-de)*(mNoData-de);
				
				mNoData = smNoData.get(j);
				speedNrmseNoData[i]+=(mNoData-se)*(mNoData-se);
			}
			
			densityNrmse[i]=100*Math.sqrt(densityNrmse[i]/cells)/maxDensity;
			speedNrmse[i]=100*Math.sqrt(speedNrmse[i]/cells)/maxSpeed;
			
			densityNrmseNoData[i]=100*Math.sqrt(densityNrmseNoData[i]/cells)/maxDensity;
			speedNrmseNoData[i]=100*Math.sqrt(speedNrmseNoData[i]/cells)/maxSpeed;
			
			double numUp = trueSolution.numUp+1;
			densityANrmse[i]= (densityANrmse[i]*(numUp-1) + densityNrmse[i])/numUp ;
			speedANrmse[i]= (speedANrmse[i]*(numUp-1) + speedNrmse[i])/numUp ;
			
			densityANrmseNoData[i]= (densityANrmseNoData[i]*(numUp-1) + densityNrmseNoData[i])/numUp ;
			speedANrmseNoData[i]= (speedANrmseNoData[i]*(numUp-1) + speedNrmseNoData[i])/numUp ;
			
			//round to two decimals
			densityNrmse[i]=Math.ceil(100*densityNrmse[i])/100;
			densityANrmse[i]= Math.ceil(100*densityANrmse[i])/100;
			speedNrmse[i]=Math.ceil(100*speedNrmse[i])/100;
			speedANrmse[i]= Math.ceil(100*speedANrmse[i])/100;
			
			densityNrmseNoData[i]=Math.ceil(100*densityNrmseNoData[i])/100;
			densityANrmseNoData[i]=Math.ceil(100*densityANrmseNoData[i])/100;
			speedNrmseNoData[i]= Math.ceil(100*speedNrmseNoData[i])/100;
			speedANrmseNoData[i]= Math.ceil(100*speedANrmseNoData[i])/100;
		}
		
	}
	
	public void componentResized(ComponentEvent arg0) {
		pack();
	}

	public void componentShown(ComponentEvent arg0) {
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}
	
	public void save() {
		int width = 1000;
	    int height = 600;
	    
	    String name = JOptionPane.showInputDialog(this, "Name of the plots:");
	    if (name!=null) {
		    FileOutputStream fos;
		    try {
				fos = new FileOutputStream(savingDirectory+name+"density.jpg");
				ChartUtilities.writeChartAsJPEG(fos, densityPlot.getChart(), width, height);
				NRMSEtable.saveToCSV(densityNrmse, densityANrmse, savingDirectory+name+"density" );
				
				fos.close();
				
				fos = new FileOutputStream(savingDirectory+name+"speed.jpg");
				ChartUtilities.writeChartAsJPEG(fos, speedPlot.getChart(), width, height);
				NRMSEtable.saveToCSV(speedNrmse, speedANrmse, savingDirectory+name+"speed" );
				fos.close();
				
				XYSeriesCollection data = new XYSeriesCollection();
				XYSeries points = trueSolution.createXYSeries();
				points.setKey(points.getKey()+"   ");
				data.addSeries(points);
				String title = "";
				for (int i = 0; i<numEstimations; i++) {
					points = listEstimations.get(i).createSpeedXYSeries();
					points.setKey("Filter "+Integer.toString(i)+" = "+points.getKey()+"   ");
					data.addSeries(points);
					title+= listEstimations.get(i).getSpeedFunction().describeParameters()+"\n";
				}
				fos = new FileOutputStream(savingDirectory+name+"SF.jpg");
				ChartUtilities.writeChartAsJPEG(fos, GraphWindow.createChart(data, title, "Density", "Speed", color), width, height);
				fos.close();
		    }
			 
			
			catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	
    
	public void setSave() {
		savingDirectory = JOptionPane.showInputDialog(this, "Saving directory:", savingDirectory);
	}
	
	

}
