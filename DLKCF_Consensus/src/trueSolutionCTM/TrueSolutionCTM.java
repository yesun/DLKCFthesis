package trueSolutionCTM;

import org.jblas.DoubleMatrix;
import doubleMatrix.GaussianGenerator;

public abstract class TrueSolutionCTM {

	public double rhoMax;
	public double speedMax;
	public double rhoCritical;
	
	public double dt;
	public double dx;
	public int cells;
	public int numSections;
	
	public DoubleMatrix trueStatesCTM;
	public DoubleMatrix trueStatesCTMPrior;
	
	public int measurePerturb;//If there exists low quality sensors
	public DoubleMatrix measureVarDensity;
	public GaussianGenerator measureGeneratorDensity;
	public int distMeasurement;
	public DoubleMatrix measurementCTM; //all the sensor data on the entire freeway
	
	public int numUp;
		
	int locDis;

	abstract public double speed(double density);
	abstract public void propagateCTM();
	abstract public void getMeasurementCTM();	
	abstract public double sending(double density);
	abstract public double receiving(double density);
	abstract public double computeFlux(double density1, double density2);

	
	public void initial(double _dt, double _dx, int _cells, int _distMeasurement, int _numSections, int _measurePerturb) {

		dx = _dx;
		dt = _dt;		
		cells=_cells;
		numSections=_numSections;		
		distMeasurement=_distMeasurement;		
		measurePerturb=_measurePerturb;
		trueStatesCTM=DoubleMatrix.zeros(cells,1);
		
		//**Start setting initial condition for the true state
		double rhoLeft = 0.8;
		double rhoRight = 0.2;
		locDis = (int)cells/2; 

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
		//**End setting initial condition for the true state
		trueStatesCTMPrior=trueStatesCTM.dup();
		
		measurementCTM=DoubleMatrix.zeros(((cells-1)/distMeasurement)+1,1);	
		measureVarDensity = DoubleMatrix.eye(measurementCTM.getRows()).mul(0.0009);//*Measurement error covariance matrix, assuming all the sensors are of good quality
		if (measurePerturb==1){
            for (int i=1;i<5;i++){
            	measureVarDensity.put(i*3,i*3,0.09);//*assign low quality sensors 
            }
		}
	    measureGeneratorDensity = new GaussianGenerator(measureVarDensity);
		
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
	
	public void getMeasurement() {			
		getMeasurementCTM();	
	}

}
