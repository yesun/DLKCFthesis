package graph;

import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

@SuppressWarnings("serial")
public class GraphWindow extends JFrame{

	public GraphWindow(XYSeriesCollection data, String title, String xAxis, String yAxis) {
		super("");
		createPanel(data,title,xAxis,yAxis);
	}
	
	public GraphWindow(XYSeriesCollection data, String title, String xAxis, String yAxis, Color[] color) {
		super("");
		createPanel(data,title,xAxis,yAxis, color);
	}
	
	public GraphWindow(XYSeries points, String title, String xAxis, String yAxis) {
		super("");
		XYSeriesCollection data = new XYSeriesCollection(points);
		createPanel(data,title,xAxis,yAxis);
	}
	
	public GraphWindow(XYSeries[] points, String title, String xAxis, String yAxis) {
		super("");
		XYSeriesCollection data = new XYSeriesCollection();
		for (int i = 0; i<points.length; i++) data.addSeries(points[i]);
		createPanel(data,title,xAxis,yAxis);
	}
	

	public void createPanel(XYSeriesCollection data, String title, String xAxis, String yAxis){
		JFreeChart chart = ChartFactory.createXYLineChart(title,xAxis,yAxis,data,PlotOrientation.VERTICAL,true,false,false);
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);
		
		TextTitle newTitle = chart.getTitle();
		newTitle.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
		chart.setTitle(newTitle);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        //renderer.setSeriesLinesVisible(0, false);
        //renderer.setSeriesShapesVisible(0, true);
		chart.getXYPlot().setRenderer(renderer);
		
		ChartPanel chartPanel = new ChartPanel(chart);

		setPreferredSize(new java.awt.Dimension(1200, 700));
		setVisible(true);
		setContentPane(chartPanel);
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		
	}
	
	public void createPanel(XYSeriesCollection data, String title, String xAxis, String yAxis, Color[] color){
		JFreeChart chart = ChartFactory.createXYLineChart(title,xAxis,yAxis,data,PlotOrientation.VERTICAL,true,false,false);
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);
		
		TextTitle newTitle = chart.getTitle();
		newTitle.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
		chart.setTitle(newTitle);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        //renderer.setSeriesLinesVisible(0, false);
        //renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesPaint(0, color[0]);
		renderer.setSeriesPaint(1, color[0]);
		for (int i = 0; i<data.getSeriesCount(); i++) renderer.setSeriesPaint(i, color[i]);
	  
		
		chart.getXYPlot().setRenderer(renderer);
		
		ChartPanel chartPanel = new ChartPanel(chart);

		setPreferredSize(new java.awt.Dimension(1200, 700));
		setVisible(true);
		setContentPane(chartPanel);
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
	}

	public static JFreeChart createChart(XYSeriesCollection data, String title, String xAxis, String yAxis, Color[] color) {
		JFreeChart chart = ChartFactory.createXYLineChart(title,xAxis,yAxis,data,PlotOrientation.VERTICAL,true,false,false);
		chart.getXYPlot().setBackgroundPaint(Color.WHITE);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		for (int i = 0; i<data.getSeriesCount(); i++) renderer.setSeriesPaint(i, color[i]);
		TextTitle newTitle = chart.getTitle();
		newTitle.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
		chart.setTitle(newTitle);
		return chart;
	}
	
	
}
