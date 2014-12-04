package graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeriesCollection;


@SuppressWarnings("serial")
public class Plot extends JFrame{

	boolean isDeviation = false;

	JPanel panel;
	ChartPanel chartPanel;
	LegendTitle legend;
	
	JPanel nrmsePanel;
	JLabel[] nrmseLabel;
	
	JPanel anrmsePanel;
	JLabel[] anrmseLabel;
	
	static Color[] color = ControlPanel.color;
		
	int numEstimations;
	
	JFreeChart chart;
	
	//Generic class for densities and velocities plots
	public Plot(String S, int _numEstimations) {
		super(S);

		numEstimations = _numEstimations;
		
		panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel,BoxLayout.Y_AXIS);
		panel.setLayout(layout);
	
		makeNrmsePanel();
		
		chart = ChartFactory.createXYLineChart("","","",new XYSeriesCollection(),PlotOrientation.VERTICAL,false,false,false);
		
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);

		chartPanel = new ChartPanel(chart);
		panel.add(chartPanel);
		
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		dim.setSize(dim.getWidth()*0.75, dim.getHeight()*0.75);
		chartPanel.setPreferredSize(dim);
		
		
		if (S.startsWith("D")) setLocation(360,45);
		else setLocation(480,75);
			
		setContentPane(panel);
		pack();
		setVisible(true);

		makeLegend();

	}
	
	
	public void updateGraph(String title, XYSeriesCollection data, double[] nrmse, double[] anrmse) {
		
		if (title.startsWith("D")) chart = ChartFactory.createXYLineChart(title,"Position","Density",data,PlotOrientation.VERTICAL,false,false,false);
		else  chart = ChartFactory.createXYLineChart(title,"Position","Speed",data,PlotOrientation.VERTICAL,false,false,false);
		
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);
		
        chart.getXYPlot().setRenderer(makeRenderer((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()));
        chart.addLegend(legend);

		ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
		yAxis.setTickLabelFont(new Font("SansSerif",Font.BOLD,16));
		yAxis.setLabelFont(new Font("SansSerif",Font.BOLD,20));
		
		ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
		xAxis.setTickLabelFont(new Font("SansSerif",Font.BOLD,16));
		xAxis.setLabelFont(new Font("SansSerif",Font.BOLD,20));
		
        chartPanel.setChart(chart);
        
		for (int i =0; i<numEstimations; i++) {
			nrmseLabel[i].setText("Filter "+Integer.toString(i)+" = "+Double.toString(nrmse[i]));
			anrmseLabel[i].setText("Filter "+Integer.toString(i)+" = "+Double.toString(anrmse[i]));
		}
		
	}

	public void updateGraph(String title, XYSeriesCollection data, double[] nrmse, double[] anrmse, double[] nrmseNoData, double[] anrmseNoData) {
		
		if (title.startsWith("D")) chart = ChartFactory.createXYLineChart(title,"Position","Density",data,PlotOrientation.VERTICAL,false,false,false);
		else  chart = ChartFactory.createXYLineChart(title,"Position","Speed",data,PlotOrientation.VERTICAL,false,false,false);
		
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);
		
        chart.getXYPlot().setRenderer(makeRenderer((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()));
        chart.addLegend(legend);

		ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
		yAxis.setTickLabelFont(new Font("SansSerif",Font.BOLD,16));
		yAxis.setLabelFont(new Font("SansSerif",Font.BOLD,20));
		
		ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
		xAxis.setTickLabelFont(new Font("SansSerif",Font.BOLD,16));
		xAxis.setLabelFont(new Font("SansSerif",Font.BOLD,20));
		
        chartPanel.setChart(chart);
        
		for (int i =0; i<numEstimations; i++) {
			nrmseLabel[i].setText("Filter "+Integer.toString(i)+" = "+Double.toString(nrmse[i])+" ("+Double.toString(nrmseNoData[i])+")");
			anrmseLabel[i].setText("Filter "+Integer.toString(i)+" = "+Double.toString(anrmse[i])+" ("+Double.toString(anrmseNoData[i])+")");
		}
		
	}

	
	
	public void makeNrmsePanel() {
    	nrmsePanel = new JPanel(new GridLayout(1,0));
		nrmsePanel.add(new JLabel("Normalized root mean square errors (%)"));
		nrmseLabel = new JLabel[numEstimations];
		for (int i =0; i<numEstimations; i++) {
			nrmseLabel[i] = new JLabel(("Filter "+Integer.toString(i)+" = 0"));
			nrmsePanel.add(nrmseLabel[i]);
		}
		panel.add(nrmsePanel);
		
    	anrmsePanel = new JPanel(new GridLayout(1,0));
		anrmsePanel.add(new JLabel("Averaged normalized root mean square errors (%)"));
		anrmseLabel = new JLabel[numEstimations];
		for (int i =0; i<numEstimations; i++) {
			anrmseLabel[i] = new JLabel(("Filter "+Integer.toString(i)+" = 0"));
			anrmsePanel.add(anrmseLabel[i]);
		}
		panel.add(anrmsePanel);
	}
	
	
	public void updateNrmsePanel() {
		nrmsePanel.removeAll();
		nrmsePanel.add(new JLabel("Normalized root mean square errors (%)"));
		nrmseLabel = new JLabel[numEstimations];
		for (int i =0; i<numEstimations; i++) {
			nrmseLabel[i] = new JLabel(("Filter "+Integer.toString(i)+" = 0"));
			nrmsePanel.add(nrmseLabel[i]);
		}
		
		anrmsePanel.removeAll();
		anrmsePanel.add(new JLabel("Averaged normalized root mean square errors (%)"));
		anrmseLabel = new JLabel[numEstimations];
		for (int i =0; i<numEstimations; i++) {
			anrmseLabel[i] = new JLabel(("Filter "+Integer.toString(i)+" = 0"));
			anrmsePanel.add(anrmseLabel[i]);
		}
	}
	
	
	public void setIsDeviation (boolean _isDeviation) {
		isDeviation = _isDeviation;
	}
	
	private void makeLegend() {
        final LegendItemCollection legendCollection = new LegendItemCollection();
        
        Shape line = new Rectangle(15, 2);
        Shape point = new Rectangle(4,4);
        String space = "         ";
        legendCollection.add(new LegendItem("Exact solution"+space, null, null, null, line, color[0]) );
        legendCollection.add(new LegendItem("Measure points"+space, null, null, null, point, color[0]));
        for (int i = 0; i< numEstimations; i++) legendCollection.add(new LegendItem("Filter "+Integer.toString(i)+space, null, null, null, line, color[i+1]));
        
        LegendItemSource source =new LegendItemSource() {
			
			public LegendItemCollection getLegendItems() {
				return legendCollection;
			}
		};
		legend = new LegendTitle(source);
		legend.setItemFont(new Font("SansSerif",Font.BOLD,16));
	}
	
//Rendered that does not show variances when deviations are on
	private XYLineAndShapeRenderer makeRenderer(XYLineAndShapeRenderer renderer) {
		
		int multip;
		
		if (isDeviation)multip=3;
		else multip=2;
		
		double d = multip*numEstimations+2;
		
        renderer.setSeriesPaint(0, color[0]);
        renderer.setSeriesPaint(1, color[0]);
        for (int i = 2; i<d; i++) {
        	renderer.setSeriesPaint(i, color[((i-2)/multip)+1]);
        } 
        
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        
        if (isDeviation) {
        	BasicStroke stroke = new BasicStroke(0.75f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {5.0f, 8.0f}, 0.0f);
        	for (int i = 0; i<numEstimations; i++) {
        		int j = 3*(i+1);
        		renderer.setSeriesStroke(j, stroke);
        		renderer.setSeriesStroke(j+1, stroke);
        	}
        }
        
        return renderer;
        
	}
	
// Old renderer with both variances and deviations
//	private XYLineAndShapeRenderer makeRenderer(XYLineAndShapeRenderer renderer) {
//        double d;
//		if (isDeviation) d=4*numEstimations+2;
//        else d=2*numEstimations+2;
//		
//        renderer.setSeriesPaint(0, color[0]);
//        renderer.setSeriesPaint(1, color[0]);
//        for (int i = 2; i<d; i++) {
//        	if (isDeviation) renderer.setSeriesPaint(i, color[((i-2)/4)+1]);
//        	else renderer.setSeriesPaint(i, color[((i-2)/2)+1]);
//        } 
//        
//        renderer.setSeriesLinesVisible(1, false);
//        renderer.setSeriesShapesVisible(1, true);
//        
//        if (isDeviation) {
//        	BasicStroke stroke = new BasicStroke(0.75f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] {5.0f, 8.0f}, 0.0f);
//        	for (int i = 0; i<numEstimations; i++) {
//        		int j = 4*(i+1);
//        		renderer.setSeriesStroke(j, stroke);
//        		renderer.setSeriesStroke(j+1, stroke);
//        	}
//        }
//        
//        return renderer;
//	}
	
	
	public JFreeChart getChart() {
		return chart;
	}
	
	public void save(String adress) {
		
		
	}
	
}
