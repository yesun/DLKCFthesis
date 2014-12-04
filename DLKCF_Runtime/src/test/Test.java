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
import graph.ControlPanel;
import graph.Printer;


@SuppressWarnings("unused")
public class Test {

	public static void main(String[] args) {
		int cellsec=210;
		int overlapsec=10;
		int Nsecs = 1;
		int Shocksec =0;
		int cells = (Nsecs-1)*(cellsec-overlapsec)+cellsec;
		
		
		//Model.findDensityCorrelation(new Triangular(), new Triangular(), cells);
		//SpeedFunction.printFamilies(new FamilyGreenshield());
		
		//new Triangular().printFlux(true);
		//SpeedFunction.printFamilies(new FamilyGreenshield());
				
//		new ControlPanel(1.0d, 1000/((double)cells), cells);
	
//		double rhoLeft = 0.7;
//		double rhoRight = 0.3;
//		TrueSolution trueSolution = new GreenshieldTrue(rhoLeft, rhoRight, 1d, 1000d/((double)cells), cells);
//		RoadModel[] roadModels = new RoadModel[4];
//		roadModels[0] = RoadModel.createRoadModel(trueSolution, "D","speed","SmuldersGen",false);
//		roadModels[1] = RoadModel.createRoadModel(trueSolution, "S","speed","SmuldersGen",false);
//		roadModels[2] = RoadModel.createRoadModel(trueSolution, "DSlinear","speed","SmuldersGen",false);
//		roadModels[3] = RoadModel.createRoadModel(trueSolution, "DSlinear","speed","SmuldersGen",true);
//		Estimation.exportResult(roadModels, trueSolution, 1200, "1");
		
//		double rhoLeft = 0.3;
//		double rhoRight = 0.7;
//		TrueSolution trueSolution = new GreenshieldTrue(rhoLeft, rhoRight, 1d, 1000d/((double)cells), cells);
//		RoadModel[] roadModels = new RoadModel[4];
//		roadModels[0] = RoadModel.createRoadModel(trueSolution, "D","speed","SmuldersGen",false);
//		roadModels[1] = RoadModel.createRoadModel(trueSolution, "S","speed","SmuldersGen",false);
//		roadModels[2] = RoadModel.createRoadModel(trueSolution, "DSlinear","speed","SmuldersGen",false);
//		roadModels[3] = RoadModel.createRoadModel(trueSolution, "DSlinear","speed","SmuldersGen",true);
//		Estimation.exportResult(roadModels, trueSolution, 1200, "5");
		
//		double rhoLeft = 0.7;
//		double rhoRight = 0.3;
//		TrueSolution trueSolution = new GreenshieldTrue(rhoLeft, rhoRight, 1d, 1000d/((double)cells), cells);
//		RoadModel[] roadModels = new RoadModel[2];
//		roadModels[0] = RoadModel.createRoadModel(trueSolution, "D","speed","SmuldersGen",false);
//		roadModels[1] = RoadModel.createRoadModel(trueSolution, "DSlinear","speed","SmuldersGen",false);
//		Estimation.exportResult(roadModels, trueSolution, 1200, "3");
		
//		double rhoLeft = 0.7;
//		double rhoRight = 0.3;
//		TrueSolution trueSolution = new GreenshieldTrue(rhoLeft, rhoRight, 1d, 1000d/((double)cells), cells);
//		RoadModel[] roadModels = new RoadModel[2];
//		roadModels[0] = RoadModel.createRoadModel(trueSolution, "S","speed","SmuldersGen",false);
//		roadModels[1] = RoadModel.createRoadModel(trueSolution, "DSlinear","speed","SmuldersGen",false);
//		Estimation.exportResult(roadModels, trueSolution, 1200, "4");
		
//        Matrix a=new Matrix(2,1,2);
//        a.print(1, 1);
		double rhoLeft = 0.2;
	    double rhoRight = 0.8;
//		double rhoLeft = 0.1;
//	    double rhoRight = 0.35;
//	    double rhoLeft = 0.7;
//	    double rhoRight = 0.2;
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
//	    System.out.print((int)cellsec/2);
	    
	    
//	    System.out.print(1d/(1000d/((double)cells)));
	    
//	    int a =(int)(5/2);
	    
	    
//	    System.out.print(a);
	    
//      System.out.println(trueSolution[0].numSections);
//	    Section[] roadModelsec=new Section[11];
//	    for (int i=0;i<11;i++){
//	    	trueSolution[i].setSections();
//	    	trueSolution[i].propagate();
//		}
	    Estimation.exportResult(trueSolution, 40, "CF72_(M_0.0009_bdry0.0009(OnlyNeededBdry)VSS0.0004)_Var_standingshock1_c_XLarge1130_1");
//		System.out.print(roadModels[0].getSpeedFunction().getClass().getSimpleName());
//		Estimation estimation = new Estimation(roadModels[0], "EnKF", false);
	//	System.out.print(roadModels[0].modelVar.get(2,2)); //
//		
//		double rhoLeft = 0.1;
//		double rhoRight = 0.8;
//		TrueSolution trueSolution = new GreenshieldTrue(rhoLeft, rhoRight, 1d, 1000d/((double)cells), cells);
//		RoadModel[] roadModels = new RoadModel[1];
//		roadModels[0] = RoadModel.createRoadModel(trueSolution, "S","speed","SmuldersGen",false);
//		Estimation.exportResult(roadModels, trueSolution, 1200, "vCTM2");
//		
//		double rhoLeft = 0.7;
//		double rhoRight = 0.2;
//		TrueSolution trueSolution = new SmuldersTrue(rhoLeft, rhoRight, 1d, 1000d/((double)cells), cells);
//		RoadModel[] roadModels = new RoadModel[1];
//		roadModels[0] = RoadModel.createRoadModel(trueSolution, "S","speed","SmuldersGen",false);
//		Estimation.exportResult(roadModels, trueSolution, 1200, "Smulders");
//		
		
//		Estimation.compareEstimations(roadModels, trueSolution, 600);
		

		
		//MITSIM m = new MITSIM();
		//m.readDensity();
		
//		SpeedFunction sf=SpeedFunction.createSpeedFunction("Greenshield");
//		double[][] limit={{0.8,1.2},{0.8,1.2},{1.2,1.8},{0.8,1.2}};
//		sf.getParametersNoise(limit);
//		SpeedFunction sf1=SpeedFunction.createSpeedFunction("Greenshield");
//		DoubleMatrix para=new DoubleMatrix(sf1.parameters);
//		para.print();
		

	}
	
}
