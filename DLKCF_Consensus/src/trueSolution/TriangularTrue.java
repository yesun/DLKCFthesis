package trueSolution;
import trueSolutionCTM.TrueSolutionCTM;

public class TriangularTrue extends TrueSolution{
	
	public TriangularTrue(TrueSolutionCTM _trueCTM, double _dt, double _dx, int _cellsec, int _overlapsec, int _index, int _numSecs,int _perturb, int _measurePerturb, boolean _individual){
		initial(_trueCTM, _dt, _dx, _cellsec, _overlapsec, _index,_numSecs, _perturb, _measurePerturb,_individual);
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

	public void propagate() {
	trueStatesPrior=trueStates;
	trueStates=trueCTM.trueStatesCTM.getRange(index*(cellsec-overlapsec), index*(cellsec-overlapsec)+cellsec, 0, 1);	
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
