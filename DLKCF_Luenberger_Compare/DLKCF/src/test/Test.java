package test;


import org.jblas.DoubleMatrix;
import doubleMatrix.Concat;
import model.D;
import model.RoadModel;
import Jama.*;
import section.Section;
import trueSolution.TriangularTrue;
import trueSolution.TrueSolution;
import filters.Estimation;
import trueSolutionCTM.*;

@SuppressWarnings("unused")
public class Test {

	public static void main(String[] args) {
        //**Start Network setup
		int cellsec=10;//*Number of cells in one section
		int overlapsec=1;//*Number of overlapping cells between adjacent sections
		int Nsecs = 15;//*Number of sections
		int Shocksec =7;//*The index of the section with a shock located initially 
		int cells = (Nsecs-1)*(cellsec-overlapsec)+cellsec;//total number of cells
		double dt=1d;//time interval
		double dx=1000d/((double)cells);//cell length
		//**End Network setup
	    
	    TrueSolutionCTM trueSolutionCTM= new TriangularTrueCTM(dt, dx,cells);//True solution computed by CTM
	    
	    TrueSolution[] trueSolution=new TrueSolution[Nsecs];//True solution in each section
	    for(int i=0;i<Nsecs;i++){
	    	trueSolution[i] = new TriangularTrue(trueSolutionCTM, dt, dx, cellsec, overlapsec, i, Nsecs);
	    }
	    //Set adjacent sections
	    if (Nsecs==1){
	    	 trueSolution[0].trueRight=trueSolution[0];
	    	 trueSolution[0].trueLeft=trueSolution[0];
	    }
	    else{
	    	trueSolution[0].trueRight=trueSolution[1];
		    trueSolution[Nsecs-1].trueLeft=trueSolution[Nsecs-2];
		    for(int i=1;i<Nsecs-1;i++){
		    	trueSolution[i].trueLeft=trueSolution[i-1];
		    	trueSolution[i].trueRight=trueSolution[i+1];
		    }
	    }


	    Estimation.exportResult(trueSolutionCTM, trueSolution, 2000,"1sideSin1sideFC_NotsameSensorsinFC_(M0.01VSS0.0081)_136cells_consensus_fixedCode1");
	}
	
}
