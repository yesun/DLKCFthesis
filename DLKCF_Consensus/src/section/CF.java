package section;
import org.jblas.DoubleMatrix;

public class CF extends Section {	
	
	static double[] paramDefault = {1.0, 0.225, 1.0};
		
	public CF() {
		paramDescription = new String[3];
		paramDescription[0] = "Maximal Density";
		paramDescription[1] = "Critical Density";
		paramDescription[2] = "Maximal Speed";
		setNewParameters(paramDefault);
		parametersIni = paramDefault;
	}
	
	public CF(double[] params) {
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
		DoubleMatrix A=DoubleMatrix.zeros(cellsec,cellsec);
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		if (wavefront!=cellsec-1){
			for(int i=0;i<=wavefront;i++){
				A.put(i, i, 1.0-(w*dt)/dx);	
			}
			if (wavefront!=0){
				for(int i=0;i<wavefront;i++){
					A.put(i, i+1, (w*dt)/dx);	
				}
			}		
			for(int i=wavefront+1;i<cellsec;i++){
				A.put(i, i, 1.0-(speedMax*dt)/dx);	
			}
			if (wavefront!=cellsec-2){
				for(int i=wavefront+1;i<cellsec-1;i++){
					A.put(i+1, i, (speedMax*dt)/dx);	
				}
			}
		}
		else{
			A.put(cellsec-1, cellsec-1, 1);
			for(int i=0;i<cellsec-1;i++){
				A.put(i, i, 1.0-(w*dt)/dx);	
			}	
			for(int i=0;i<cellsec-1;i++){
				A.put(i, i+1, (w*dt)/dx);	
			}	
		}				
		ModelA=A;
	}
	
	public void getModelB1(){
		DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);		
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		if (wavefront!=cellsec-1){
			B1.put(wavefront,wavefront,(w*dt)/dx);
		}
		
		ModelB1=B1;	
	}
	public void getModelB2(){
		DoubleMatrix B2=DoubleMatrix.zeros(cellsec,cellsec);
		if (wavefront!=cellsec-1){
			B2.put(wavefront,wavefront+1,-dt/dx);
			B2.put(wavefront+1,wavefront+1,dt/dx);
		}	
		ModelB2=B2;	
	}
	
	public void getwavefront(){	
		DoubleMatrix wf=DoubleMatrix.ones(cellsec,1);
		for (int m=0;m<cellsec;m++){
			wf.put(m,0,cellsec-1);
		}
		for (int m=0;m<cellsec-1;m++){
			if (densitysec1.get(m,0)>rhoCriticalTrue&&densitysec1.get(m+1,0)<=rhoCriticalTrue){
				wf.put(m,0,m);
			}
		}
		int count=cellsec-1;
		for(int m=0; m<cellsec-1;m++){
            if (wf.get(m,0)<count){
            	count=(int)wf.get(m,0);
            }
		}
		_wavefront=count;
	}

	@Override
	public void getwavefrontfromEstimation() {
		// TODO Auto-generated method stub
		DoubleMatrix wf=DoubleMatrix.ones(cellsec,1);
		for (int m=0;m<cellsec;m++){
			wf.put(m,0,cellsec-1);
		}
		for (int m=0;m<cellsec-1;m++){
			if (Estimates.get(m,0)>rhoCritical&&Estimates.get(m+1,0)<=rhoCritical){
				wf.put(m,0,m);
			}
		}
		int count=cellsec-1;
		for(int m=0; m<cellsec-1;m++){
            if (wf.get(m,0)<count){
            	count=(int)wf.get(m,0);
            }
		}
		wavefront=count;
	}
}