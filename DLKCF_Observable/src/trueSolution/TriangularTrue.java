package trueSolution;

import org.jblas.DoubleMatrix;

public class TriangularTrue extends TrueSolution{
	
	public TriangularTrue(double _rhoLeft, double _rhoRight,  double _totalrhoLeft, double _totalrhoRight, double _dt, double _dx, int _cellsec, int _overlapsec, int _index, int _numSecs){
		rhoCritical = 0.225; 
		rhoMax = 1; 
		speedMax = 1; 
		initial(_rhoLeft, _rhoRight, _totalrhoLeft, _totalrhoRight,_dt, _dx, _cellsec, _overlapsec, _index,_numSecs);
	}
	
	public double speed(double density) {
		if (density<=rhoCritical) return speedMax;
		else return rhoCritical*speedMax*(rhoMax-density)/(density*(rhoMax-rhoCritical));
	}

	public double fluxPrime(double density) {
		if (density == rhoLeft) return speedMax*rhoCritical/(rhoCritical-rhoMax);
		return speedMax;
	}

	public double fluxPrimeInverse(double speed) {
		return rhoCritical;
	}

	@Override
	public void propagate() {
		// TODO Auto-generated method stub
		DoubleMatrix _densitynext=DoubleMatrix.zeros(trueStates.getRows(), 1);
		trueStatesPrior=trueStates;
		if(section.getClass().getSimpleName().equals("FC")){
			_densitynext=section._ModelA.mmul(trueStates).add(section._ModelB1.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoMax))).add(section._ModelB2.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoCritical*section.speedMax)));
			trueStates=_densitynext;	
		}
		else{
		_densitynext=section.ModelA.mmul(trueStates).add(section.ModelB1.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoMax))).add(section.ModelB2.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoCritical*section.speedMax)));
		trueStates=_densitynext;
		}		
	}
	
	public double sending(double density) {
		// TODO Auto-generated method stub
		if (density>=rhoCritical){
			return rhoCritical*speedMax;
		}
		else return density*speedMax;
	}

	@Override
	public double receiving(double density) {
		// TODO Auto-generated method stub
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
