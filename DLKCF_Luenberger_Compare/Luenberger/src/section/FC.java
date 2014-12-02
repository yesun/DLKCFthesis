package section;
import org.jblas.DoubleMatrix;

public class FC extends Section {		
	static double[] paramDefault = {1.0, 0.225, 1.0};	
	public FC() {
		paramDescription = new String[3];
		paramDescription[0] = "Maximal Density";
		paramDescription[1] = "Critical Density";
		paramDescription[2] = "Maximal Speed";
		setNewParameters(paramDefault);
		parametersIni = paramDefault;
	}
	
	public FC(double[] params) {
		paramDescription = new String[3];
		paramDescription[0] = "Maximal Density";
		paramDescription[1] = "Critical Density";
		paramDescription[2] = "Maximal Speed";
		setNewParameters(params);
		parametersIni = params;
	}

	public void getNewParameters() {
	}	
	
	public void setNewParameters(double[] params) {
		parameters = params;
		rhoMax = parameters[0];
		rhoCritical = parameters[1];
		speedMax = parameters[2];
	}

	public void setParamDefault(double[] newParamDefault) {
		paramDefault = newParamDefault;
	}

	public void getModelA(){
		if (wavedirection==0){
			DoubleMatrix A=DoubleMatrix.zeros(cellsec,cellsec);		
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (wavefront==0){				
				A.put(0,0,1);
				A.put(0,1,(w*dt)/dx);
				A.put(cellsec-1, cellsec-1, 1.0-(w*dt)/dx);			
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i, 1.0-(w*dt)/dx);	
				}	
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i+1, (w*dt)/dx);	
				}	
				ModelA=A;
			}

			else{				
				A.put(0,0,1.0-(speedMax*dt)/dx);
				A.put(cellsec-1,cellsec-1, 1.0-(w*dt)/dx);								
				for(int i=1;i<wavefront;i++){
					A.put(i, i, 1.0-(speedMax*dt)/dx);	
					A.put(i, i-1, (speedMax*dt)/dx);
				}				
				A.put(wavefront, wavefront, 1);
				A.put(wavefront, wavefront-1, (speedMax*dt)/dx);
				A.put(wavefront, wavefront+1, (w*dt)/dx);				
				for(int i=wavefront+1;i<cellsec-1;i++){
					A.put(i, i, 1.0-(w*dt)/dx);	
					A.put(i, i+1, (w*dt)/dx);	
				}
				ModelA=A;
			}
		}
		else if (wavedirection==1){
			DoubleMatrix A=DoubleMatrix.zeros(cellsec,cellsec);
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (wavefront==0){			
				A.put(0, 0, 1.0-(speedMax*dt)/dx);				
				A.put(1, 1, 1);				
				A.put(1,0,(speedMax*dt)/dx);
				A.put(1,2,(w*dt)/dx);				
				A.put(cellsec-1, cellsec-1, 1.0-(w*dt)/dx);				
				for(int i=2;i<cellsec-1;i++){
					A.put(i, i, 1.0-(w*dt)/dx);	
				}	
				for(int i=2;i<cellsec-1;i++){
					A.put(i, i+1, (w*dt)/dx);	
				}	
				ModelA=A;
			}

			else if (wavefront==cellsec-2){
				A.put(0, 0, 1.0-(speedMax*dt)/dx);
				A.put(cellsec-1, cellsec-1, 1.0);
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i, 1.0-(speedMax*dt)/dx);	
				}
				for(int i=1;i<cellsec;i++){
					A.put(i, i-1, (speedMax*dt)/dx);	
				}	
				ModelA=A;
			}
			else{				
				A.put(0,0,1.0-(speedMax*dt)/dx);
				A.put(cellsec-1,cellsec-1, 1.0-(w*dt)/dx);				
				for(int i=1;i<=wavefront;i++){
					A.put(i, i, 1.0-(speedMax*dt)/dx);	
					A.put(i, i-1, (speedMax*dt)/dx);
				}				
				A.put(wavefront+1, wavefront+1, 1);
				A.put(wavefront+1, wavefront, (speedMax*dt)/dx);
				A.put(wavefront+1, wavefront+2, (w*dt)/dx);				
				for(int i=wavefront+2;i<cellsec-1;i++){
					A.put(i, i, 1.0-(w*dt)/dx);	
					A.put(i, i+1, (w*dt)/dx);	
				}
				ModelA=A;
			}
		}		
		if (_wavedirection==0){			
			DoubleMatrix _A=DoubleMatrix.zeros(cellsec,cellsec);
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (_wavefront==0){			
				_A.put(0, 0, 1);
				_A.put(0,1,(w*dt)/dx);
				_A.put(cellsec-1, cellsec-1, 1.0-(w*dt)/dx);				
				for(int i=1;i<cellsec-1;i++){
					_A.put(i, i, 1.0-(w*dt)/dx);	
				}	
				for(int i=1;i<cellsec-1;i++){
					_A.put(i, i+1, (w*dt)/dx);	
				}	
				_ModelA=_A;
			}
			else{				
				_A.put(0,0,1.0-(speedMax*dt)/dx);
				_A.put(cellsec-1,cellsec-1, 1.0-(w*dt)/dx);				
				for(int i=1;i<_wavefront;i++){
					_A.put(i, i, 1.0-(speedMax*dt)/dx);	
					_A.put(i, i-1, (speedMax*dt)/dx);
				}				
				_A.put(_wavefront, _wavefront, 1);
				_A.put(_wavefront, _wavefront-1, (speedMax*dt)/dx);
				_A.put(_wavefront, _wavefront+1, (w*dt)/dx);				
				for(int i=_wavefront+1;i<cellsec-1;i++){
					_A.put(i, i, 1.0-(w*dt)/dx);	
					_A.put(i, i+1, (w*dt)/dx);	
				}
				_ModelA=_A;
			}
		}
		else if(_wavedirection==1){		
			DoubleMatrix _A=DoubleMatrix.zeros(cellsec,cellsec);
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (_wavefront==0){			
				_A.put(0, 0, 1.0-(speedMax*dt)/dx);				
				_A.put(1, 1, 1);
				_A.put(1,0,(speedMax*dt)/dx);
				_A.put(1,2,(w*dt)/dx);				
				_A.put(cellsec-1, cellsec-1, 1.0-(w*dt)/dx);				
				for(int i=2;i<cellsec-1;i++){
					_A.put(i, i, 1.0-(w*dt)/dx);	
				}	
				for(int i=2;i<cellsec-1;i++){
					_A.put(i, i+1, (w*dt)/dx);	
				}	
				_ModelA=_A;
			}

			else if (_wavefront==cellsec-2){				
				_A.put(0, 0, 1.0-(speedMax*dt)/dx);
				_A.put(cellsec-1, cellsec-1, 1.0);
				for(int i=1;i<cellsec-1;i++){
					_A.put(i, i, 1.0-(speedMax*dt)/dx);	
				}
				for(int i=1;i<cellsec;i++){
					_A.put(i, i-1, (speedMax*dt)/dx);	
				}	
				_ModelA=_A;
			}
			else{
				_A.put(0,0,1.0-(speedMax*dt)/dx);
				_A.put(cellsec-1,cellsec-1, 1.0-(w*dt)/dx);				
				for(int i=1;i<=_wavefront;i++){
					_A.put(i, i, 1.0-(speedMax*dt)/dx);	
					_A.put(i, i-1, (speedMax*dt)/dx);
				}				
				_A.put(_wavefront+1, _wavefront+1, 1);
				_A.put(_wavefront+1, _wavefront, (speedMax*dt)/dx);
				_A.put(_wavefront+1, _wavefront+2, (w*dt)/dx);				
				for(int i=_wavefront+2;i<cellsec-1;i++){
					_A.put(i, i, 1.0-(w*dt)/dx);	
					_A.put(i, i+1, (w*dt)/dx);	
				}
				_ModelA=_A;
			}
		}			
	}
	
	public void getModelB1(){
		if (wavedirection==0){
			DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			B1.put(wavefront,wavefront+1,-(w*dt)/dx);
			B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
			ModelB1=B1;				
		}
		else if (wavedirection==1){
			DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);			
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (wavefront==0){
				B1.put(1,2,-(w*dt)/dx);
				B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
				ModelB1=B1;
			}
			else if (wavefront==cellsec-2){
				ModelB1=B1;
			}
			else{
				B1.put(wavefront+1,wavefront+2,-(w*dt)/dx);
				B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
				ModelB1=B1;	
			}			
		}
		if (_wavedirection==0){			
			DoubleMatrix _B1=DoubleMatrix.zeros(cellsec,cellsec);	
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			_B1.put(_wavefront,_wavefront+1,-(w*dt)/dx);
			_B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
			_ModelB1=_B1;	
			
		}
		else {
			DoubleMatrix _B1=DoubleMatrix.zeros(cellsec,cellsec);	
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (_wavefront==0){
				_B1.put(1,2,-(w*dt)/dx);
				_B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
				_ModelB1=_B1;
			}
			else if (_wavefront==cellsec-2){
				_ModelB1=_B1;
			}
			else{
				_B1.put(_wavefront+1,_wavefront+2,-(w*dt)/dx);
				_B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
				_ModelB1=_B1;	
			}	
		}	
	}
	public void getModelB2(){
		DoubleMatrix B2=DoubleMatrix.zeros(cellsec,cellsec);
		DoubleMatrix _B2=DoubleMatrix.zeros(cellsec,cellsec);
		ModelB2=B2;	
		_ModelB2=_B2;			
	}
	
	public void getModelB3(){
		DoubleMatrix B3=DoubleMatrix.zeros(cellsec,2);
		B3.put(0,0,dt/dx);
		B3.put(cellsec-1,1,-dt/dx);	
		ModelB3=B3;
		_ModelB3=B3;
	}
	
	public void getwavefront(){
		int count=-1;
		for(int m=0; m<cellsec;m++){
			if (densitysec1.get(m,0)<=rhoCritical){
				count++;
			}
		}
		_wavefront=count;	
	}
	@Override

	public void getwavefrontfromEstimation() {
		// TODO Auto-generated method stub
		int count=-1;
		for(int m=0; m<cellsec;m++){
			if (Estimates.get(m,0)<=rhoCritical){
				count++;
			}
		}
		wavefront=count;		
	}
	
	
}