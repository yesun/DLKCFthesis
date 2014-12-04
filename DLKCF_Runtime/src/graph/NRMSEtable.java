package graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NRMSEtable extends JPanel{

	double[] nrmse;
	double[] anrmse;
	
	int width;
	int heigth;
	int num;
	
	public NRMSEtable(double[] _nrmse, double[] _anrmse) {
		nrmse = _nrmse;
		anrmse = _anrmse;
		num = nrmse.length;
		width = 20+60*2;
		heigth = 40*(num+1);
		
		setPreferredSize(new Dimension(width, heigth));
		setVisible(true);
		
	}
	
	
	public static void saveToPNG(double[] nrmse, double[] anrmse, String directory) {
		
		NRMSEtable table = new NRMSEtable(nrmse, anrmse);
		
		BufferedImage bi = new BufferedImage(table.width, table.heigth, BufferedImage.TYPE_INT_ARGB); 
		Graphics g = bi.createGraphics();
		table.paint(g); 
		g.dispose();
		try{
			ImageIO.write(bi,"png",new File(directory+"rmse.png"));
			bi.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void saveToCSV(double[] nrmse, double[] anrmse, String directory) {
		try {
			FileWriter writer = new FileWriter(new File(directory+"rmse.csv"));
			writer.write("Filter, NRMSE, ANRMSE\n");
			for (int i = 0; i<nrmse.length; i++) writer.write(
					Integer.toString(i)+", "
					+Double.toString(roundTwo(nrmse[i]))+", "
					+Double.toString(roundTwo(anrmse[i]))+"\n");
			writer.flush();
			writer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void saveARNMSEToCSV(double[] anrmse, double[] anrmseNoData, String directory) {
		try {
			FileWriter writer = new FileWriter(new File(directory+"compareANRMSE.csv"));
			writer.write("Filter, ANRMSE, ANRMSEnodata\n");
			for (int i = 0; i<anrmse.length; i++) writer.write(
					Integer.toString(i)+", "
					+Double.toString(roundTwo(anrmse[i]))+", "
					+Double.toString(roundTwo(anrmseNoData[i]))+"\n");
			writer.flush();
			writer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, heigth);
		g.setColor(Color.BLACK);
		g.drawLine(20, 0, 20, heigth);
		g.drawLine(80, 0, 80, heigth);
		g.drawString("NRMSE", 10+20, 20);
		g.drawString("ANRMSE", 10+80, 20);
		for (int i =0; i<num; i++) {
			double n = roundTwo(nrmse[i]);
			double an = roundTwo(anrmse[i]);
			int posY = (i+1)*heigth/(num+1);
			
			g.setColor(ControlPanel.color[i+1]);
			g.drawString(Integer.toString(i), 5, 20+posY);
			
			g.setColor(Color.BLACK);
			g.drawString(String.valueOf(n), 20+20, 20+posY);
			g.drawString(String.valueOf(an), 20+80, 20+posY);
			g.drawLine(0, posY, width, posY);
		}
		
	}
	
	private static double roundTwo(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Double.valueOf(twoDForm.format(d).replace(",", "."));
}
	
	
}
