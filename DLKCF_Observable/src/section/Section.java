package section;

import org.jblas.DoubleMatrix;


import trueSolution.TrueSolution;


public abstract class Section {

	public DoubleMatrix ModelA; //matrix A in SMM used for estimation
	public DoubleMatrix ModelB1;//matrix B^{\rho} in SMM used for estimation
	public DoubleMatrix ModelB2;//matrix B^{q} in SMM used for estimation
	public DoubleMatrix _ModelA;//true matrix A in SMM
	public DoubleMatrix _ModelB1;//true matrix B^{\rho}
	public DoubleMatrix _ModelB2;//true matrix B^{q}
	public DoubleMatrix Estimates;//estimates given by agent
	public int wavefront;//estimated location of the shock
	public int _wavefront;//true location of the shock
	
	public double rhoMax;
	public double speedMax; 
	public double rhoCritical;
	public double parameters[];
	public double[] parametersIni;//initial parameters
	public String[] paramDescription;
	public int cellsec;
	public DoubleMatrix densitysec1;
	public double dt;
	public double dx;
	public int index;
	
	public int wavedirection;//estimated shock moving direction 
	public int _wavedirection;//true shock moving direction 
	
	abstract public void setNewParameters(double[] params);
	abstract public void setParamDefault(double[] newParamDefault);
	abstract public void getModelA();
	abstract public void getModelB1();
	abstract public void getModelB2();
	abstract public void getwavefront();
	abstract public void getwavefrontfromEstimation();
		
 	public static Section createSection(String nameSection, TrueSolution _truesolution) {
		String name = "section."+nameSection;
 		try {
			@SuppressWarnings("rawtypes")
			Class cl = Class.forName(name);
			@SuppressWarnings("unchecked")
			Section sec = (Section) cl.getConstructor().newInstance();
			sec.getparamsfromtruesolution(_truesolution);
			
			return sec;
 		}
		catch(Throwable t) {System.out.println(t);}				
		return null;
	}

	public void getparamsfromtruesolution(TrueSolution _truesolution){
		cellsec=_truesolution.cellsec;
		dt=_truesolution.dt;
		dx=_truesolution.dx;
		densitysec1=DoubleMatrix.zeros(cellsec,1);
		ModelA=DoubleMatrix.zeros(cellsec,cellsec);
		ModelB1=ModelA.dup();
		ModelB2=ModelA.dup();
		Estimates=densitysec1.dup();
	}
	
	public void setEstimates(DoubleMatrix _estimates ){
		Estimates=_estimates;
	}

}
	

