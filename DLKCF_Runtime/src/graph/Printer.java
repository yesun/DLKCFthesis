package graph;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import org.jblas.DoubleMatrix;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;


@SuppressWarnings("serial")
public class Printer extends ApplicationFrame {
	
	DoubleMatrix M;
	double max;
	String name;
	int rows;
	int columns;
	
	public Printer(DoubleMatrix _M, String _name) {
		super("");
		M = _M;
		max = M.max();
		name = _name;
		rows = M.rows;
		columns = M.columns;
	}
	
	public Printer(DoubleMatrix _M) {
			super("");
			M = _M;
			max = M.max();
			name = "";
			rows = M.rows;
			columns = M.columns;
	}
	
	public static void p(DoubleMatrix A, int k) {
		String S="#.";
		for (int i=0;i<k;i++) S+="#";
        DecimalFormat twoDForm = new DecimalFormat(S);
		if (A.isColumnVector()) {
			for (int i= 0; i<A.rows; i++) {
				System.out.print(Double.valueOf(twoDForm.format(A.get(i)))+" ");
			}
			System.out.println();
		}
		else {
			for (int i =0; i<A.rows; i++) {
				for (int j= 0; j<A.columns; j++) {
					System.out.print(Double.valueOf(twoDForm.format(A.get(i,j)))+" ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public static void p(DoubleMatrix A) {

		if (A.isColumnVector()) {
			for (int i= 0; i<A.rows; i++) {
				System.out.print(A.get(i)+" ");
			}
			System.out.println();
		}
		else {
			for (int i =0; i<A.rows; i++) {
				for (int j= 0; j<A.columns; j++) {
					System.out.print(A.get(i,j)+" ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	public void paint() {
	
	    JPanel chartPanel = createPanel();
	    chartPanel.setPreferredSize(new java.awt.Dimension  (1200, 700));
	    
	    setContentPane(chartPanel);
        pack(); 
        RefineryUtilities.centerFrameOnScreen(this); 
        this.setVisible(true); 
        
	}
	
    private XYZDataset createDataset() { 
        return new XYZDataset() { 
            public int getSeriesCount() { 
                return columns; 
            } 
            public int getItemCount(int series) { 
                return rows; 
            } 
            public Number getX(int series, int item) { 
                return new Double(getXValue(series, item)); 
            } 
            public double getXValue(int series, int item) { 
                return series; 
            } 
            public Number getY(int series, int item) { 
                return new Double(getYValue(series, item)); 
            } 
            public double getYValue(int series, int item) { 
                return rows-item; 
            } 
            public Number getZ(int series, int item) { 
                return new Double(getZValue(series, item)); 
            } 
            public double getZValue(int series, int item) { 
                double x = getXValue(series, item); 
                double y = getYValue(series, item); 
                return M.get(rows-(int)y, (int)x)/max; 
            } 
            
            public void addChangeListener(DatasetChangeListener listener) { 
                // ignore - this dataset never changes 
            } 
            public void removeChangeListener(DatasetChangeListener listener) { 
                // ignore 
            } 
            public DatasetGroup getGroup() { 
                return null; 
            } 
            public void setGroup(DatasetGroup group) { 
                // ignore 
            } 
            @SuppressWarnings("rawtypes")
			public Comparable getSeriesKey(int series) { 
                return "sin(sqrt(x + y))"; 
            } 
            @SuppressWarnings("rawtypes")
			public int indexOf(Comparable seriesKey) { 
                return 0; 
            } 
            public DomainOrder getDomainOrder() { 
                return DomainOrder.ASCENDING; 
            }         
        }; 
    } 
     
    public JPanel createPanel() { 
        return new ChartPanel(createChart(createDataset())); 
    } 
     
    private JFreeChart createChart(XYZDataset dataset) {
        NumberAxis xAxis = new NumberAxis("x Axis");
        NumberAxis yAxis = new NumberAxis("y Axis");

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYBlockRenderer r = new XYBlockRenderer();
        LookupPaintScale ps = new LookupPaintScale(-1.0,1.0,Color.GRAY);
        
       	float nbScale = 40;
        for (int i = 1 ; i<nbScale; i++) ps.add(-1+2*i/nbScale, Color.getHSBColor(i / nbScale, 1.0f, 1.0f) );
        ps.add(0, new Color(185,185,185));
        ps.add(-.05, new Color(185,185,185));
        ps.add(-1,Color.getHSBColor(1 / nbScale, 1.0f, 1.0f));

        r.setPaintScale(ps);
        r.setBlockHeight(1.0f);
        r.setBlockWidth(1.0f);
        plot.setRenderer(r);
        JFreeChart chart = new JFreeChart("Max of "+name+" = "+Double.toString(max),JFreeChart.DEFAULT_TITLE_FONT,plot,false);
        NumberAxis scaleAxis = new NumberAxis("Scale");
        scaleAxis.setUpperBound(100);
        scaleAxis.setAxisLinePaint(Color.white);
        scaleAxis.setTickMarkPaint(Color.white);
        PaintScaleLegend legend = new PaintScaleLegend(ps, 
                scaleAxis);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        legend.setPadding(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(50);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(legend);
        chart.setBackgroundPaint(Color.white);
        return chart;
    }
    
}