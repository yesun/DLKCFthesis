package filters;

import org.jblas.DoubleMatrix;
import doubleMatrix.InverseMatrix;

public class KCF extends Filter{
	
	DoubleMatrix measurements;	
	public KCF(Estimation _estimation) {
		initial(_estimation);
	}
	
	public void nextStep() {
		forecast();
		analysis();
		numUp++;
	}
	
	public void nextStepNoData() {
		forecastNoData();
		numUp++;
	}
	
	private void forecast() {
		f_mean = propagate(mean);
		f_var = computeVar(var);
		priorVar=var;		
	}
	private void forecastNoData() {
		mean = propagate(mean);
		priorVar=var;
		var = computeVar(var);		
	}
	
	private void analysis() {
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
			measurements = getMeasurements();			
			updateState(measurements);
		}
		else {mean =f_mean; var = f_var;}
	}

	private void updateState(DoubleMatrix measureVector) {
		DoubleMatrix A=new DoubleMatrix(roadModel.sizeMeasurements,roadModel.sizeMeasurements);
		A = InverseMatrix.invPoSym( (measure.mmul(f_var.mmul(measure.transpose()))).add(measureVar) );
		Kgain = f_var.mmul(measure.transpose()).mmul(A);		
		mean=f_mean.add(Kgain.mmul(measureVector.sub(measure.mmul(f_mean))));		
		var = f_var.sub(Kgain.mmul(measure.mmul(f_var)));	
	}	
}
