package model;

import org.jblas.DoubleMatrix;

public class D extends RoadModel{

	public D() {
	}	
	
	public void initialThis() {
		if ((section.index)%2==0){
			measureVar=DoubleMatrix.eye(trueSolution.sizeMeasurements).mul(0.0081);//*Setting measurement error covariance matrix
		}
		else{
			measureVar=DoubleMatrix.eye(trueSolution.sizeMeasurements).mul(0.0081);	
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
			measure.put(0,0,1);
			measure.put(1, cellsec-1,1);
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
