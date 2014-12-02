package filters;

import model.RoadModel;
import org.jblas.DoubleMatrix;
import doubleMatrix.InverseMatrix;
import section.*;

public abstract class Filter {

	public DoubleMatrix var;
	public DoubleMatrix mean;
	public DoubleMatrix f_var;
	public DoubleMatrix f_mean;		
	public DoubleMatrix Kgain;	
	public DoubleMatrix measure;
	public DoubleMatrix measurements;
	public DoubleMatrix C1;
	public DoubleMatrix C2;
	public DoubleMatrix FG;
	public DoubleMatrix F;
	public DoubleMatrix G;
	
	public double gamma;
	
	int size;
	int sizeMeasurements;
	int cellsec;	
	int stepMeasurements;
	int numUp;
	
	DoubleMatrix modelVar;
	DoubleMatrix measureVar;	
	DoubleMatrix priorVar;
	
	Section section;
	RoadModel roadModel;
	Estimation estimation;
	
	abstract public void nextStep();
	abstract public void nextStepNoAnalysis();
		
	protected void initial(Estimation _estimation) {
		estimation = _estimation;
		roadModel = estimation.roadModel;
		section =estimation.roadModel.section;		
		getNewParametersFromModel();		
		mean = roadModel.initialMean;
		f_mean=mean.dup();
		var = roadModel.initialVar;
		f_var=var.dup();
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
			DoubleMatrix I=DoubleMatrix.eye(size);
			DoubleMatrix S=measure.transpose().mmul(InverseMatrix.invPoSym(measureVar)).mmul(measure);
			F=I.sub(var.mmul(S));
			DoubleMatrix A1=section.ModelA;
			G=(A1.mmul(priorVar).mmul(A1.transpose())).add(modelVar).add(f_var.mmul(S).mmul(f_var));
			FG=F.mmul(G);
			C1=F.mmul(G).mmul(I1).mul(gamma);
			C2=F.mmul(G).mmul(I2).mul(gamma);
			DoubleMatrix mean1=new DoubleMatrix(size,1);
			mean1=mean.add(C1.mmul(_filter1.f_mean.getRange(_filter1.size-overlap,_filter1.size,0,1).sub(f_mean.getRange(0,overlap,0,1)))).add(C2.mmul(_filter2.f_mean.getRange(0,overlap,0,1).sub(f_mean.getRange(size-overlap,size,0,1))));
			return mean1;
		}
		else{return mean;}
	}
	
    public DoubleMatrix Consensus1(Filter _filter1){
	
	    int overlap=roadModel.trueSolution.overlapsec;
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
			F=new DoubleMatrix(size,size);
			G=new DoubleMatrix(size,size);
			
			C1=new DoubleMatrix(size,overlap);
			DoubleMatrix I1=DoubleMatrix.zeros(size,overlap);
			for (int i=0;i<overlap;i++){
				I1.put(i,i,1);
			}
			
			DoubleMatrix I=DoubleMatrix.eye(size);
			DoubleMatrix S=measure.transpose().mmul(InverseMatrix.invPoSym(measureVar)).mmul(measure);
			F=I.sub(var.mmul(S));
			DoubleMatrix A1=section.ModelA;
			G=(A1.mmul(priorVar).mmul(A1.transpose())).add(modelVar).add(f_var.mmul(S).mmul(f_var));
			FG=F.mmul(G);
			C1=F.mmul(G).mmul(I1).mul(gamma);
			
			DoubleMatrix mean1=new DoubleMatrix(size,1);
			mean1=mean.add(C1.mmul(_filter1.f_mean.getRange(_filter1.size-overlap,_filter1.size,0,1).sub(f_mean.getRange(0,overlap,0,1))));
			return mean1;
		}
		else {return mean;}
	}
	
    public DoubleMatrix Consensus2(Filter _filter2){
    	int overlap=roadModel.trueSolution.overlapsec;
    	if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
        	F=new DoubleMatrix(size,size);
        	G=new DoubleMatrix(size,size);
        	
        	C2=new DoubleMatrix(size,overlap);
        	DoubleMatrix I2=DoubleMatrix.zeros(size,overlap);
        	for (int i=size-overlap;i<size;i++){
        		I2.put(i,i-(size-overlap),1);
        	}
        	DoubleMatrix I=DoubleMatrix.eye(size);
        	DoubleMatrix S=measure.transpose().mmul(InverseMatrix.invPoSym(measureVar)).mmul(measure);
        	F=I.sub(var.mmul(S));
        	DoubleMatrix A1=section.ModelA;
        	G=(A1.mmul(priorVar).mmul(A1.transpose())).add(modelVar).add(f_var.mmul(S).mmul(f_var));
        	FG=F.mmul(G);
        	C2=F.mmul(G).mmul(I2).mul(gamma);
        	DoubleMatrix mean1=new DoubleMatrix(size,1);
        	mean1=mean.add(C2.mmul(_filter2.f_mean.getRange(0,overlap,0,1).sub(f_mean.getRange(size-overlap,size,0,1))));
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
		size = roadModel.section.cellsec;
		modelVar = roadModel.modelVar;
		sizeMeasurements = roadModel.sizeMeasurements;
		stepMeasurements = roadModel.stepMeasurements;
		measureVar = roadModel.measureVar;
		measure = roadModel.measure;
	}
	
}
