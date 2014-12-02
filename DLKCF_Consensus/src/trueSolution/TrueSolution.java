package trueSolution;

import org.jblas.DoubleMatrix;
import section.*;
import model.*;
import trueSolutionCTM.*;

public abstract class TrueSolution {

	public double rhoLeft; //left initial condition of the section
	public double rhoRight; //right initial condition of the section
	public double totalrhoLeft; //left initial condition of the entire freeway
	public double totalrhoRight; //right initial condition of the entire freeway
	
	public double rhoMax;
	public double speedMax;
	public double rhoCritical;
	
	public double dt;
	public double dx;
	public int cells;
	public int cellsec;
	public int overlapsec;
	public int index;
	public int numSections;
	
	public Section section;
	public TrueSolutionCTM trueCTM;
	
	public int perturb;// If model parameters are perturbed in estimators
	public int measurePerturb;	//If there exists low quality agents
    public boolean individual;//If inter-agent communication is allowed

	public DoubleMatrix trueStates;
	public DoubleMatrix trueStatesPrior;
	
	public DoubleMatrix measureVarDensity; //the measurement error covariance matrix, obtained from trueCTM	
	public DoubleMatrix measurementsDensity;//sensor data, obtained from trueCTM
	public int stepMeasurements=1;//number of steps between two measurements
	public int sizeMeasurements; //for one state vector

	public int numUp;
	
	public TrueSolution trueLeft;
	public TrueSolution trueRight;

	abstract public double speed(double density);
	abstract public double fluxPrime(double density);
	abstract public double fluxPrimeInverse(double speed);
	abstract public void propagate();	
	abstract public double sending(double density);
	abstract public double receiving(double density);
	abstract public double computeFlux(double density1, double density2);
	
	public void initial(TrueSolutionCTM _trueCTM, double _dt, double _dx, int _cellsec, int _overlapsec, int _index,int _numSecs,int _perturb, int _measurePerturb, boolean _individual) {
		trueCTM=_trueCTM;
		rhoMax=trueCTM.rhoMax;
		rhoCritical=trueCTM.rhoCritical;
		speedMax=trueCTM.speedMax;
		index=_index;
		dx = _dx;
		dt = _dt;
		perturb=_perturb;
		measurePerturb=_measurePerturb;
		individual=_individual;
		
		cellsec=_cellsec;
	    overlapsec=_overlapsec;
		numSections=_numSecs;
		cells=(numSections-1)*(cellsec-overlapsec)+cellsec;
				
		sizeMeasurements=4;//uptream & downstream measurements + two interior sensors
		if (overlapsec==1){
			measureVarDensity = trueCTM.measureVarDensity.getRange(index*3, index*3+4, index*3, index*3+4).dup();
		}
		else{
		    measureVarDensity = trueCTM.measureVarDensity.getRange(index*2, index*2+4, index*2, index*2+4).dup();
		}

		trueStates=DoubleMatrix.zeros(cellsec,1);
		trueStates=trueCTM.trueStatesCTM.getRange(index*(cellsec-overlapsec), index*(cellsec-overlapsec)+cellsec, 0, 1);
		trueStatesPrior=trueStates.dup();
		
		rhoLeft=trueStates.get(0,0);
		rhoRight=trueStates.get(cellsec-1,0);
		totalrhoLeft=trueCTM.trueStatesCTM.get(0,0);
		totalrhoRight=trueCTM.trueStatesCTM.get(cells-1,0);	
		
		numUp = 0;
	}

	public double[] getDensityBoundaries() {
		double[] res = new double[2];
		res[0] = trueStates.get(0,0);
		res[1] = trueStates.get(cellsec-1,0);
		return res;
	}

	public double flux(double density) { 	
		return density*speed(density);
	}

	public void newBoundaries(double _rhoLeft, double _rhoRight) {
		numUp = 0;
		rhoLeft = _rhoLeft;
		rhoRight = _rhoRight;
        trueStates=DoubleMatrix.zeros(cellsec,1);
		
		int locDis = (int)cells/2; 		
		for(int i=0;i<locDis;i++){
			trueStates.put(i,0,rhoLeft);
		}
		for(int i=locDis;i<cellsec;i++){
			trueStates.put(i,0,rhoRight);
		}
	}
	
	public void update() {		
		numUp++;
		propagate();	
	}
	
    public void updatemeasurement() {		
		newMeasurements();		
	}
    

	
	private void newMeasurements() {
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) getMeasurementsAll();
	}
	
	private void getMeasurementsAll() {
		measurementsDensity = DoubleMatrix.zeros(sizeMeasurements, 1);			
		if (overlapsec==1){	
			measurementsDensity=trueCTM.measurementCTM.getRange(index*3, index*3+4, 0, 1);
		}
		else{
			measurementsDensity=trueCTM.measurementCTM.getRange(index*2, index*2+4, 0, 1);
		}			
	}

 	@SuppressWarnings({ "rawtypes", "unchecked" })

 	public Section setSections() {
 		double[] paramsEven=new double [4];
 		double[] paramsOdd=new double [4];
 		//no perturbations on the model parameters
 		if (perturb==0){
 	 		paramsEven[0]=rhoMax;
 	 		paramsEven[1]=rhoCritical;
 	 		paramsEven[2]=speedMax;
 	 		paramsEven[3]=-1;
 	 		paramsOdd=paramsEven;
 		}
 		//*with perturbations on the model parameters
 		else {
 	 		paramsOdd[0]=1.1;
 	 		paramsOdd[1]=0.3;
 	 		paramsOdd[2]=0.9;
 	 		paramsOdd[3]=-1;
 	 		paramsEven[0]=0.9;
 	 		paramsEven[1]=0.2;
 	 		paramsEven[2]=1.2;
 	 		paramsEven[3]=-1;	 		
 		}		
 		Section secs ;
		
 		DoubleMatrix _measurement=DoubleMatrix.zeros(sizeMeasurements, 0);	
 		if (overlapsec==1){
			 _measurement=trueCTM.measurementCTM.getRange(index*3, index*3+4, 0, 1);
		}
		else{
			 _measurement=trueCTM.measurementCTM.getRange(index*2, index*2+4, 0, 1);
		}
 		
 		//determine the modes from boundary measurements
 			if ((index)%2==0){
 				if (_measurement.get(0,0)<=paramsEven[1] && _measurement.get(3,0)<=paramsEven[1]){
	 				secs=Section.createSection("FF",this);
	 			}			
	 			else if (_measurement.get(0,0)>paramsEven[1] && _measurement.get(3,0)>paramsEven[1]){
	 				secs=Section.createSection("CC",this);
	 			}
	 			else if (_measurement.get(0,0)>paramsEven[1] && _measurement.get(3,0)<=paramsEven[1]){
	 				secs=Section.createSection("CF",this);	 					
	 			}
	 			else {
	 				secs=Section.createSection("FC",this);
	 			}
 			}
 			else{
 				if (_measurement.get(0,0)<=paramsOdd[1] && _measurement.get(3,0)<=paramsOdd[1]){
 	 				secs=Section.createSection("FF",this);
 	 			}
 	 			else if (_measurement.get(0,0)>paramsOdd[1] && _measurement.get(3,0)>paramsOdd[1]){
 	 				secs=Section.createSection("CC",this);
 	 			}
 	 			else if (_measurement.get(0,0)>paramsOdd[1] && _measurement.get(3,0)<=paramsOdd[1]){
 	 				secs=Section.createSection("CF",this);		
 	 			}
 	 			else {
 	 				secs=Section.createSection("FC",this);
 	 			}
 			}
		    secs.densitysec1=trueStates;
		    secs.index=index;	    
		    if ((secs.index)%2==0){
		    	secs.setNewParameters(paramsEven);
		    }
		    else{
		    	secs.setNewParameters(paramsOdd);
		    }		 		    
		    if (secs.getClass().getSimpleName().equals("CF")){
		    	secs.getwavefront();
 		    }
		    else if (secs.getClass().getSimpleName().equals("FC")){
		    	secs.getwavefront();
 		    }		    
		    if (secs.getClass().getSimpleName().equals("FF")||secs.getClass().getSimpleName().equals("CC")){
		    	secs.getModelA();
	 		    secs.getModelB1();
			    secs.getModelB2();
		    }
 		return secs;
 	}
 	
 	
 	public RoadModel setRoadModels(){
 		Section secs = setSections();
 		RoadModel rms;
 		rms=RoadModel.createRoadModel(this, secs, "D");		
 		return rms;
 	}
 	
 	public void updateMeasurement(){
 	}
}
