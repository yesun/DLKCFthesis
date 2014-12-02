package trueSolutionCTM;

import org.jblas.DoubleMatrix;

public class TriangularTrueCTM extends TrueSolutionCTM{

	//Defined as a public double in SpeedFunction
	
	public TriangularTrueCTM(double _dt, double _dx, int _cells){	
		rhoCritical = 0.225; //*true critical density
		rhoMax = 1; //*true maximal density
		speedMax = 1; //*true maximal speed
		initial(_dt, _dx, _cells);
	}

	public double speed(double density) {
		if (density<=rhoCritical) return speedMax;
		else return rhoCritical*speedMax*(rhoMax-density)/(density*(rhoMax-rhoCritical));
	}

	@Override
	public void propagateCTM() {
		DoubleMatrix _densitynext=DoubleMatrix.zeros(trueStatesCTM.getRows(), 1);
		trueStatesCTMPrior=trueStatesCTM;
		_densitynext.put(trueStatesCTM.getRows()-1,0,trueStatesCTM.get(trueStatesCTM.getRows()-1,0));
		_densitynext.put(0,0,trueStatesCTM.get(0,0));
		for (int i=1;i<trueStatesCTM.getRows()-1;i++){
			_densitynext.put(i,0,trueStatesCTM.get(i,0)+(dt/dx)*(computeFlux(trueStatesCTM.get(i-1,0),trueStatesCTM.get(i,0))-computeFlux(trueStatesCTM.get(i,0),trueStatesCTM.get(i+1,0))));
		}
		trueStatesCTM=_densitynext;
	}
	
	public double sending(double density) {
		if (density>=rhoCritical){
			return rhoCritical*speedMax;
		}
		else return density*speedMax;
	}

	@Override
	public double receiving(double density) {
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		if (density<=rhoCritical){
			return rhoCritical*speedMax;
		}
		else return w*(rhoMax-density);
	}
	
	@Override
	public double computeFlux(double density1, double density2) {
	    if (sending(density1)<=receiving(density2)){
	    	return sending(density1);
	    }
	    else return receiving(density2);
	}



}
