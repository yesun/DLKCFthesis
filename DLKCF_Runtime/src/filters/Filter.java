package filters;

import model.RoadModel;

import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import doubleMatrix.InverseMatrix;
import section.*;




public abstract class Filter {

	public DoubleMatrix var;
	public DoubleMatrix mean;
	public DoubleMatrix f_var;
	public DoubleMatrix f_mean;
//	public DoubleMatrix f_varNoData;
	
	public double gamma;
	public DoubleMatrix Kgain;
	
	public DoubleMatrix measurements;
	public DoubleMatrix C1;
	public DoubleMatrix C2;
	public DoubleMatrix FG;
	public DoubleMatrix F;
	public DoubleMatrix G;

//	public DoubleMatrix meanWithNoData;
	
	
	RoadModel roadModel;
	Estimation estimation;
	
//	boolean isNewNoise;
	int size;
	int sizeMeasurements;
	int cellsec;
	
	int stepMeasurements;
	
//	int number; //number of samples
//	DoubleMatrix samples;
//	DoubleMatrix samplesWithNoData;
	
	DoubleMatrix modelVar;
	
	DoubleMatrix measureVar;
	
	DoubleMatrix priorVar;
	
	public DoubleMatrix measure; //public only for test
	
	int numUp;
	
	Section section;
	
//	DoubleMatrix coef;
	
	abstract public void nextStep();
	abstract public void nextStepNoData();
	
	
	protected void initial(Estimation _estimation) {
		estimation = _estimation;
		roadModel = estimation.roadModel;
		section =estimation.roadModel.section;//!!!section is time variant!!!
		
		
		getNewParametersFromModel();
		
		mean = roadModel.initialMean;
//		meanWithNoData = mean.dup();//¸´ÖÆ
		f_mean=mean.dup();
		var = roadModel.initialVar;
		f_var=var.dup();
//		f_varNoData=var.dup();
		priorVar=var.dup();
		
		numUp = 0;
	}
	
	public static Filter createFilter(Estimation _estimation) {
		Filter f = null;
        f = new KCF(_estimation);
		return f;	 	
	}
	
	public DoubleMatrix Consensus(Filter _filter1, Filter _filter2){
		int overlap=roadModel.trueSolution.overlapsec;
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
			F=new DoubleMatrix(size,size);
			G=new DoubleMatrix(size,size);
			//NEED CHANGE
			C1=new DoubleMatrix(size,overlap);
			C2=new DoubleMatrix(size,overlap);
			DoubleMatrix I1=DoubleMatrix.zeros(size,overlap);
			for (int i=0;i<overlap;i++){
				I1.put(i,i,1);
			}
			DoubleMatrix I2=DoubleMatrix.zeros(size,overlap);
			for (int i=size-overlap;i<size;i++){
				I2.put(i,i-(size-overlap),1);
			}
			//NEED CHANGE
			DoubleMatrix I=DoubleMatrix.eye(size);
			DoubleMatrix S=measure.transpose().mmul(InverseMatrix.invPoSym(measureVar)).mmul(measure);
			F=I.sub(var.mmul(S));
			DoubleMatrix A1=section.ModelA;
			G=(A1.mmul(priorVar).mmul(A1.transpose())).add(modelVar).add(f_var.mmul(S).mmul(f_var));
			FG=F.mmul(G);
			C1=F.mmul(G).mmul(I1).mul(gamma);
			C2=F.mmul(G).mmul(I2).mul(gamma);
			DoubleMatrix mean1=new DoubleMatrix(size,1);
			//NEED CHANGE
			mean1=mean.add(C1.mmul(_filter1.f_mean.getRange(_filter1.size-overlap,_filter1.size,0,1).sub(f_mean.getRange(0,overlap,0,1)))).add(C2.mmul(_filter2.f_mean.getRange(0,overlap,0,1).sub(f_mean.getRange(size-overlap,size,0,1))));
			//NEED CHANGE
			return mean1;
			}
			else{return mean;}
	}
	
public DoubleMatrix Consensus1(Filter _filter1){
	
	    int overlap=roadModel.trueSolution.overlapsec;
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
		
		F=new DoubleMatrix(size,size);
		G=new DoubleMatrix(size,size);
		
		//NEED CHANGE
		C1=new DoubleMatrix(size,overlap);
		DoubleMatrix I1=DoubleMatrix.zeros(size,overlap);
		for (int i=0;i<overlap;i++){
			I1.put(i,i,1);
		}
		//NEED CHANGE
		
		DoubleMatrix I=DoubleMatrix.eye(size);
		DoubleMatrix S=measure.transpose().mmul(InverseMatrix.invPoSym(measureVar)).mmul(measure);
		F=I.sub(var.mmul(S));
		DoubleMatrix A1=section.ModelA;
		G=(A1.mmul(priorVar).mmul(A1.transpose())).add(modelVar).add(f_var.mmul(S).mmul(f_var));
		FG=F.mmul(G);
		C1=F.mmul(G).mmul(I1).mul(gamma);
		
		DoubleMatrix mean1=new DoubleMatrix(size,1);
		//NEED CHANGE
		mean1=mean.add(C1.mmul(_filter1.f_mean.getRange(_filter1.size-overlap,_filter1.size,0,1).sub(f_mean.getRange(0,overlap,0,1))));
		//NEED CHANGE
		return mean1;
		}
		else {return mean;}
	}
	
public DoubleMatrix Consensus2(Filter _filter2){
	
	int overlap=roadModel.trueSolution.overlapsec;
	if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
	
	F=new DoubleMatrix(size,size);
	G=new DoubleMatrix(size,size);
	
	//NEED CHANGE
	C2=new DoubleMatrix(size,overlap);
	DoubleMatrix I2=DoubleMatrix.zeros(size,overlap);
	for (int i=size-overlap;i<size;i++){
		I2.put(i,i-(size-overlap),1);
	}
	//NEED CHANGE
	DoubleMatrix I=DoubleMatrix.eye(size);
	DoubleMatrix S=measure.transpose().mmul(InverseMatrix.invPoSym(measureVar)).mmul(measure);
	F=I.sub(var.mmul(S));
	DoubleMatrix A1=section.ModelA;
	G=(A1.mmul(priorVar).mmul(A1.transpose())).add(modelVar).add(f_var.mmul(S).mmul(f_var));
	FG=F.mmul(G);
	C2=F.mmul(G).mmul(I2).mul(gamma);
	DoubleMatrix mean1=new DoubleMatrix(size,1);
	//NEED CHANGE
	mean1=mean.add(C2.mmul(_filter2.f_mean.getRange(0,overlap,0,1).sub(f_mean.getRange(size-overlap,size,0,1))));
	//NEED CHANGE
	return mean1;
	}
	else {return mean; }
}
	
    

	
	protected DoubleMatrix propagate(DoubleMatrix _density) {
		return estimation.propagate(_density);
	}
	
	protected DoubleMatrix getMeasurements() {
		return roadModel.getMeasureVector();
	}
	
	protected DoubleMatrix computeVar(DoubleMatrix _Var){
		DoubleMatrix _fVar=DoubleMatrix.zeros(_Var.getRows(), _Var.getColumns());
		_fVar=roadModel.section.ModelA.mmul(_Var).mmul(roadModel.section.ModelA.transpose()).add(modelVar);
		return _fVar;
	}
	
	protected void getNewParametersFromModel() {
		size = roadModel.size;
		modelVar = roadModel.modelVar;
		sizeMeasurements = roadModel.sizeMeasurements;
		stepMeasurements = roadModel.stepMeasurements;
		measureVar = roadModel.measureVar;
		measure = roadModel.measure;
	}
	

	

	

	

	
	/* protected void checkSamples() {
		double rhoMax = road.rhoMax;
		double speedMax = 30;
		for (int j=0; j<number; j++) {
			for (int i=0; i<cells; i++) {	
				double d = samples.get(i, j);
				if (d>rhoMax || d<0) System.out.println("Density out of boundaries in samples "+j+" cell "+i+" "+d);
				if (size > cells) {
					d = samples.get(i+cells,j);
					if (d>speedMax || d<0) System.out.println("Speed out of boundaries in samples "+j+" cell "+i+" "+d);
				}
			}
		}
	}
	*/
	

	
}
