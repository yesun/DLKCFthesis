package doubleMatrix;

import java.util.Random;

import org.jblas.Decompose;
import org.jblas.DoubleMatrix;

public class GaussianGenerator{

	int size;
	double meandouble;
	DoubleMatrix mean;
	double sqrt_vardouble;
	DoubleMatrix sqrt_var;
	Random[] rand;
	static Random sRand = new Random();
	
	public GaussianGenerator(DoubleMatrix _mean, DoubleMatrix _var) {
		mean = _mean;
		size= mean.length;
		sqrt_var=DoubleMatrix.zeros(size,size);
		try {sqrt_var  = Decompose.cholesky(_var);}
		catch(Exception e) {System.out.println("Error in generating gaussian draw");}
		rand = new Random[size];
		for (int i= 0; i<size; i++) {
			rand[i]= new Random();
		}
	}	

	public GaussianGenerator(DoubleMatrix _var) {
		size= _var.columns;
		mean = DoubleMatrix.zeros(size);
		sqrt_var=DoubleMatrix.zeros(size,size);
		try {sqrt_var  = Decompose.cholesky(_var);} 
		catch(Exception e) {System.out.println("Error in generating gaussian draw");}
		
		rand = new Random[size];
		for (int i= 0; i<size; i++) {
			rand[i]= new Random();
		}
		
	}	

	public GaussianGenerator(double _mean, double _var) {
		meandouble = _mean;
		size= 1;
		sqrt_vardouble = Math.sqrt(_var);
	}

	public GaussianGenerator(double _var) {
		meandouble = 0;
		size= 1;
		sqrt_vardouble = Math.sqrt(_var);
	}	
	
	
	public DoubleMatrix sample() {
		DoubleMatrix v = DoubleMatrix.zeros(size,1);
		for (int i=0; i<size; i++) {
			v.put(i,0,rand[i].nextGaussian());
		}
		return mean.add(sqrt_var.mmul(v));
	}

	public double restrictedsample(double left, double right) {
		double m = right+1;
		double v;
		while (m<= left || m>=right){
				v=sRand.nextGaussian();
			   m=meandouble+sqrt_vardouble*v;
			}
		return m;
	}	
	
	
	
	public double value() {
		DoubleMatrix v = DoubleMatrix.zeros(size,1);
		for (int i=0; i<size; i++) {
			v.put(i,0,rand[i].nextGaussian());
		}
		return mean.add(sqrt_var.mmul(v)).get(0);
	}
	
	public static DoubleMatrix restrictedsamplevector(DoubleMatrix _mean, DoubleMatrix _Diagvar, DoubleMatrix limits) {
		DoubleMatrix v = DoubleMatrix.zeros(limits.rows,1);
		GaussianGenerator[] m=new GaussianGenerator[limits.rows];
		for (int i=0; i<limits.rows; i++) {
			m[i]=new GaussianGenerator(_mean.get(i,0), _Diagvar.get(i,0));//attention: _Diagvar is a vector
		}
		for (int i=0; i<limits.rows; i++) {
			v.put(i,0,m[i].restrictedsample(limits.get(i, 0),limits.get(i, 1)));
		}
		return v;
	}	
	
	public static double draw(double var) {
		if (var<0) throw new Error("Negative variance"+var);
		return sRand.nextGaussian()*Math.sqrt(var);
	}
	
	public static double logDraw(double var) {
		if (var<0) throw new Error("Negative variance"+var);
		return Math.exp(sRand.nextGaussian()*Math.sqrt(var));
	}
	
	public static double absDraw(double var) {
		if (var<0) throw new Error("Negative variance"+var);
		return Math.abs(sRand.nextGaussian()*Math.sqrt(var));
	}
	
	public static double uniDraw(double min, double max) {
		return min + (max-min)*sRand.nextDouble();
	}
	
}
