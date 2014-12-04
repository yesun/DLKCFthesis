package model;

import org.jblas.DoubleMatrix;

public class D extends RoadModel{

	public D() {
	}	
	
	public void initialThis() {
		
		
		
		//NEED CHANGE /MEASUREMENT　VARIANCE STRUCTURE//
//		if (section.index==0){
//			measureVar=trueSolution.measureVarDensity.getRange(0,c-1,0,c-1);
//		}
//		else if (section.index==trueSolution.numSections-1){
//			measureVar=trueSolution.measureVarDensity.getRange(trueSolution.sizeMeasurements-(c-1),trueSolution.sizeMeasurements,trueSolution.sizeMeasurements-(c-1),trueSolution.sizeMeasurements);
//		}
//		else{
//			measureVar = trueSolution.measureVarDensity.getRange(section.index*(c-2)+1, section.index*(c-2)+1+(c-2), section.index*(c-2)+1, section.index*(c-2)+1+(c-2));
//		}
		
		
//		sizeMeasurements = measureVar.getColumns();
		//NEED CHANGE /MEASUREMENT　VARIANCE STRUCTURE//
		
		//NEED CHANGE /MEASUREMENT　VARIANCE STRUCTURE//
		measureVar=trueSolution.measureVarDensity;
		
		sizeMeasurements = measureVar.getColumns();
		//NEED CHANGE /MEASUREMENT　VARIANCE STRUCTURE//
		
        size = cellsec; 
		modelVar = modelVarDensity;
		initialVar = initialVarDensity;
		initialMean = initialDensity;
		section.Estimates=initialMean;
		
		measure = DoubleMatrix.zeros(sizeMeasurements,size);
		for (int i=0;i<sizeMeasurements;i++){
		
				if(i<2){
					measure.put(i, i*(trueSolution.overlapsec-1),1);
				}
				else{
					measure.put(i, cellsec-1-(3-i)*(trueSolution.overlapsec-1),1);
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
