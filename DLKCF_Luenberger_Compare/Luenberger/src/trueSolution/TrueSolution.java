package trueSolution;

import org.jblas.DoubleMatrix;
import doubleMatrix.GaussianGenerator;
import section.*;
import model.*;



public abstract class TrueSolution {

    int locDis; 
	
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
	
	public Section section;
	public int numSections;
		
	public DoubleMatrix measureVarDensity;//the measurement error covariance matrix
	public GaussianGenerator measureGeneratorDensity;	
	public DoubleMatrix measurementsDensity;//sensor data
	
	public DoubleMatrix trueStates;
	public DoubleMatrix currenttrueStates;	//used for computing ecolution of true density
	public DoubleMatrix boundaryFlux;
	
	public int stepMeasurements=1;//number of steps between two measurements
	public int sizeMeasurements; //for one state vector

	public int numUp;
	
	public TrueSolution trueLeft;
	public TrueSolution trueRight;

	abstract public double speed(double density);
	abstract public void propagate();
	abstract public double sending(double density);
	abstract public double receiving(double density);
	abstract public double computeFlux(double density1, double density2);
	
	public void initial(double _rhoLeft, double _rhoRight, double _totalrhoLeft, double _totalrhoRight, double _dt, double _dx, int _cellsec,int _overlapsec, int _index, int _numSecs) {
		index=_index;
		rhoLeft = _rhoLeft;
		rhoRight = _rhoRight;
		totalrhoLeft=_totalrhoLeft;
		totalrhoRight=_totalrhoRight;
		dx = _dx;
		dt = _dt;
		
		cellsec=_cellsec;
		overlapsec=_overlapsec;
		numSections=_numSecs;	
		cells=(numSections-1)*(cellsec-overlapsec)+cellsec;
				
		sizeMeasurements=2;//upstream and downstream measurements
		measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0081);//*measurement error covariance matrix
		measureGeneratorDensity = new GaussianGenerator(measureVarDensity);

		trueStates=DoubleMatrix.zeros(cellsec,1);
		currenttrueStates=trueStates.dup();

		//**Start setting initial condition for true solution
		locDis = (int)cellsec/2; 		
		for(int i=0;i<locDis;i++){
			trueStates.put(i,0,rhoLeft);
		}
		for(int i=locDis;i<cellsec;i++){
			trueStates.put(i,0,rhoRight);
		}		
		if (index==0){
			for(int i=0;i<5;i++){
				trueStates.put(i,0,totalrhoRight);
			}
		}		
		if (index==numSections-1){
			for(int i=cellsec-5;i<cellsec;i++){
				trueStates.put(i,0,0.35);
			}
		}		
		//**end setting initial condition of the true state
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
		DoubleMatrix noiseDensity = measureGeneratorDensity.sample();		
		for (int i=0; i<sizeMeasurements; i++) {			
			double pointDensity = trueStates.get(i*(cellsec-1),0);
//			double pointDensity = trueStates.get(i*distMeasurements,0);
//			pointDensity+=noiseDensity.get(i);//*uncomment to turn on measurement noise
			measurementsDensity.put(i, pointDensity);
		}		
	}

 	@SuppressWarnings({ "rawtypes", "unchecked" })
 	
 	public Section setSections() {	
 		Section secs;
 		if (trueStates.get(0,0)<=rhoCritical && trueStates.get(cellsec-1,0)<=rhoCritical){
			secs=Section.createSection("FF",this);
		}
		else if (trueStates.get(0,0)>rhoCritical && trueStates.get(cellsec-1,0)>rhoCritical){
			secs=Section.createSection("CC",this);
		}
		else if (trueStates.get(0,0)>rhoCritical && trueStates.get(cellsec-1,0)<=rhoCritical){
			secs=Section.createSection("CF",this);				
		}
		else {
			secs=Section.createSection("FC",this);
		}
		secs.densitysec1=trueStates;
		secs.index=index;
				    
		if (secs.getClass().getSimpleName().equals("CF")){
		    secs.getwavefront();
 		}
		else if (secs.getClass().getSimpleName().equals("FC")){
		    secs.getwavefront();
 		}
		if (secs.getClass().getSimpleName().equals("CF")||secs.getClass().getSimpleName().equals("FF")||secs.getClass().getSimpleName().equals("CC")){
		    secs.getModelA();
	 		secs.getModelB1();
			secs.getModelB2();
			secs.getModelB3();
		 }
 		return secs;
 	}
 	
 	public RoadModel setRoadModels(){
 		Section secs = setSections();
 		RoadModel rms;		
 		rms=RoadModel.createRoadModel(this, secs, "D");	
 		return rms;
 	}
}
