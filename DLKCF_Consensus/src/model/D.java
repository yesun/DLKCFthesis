package model;

import org.jblas.DoubleMatrix;

public class D extends RoadModel{

	public D() {
	}	
	
	public void initialThis() {
		double trust=0.0009;//*measurement error standard deviation for the good sensors (based on the estimator's knowledge)	
		if (trueSolution.measurePerturb==1){
		if (trueSolution.individual){			
			if ((section.index)%2==0){
				measureVar=DoubleMatrix.eye(trueSolution.sizeMeasurements).mul(trust);
			}
			else{			
				measureVar=trueSolution.measureVarDensity.dup();
			}
		}
		else{
			if (section.index==0){
				measureVar=trueSolution.measureVarDensity.dup();
				for (int i=0;i<3;i++){
					measureVar.put(i,i,trust);
				}
			}
			else if (section.index==trueSolution.numSections-1){
				if ((section.index)%2==0){
					measureVar=trueSolution.measureVarDensity.dup();
					for (int j=1;j<4;j++){
						measureVar.put(j,j,trust);
					}
				}
				else{
					measureVar=trueSolution.measureVarDensity.dup();
					measureVar.put(0,0,trust);	
				}
			}
			else{
				if ((section.index)%2==0){
					measureVar=trueSolution.measureVarDensity.dup();
					measureVar.put(1,1,trust);
					measureVar.put(2,2,trust);
				}
				else{
					measureVar=trueSolution.measureVarDensity.dup();
					measureVar.put(0,0,trust);
					measureVar.put(3,3,trust);
				}
			}
		}
		
		}
		else{
			measureVar=trueSolution.measureVarDensity.dup();
		}
		sizeMeasurements = measureVar.getColumns();
		
        size = cellsec; 
		modelVar = modelVarDensity;
		initialVar = initialVarDensity;
		initialMean = initialDensity;
		section.Estimates=initialMean;
		
		measure = DoubleMatrix.zeros(sizeMeasurements,size);
		if (trueSolution.overlapsec!=1){
		    for (int i=0;i<sizeMeasurements;i++){	
				if(i<2){
					measure.put(i, i*(trueSolution.overlapsec-1),1);
				}
				else{
					measure.put(i, cellsec-1-(3-i)*(trueSolution.overlapsec-1),1);
				}			
		    }	
		}
		else{
			int distMeasurements=(cellsec-1)/(sizeMeasurements-1);
			for (int i=0;i<sizeMeasurements;i++){
				measure.put(i,i*distMeasurements,1);
			}
		}
	}
	
	public DoubleMatrix propagate(DoubleMatrix _density) {
		DoubleMatrix _densitynext=DoubleMatrix.zeros(_density.getRows(), 1);
		_densitynext=section.ModelA.mmul(_density).add(section.ModelB1.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoMax))).add(section.ModelB2.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoCritical*section.speedMax)));
		return _densitynext;			
	}

    public DoubleMatrix getMeasureVector(){  
    	return trueSolution.measurementsDensity;
    }
    
	public DoubleMatrix getDensityMean(DoubleMatrix mean){
		return mean;
	}
	
	public DoubleMatrix getDensityVar(DoubleMatrix var){
		return var;
	}   
}
