package section;

import org.jblas.DoubleMatrix;
import trueSolution.TrueSolution;

public abstract class Section {

	public DoubleMatrix ModelA;
	public DoubleMatrix ModelB1;
	public DoubleMatrix ModelB2;
	public DoubleMatrix _ModelA;
	public DoubleMatrix _ModelB1;
	public DoubleMatrix _ModelB2;
	public DoubleMatrix Estimates;
	public int wavefront;
	public int _wavefront;
	
	public double rhoMax;
	public double speedMax; 
	public double rhoCritical;
	public double rhoMaxTrue;
	public double speedMaxTrue; 
	public double rhoCriticalTrue;
	public double parameters[];
	public double[] parametersIni;
	public String[] paramDescription;
	public int cellsec;
	public DoubleMatrix densitysec1;
	public double dt;
	public double dx;
	public int index;	
	public int wavedirection;//moving direction of the estimated shock
	public int _wavedirection;//moving direction of the true shock

	abstract public void getNewParameters();
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
		rhoMaxTrue=_truesolution.rhoMax;
		speedMaxTrue=_truesolution.speedMax;
		rhoCriticalTrue=_truesolution.rhoCritical;
	}
	
	public void setEstimates(DoubleMatrix _estimates ){
		Estimates=_estimates;
	}
}
	

