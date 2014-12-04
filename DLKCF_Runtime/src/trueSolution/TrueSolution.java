package trueSolution;

import org.jblas.DoubleMatrix;
import org.jfree.data.xy.XYSeries;

import doubleMatrix.GaussianGenerator;
import filters.Filter;
import section.*;
import model.*;



public abstract class TrueSolution {

	double llocDis;
	int locDis; //localization of the discontinuity
	
	public double rhoLeft; //left initial condition 
	public double rhoRight; //right initial condition
	public double totalrhoLeft; //left initial condition 
	public double totalrhoRight; //right initial condition
	//[question] what's the use of the following two variables?
	protected double fluxPrimeLeft;
	protected double fluxPrimeRight;
	
	public double lambda; //speed of shock wave (if wave == true)
	boolean wave; //indicates whether there is a shock wave
	
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
	

	
	
	
	//PARAMETER CHANGE//
	public int numSections;
	//PARAMETER CHANGE//
	
	//the measurement error covariance matrix
	public DoubleMatrix measureVarDensity;
	public GaussianGenerator measureGeneratorDensity;
	
//	public DoubleMatrix measureVarSpeed;
//	public GaussianGenerator measureGeneratorSpeed;
	
	public DoubleMatrix measurementsDensity;
	
//	public DoubleMatrix measurementsSpeed;
	
	public DoubleMatrix trueStates;
//	public DoubleMatrix trueStatesCTM;
	public DoubleMatrix trueStatesPrior;
	
	//Is this series of points where there is a measurement?
//	public XYSeries densityPoints = new XYSeries("Density Measure points");
//	public XYSeries speedPoints = new XYSeries("Speed Measure points");
	
	public int stepMeasurements=1;//steps between two measurements
	public int sizeMeasurements; //for one state vector
	
//	public int distMeasurements; //distance between two measurements
	
//	public int[] measureLocation;

	//What is numUp?
	public int numUp;
	
	public TrueSolution trueLeft;
	public TrueSolution trueRight;

	abstract void computeWave();
	abstract public double speed(double density);
	abstract public double fluxPrime(double density);
	abstract public double fluxPrimeInverse(double speed);
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
		
		
		llocDis = ((int)cells/2 )*dx-dx/2;
		
		//SYSTEM STRUCTURE//
				cellsec=_cellsec;
				overlapsec=_overlapsec;
				numSections=_numSecs;
				
				cells=(numSections-1)*(cellsec-overlapsec)+cellsec;
				
				
				sizeMeasurements=4;
				
//			    measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0025*(index+1));//DoubleMatrix.eye generates identical matrix
			    measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0004);
			    measureGeneratorDensity = new GaussianGenerator(measureVarDensity);
				
//				measureLocation = new int[sizeMeasurements];
//				for(int i = 0; i<sizeMeasurements; i++) {
//					measureLocation[i] = i*distMeasurements;		
//				}
				//SYSTEM STRUCTURE//

		trueStates=DoubleMatrix.zeros(cellsec,1);
//		trueStatesCTM=DoubleMatrix.zeros(cells,1);
		
		//[question] why compute locDis by this 
		locDis = (int)cellsec/2; //localization of the discontinuity
//		int locDis1 = (int)cells/2;
		for(int i=0;i<locDis;i++){
			trueStates.put(i,0,rhoLeft);
		}
		for(int i=locDis;i<cellsec;i++){
			trueStates.put(i,0,rhoRight);
		}
		
//		if (index==0){
//			for(int i=0;i<5;i++){
//				trueStates.put(i,0,totalrhoRight);
//			}
//		}
		
//		if (index==numSections-1){
//			for(int i=cellsec-5;i<cellsec;i++){
//				trueStates.put(i,0,0.35);
//			}
//		}
		

		
		
		trueStatesPrior=trueStates.dup();
		
//		if (index==0){
//			for(int i=0;i<7;i++){
//				trueStates.put(i,0,totalrhoLeft);
//			}
//			for(int i=7;i<cellsec;i++){
//				trueStates.put(i,0,totalrhoRight);
//			}
//		}
		
		numUp = 0;
		computeWave();
	}
	//......later
	public double[] getDensityBoundaries() {
		double[] res = new double[2];
		res[0] = trueStates.get(0,0);
		res[1] = trueStates.get(cellsec-1,0);
		return res;
	}
	
//------	
	
	//Get Density Value From Given Position
	public double getDensityValueFromPos(double pos) {
		return getDensityValue(pos, ((double)numUp)*dt);//number of steps up to now
	}
	//the abstract method 'speed' was defined before in TrueSolution [question] how to treat noise in speed function?
	
	
	public double getDensityValue(double x, double t) {	
		double xx = x-llocDis; //because not centered
		if (wave) {
			if ( xx <= lambda*t) return totalrhoLeft;
			else return totalrhoRight;
		}
		
		else {
				if (xx < fluxPrimeLeft*t) return totalrhoLeft;
				else {
					if (xx > fluxPrimeRight*t) return totalrhoRight;
					return fluxPrimeInverse(xx/t);//prime is ' in paper
				}
		}
		
	}
	
//	public DoubleMatrix getInitialDensity() {
//		int i=150;
//		DoubleMatrix initialDensity=new DoubleMatrix (cells,1);
//		for (int k=0;k<cells;k++){
//			if(k<i){
//				initialDensity.put(k,0,rhoLeft);
//			}
//			else{
//				initialDensity.put(k,0,rhoRight);
//			}
//		}
//		return initialDensity;
//		
//	}

	public double flux(double density) { 	
		return density*speed(density);
	}
	//what is the use of this method?
	public void newBoundaries(double _rhoLeft, double _rhoRight) {
		numUp = 0;
		rhoLeft = _rhoLeft;
		rhoRight = _rhoRight;
        trueStates=DoubleMatrix.zeros(cellsec,1);
		
		//[question] why compute locDis by this 
		locDis = (int)cells/2; //localization of the discontinuity
		
		for(int i=0;i<locDis;i++){
			trueStates.put(i,0,rhoLeft);
		}
		for(int i=locDis;i<cellsec;i++){
			trueStates.put(i,0,rhoRight);
		}
	}
	
//	public XYSeries getDensityXYSeries() {
//		return densityPoints;
//	}
	
	
	
	public void update() {
		
		numUp++;
		propagate();
//		newMeasurements();
		
	}
	
    public void updatemeasurement() {
		
		newMeasurements();
		
	}
	
	private void newMeasurements() {
		//[question] what is this 'if' used for
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) getMeasurementsAll();
//		else {
//			densityPoints.clear();
//
//		}
	}
	
	private void getMeasurementsAll() {
		
//		DoubleMatrix trueStatesCTM_sec=trueStatesCTM.getRange(section.index*(cellsec-overlapsec), section.index*(cellsec-overlapsec)+cellsec, 0, 1);
//		if (section.getClass().getSimpleName().equals("FF")||section.getClass().getSimpleName().equals("CC")||section.getClass().getSimpleName().equals("CF")){
			
			

			measurementsDensity = DoubleMatrix.zeros(sizeMeasurements, 1);
			
			DoubleMatrix noiseDensity = measureGeneratorDensity.sample();
			
			
			
			for (int i=0; i<sizeMeasurements; i++) {
				if (i<2){
					double pointDensity = trueStates.get(i*(overlapsec-1),0);
//[Measurement noise]
//                    pointDensity+=noiseDensity.get(i);
					measurementsDensity.put(i, pointDensity);
				}
				else{
//[Double check]
					double pointDensity = trueStates.get(cellsec-1-(3-i)*(overlapsec-1),0);
//[Measurement noise]
//				    pointDensity+=noiseDensity.get(i);
					measurementsDensity.put(i, pointDensity);
				}
				
				
			}
//		}
		
//		else{
			
			

//			measurementsDensity = DoubleMatrix.zeros(sizeMeasurements, 1);
			
//			DoubleMatrix noiseDensity = measureGeneratorDensity.sample();
			
//			for (int i=0; i<sizeMeasurements; i++) {
				
//				double pointDensity = trueStates.get(i*(cellsec-1),0);

//[Measurement noise]
//				pointDensity+=noiseDensity.get(i);
//				measurementsDensity.put(i, pointDensity);
//			}
//		}
		

//		densityPoints.clear();
//		if (index==0){
//			
//			for (int i=0; i<sizeMeasurements-1; i++) {
//				
//				double pointDensity = trueStates.get(i*distMeasurements,0);

//				pointDensity+=noiseDensity.get(i);
//				measurementsDensity.put(i, pointDensity);

//				densityPoints.add(l, pointDensity);

//			}
			
//			for (int i=sizeMeasurements-1; i<sizeMeasurements; i++ ){
//				double pointDensity = trueRight.trueStates.get(distMeasurements,0);
//				measurementsDensity.put(i, pointDensity);
//			}
//			
//		}
		
//		else if (index==numSections-1){
//			
//			for (int i=1; i<sizeMeasurements; i++) {
//				
//				double pointDensity = trueStates.get(i*distMeasurements,0);

//				pointDensity+=noiseDensity.get(i);
//				measurementsDensity.put(i, pointDensity);

//				densityPoints.add(l, pointDensity);

//			}
			
//			for (int i=0; i<1; i++ ){
//				double pointDensity = trueLeft.trueStates.get((sizeMeasurements-2)*distMeasurements,0);
//				measurementsDensity.put(i, pointDensity);
//			}
			
			
//		}
		
//		else{
			
//			for (int i=0; i<1; i++ ){
//				double pointDensity = trueLeft.trueStates.get((sizeMeasurements-2)*distMeasurements,0);
//				measurementsDensity.put(i, pointDensity);
//			}
			
//            for (int i=1; i<sizeMeasurements-1; i++) {
//				
//				double pointDensity = trueStates.get(i*distMeasurements,0);

//				pointDensity+=noiseDensity.get(i);
//				measurementsDensity.put(i, pointDensity);

//				densityPoints.add(l, pointDensity);

//			}
            
 //           for (int i=sizeMeasurements-1; i<sizeMeasurements; i++ ){
//				double pointDensity = trueRight.trueStates.get(distMeasurements,0);
//				measurementsDensity.put(i, pointDensity);
//			}
//		}

		
		
	}
//use graph window to show relationship between speed and density

	
	

	//[question] what's this method used for
 	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static TrueSolution createTrueSolution(String nameTrueSolution, double rhoLeft, double rhoRight, double dt, double dx, int cells) {		
 //		String name = "trueSolution."+nameTrueSolution;
 //		try {
//			Class cl = Class.forName(name);
//			
//		    Class[] types = new Class[]{Double.class, Double.class, Double.class, Double.class,  Integer.class};
//		    TrueSolution ts = (TrueSolution) cl.getConstructor(types).newInstance(new Object[]{rhoLeft, rhoRight, dt, dx, cells});
//		 
 // 			return ts;
//		}
//		catch(Throwable t) {System.out.println(t);}
//		
//		return null;
//	}
 	
//	public DoubleMatrix getDiscretizedTrueSolution() {
 	
// 		DoubleMatrix DTS= DoubleMatrix.zeros(cells, 1);
 //		for (int j=0; j<cells ; j++) {
//			double pos = ((double)j)*dx;
//			DTS.put(j,0, getDensityValueFromPos(pos) );
//		}
// 		return DTS;
// 	}
 	
 	public Section setSections() {
 		
 //		SpeedFunction[] sfs = new SpeedFunction[numSections];
 		
 //		SpeedFunction[] sfs = new SpeedFunction[2];
 //		
 		Section secs;
		//DETERMINE¡¡MODE BY TRUESOLN OR MEASUREMENT//
 		
 	//		for (int i=0; i<2; i++){
 		if (trueStates.get(0,0)<=rhoCritical && trueStates.get(cellsec-1,0)<=rhoCritical){
			secs=Section.createSection("FF",this);
	//			sfs[i]=SpeedFunction.createSpeedFunction("Greenshield");
			}
			else if (trueStates.get(0,0)>rhoCritical && trueStates.get(cellsec-1,0)>rhoCritical){
			secs=Section.createSection("CC",this);
	//			sfs[i]=SpeedFunction.createSpeedFunction("Triangular");
			}
			else if (trueStates.get(0,0)>rhoCritical && trueStates.get(cellsec-1,0)<=rhoCritical){
			    secs=Section.createSection("CF",this);
	//			sfs[i]=SpeedFunction.createSpeedFunction("SmuldersGen");
				
		}
			//FC MODE UNDER DEVELOPMENT//
			else {
			secs=Section.createSection("FC",this);
	//			sfs[i]=SpeedFunction.createSpeedFunction("Greenshiled");
			}
 			//FC MODE UNDER DEVELOPMENT//
 			//DETERMINE¡¡MODE BY TRUESOLN OR MEASUREMENT//
            
 	//	    secs.densitysec=trueStates;
		    secs.densitysec1=trueStates;
		    secs.index=index;
		
 		    
		    if (secs.getClass().getSimpleName().equals("CF")){
		    	secs.getwavefront();
 		    }
		    else if (secs.getClass().getSimpleName().equals("FC")){
//		    	secs.wavefront=0;
		    	secs.getwavefront();
 //		    	System.out.print(secs[i].wavefront);
 		    }
		    
		    if (secs.getClass().getSimpleName().equals("CF")||secs.getClass().getSimpleName().equals("FF")||secs.getClass().getSimpleName().equals("CC")){
		    	secs.getModelA();
	 		    secs.getModelB1();
			    secs.getModelB2();
		    }
 		    
		    
 		
 		

 		
 //		secs[0].ModelA.mmul(secs[0].densitysec).addi(secs[0].ModelB1.mmul(DoubleMatrix.ones(secs[0].cellsec, 1).mmul(secs[0].rhoMax))).addi(secs[0].ModelB2.mmul(DoubleMatrix.ones(secs[0].cellsec, 1).mmul(secs[0].rhoCritical*secs[0].speedMax))).print();;
 //		DoubleMatrix A=new DoubleMatrix(3,2);
 //		A.print();
 //		RoadModel rm=RoadModel.createRoadModel(this, secs[1], "D");
 //		getMeasurementsAll();
 //		measurementsDensity.print();
 //		rm.getMeasureVector().print();
 		
 //		double[] a={1,2,3,4};
 //		DoubleMatrix aa=new DoubleMatrix(a);
 //		aa.getRange(0, 2, 0, 1).print();
 //		System.out.print(secs[1].index);
 		return secs;
 //		for (int i=0; i<numSections; i++){
 //           secs[i].ModelA.print();
 //		}
 //		for (int i=0; i<numSections; i++){
 //			sections[i].getModelA();
 //			sections[i].getModelB1();
 //			sections[i].getModelB2();
 //		}
 //		for (int i=0; i<numSections; i++){
            //System.out.print(sections[i].getClass().getSimpleName());
 //		}
 	}
 	
 	public RoadModel setRoadModels(){
 		Section secs = setSections();
 		RoadModel rms;
 		
 			rms=RoadModel.createRoadModel(this, secs, "D");
 		
 		return rms;
 	}
 	
 	public void refinetrueSolution(){
 		if (numSections!=1){
 //	 		if(totalrhoLeft<=rhoCritical&&totalrhoRight>rhoCritical){
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
 //	 		}
 //	 		else{
 //	 			if(section.index==0){
 //	 				if (section.getClass().getSimpleName().equals("FF")||section.getClass().getSimpleName().equals("CF")){
 //	 					trueStates=trueStates;
 //	 				}
 //	 				else if (section.getClass().getSimpleName().equals("CC")){
 //	 					trueStates.put(cellsec-1,0,trueRight.trueStates.get(overlapsec-1,0));
 //	 				}
 	 				
 //	 			}
 //	 			else if (section.index==numSections-1){
 //	 				if (section.getClass().getSimpleName().equals("CC")||section.getClass().getSimpleName().equals("CF")){
 //	 					trueStates=trueStates;
 //	 				}
 //	 				else if (section.getClass().getSimpleName().equals("FF")){
 //	 					trueStates.put(0,0,trueLeft.trueStates.get(cellsec-overlapsec,0));
 //	 				}
 	 				
 //	 			}
// 	 			else{
// 	 				if (section.getClass().getSimpleName().equals("FF")){
 //	 					trueStates.put(0,0,trueLeft.trueStates.get(cellsec-overlapsec,0));
 //	 				}
 //	 				else if (section.getClass().getSimpleName().equals("CC")){
 //	 					trueStates.put(cellsec-1,0,trueRight.trueStates.get(overlapsec-1,0));
 //	 				}
 	 				
// 	 			}
 //	 		}
 		}

	}
 	
 	public void updateMeasurement(){
//		if (section.getClass().getSimpleName().equals("FF")||section.getClass().getSimpleName().equals("CC")||section.getClass().getSimpleName().equals("CF")){
//			sizeMeasurements=4;
//			measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0025*(index+1));
//		    measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.005);//DoubleMatrix.eye generates identical matrix
//			measureGeneratorDensity = new GaussianGenerator(measureVarDensity);
//			
			
//		}
		
//		else{
//			sizeMeasurements=2;
//			measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.0025*(index+1));
//		    measureVarDensity = DoubleMatrix.eye(sizeMeasurements).mul(0.005);//DoubleMatrix.eye generates identical matrix
//			measureGeneratorDensity = new GaussianGenerator(measureVarDensity);			
//		}
 	}
 	
// 	public void test(){
 //		SpeedFunction[] sfs = new SpeedFunction[numSections];
//	    sfs[0]=SpeedFunction.createSpeedFunction("Greenshield");
//		double[][] limit={{0.8,1.2},{0.8,1.2},{1.2,1.8},{0.8,1.2}};
//		sfs[0].getParametersNoise(limit);
//		sfs[1]=SpeedFunction.createSpeedFunction("Greenshield");
//		DoubleMatrix para=new DoubleMatrix(sfs[1].parameters);
//		para.print();
 //	}
 	
//	public void test1(){
 //		Section ys=Section.createSection("FF",this);
 //		SpeedFunction sfs=SpeedFunction.createSpeedFunction("Greenshield");
 //	}
}
