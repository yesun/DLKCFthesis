package model;

import org.jblas.DoubleMatrix;

public class D extends RoadModel{

	public D() {
	}	
	
	public void initialThis() {	
		measureVar=trueSolution.measureVarDensity;		
		sizeMeasurements = measureVar.getColumns();
		modelVar = modelVarDensity;
		initialVar = initialVarDensity;
		initialMean = initialDensity;
		section.Estimates=initialMean;
		size=cellsec;
		measure = DoubleMatrix.zeros(sizeMeasurements,size);
		measure.put(0,0,1);
		measure.put(1,cellsec-1,1);
	}

	
	public DoubleMatrix propagate(DoubleMatrix _density) {		
		DoubleMatrix _densitynext=DoubleMatrix.zeros(_density.getRows(), 1);
		//here assuming the inflow and out flow of each section is completely known
		_densitynext=section.ModelA.mmul(_density).add(section.ModelB1.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoMax))).add(section.ModelB2.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoCritical*section.speedMax))).add(section.ModelB3.mmul(trueSolution.boundaryFlux));
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
