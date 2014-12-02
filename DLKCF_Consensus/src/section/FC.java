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
				A.put(cellsec-1, cellsec-1, 1);
			
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i, 1.0-(w*dt)/dx);	
				}	
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i+1, (w*dt)/dx);	
				}	
				ModelA=A;
			}
			else if (wavefront==cellsec-1){
				A.put(0, 0, 1.0);
				A.put(cellsec-1,cellsec-1,1);
				
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i, 1.0-(speedMax*dt)/dx);	
				}
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i-1, (speedMax*dt)/dx);	
				}	
				ModelA=A;
			}
			else{			
				A.put(0,0,1);
				A.put(cellsec-1,cellsec-1, 1);
							
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
				A.put(0, 0, 1);			
				A.put(1, 1, 1);				
				A.put(1,0,(speedMax*dt)/dx);
				A.put(1,2,(w*dt)/dx);				
				A.put(cellsec-1, cellsec-1, 1);			
				for(int i=2;i<cellsec-1;i++){
					A.put(i, i, 1.0-(w*dt)/dx);	
				}	
				for(int i=2;i<cellsec-1;i++){
					A.put(i, i+1, (w*dt)/dx);	
				}	
				ModelA=A;
			}
			else if (wavefront==cellsec-1){
				A.put(0, 0, 1.0);				
				for(int i=1;i<cellsec;i++){
					A.put(i, i, 1.0-(speedMax*dt)/dx);	
				}
				for(int i=1;i<cellsec;i++){
					A.put(i, i-1, (speedMax*dt)/dx);	
				}	
				ModelA=A;
			}
			else if (wavefront==cellsec-2){
				A.put(0, 0, 1.0);
				A.put(cellsec-1, cellsec-1, 1.0);
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i, 1.0-(speedMax*dt)/dx);	
				}
				for(int i=1;i<cellsec-1;i++){
					A.put(i, i-1, (speedMax*dt)/dx);	
				}	
				ModelA=A;
			}
			else{				
				A.put(0,0,1);
				A.put(cellsec-1,cellsec-1, 1);								
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
	}

	public void getModelB1(){
		if (wavedirection==0){
			DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (wavefront==0){
				ModelB1=B1;
			}
			else if (wavefront==cellsec-1){
				ModelB1=B1;
			}
			else{
				B1.put(wavefront,wavefront+1,-(w*dt)/dx);
				ModelB1=B1;	
			}						
		}
		else if (wavedirection==1){
			DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);			
			double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
			if (wavefront==0){
				B1.put(1,2,-(w*dt)/dx);
				ModelB1=B1;
			}
			else if (wavefront==cellsec-1){
				ModelB1=B1;
			}
			else if (wavefront==cellsec-2){
				ModelB1=B1;
			}
			else{
				B1.put(wavefront+1,wavefront+2,-(w*dt)/dx);
				ModelB1=B1;	
			}			
		}		
	}
	public void getModelB2(){
		DoubleMatrix B2=DoubleMatrix.zeros(cellsec,cellsec);
		ModelB2=B2;			
	}
	
	public void getwavefront(){
		int count=-1;
		for(int m=0; m<cellsec;m++){
			if (densitysec1.get(m,0)<=rhoCriticalTrue){
				count++;
			}
			else{count=count;}
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
			if (Estimates.get(m,0)<=rhoCritical&&Estimates.get(m+1,0)>rhoCritical){
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