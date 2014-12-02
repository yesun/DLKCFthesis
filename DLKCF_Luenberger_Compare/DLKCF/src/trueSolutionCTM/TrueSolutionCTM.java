package trueSolutionCTM;

import org.jblas.DoubleMatrix;

public abstract class TrueSolutionCTM {


    
	
	public double rhoMax;
	public double speedMax;
	public double rhoCritical;
	
	public double dt;
	public double dx;
	public int cells;
	int locDis; //location of the discontinuity
	
	public DoubleMatrix trueStatesCTM;
	public DoubleMatrix trueStatesCTMPrior;
    public int numUp;


	abstract public double speed(double density);
	abstract public void propagateCTM();	
	abstract public double sending(double density);
	abstract public double receiving(double density);
	abstract public double computeFlux(double density1, double density2);

	
	public void initial(double _dt, double _dx, int _cells) {

		dx = _dx;
		dt = _dt;		
		cells=_cells;

		trueStatesCTM=DoubleMatrix.zeros(cells,1);

		//**Start setting initial conditions
		
		double rhoLeft=0.8;
		double rhoRight=0.2;
		//Note: These rhoLeft and rhoLeft are just intermediate variables used for defining true initial conditions, they are not the same as the public variable rhoLeft and rhoLeft in trueSolution.
		
		locDis = (int)cells/2; //location of the discontinuity

		for(int i=0;i<locDis;i++){
			trueStatesCTM.put(i,0,rhoLeft);
		}
		for(int i=locDis;i<cells;i++){
			trueStatesCTM.put(i,0,rhoRight);
		}
		
		for(int i=0;i<5;i++){
			trueStatesCTM.put(i,0,rhoRight);
		}

		for(int i=cells-5;i<cells;i++){
			trueStatesCTM.put(i,0,0.35);
		}
		//**End setting initial conditions

		trueStatesCTMPrior=trueStatesCTM.dup();
		
		numUp = 0;
	}

	public double[] getDensityBoundaries() {
		double[] res = new double[2];
		res[0] = trueStatesCTM.get(0,0);
		res[1] = trueStatesCTM.get(cells-1,0);
		return res;
	}

	public double flux(double density) { 	
		return density*speed(density);
	}
		
	public void update() {		
		numUp++;
		propagateCTM();	
	}

}
