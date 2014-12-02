package trueSolution;

import org.jblas.DoubleMatrix;

public class TriangularTrue extends TrueSolution{
	
	public TriangularTrue(double _rhoLeft, double _rhoRight,  double _totalrhoLeft, double _totalrhoRight, double _dt, double _dx, int _cellsec, int _overlapsec, int _index, int _numSecs){
		rhoCritical = 0.225;//*critical density
		rhoMax = 1; //*maximal density
		speedMax = 1; //*maximal speed
		initial(_rhoLeft, _rhoRight, _totalrhoLeft, _totalrhoRight,_dt, _dx, _cellsec, _overlapsec, _index,_numSecs);
		boundaryFlux=DoubleMatrix.zeros(2,1);
		boundaryFlux.put(0,0,speedMax*totalrhoLeft);
		boundaryFlux.put(1,0,((rhoCritical*speedMax)/(rhoMax-rhoCritical))*totalrhoRight);
	}
	
	public double speed(double density) {
		if (density<=rhoCritical) return speedMax;
		else return rhoCritical*speedMax*(rhoMax-density)/(density*(rhoMax-rhoCritical));
	}
	
	@Override
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
	
	public void propagate() {
		// TODO Auto-generated method stub
		double inflow=0;
		double outflow=0;
		if (index!=0){
			inflow=computeFlux(trueLeft.currenttrueStates.get(cellsec-2,0),trueLeft.currenttrueStates.get(cellsec-1,0));
			boundaryFlux.put(0,0,inflow);
		}
		if (index!=numSections-1){
			outflow=computeFlux(trueRight.currenttrueStates.get(0,0),trueRight.currenttrueStates.get(1,0));
			boundaryFlux.put(1,0,outflow);
		}
		DoubleMatrix _densitynext=DoubleMatrix.zeros(trueStates.getRows(), 1);
		if(section.getClass().getSimpleName().equals("FC")){
			_densitynext=section._ModelA.mmul(trueStates).add(section._ModelB1.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoMax))).add(section._ModelB2.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoCritical*section.speedMax))).add(section._ModelB3.mmul(boundaryFlux));
			trueStates=_densitynext;	
		}
		else{
		_densitynext=section.ModelA.mmul(trueStates).add(section.ModelB1.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoMax))).add(section.ModelB2.mmul(DoubleMatrix.ones(cellsec, 1).mmul(section.rhoCritical*section.speedMax))).add(section.ModelB3.mmul(boundaryFlux));
		trueStates=_densitynext;
		}		
	}
}
