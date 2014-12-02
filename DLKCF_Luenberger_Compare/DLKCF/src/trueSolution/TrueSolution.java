package trueSolution;

import org.jblas.DoubleMatrix;
import doubleMatrix.GaussianGenerator;
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
	
	int locDis; 

	public DoubleMatrix measureVarDensity;//the measurement error covariance matrix
	public GaussianGenerator measureGeneratorDensity;//To generate measurement noise	
	public DoubleMatrix measurementsDensity;//measurement(sensor data)
	public int stepMeasurements=1;//*steps between two measurements
	public int sizeMeasurements; 
	
	public DoubleMatrix trueStates;
	public DoubleMatrix trueStatesPrior;//true state at the previous time step

	public int numUp;//current time step 
	
	public TrueSolution trueLeft;//left adjacent section
	public TrueSolution trueRight;//right adjacent section

	abstract public double speed(double density);
	abstract public double fluxPrime(double density);
	abstract public double fluxPrimeInverse(double speed);
	abstract public void propagate();	
	abstract public double sending(double density);
	abstract public double receiving(double density);
	abstract public double computeFlux(double density1, double density2);

	
	public void initial(TrueSolutionCTM _trueCTM, double _dt, double _dx, int _cellsec,int _overlapsec, int _index, int _numSecs) {
		
		trueCTM=_trueCTM;
		index=_index;
		dx = _dx;
		dt = _dt;
		
		cellsec=_cellsec;
		overlapsec=_overlapsec;
		numSections=_numSecs;
		cells=(numSections-1)*(cellsec-overlapsec)+cellsec;
		sizeMeasurements=2;
		measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0081);//*Measurement error covariance matrix
		measureGeneratorDensity = new GaussianGenerator(measureVarDensity);
		locDis = (int)cellsec/2; //localization of the discontinuity

		trueStates=DoubleMatrix.zeros(cellsec,1);
		trueStates=trueCTM.trueStatesCTM.getRange(index*(cellsec-overlapsec), index*(cellsec-overlapsec)+cellsec, 0, 1);		
		rhoLeft=trueStates.get(0,0);
		rhoRight=trueStates.get(cellsec-1,0);
		totalrhoLeft=trueCTM.trueStatesCTM.get(0,0);
		totalrhoRight=trueCTM.trueStatesCTM.get(cells-1,0);	
		trueStatesPrior=trueStates.dup();
		
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

		locDis = (int)cells/2; 
		
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
    
    //sharing sensor data with neighbors
    public void sharemeasurement() {	
		if (overlapsec!=1){
			if (index==0){
				measurementsDensity.put(3,0,trueRight.measurementsDensity.get(1,0));
			}
			else if (index==numSections-1){
				measurementsDensity.put(0,0,trueLeft.measurementsDensity.get(2,0));
			}
			else{
				measurementsDensity.put(0,0,trueLeft.measurementsDensity.get(2,0));
				measurementsDensity.put(3,0,trueRight.measurementsDensity.get(1,0));
			}
		}
		else{
			if (index!=numSections-1){
				measurementsDensity.put(1,0,trueRight.measurementsDensity.get(0,0));
			}
		}		
	}
	
	private void newMeasurements() {
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) getMeasurementsAll();
	}
	
	private void getMeasurementsAll() {
		
		measurementsDensity = DoubleMatrix.zeros(sizeMeasurements, 1);
		DoubleMatrix noiseDensity = measureGeneratorDensity.sample();
		noiseDensity=DoubleMatrix.zeros(noiseDensity.getRows());//*comment to turn on measurement noise
					
		if (overlapsec!=1){	
			if (index==0){
				
				for (int i=0;i<2;i++){
					double pointDensity = trueStates.get(i*(overlapsec-1),0);
                    pointDensity+=noiseDensity.get(i);
					measurementsDensity.put(i, pointDensity);
				}
				double pointDensity2 = trueStates.get(cellsec-1-(3-2)*(overlapsec-1),0);
				pointDensity2+=noiseDensity.get(2);
				measurementsDensity.put(2, pointDensity2);
			}
			else if (index==numSections-1){
				for (int i=0;i<2;i++){
					double pointDensity = trueStates.get(cellsec-1-i*(overlapsec-1),0);
                    pointDensity+=noiseDensity.get(sizeMeasurements-1-i);
					measurementsDensity.put(sizeMeasurements-1-i, pointDensity);
				}
				
				double pointDensity1 = trueStates.get(cellsec-1-2*(overlapsec-1),0);
					pointDensity1+=noiseDensity.get(1);
				    measurementsDensity.put(1, pointDensity1);
																															
			}
			else{
				double pointDensity1 = trueStates.get((overlapsec-1),0);
	            pointDensity1+=noiseDensity.get(1);
				measurementsDensity.put(1, pointDensity1);
				double pointDensity2 = trueStates.get(cellsec-1-(3-2)*(overlapsec-1),0);
				pointDensity2+=noiseDensity.get(2);
				measurementsDensity.put(2, pointDensity2);
			}											
		}
		else{
			if (index!=numSections-1){
				double pointDensity = trueStates.get(0,0);
	            pointDensity+=noiseDensity.get(0);
			    measurementsDensity.put(0, pointDensity);								
			}
			else{
				for (int i=0; i<sizeMeasurements; i++) {
					double pointDensity = trueStates.get(i*(cellsec-1),0);
                    pointDensity+=noiseDensity.get(i);
				    measurementsDensity.put(i, pointDensity);
				    }			
			}
		}		
	}

@SuppressWarnings({ "rawtypes", "unchecked" })

//determine the mode from boundary measurements
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
