package section;
import org.jblas.DoubleMatrix;

public class CC extends Section {	
	static double[] paramDefault = {1.0, 0.225, 1.0,-1};
	
	public CC() {
		paramDescription = new String[4];
		paramDescription[0] = "Maximal Density";
		paramDescription[1] = "Critical Density";
		paramDescription[2] = "Maximal Speed";
		paramDescription[3] = "Position of Wavefront";
		setNewParameters(paramDefault);
		parametersIni = paramDefault;
	}
	
	public CC(double[] params) {
		paramDescription = new String[4];
		paramDescription[0] = "Maximal Density";
		paramDescription[1] = "Critical Density";
		paramDescription[2] = "Maximal Speed";
		paramDescription[3] = "Position of Wavefront";
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
		wavefront=(int) parameters[3];
	}

	public void setParamDefault(double[] newParamDefault) {
		paramDefault = newParamDefault;
	}
	
	public void getModelA(){
		DoubleMatrix A=DoubleMatrix.zeros(cellsec,cellsec);
		
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		A.put(0,0,1);
		A.put(cellsec-1, cellsec-1, 1.0-(w*dt)/dx);
		for(int i=1;i<cellsec-1;i++){
			A.put(i, i, 1.0-(w*dt)/dx);	
		}	
		for(int i=0;i<cellsec-1;i++){
			A.put(i, i+1, (w*dt)/dx);	
		}	
		ModelA=A;
	}
	
	public void getModelB1(){
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);
		B1.put(0,1,-(w*dt)/dx);
		B1.put(cellsec-1,cellsec-1,(w*dt)/dx);
		ModelB1=B1;	
	}
	public void getModelB2(){
		DoubleMatrix B2=DoubleMatrix.zeros(cellsec,cellsec);
		ModelB2=B2;	
	}
	
	public void getModelB3(){
		DoubleMatrix B3=DoubleMatrix.zeros(cellsec,2);
		B3.put(0,0,dt/dx);
		B3.put(cellsec-1,1,-dt/dx);
		ModelB3=B3;	
	}
	
	public void getwavefront(){
		wavefront=-1;
	}

	@Override
	public void getwavefrontfromEstimation() {
		// TODO Auto-generated method stub		
	}
}