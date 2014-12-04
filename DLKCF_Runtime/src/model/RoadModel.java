package model;

import graph.Printer;

import org.jblas.DoubleMatrix;
import org.jfree.data.xy.XYSeries;

import doubleMatrix.Concat;
import doubleMatrix.GaussianGenerator;
import trueSolution.TriangularTrue;
import trueSolution.TrueSolution;//对于有public限制符的，也必须import
import section.*;


@SuppressWarnings("unused")
public abstract class RoadModel {

	abstract public void initialThis();
	abstract public DoubleMatrix getDensityMean(DoubleMatrix mean);
	abstract public DoubleMatrix getDensityVar(DoubleMatrix var);
//	abstract public DoubleMatrix getSpeedMean(DoubleMatrix mean);
//	abstract public DoubleMatrix getSpeedVar(DoubleMatrix var);
	abstract public DoubleMatrix propagate(DoubleMatrix _density);
//	abstract public DoubleMatrix propagatewithnoise(DoubleMatrix samples, boolean isNoise);
	abstract public DoubleMatrix getMeasureVector();	
	
//	public int type;
	
	public DoubleMatrix modelVar;//modelVar in Filter
	
	public DoubleMatrix initialMean;//initial guess
	public DoubleMatrix initialVar;//initial guess of the variance
	
//	public boolean isAdaptive;
	
	public int size; //for the local section
	public int sizeMeasurements; //for the local section
	public int stepMeasurements;
	
	public DoubleMatrix measureVar;
	public DoubleMatrix measure;

	//should contain what exact speed model used 
	public TrueSolution trueSolution;
	public Section section;
	
	public String nameModel;
	public GaussianGenerator initialGeneratorDensity;
	

	int cellsec;
	
//	Propagator propagator; 
//	SpeedFunction speedFunction;
	
	DoubleMatrix initialDensity;//used to set initialMean
//	DoubleMatrix initialSpeed;
	
	public DoubleMatrix modelVarDensity;//used to set modelVar&modelVar in Filter
//	public DoubleMatrix modelVarSpeed;

	DoubleMatrix initialVarDensity;//used to set initialVar
//	DoubleMatrix initialVarSpeed;
//	DoubleMatrix initialVarParameters;
	
//	String measurementType;
	
	public void initial(TrueSolution _trueSolution, Section _section) {
		
		trueSolution = _trueSolution;
		section = _section;
		cellsec = section.cellsec;
		
		stepMeasurements = trueSolution.stepMeasurements;

		//later...public void
		defineVarAndMean();
        //later...abstract class
		initialThis(); 
		
 		nameModel = getClass().getSimpleName()+"_";
		nameModel+=Integer.toString((int) Math.floor(10*trueSolution.rhoLeft));
		nameModel+=Integer.toString((int) Math.floor(10*trueSolution.rhoRight));
		nameModel+="_";
		nameModel+=Integer.toString((int) Math.floor(section.index));
	}
		
	public double getSpaceStep() {
		return trueSolution.dx;
	}
//[question]don't quite understand
	public static RoadModel createRoadModel(TrueSolution _trueSolution, Section _section, String nameEvolution ) {
 		
 		
 		String name = "model."+nameEvolution;
 		try {			
			@SuppressWarnings("rawtypes")
			Class cl = Class.forName(name);			
			@SuppressWarnings("unchecked")
			RoadModel m = (RoadModel) cl.getConstructor().newInstance(); 
			m.initial(_trueSolution, _section);

			return m;
		}
		catch(Throwable t) {System.out.println("Road model creation has failed");t.printStackTrace();}
		return null;
	}
 	
 	public void defineVarAndMean() {
		double rhoLeft = section.densitysec1.get(0,0);//This rhoLeft only valid in this local class
		double rhoRight = section.densitysec1.get(cellsec-1,0);
		if (section.index==0){
			System.out.print(rhoLeft);
			System.out.print(rhoRight);
		}
		
		double wholeRhoLeft=trueSolution.totalrhoLeft;
		double wholeRhoRight=trueSolution.totalrhoRight;
		DoubleMatrix initialGuess=getInitial(wholeRhoLeft, wholeRhoRight);
		
 		
 		initialVarDensity = DoubleMatrix.eye(cellsec);
		double medium = 0.01*(rhoLeft + rhoRight);
		double a = 2/((double)cellsec);
		for (int i = 0; i<cellsec; i++) {
			//[question]this is quite different from defined in the paper
			initialVarDensity.put(i,i, 0.001*(section.index+1) + medium*Math.pow(1-Math.abs(i*a-1), 1));//used to add some randomness to initial guess
	
			//initialVarDensity.put(i,i,0);
		}
		
		initialGeneratorDensity = new GaussianGenerator(initialVarDensity);

		//NEED CHANGE /PUT LARGER VARIANCE IN THE STATE ASSUMED TO BE CONSTANT//
		modelVarDensity = DoubleMatrix.eye(cellsec).mmul(0.0009);

		//NEED CHANGE /PUT LARGER VARIANCE IN THE STATE ASSUMED TO BE CONSTANT//
		
		//initialVarDensity = DoubleMatrix.eye(cells).mmul(0.005);	
//		initialDensity = trueSolution.getInitialDensity().getRange(section.index*(cellsec-1), (section.index+1)*(cellsec-1)+1, 0, 1);
//		initialDensity = section.densitysec;	
//		initialDensity = getRiemman(rhoLeft, rhoRight);
//RELATED TO SYSTEM STRUCTURE
		initialDensity = initialGuess.getRange(section.index*(cellsec-trueSolution.overlapsec),section.index*(cellsec-trueSolution.overlapsec)+cellsec ,0,1);
//RELATED TO SYSTEM STRUCTURE		
		DoubleMatrix initialNoiseDensity = initialGeneratorDensity.sample();
//		DoubleMatrix initialNoiseDensity1 = DoubleMatrix.ones(cellsec, 1).mmul(0.05*(section.index+1));
		initialDensity=initialDensity.add(initialNoiseDensity);
		if (section.getClass().getSimpleName().equals("FC")){
			if (section.index==0){
				for(int i=0;i<3;i++){
					initialDensity.put(i,0,0);
				}
				
				for(int i=3;i<10;i++){
					initialDensity.put(i,0,1);
				}
			}
			else if (section.index==trueSolution.numSections-1){
				for(int i=0;i<7;i++){
					initialDensity.put(cellsec-1-i,0,1);
				}
				
				for(int i=7;i<10;i++){
					initialDensity.put(cellsec-1-i,0,0);
				}
			}
			
		}
		
//		if(section.index==1){
//			initialDensity.put(0,0,0);
//			for(int i=1;i<cellsec;i++){
//				initialDensity.put(i,0,1);
//			}
//		}
//		else{
//			for(int i=0;i<cellsec;i++){
//				initialDensity.put(i,0,1);
//			}
//		}
//		if (section.getClass().getSimpleName().equals("FF")){
//			for(int i=0;i<cellsec;i++){
//				initialDensity.put(i,0,0);
//			}
//		}
//		else if (section.getClass().getSimpleName().equals("CC")){
//			for(int i=0;i<cellsec;i++){
//				initialDensity.put(i,0,1);
//			}
//		}
//		else if (section.getClass().getSimpleName().equals("CF")){
//			for(int i=0;i<(int)cellsec/2;i++){
//				initialDensity.put(i,0,1);
//			}
//			for(int i=(int)cellsec/2;i<cellsec;i++){
//				initialDensity.put(i,0,0);
//			}
//		}
//		else{
//			for(int i=0;i<(int)cellsec/2;i++){
//				initialDensity.put(i,0,0);
//			}
//			for(int i=(int)cellsec/2;i<cellsec;i++){
//				initialDensity.put(i,0,1);
//			}
//		}
		
//		initialDensity=initialDensity.add(initialNoiseDensity);
		//[?question?] different from defined in the paper
//		initialSpeed = getRiemman(speedLeft,speedRight);
//		initialSpeed = getInitial(speedLeft,speedRight);
//		initialSpeed = new DoubleMatrix(cells);
//		for (int i = 0; i<cells; i++) initialSpeed.put(i,trueSolution.speed(initialDensity.get(i)));
//		for (int i = 0; i<cells; i++) initialSpeed.put(i,speedFunction.getValue(initialDensity.get(i)));
		//[?question?] initial Density already defined before
//		for (int i = 0; i<cells; i++) initialDensity.put(i,speedFunction.getInverseValue(initialSpeed.get(i), false));
 	}
 	
	public DoubleMatrix getInitial(double left, double right) {
		DoubleMatrix initial = new DoubleMatrix(trueSolution.cells);
		double slope = (right - left) / ((double)trueSolution.cells); 
		for(int i=0; i<trueSolution.cells; i++) {
			initial.put(i,left + i*slope);
		}
		return initial;
	}
	
//	public DoubleMatrix getInitial(double left, double right) {
//		DoubleMatrix initial = new DoubleMatrix(cells);
//		for(int i=0; i<((int) cells/2); i++) {
//			initial.put(i,left);
//		}
//		for(int i=((int) cells/2); i<cells; i++) {
//			initial.put(i, right);
//		}
//		return initial;
//	}
	
//	public void setInitial(DoubleMatrix _initialDensity) {
//		initialDensity = _initialDensity;
//	}
	
	public DoubleMatrix getRiemman(double left, double right) {
		DoubleMatrix initial = new DoubleMatrix(cellsec);
		for(int i=0; i<((int) cellsec/2); i++) {
			initial.put(i,left);
		}
		for(int i=((int) cellsec/2); i<cellsec; i++) {
			initial.put(i, right);
		}
		return initial;
	}
	
	public void newBoundaries(double rhoLeft, double rhoRight) {
//		speedFunction.setIni();
		trueSolution.newBoundaries(rhoLeft, rhoRight);
//		propagator = new Propagator(trueSolution.dt/trueSolution.dx, rhoLeft, rhoRight);
		section=trueSolution.setSections();
		initial(trueSolution,section);
	}
	
//	public XYSeries createSpeedXYSeries() {
//		return speedFunction.createXYSeries();
//	}
	
//	public void setSpeedFunction(SpeedFunction S) {
//		speedFunction = S;
//	}
	
//	public SpeedFunction getSpeedFunction() {
//		return speedFunction;
//	}
	
	public void updateTrueSolution() {
		trueSolution.update();
	}
	
	public void setSetion(Section _section){
		section=_section;
		
	}
	
	public void updateModelVar() {
		if(section.getClass().getSimpleName().equals("FF")){
			modelVar.put(0,0,0.0009);
//			modelVar.put(cellsec-1,cellsec-1,0.25);
//			modelVar=modelVar;
		}
		else if (section.getClass().getSimpleName().equals("CC")){
//			modelVar.put(0,0,0.25);
			modelVar.put(cellsec-1,cellsec-1,0.0009);
//			modelVar=modelVar;
		}
		else if (section.getClass().getSimpleName().equals("FC")){
			modelVar.put(cellsec-1,cellsec-1,0.0009);
			modelVar.put(0,0,0.0009);
//			modelVar=modelVar;
		}
		else{
//			modelVar.put(cellsec-1,cellsec-1,0.25);
//			modelVar.put(0,0,0.25);
		}
//		modelVar=DoubleMatrix.eye(cellsec).mmul(4);
	}
	
	public void updateMeasurement(){
        measureVar=trueSolution.measureVarDensity;
		
		sizeMeasurements = measureVar.getColumns();
		
		measure = DoubleMatrix.zeros(sizeMeasurements,size);
		if (sizeMeasurements==2){
			measure.put(0,0,1);
			measure.put(1,cellsec-1,1);
		}
		else{
			for (int i=0;i<sizeMeasurements;i++){
				if(i<2){
					measure.put(i, i*(trueSolution.overlapsec-1),1);
				}
				else{
					measure.put(i, cellsec-1-(3-i)*(trueSolution.overlapsec-1),1);
				}
				
			}	
		}
			
	}
	
	
}
