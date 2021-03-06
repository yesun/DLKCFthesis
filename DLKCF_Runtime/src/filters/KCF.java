package filters;

import org.jblas.DoubleMatrix;

import doubleMatrix.GaussianGenerator;
import doubleMatrix.InverseMatrix;


public class KCF extends Filter{
	
	
	DoubleMatrix measureError; //samples of measurements errors
	
	GaussianGenerator measureGenerator;
	
	DoubleMatrix measurements;
	
	public KCF(Estimation _estimation) {

		initial(_estimation);
		
//		GaussianGenerator initialNoise = new GaussianGenerator(var);
//		mean=mean.add(initialNoise.sample());
//		for (int i = 0; i<number; i++) samples.putColumn(i, mean);
		
//		samplesWithNoData = samples.dup();
//		measureGenerator = new GaussianGenerator(measureVar);
	}
	
	public void nextStep() {
		//getNewParametersFromModel();
		//measureGenerator = new GaussianGenerator(measureVar);
		
		forecast();
		analysis();
		
		
		numUp++;
	}
	public void nextStepNoData() {
		//getNewParametersFromModel();
		//measureGenerator = new GaussianGenerator(measureVar);
		
		forecastNoData();
//		analysis();
		
		
		numUp++;
	}
	
	private void forecastNoData() {
		mean = propagate(mean);
		priorVar=var;
		var = computeVar(var);
		
		
	}
	
	private void forecast() {
		f_mean = propagate(mean);
		f_var = computeVar(var);
		priorVar=var;
		
	}
	
	private void analysis() {
		if (numUp - stepMeasurements*(numUp/stepMeasurements) == 0) {
			measurements = getMeasurements();
			
			updateState(measurements);
		}
		else {mean =f_mean; var = f_var;}
//		mean = computeMean(samples); var = f_var;
	}
	
	
	
	
	
	private void updateState(DoubleMatrix measureVector) {
		DoubleMatrix A=new DoubleMatrix(roadModel.sizeMeasurements,roadModel.sizeMeasurements);
		A = InverseMatrix.invPoSym( (measure.mmul(f_var.mmul(measure.transpose()))).add(measureVar) );
		Kgain = f_var.mmul(measure.transpose()).mmul(A);		
		mean=f_mean.add(Kgain.mmul(measureVector.sub(measure.mmul(f_mean))));
		
		var = f_var.sub(Kgain.mmul(measure.mmul(f_var)));
		
	}
		
	
}
