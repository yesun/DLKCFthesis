package section;
import org.jblas.DoubleMatrix;
import doubleMatrix.GaussianGenerator;

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
		
		getwavefront();
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		
		for(int i=0;i<=wavefront;i++){
			A.put(i, i, 1.0-(w*dt)/dx);	
		}
		for(int i=0;i<wavefront;i++){
			A.put(i, i+1, (w*dt)/dx);	
		}
		for(int i=wavefront+1;i<cellsec;i++){
			A.put(i, i, 1.0-(speedMax*dt)/dx);	
		}
		for(int i=wavefront+1;i<cellsec-1;i++){
			A.put(i+1, i, (speedMax*dt)/dx);	
		}
		ModelA=A;
	}
	
	public void getModelB1(){
		DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);
		getwavefront();
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		B1.put(wavefront,wavefront,(w*dt)/dx);
		ModelB1=B1;	
	}
	public void getModelB2(){
		DoubleMatrix B2=DoubleMatrix.zeros(cellsec,cellsec);
		getwavefront();
		B2.put(wavefront,wavefront+1,-dt/dx);
		B2.put(wavefront+1,wavefront+1,dt/dx);
		ModelB2=B2;	
	}
	
	public void getwavefront(){	
					int count=-1;
					for(int m=0; m<cellsec;m++){
						if (densitysec1.get(m,0)>rhoCritical){
							count++;
						}
						else{count=count;}
					}
					wavefront=count;
								
}

	@Override
	public void getwavefrontfromEstimation() {
		// TODO Auto-generated method stub
		
	}
}