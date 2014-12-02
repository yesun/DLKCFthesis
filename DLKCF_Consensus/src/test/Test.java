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
		//**Start network setup
		//Note: Three kinds of filters in the paper (i.e. DLKCF, DLKF and individual local KFs) are running simultaneously with the same true solution
		//All the variables indexed with 1 correspond to DLKF, and variables indexed with 2 correspond to individual KF, and variables with no index are for the DLKCF
		int cellsec=28;//*Number of cells in each section
		int cellsec1=28;
		int cellsec2=28;
		int overlapsec=10;//*Number of overlapping cells between adjacent sections
		int overlapsec1=10;
		int overlapsec2=1;//Individual local KFs have only one overlapping cells on the boundaries
		int Nsecs = 7;//*Number of sections on the entire stretch of road
		int Nsecs1 = 7;
		int Nsecs2 = 5;
		int Shocksec =3;//*The index of the section with a status transition located initially 
		int Shocksec1 =3;
		int Shocksec2 =2;
		int cells = (Nsecs-1)*(cellsec-overlapsec)+cellsec;//total number of cells
		int cells1 = (Nsecs1-1)*(cellsec1-overlapsec1)+cellsec1;
		int cells2 = (Nsecs2-1)*(cellsec2-overlapsec2)+cellsec2;
		int distMeasure=overlapsec-1;//distance between two sensors
		double dt=1d;
		double dx=1000d/((double)cells);
		//**End network setup		
	    
	    TrueSolutionCTM trueSolutionCTM= new TriangularTrueCTM(dt, dx,cells,distMeasure,Nsecs,1);//* For the last variable, 0 means no low quality sensors and 1 means low quality sensors exist.
	    
	    TrueSolution[] trueSolution=new TrueSolution[Nsecs];
	    for(int i=0;i<Nsecs;i++){
	    	trueSolution[i] = new TriangularTrue(trueSolutionCTM, dt, dx, cellsec, overlapsec, i, Nsecs,1,1,false);//* The last three variables sequentially stand for existence of model parameters perturbation (0 for false and 1 for true), low quality agents (0 for false and 1 for true) and inter-agent communications    
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
	    
	    TrueSolution[] trueSolution1=new TrueSolution[Nsecs1];
	    for(int i=0;i<Nsecs1;i++){
	    	trueSolution1[i] = new TriangularTrue(trueSolutionCTM, dt, dx, cellsec1, overlapsec1, i, Nsecs1,1,1,false);
	    }
	    if (Nsecs1==1){
	    	 trueSolution1[0].trueRight=trueSolution1[0];
	    	 trueSolution1[0].trueLeft=trueSolution1[0];
	    }
	    else{
	    	trueSolution1[0].trueRight=trueSolution1[1];
		    trueSolution1[Nsecs1-1].trueLeft=trueSolution1[Nsecs1-2];
		    for(int i=1;i<Nsecs1-1;i++){
		    	trueSolution1[i].trueLeft=trueSolution1[i-1];
		    	trueSolution1[i].trueRight=trueSolution1[i+1];
		    }
	    }
	    
	    TrueSolution[] trueSolution2=new TrueSolution[Nsecs2];
	    for(int i=0;i<Nsecs2;i++){
	    	trueSolution2[i] = new TriangularTrue(trueSolutionCTM, dt, dx, cellsec2, overlapsec2, i, Nsecs2,1,1,true);
	    }
	    if (Nsecs2==1){
	    	 trueSolution2[0].trueRight=trueSolution2[0];
	    	 trueSolution2[0].trueLeft=trueSolution2[0];
	    }
	    else{
	    	trueSolution2[0].trueRight=trueSolution2[1];
		    trueSolution2[Nsecs2-1].trueLeft=trueSolution2[Nsecs2-2];
		    for(int i=1;i<Nsecs2-1;i++){
		    	trueSolution2[i].trueLeft=trueSolution2[i-1];
		    	trueSolution2[i].trueRight=trueSolution2[i+1];
		    }
	    }
	    
	    Estimation.exportResult(trueSolutionCTM, trueSolution,trueSolution1,trueSolution2, 2000,"(M0.0025VS0.09_0.0009)_CTMpert1_AllTrue11_evenTrust_FCConsensus_fixcode_initialNoiseOn");
	}
	
}
