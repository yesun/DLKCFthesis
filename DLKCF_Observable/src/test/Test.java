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
		int cellsec=28;
		int overlapsec=10;
		int Nsecs = 5;
		int Shocksec =2;
		int cells = (Nsecs-1)*(cellsec-overlapsec)+cellsec;

	    double rhoLeft = 0.7;
	    double rhoRight = 0.2;
	    TrueSolution[] trueSolution=new TrueSolution[Nsecs];
	    for(int i=0;i<Shocksec;i++){
	    	trueSolution[i] = new TriangularTrue(rhoLeft, rhoLeft,rhoLeft,rhoRight, 1d, 1000d/((double)cells), cellsec, overlapsec, i, Nsecs);
	    }
	    trueSolution[Shocksec] = new TriangularTrue(rhoLeft, rhoRight,rhoLeft,rhoRight, 1d, 1000d/((double)cells), cellsec, overlapsec, Shocksec, Nsecs);
	    for(int i=Shocksec+1;i<Nsecs;i++){
	    	trueSolution[i] = new TriangularTrue(rhoRight, rhoRight,rhoLeft,rhoRight, 1d, 1000d/((double)cells), cellsec, overlapsec, i,Nsecs);
	    }
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

	    Estimation.exportResult(trueSolution, 2000, "CF72_(M_0.0009_bdry0.0009(OnlyNeededBdry)VSS0.0004)_checkLyapunov1");

		

	}
	
}
