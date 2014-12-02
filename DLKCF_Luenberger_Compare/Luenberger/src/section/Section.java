package section;

import org.jblas.DoubleMatrix;
import trueSolution.TrueSolution;


public abstract class Section {

	//useful variables for constructing SMM
	//Different from the DLKCF, here we do not assign boundary dynamics for boundary cells
	public DoubleMatrix ModelA;
	public DoubleMatrix ModelB1;
	public DoubleMatrix ModelB2;
	public DoubleMatrix ModelB3;
	public DoubleMatrix _ModelA;
	public DoubleMatrix _ModelB1;
	public DoubleMatrix _ModelB2;
	public DoubleMatrix _ModelB3;
	public DoubleMatrix Estimates;
	public int wavefront;
	public int _wavefront;
	public int wavedirection;
	public int _wavedirection;	
	
	public double rhoMax;
	public double speedMax; 
	public double rhoCritical;
	public double parameters[];
	public double[] parametersIni;
	public String[] paramDescription;
	public int cellsec;
	public DoubleMatrix densitysec1;
	public double dt;
	public double dx;
	public int index;	

	abstract public void getNewParameters();
	abstract public void setNewParameters(double[] params);
	abstract public void setParamDefault(double[] newParamDefault);
	abstract public void getModelA();
	abstract public void getModelB1();
	abstract public void getModelB2();
	abstract public void getModelB3();
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
	

