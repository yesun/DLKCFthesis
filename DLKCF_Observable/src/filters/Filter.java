package filters;

import model.RoadModel;

import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import doubleMatrix.InverseMatrix;
import section.*;

// this filter is the used by each local agent
public abstract class Filter {

	
	public DoubleMatrix mean;//posterior estimate
	public DoubleMatrix var;//posterior error covariance matrix	
	public DoubleMatrix f_mean;//prior estimate
	public DoubleMatrix f_var;//prior error covariance matrix
	
	public double gamma;//scaling factor used in the consensus filter
	public DoubleMatrix Kgain;//Kalman gain
	
	public DoubleMatrix measurements;
	public DoubleMatrix C1;//censensus gain associated with the left neighbor
	public DoubleMatrix C2;//censensus gain associated with the right neighbor
	public DoubleMatrix FG;//F.F.mmul(G)
	public DoubleMatrix F;//F matrix defined in the consensus gain 
	public DoubleMatrix G;//G matrix defined in the consensus gain
	
	RoadModel roadModel;
	Estimation estimation;
	
	int size;
	int sizeMeasurements;
	int cellsec;
	int stepMeasurements;	
	DoubleMatrix modelVar;	
	DoubleMatrix measureVar;	
	DoubleMatrix priorVar;//note that this is the posterior error covariance matrix in the previous step
	public DoubleMatrix measure; //public only for test	
	int numUp;	
	Section section;
	
	abstract public void nextStep();
	abstract public void nextStepNoData();
	
	
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
	
//consensus with the left and right neighbors
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
//consensus only with neighbor on the left
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
//consensus only with neighbor on the right
    public DoubleMatrix Consensus2(Filter _filter2){	
	    int overlap=roadModel.trueSolution.overlapsec;
	    if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0){	
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
		size = roadModel.size;
		modelVar = roadModel.modelVar;
		sizeMeasurements = roadModel.sizeMeasurements;
		stepMeasurements = roadModel.stepMeasurements;
		measureVar = roadModel.measureVar;
		measure = roadModel.measure;
	}	
}
