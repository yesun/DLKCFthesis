package trueSolution;

import org.jblas.DoubleMatrix;
import org.jfree.data.xy.XYSeries;

import doubleMatrix.GaussianGenerator;
import filters.Filter;
import section.*;
import model.*;


//this TrueSolution is the used to compute the true state in each local section
public abstract class TrueSolution {


	int locDis; //location of the discontinuity
	
	public double rhoLeft; //left initial condition of the section
	public double rhoRight; //right initial condition of the section
	public double totalrhoLeft; //left initial condition of the network
	public double totalrhoRight; //right initial condition network

	
	public double rhoMax;
	public double speedMax;
	public double rhoCritical;
	
	public double dt;
	public double dx;
	public int cells;//total number of cells in the network
	public int cellsec;//number of cells in the section
	public int overlapsec;//number of cells in the overlapping region
	public int index;//section index
	
	public Section section;
	public int numSections;//number of sections in the network
	
	
	public DoubleMatrix measureVarDensity;//the measurement error covariance matrix
	public GaussianGenerator measureGeneratorDensity;
	public DoubleMatrix measurementsDensity;
	
	public DoubleMatrix trueStates;
	public DoubleMatrix trueStatesPrior;//true state of the previous one step
	
	
	public int stepMeasurements=1;//steps between two measurements
	public int sizeMeasurements; //dimension of the measurement vector in the section
	
	public int numUp;//current time step
	
	public TrueSolution trueLeft;//true solution of the left section
	public TrueSolution trueRight;//true solution of the right section
	



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
		
		

		
//begin{network setup}
		cellsec=_cellsec;
		overlapsec=_overlapsec;
		numSections=_numSecs;
		cells=(numSections-1)*(cellsec-overlapsec)+cellsec;
		sizeMeasurements=4;				
//	    measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0025*(index+1));//DoubleMatrix.eye generates identical matrix
	    measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0004);
		measureGeneratorDensity = new GaussianGenerator(measureVarDensity);
//end{network setup}

		trueStates=DoubleMatrix.zeros(cellsec,1);		
		locDis = (int)cellsec/2; //location of the discontinuity
		for(int i=0;i<locDis;i++){
			trueStates.put(i,0,rhoLeft);
		}
		for(int i=locDis;i<cellsec;i++){
			trueStates.put(i,0,rhoRight);
		}
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
	
	private void newMeasurements() {
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) getMeasurementsAll();
	}
	
	private void getMeasurementsAll() {
		measurementsDensity = DoubleMatrix.zeros(sizeMeasurements, 1);
		DoubleMatrix noiseDensity = measureGeneratorDensity.sample();
		for (int i=0; i<sizeMeasurements; i++) {
				if (i<2){
					double pointDensity = trueStates.get(i*(overlapsec-1),0);
//[Measurement noise]
//                  pointDensity+=noiseDensity.get(i);
					measurementsDensity.put(i, pointDensity);
				}
				else{
					double pointDensity = trueStates.get(cellsec-1-(3-i)*(overlapsec-1),0);
//[Measurement noise]
//				    pointDensity+=noiseDensity.get(i);
					measurementsDensity.put(i, pointDensity);
				}
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
		    }
 		return secs;
 	}
 	
 	public RoadModel setRoadModels(){
 		Section secs = setSections();
 		RoadModel rms;
 		
 			rms=RoadModel.createRoadModel(this, secs, "D");
 		
 		return rms;
 	}
 	
 	public void refinetrueSolution(){
 		if (numSections!=1){
 	 			if(section.index==0){
 	 				trueStates.put(cellsec-1,0,trueRight.trueStates.get(overlapsec-1,0));
 	 			}
 	 			else if (section.index==numSections-1){
 	 				trueStates.put(0,0,trueLeft.trueStates.get(cellsec-overlapsec,0));
 	 			}
 	 			else{
 	 				trueStates.put(0,0,trueLeft.trueStates.get(cellsec-overlapsec,0));
 						trueStates.put(cellsec-1,0,trueRight.trueStates.get(overlapsec-1,0));
 	 			}
 		}

	}
 	

}
