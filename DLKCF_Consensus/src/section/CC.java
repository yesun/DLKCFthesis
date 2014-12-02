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
		A.put(cellsec-1, cellsec-1, 1);
		double w=(rhoCritical*speedMax)/(rhoMax-rhoCritical);
		for(int i=0;i<cellsec-1;i++){
			A.put(i, i, 1.0-(w*dt)/dx);	
		}	
		for(int i=0;i<cellsec-1;i++){
			A.put(i, i+1, (w*dt)/dx);	
		}	
		ModelA=A;
	}
	
	public void getModelB1(){
		DoubleMatrix B1=DoubleMatrix.zeros(cellsec,cellsec);
		ModelB1=B1;	
	}
	public void getModelB2(){
		DoubleMatrix B2=DoubleMatrix.zeros(cellsec,cellsec);
		ModelB2=B2;	
	}
	
	public void getwavefront(){
		wavefront=-1;
	}

	@Override
	public void getwavefrontfromEstimation() {
		// TODO Auto-generated method stub
		
	}
}