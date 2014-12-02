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

@SuppressWarnings("unused")
public class Test {

	public static void main(String[] args) {
		//**Start network setup
		int cellsec=10;//*number of cells in each section
		int overlapsec=1;//each section only share one boundary cell with its adjacent sections
		int Nsecs = 15;//*total number of sections
		int Shocksec =7;//*the index of the section with a status transition located initially 
		int cells = (Nsecs-1)*(cellsec-overlapsec)+cellsec;//total number of cells
		double dt=1d;
		double dx=1000d/((double)cells);
		//**End network setup

	    double rhoLeft = 0.8;//*initial density to the left of the central status transition
	    double rhoRight = 0.2;//*initial density to the right of the central status transition
	    TrueSolution[] trueSolution=new TrueSolution[Nsecs];
	    for(int i=0;i<Shocksec;i++){
	        trueSolution[i] = new TriangularTrue(rhoLeft, rhoLeft,rhoLeft,rhoRight, dt, dx, cellsec, overlapsec, i, Nsecs);
	    }
	    trueSolution[Shocksec] = new TriangularTrue(rhoLeft, rhoRight,rhoLeft,rhoRight, dt, dx, cellsec, overlapsec, Shocksec, Nsecs);
	    for(int i=Shocksec+1;i<Nsecs;i++){
	        trueSolution[i] = new TriangularTrue(rhoRight, rhoRight,rhoLeft,rhoRight, dt, dx, cellsec, overlapsec, i,Nsecs);
	    }
	    //setting adjacent sections
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
	    Estimation.exportResult(trueSolution, 2000, "LobserverFC_(M0.0025VSS0.0081)_ModeIdentificationWritten_1sideOS1sideFC_136cells");
	}	
}
