package filters;

import filters.KCF;
import section.Section;



import org.ejml.data.*;
import org.ejml.ops.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;










import model.RoadModel;

import org.jblas.DoubleMatrix;
import org.jfree.data.xy.XYSeries;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;



import doubleMatrix.Concat;
import doubleMatrix.GaussianGenerator;
import doubleMatrix.InverseMatrix;
import trueSolution.TrueSolution;


public class Estimation {

	RoadModel roadModel;
//	Section priorSection;
	
	Filter filter;
//	String nameFilter;
	

//	int type;
	
//	GaussianGenerator modelGenerator;
	
	//[question] what's the use of isNewNoise
	public Estimation(RoadModel _roadModel, String _nameFilter) {
		roadModel = _roadModel;
		
		filter = Filter.createFilter(this);	
//		modelGenerator = new GaussianGenerator(filter.modelVar);	
	}
	

//	public void nextStep() {
//		updateTrueSolution();//
//		filter.nextStep();
//	}
	
	public void nextStepWithoutUpdateTrue() {
		filter.nextStep();
	}
	public void nextStepNoDataWithoutUpdateTrue() {
		filter.nextStepNoData();
	}
	
	
	public void updateTrueSolution() {
		roadModel.updateTrueSolution();//
	}

	
//	public void newBoundaries(double rhoLeft, double rhoRight) {
//		roadModel.newBoundaries(rhoLeft, rhoRight);
//		filter = Filter.createFilter(this);
//	}
	

	public DoubleMatrix propagate(DoubleMatrix _density) {		
      return roadModel.propagate(_density);
	}
	
	public DoubleMatrix getDensityMean() {
		return roadModel.getDensityMean(filter.mean);
	}
	
//	public DoubleMatrix getDensityVar() {
//		return roadModel.getDensityVar(filter.var);
//	}
	

//	public DoubleMatrix getVar() {
//		return filter.var;
//	}
	
//	public DoubleMatrix getMean() {
//		return filter.mean;
//	}
	

	
//	public double getSpaceStep(){
//		return roadModel.getSpaceStep();
//	}
	
	public void setSection(Section _section){
		
			roadModel.setSetion(_section);
			roadModel.updateModelVar();
//			filter.measureVar=roadModel.modelVar;
	}
	
	
	

	
	public static void exportResult(TrueSolution[] trueSolution, int limite, String folder) {
		try {
//			int totalcells = trueSolution[0].cells;
//			double spaceStep = trueSolution[0].dx;
			
			
			
			int numSections = trueSolution[0].numSections;
			
//			BufferedWriter writer;
			BufferedWriter[] writerSection = new BufferedWriter[numSections];
//			BufferedWriter[] writerVar;
			BufferedWriter writerTrue;
			BufferedWriter writerAvr;
			BufferedWriter Ltilde;
			BufferedWriter InverseVar;
			BufferedWriter writerLyap;
			BufferedWriter writerKgain;
			BufferedWriter[] writerKVar= new BufferedWriter[numSections];
			BufferedWriter[] writerError= new BufferedWriter[numSections];
			BufferedWriter[] Mode= new BufferedWriter[numSections];
			BufferedWriter[] trueSection= new BufferedWriter[numSections];
			BufferedWriter writerDisagreement;
//			BufferedWriter writerAvrError;
			

				System.out.print("Beginning of simulation ");
				
				new File("results/"+folder).mkdir();
				
//				trueSolution.newBoundaries(trueSolution.rhoLeft, trueSolution.rhoRight);
//				trueSolution.numUp=600;
				RoadModel[] roadModels=new RoadModel[numSections];

				
				for (int i=0;i<numSections;i++){					
					trueSolution[i].section=trueSolution[i].setSections();
				}
				for (int i=0;i<numSections;i++){					
					roadModels[i]=trueSolution[i].setRoadModels();
				}
				
//				Section[] secs=trueSolution.setSections();
//				for (int i = 0; i<numSections; i++) {
//					System.out.print(roadModels[i].section.getClass().getSimpleName());
//				}
				
//				for (int i = 0; i<numSections; i++) {
//					System.out.print(secs[i].getClass().getSimpleName());
//				}
				
				
//				String S1 = "results/"+folder+"/"+"AveragedResult";
				
//				writer = new BufferedWriter(new FileWriter(new File(S1+".csv")));
//				writerVar = new BufferedWriter[numEstimations];
				writerTrue = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"trueState.csv")));
				writerAvr = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"writerAvr.csv")));
				Ltilde = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Ltilde.csv")));
				writerLyap = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Lyapfun.csv")));
				InverseVar = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"InverseVar.csv")));
//				writerSection = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"SectionEstimates.csv")));
//				writerError = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"error.csv")));
//				writerAvrError = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Avrerror.csv")));
				writerDisagreement = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Disagreement.csv")));
				writerKgain = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Kgain.csv")));
				
				
				
				Estimation[] estimations = new Estimation[numSections];
				
				double[] deltaV=new double[numSections];

					

					
				
	             
				
				
				
				for (int i = 0; i<numSections; i++) {
		//			roadModels[i].newBoundaries(trueSolution.rhoLeft, trueSolution.rhoRight);
					estimations[i] = new Estimation(roadModels[i], "KCF");
					String S = "results/"+folder+"/"+roadModels[i].nameModel;
					writerSection[i] = new BufferedWriter(new FileWriter(new File(S+".csv")));
					writerError[i]=new BufferedWriter(new FileWriter(new File(S+"error.csv")));
					Mode[i]=new BufferedWriter(new FileWriter(new File(S+"Mode.csv")));
					trueSection[i]=new BufferedWriter(new FileWriter(new File(S+"trueSection.csv")));
					writerKVar[i] = new BufferedWriter(new FileWriter(new File(S+"Var.csv")));
				}            
				
			    //SYSTEM STRUCTURE VALID FOR SAME CELLSECS AND SAME OVERLAPSEC//
				int overlap=roadModels[0].trueSolution.overlapsec;
				DoubleMatrix I1=DoubleMatrix.zeros(roadModels[0].section.cellsec,overlap);
				for (int i=0;i<overlap;i++){
					I1.put(i,i,1);
				}
				DoubleMatrix I2=DoubleMatrix.zeros(roadModels[0].section.cellsec,overlap);
				for (int i=roadModels[0].section.cellsec-overlap;i<roadModels[0].section.cellsec;i++){
					I2.put(i,i-(roadModels[0].section.cellsec-overlap),1);
				}
				DoubleMatrix I123=DoubleMatrix.concatVertically(I1.transpose(), I2.transpose());
				
				DoubleMatrix H_hat=I2.transpose();
				for (int i=0;i<numSections-2;i++){
					H_hat=Concat.Diagonal(H_hat, I123);
				}
				H_hat=Concat.Diagonal(H_hat, I1.transpose());
				
				
				
				int cellsec=roadModels[0].section.cellsec;
				DoubleMatrix L_tilde=DoubleMatrix.zeros((numSections-1)*2*overlap, numSections*cellsec);
				
				for(int i=0;i<numSections-1;i++){
					for(int j=0;j<overlap;j++){
						L_tilde.put(i*(2*overlap)+j,(i+1)*(cellsec)+j, 1);
					}
					for(int j=0;j<overlap;j++){
						L_tilde.put(i*(2*overlap)+overlap+j,(i+1)*(cellsec)-overlap+j, 1);
					}
					for(int j=0;j<overlap;j++){
						L_tilde.put(i*(2*overlap)+j,(i+1)*(cellsec)-overlap+j, -1);
					}
					for(int j=0;j<overlap;j++){
						L_tilde.put(i*(2*overlap)+overlap+j,(i+1)*(cellsec)+j, -1);
					}
				}
				
//				for(int i=0;i<(numSections-1)*2*overlap;i++){
//					for(int j=0;j<numSections*cellsec;j++){
//						Ltilde.write(roundedString(L_tilde.get(i,j))+",");
//					}
//					Ltilde.write("\n");
//				}
//				Ltilde.flush(); Ltilde.close();
				//SYSTEM STRUCTURE VALID FOR SAME CELLSECS AND SAME OVERLAPSEC//
				
				DoubleMatrix[] S=new DoubleMatrix[numSections];
				
				

	             
				for (int k=0; k<limite; k++) {
					for (int i=0; i<numSections ; i++) {
						if(numSections==1){
							for (int j=0; j<trueSolution[i].cellsec ; j++) {
								writerTrue.write(trueSolution[i].trueStates.get(j)+",");							
							}
						}
						else{
							if (i==0){
								for (int j=0; j<trueSolution[i].cellsec-overlap ; j++) {
									writerTrue.write(trueSolution[i].trueStates.get(j)+",");							
								}
							}
							else if (i>0 && i<numSections-1){
								for (int j=0; j<overlap ; j++) {
									writerTrue.write((trueSolution[i].trueStates.get(j)+trueSolution[i-1].trueStates.get(cellsec-overlap+j))/2+",");	
								}
								for (int j=0; j<trueSolution[i].cellsec-2*overlap ; j++) {
									writerTrue.write(trueSolution[i].trueStates.get(overlap+j)+",");								
								}
							}
							else if (i==numSections-1){
								for (int j=0; j<overlap ; j++) {
									writerTrue.write((trueSolution[i].trueStates.get(j)+trueSolution[i-1].trueStates.get(cellsec-overlap+j))/2+",");	
								}
								for (int j=0; j<trueSolution[i].cellsec-overlap ; j++) {
									writerTrue.write(trueSolution[i].trueStates.get(overlap+j)+",");								
								}
							}
						}
						
						
						
					}
					writerTrue.write("\n");
					//-----
					
					
					
					
		//			roadModels=trueSolution.setRoadModels();
		//			for (int i = 0; i<numSections; i++) {
		//				estimations[i] = new Estimation(roadModels[i], "KCF");
		//			}
					
					DoubleMatrix[] mean = new DoubleMatrix[numSections];
		//			mean[0]=estimations[0].getDensityMean();
		//			int t;
		//			t=mean[0].length;
		//			System.out.print(t);
					
					for (int i=0; i<numSections; i++) {
						
	
						mean [i] = estimations[i].getDensityMean();
//						DoubleMatrix var=Filter.computeVar(estim.filter.samples, estim.filter.mean);
//					    DoubleMatrix varNoData=Filter.computeVar(estim.filter.samplesWithNoData, estim.filter.meanWithNoData);
						if (numSections==1){
							for (int j=0; j<mean[i].length ; j++) {
								writerAvr.write(mean[i].get(j)+",");							
							}
						}
						else{
							if (i==0){
								for (int j=0; j<mean[i].length-overlap ; j++) {
									writerAvr.write(mean[i].get(j)+",");							
								}
							}
							else if (i>0 && i<numSections-1){
								for (int j=0; j<overlap ; j++) {
									writerAvr.write((mean[i].get(j)+mean[i-1].get(cellsec-overlap+j))/2+",");	
								}
								for (int j=0; j<mean[i].length-2*overlap ; j++) {
									writerAvr.write(mean[i].get(overlap+j)+",");								
								}
							}
							else if (i==numSections-1){
								for (int j=0; j<overlap ; j++) {
									writerAvr.write((mean[i].get(j)+mean[i-1].get(cellsec-overlap+j))/2+",");	
								}
								for (int j=0; j<mean[i].length-overlap ; j++) {
									writerAvr.write(mean[i].get(overlap+j)+",");								
								}
							}
						}
						
						
						
						
						for (int j=0; j<mean[i].length ; j++) {
							writerSection[i].write(mean[i].get(j)+",");
							
						}
						
						
						
						
//						if (i==0){
//							for (int j=0; j<mean[i].length-overlap ; j++) {
//								writerError[i].write(roundedString(mean[i].get(j)-trueSolution[i].trueStates.get(j))+",");						
//							}
//							for(int j=mean[i].length-overlap;j<mean[i].length;j++){
//								writerError[i].write(roundedString(mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i+1].trueStates.get(j-(mean[i].length-overlap)))/2)+",");
//							}
//						}
//						
//						else if (i>0 && i<numSections-1){
//							for (int j=0; j<overlap ; j++) {
//								writerError[i].write(roundedString(mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i-1].trueStates.get(j+(mean[i].length-overlap)))/2)+",");
//							}
//							for (int j=0; j<mean[i].length-2*overlap ; j++) {
//								writerError[i].write(roundedString(mean[i].get(overlap+j)-trueSolution[i].trueStates.get(overlap+j))+",");								
//							}
//							for(int j=mean[i].length-overlap;j<mean[i].length;j++){
//								writerError[i].write(roundedString(mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i+1].trueStates.get(j-(mean[i].length-overlap)))/2)+",");
//							}
//						}
//						
//						else if (i==numSections-1){
//							for (int j=0; j<overlap ; j++) {
//								writerError[i].write(roundedString(mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i-1].trueStates.get(j+(mean[i].length-overlap)))/2)+",");
//							}
//							for (int j=overlap; j<mean[i].length; j++) {
//								writerError[i].write(roundedString(mean[i].get(j)-trueSolution[i].trueStates.get(j))+",");							
//							}
//						}
						
    					for (int j=0; j<mean[i].length ; j++) {
							writerError[i].write(mean[i].get(j)-trueSolution[i].trueStates.get(j)+",");
							
						}
    					
    					double sum=0;
    					for (int j=0; j<mean[i].length ; j++) {
							sum=sum+mean[i].get(j)-trueSolution[i].trueStates.get(j);
							
						}
    					writerError[i].write(sum+",");
    					
						

							
							
							
						
						
						
						//NEED CHANGE /AVERAGE ERROR//
	//					for (int j=0; j<mean[i].length ; j++) {
	//						if (i==0){
	//							writer.write(roundedString(mean[i].get(j))+",");
	//							writerError.write(roundedString(mean[i].get(j)-trueSolution.getDensityValue(i+j, trueSolution.numUp-1))+",");
	//						}
	//						else {
	//							if(j==0){
	//								writer.write(roundedString((mean[i].get(j)+mean[i-1].get(mean[i-1].length-1))/2)+",");
	//								writerError.write(roundedString((mean[i].get(j)+mean[i-1].get(mean[i-1].length-1))/2-trueSolution.getDensityValue(i*2, trueSolution.numUp-1))+",");
	//							}
	//							else{
	//								writer.write(roundedString(mean[i].get(j))+",");
	//								writerError.write(roundedString(mean[i].get(j)-trueSolution.getDensityValue(i*2+j, trueSolution.numUp-1))+",");
	//							}	
	//						}						
	//						
	//					}
	//					if (i==numSections-1){
	//						writer.write(roundedString(mean[i].get(mean[i].length-1))+",");
	//						writerError.write(roundedString(mean[i].get(mean[i].length-1)-trueSolution.getDensityValue(i*2+mean[i].length-1, trueSolution.numUp-1))+",");
	//					}
						//NEED CHANGE /AVERAGE ERROR//
	
						
						
	//					for (int j=0; j<mean[i].length-1 ; j++) {
	//						if (i==0){
	//							writer.write(roundedString(mean[i].get(j))+",");
	//						}
	//						else{
	//							if(j==0){
	//								writer.write(roundedString((mean[i].get(j)+mean[i-1].get(mean[i-1].length-1))/2)+",");
	//							}
	//							else{
	//								writer.write(roundedString(mean[i].get(j))+",");
	//							}	
	//						}
	//						
	//						
	//					}
						
//						if (i==numSections-1){
//							writer.write(roundedString(mean[i].get(mean[i].length-1))+",");
//						}
						
						
//						for (int j=0; j<(mean.length)/2 ; j++) {
//							
//							writerVar[i].write(roundedString(estim.filter.f_var.get(j,j))+",");
//							writerVarNoData[i].write(roundedString(estim.filter.f_varNoData.get(j,j))+",");								
//						}
						
//						writerVar[i].write("\n");
//						writerVarNoData[i].write("\n");
						
//						for (int j=0; j<(mean.length)/2 ; j++) {
//							for (int m=0; m<(mean.length)/2 ; m++) {
//								writerVar[i].write(roundedString(estim.filter.var.get(j,m))+",");
//								writerVarNoData[i].write(roundedString(varNoData.get(j,m))+",");	
//							}
//							writerVar[i].write("\n");
//							writerVarNoData[i].write("\n");
//							
//						}
						
//						estim.nextStepWithoutUpdateTrue();//
							
							for (int j=0; j<mean[i].length ; j++) {
								trueSection[i].write(trueSolution[i].trueStates.get(j,0)+",");
								
							}
							trueSection[i].write("\n");
						writerError[i].write("\n");
						writerSection[i].write("\n");
					}
					writerAvr.write("\n");
	//				writer.write("\n");
					
//					for (int j=0; j<mean[1].length ; j++) {
//						InverseVar.write(estimations[1].filter.var.get(j,j)+",");
//						
//					}
//					InverseVar.write("\n");
					
//					estimations[0].filter.measureVar.print();
					
					
//					for (int i=0; i<numSections; i++) {
//						
//						estimations[i].priorSection=estimations[i].roadModel.section;
//					}
					
					
					
					Section[] secs=new Section[numSections];
					for(int i=0;i<numSections;i++){
						secs[i]=trueSolution[i].setSections();//every step, update section 
					}
					for(int i=0;i<numSections;i++){
						secs[i].Estimates=mean[i];
					}
					
					for(int i=0;i<numSections;i++){
						if(secs[i].getClass().getSimpleName().equals("FC")){
							if(trueSolution[i].flux(trueSolution[i].trueStates.get(secs[i]._wavefront,0))-trueSolution[i].flux(trueSolution[i].trueStates.get(secs[i]._wavefront+1,0))>=0){
								
									secs[i]._wavedirection=0;
								
							}
							else{
								
									secs[i]._wavedirection=1;
								
							}
						}
					}
					
					
					
					
					
					for(int i=0;i<numSections;i++){
						if(secs[i].getClass().getSimpleName().equals("FC")){
							secs[i].getwavefrontfromEstimation();
			//				secs[i].wavefront=secs[i]._wavefront;
			//				secs[i].wavefront=6;
			//				secs[i].wavefront=k%2+2;
			//				secs[i].wavefront=cellsec-2-(int)(k/20);
							if(secs[i].wavefront<0){
								secs[i].wavefront=0;
							}
							if(secs[i].wavefront<=cellsec-2){
							     if(trueSolution[i].flux(secs[i].Estimates.get(secs[i].wavefront,0))-trueSolution[i].flux(secs[i].Estimates.get(secs[i].wavefront+1,0))>0){
								
								secs[i].wavedirection=0;
							
						         }
						         else{
							
								secs[i].wavedirection=1;
							
						         }
							}
							else if (secs[i].wavefront==cellsec-1){
								if(secs[i].index!=numSections-1){
									if(trueSolution[i].flux(secs[i].Estimates.get(secs[i].wavefront,0))-trueSolution[i+1].flux(secs[i].Estimates.get(overlap,0))>0){										
										secs[i].wavedirection=0;									
								    }
								    else{					
										secs[i].wavedirection=1;									
								    }
								}
								else{
									secs[i].wavedirection=0;
								}
								
							}
							
//							secs[i].wavedirection=secs[i]._wavedirection;
//							if (secs[i].wavedirection==0){
//								secs[i].wavedirection=1;
//							}
							
			//				System.out.print(secs[i].index);
							secs[i].getModelA();
				 		    secs[i].getModelB1();
						    secs[i].getModelB2();
						   
						    
								
							
						}
						
					}
			//		System.out.print(trueSolution.numUp);
                  
			//		System.out.print(secs[4].getClass().getSimpleName());
					

					
					for (int i=0; i<numSections; i++) {
						estimations[i].setSection(secs[i]);
					}
					
					for (int i=0; i<numSections; i++) {
						trueSolution[i].section=secs[i];
					}
					
					for (int i=0; i<numSections; i++) {
						Mode[i].write(estimations[i].roadModel.section.getClass().getSimpleName()+",");
						Mode[i].write(estimations[i].roadModel.section.wavefront+",");//estimated location of shock, at initial step this is not set a value (=0) 
						Mode[i].write(estimations[i].roadModel.section._wavefront+",");//true location of shock
						Mode[i].write(estimations[i].roadModel.section._wavedirection+",");
						Mode[i].write(estimations[i].roadModel.section.wavedirection+",");
						Mode[i].write("\n");
					}
					
					for (int i=0; i<numSections; i++) {
						trueSolution[i].updateMeasurement();
						roadModels[i].updateMeasurement();
						estimations[i].filter.getNewParametersFromModel();
					}
					
//					for (int i=0; i<numSections; i++) {
//						if(i==4){
//							System.out.print(estimations[i].filter.measure.getRows());
//						}
//					}
					
	//				estimations[4].roadModel.section.ModelA.print();
	//	    System.out.print(secs[5].wavefront);
	//				DoubleMatrix d=trueSolution.getDiscretizedTrueSolution();
		//			System.out.print(d.get(4*(50),0));
					
					DoubleMatrix[] error1=new DoubleMatrix[numSections];
					for (int i=0; i<numSections; i++) {
						//NEED CHANGE
						
						
						error1[i]=mean[i].sub(trueSolution[i].trueStates);
						
//						if (i==0){
//							for (int j=0; j<mean[i].length-overlap ; j++) {
//								error1[i].put(j,0,mean[i].get(j)-trueSolution[i].trueStates.get(j));						
//							}
//							for(int j=mean[i].length-overlap;j<mean[i].length;j++){
//								error1[i].put(j,0,mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i+1].trueStates.get(j-(mean[i].length-overlap)))/2);
//							}
//						}
//						
//						else if (i>0 && i<numSections-1){
//							for (int j=0; j<overlap ; j++) {
//								error1[i].put(j,0,mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i-1].trueStates.get(j+(mean[i].length-overlap)))/2);
//							}
//							for (int j=0; j<mean[i].length-2*overlap ; j++) {
//								error1[i].put(j,0,mean[i].get(overlap+j)-trueSolution[i].trueStates.get(overlap+j));						
//							}
//							for(int j=mean[i].length-overlap;j<mean[i].length;j++){
//								error1[i].put(j,0,mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i+1].trueStates.get(j-(mean[i].length-overlap)))/2);
//							}
//						}
//						
//						else if (i==numSections-1){
//							for (int j=0; j<overlap ; j++) {
//								error1[i].put(j,0,mean[i].get(j)-(trueSolution[i].trueStates.get(j)+trueSolution[i-1].trueStates.get(j+(mean[i].length-overlap)))/2);
//							}
//							for (int j=overlap; j<mean[i].length; j++) {
//								error1[i].put(j,0,mean[i].get(j)-trueSolution[i].trueStates.get(j));												
//							}
//						}
						//NEED CHANGE
						//error1[i].print();
						//InverseMatrix.invPoSym(estimations[i].filter.var).print();
						//estimations[0].filter.Kgain.print();
						//estimations[1].filter.G.print();						
					}
					
					DoubleMatrix Lyap=DoubleMatrix.zeros(1, 1);
					DoubleMatrix Lyaploc=DoubleMatrix.zeros(1, 1);
					for (int i=0; i<numSections; i++) {
						Lyap= Lyap.add(error1[i].transpose().mmul(InverseMatrix.invPoSym(estimations[i].filter.var)).mmul(error1[i]));
					}
					
					writerLyap.write(Lyap.get(0,0)+",");	
					
					for (int i=0; i<numSections; i++) {
						
						Lyaploc=error1[i].transpose().mmul(InverseMatrix.invPoSym(estimations[i].filter.var)).mmul(error1[i]);
						writerLyap.write(Lyaploc.get(0,0)+",");	
					}
					
					for (int i = 0; i<numSections; i++) {
						S[i]=(estimations[i].filter.measure.transpose()).mmul(InverseMatrix.invPoSym(estimations[i].filter.measureVar)).mmul(estimations[i].filter.measure);
					}
					
					DoubleMatrix temp6=DoubleMatrix.zeros(cellsec, cellsec);
					
					for (int i=0; i<numSections; i++) {
						temp6=InverseMatrix.invPoSym(estimations[i].filter.var).sub(estimations[i].roadModel.section.ModelA.transpose().mmul(InverseMatrix.invPoSym((estimations[i].roadModel.section.ModelA.mmul(estimations[i].filter.var).mmul(estimations[i].roadModel.section.ModelA.transpose())).add(estimations[i].filter.modelVar.add((estimations[i].filter.computeVar(estimations[i].filter.var)).mmul(S[i]).mmul(estimations[i].filter.computeVar(estimations[i].filter.var)))))).mmul(estimations[i].roadModel.section.ModelA));
//						temp6=InverseMatrix.invPoSym(estimations[i].filter.var).sub(estimations[i].roadModel.section.ModelA.transpose().mmul(InverseMatrix.invPoSym((estimations[i].roadModel.section.ModelA.mmul(estimations[i].filter.var).mmul(estimations[i].roadModel.section.ModelA)).add(estimations[i].filter.modelVar.add(estimations[i].filter.computeVar(estimations[i].filter.var).mmul(S[i]).mmul(estimations[i].filter.computeVar(estimations[i].filter.var)))))).mmul(estimations[i].roadModel.section.ModelA));
						deltaV[i]=-(error1[i].transpose().mmul(temp6).mmul(error1[i])).get(0,0);
					}
                    for (int i=0; i<numSections; i++) {
						
						
						writerLyap.write(deltaV[i]+",");	
					}
                    writerLyap.write("\n");	
					
					
					DoubleMatrix disag=DoubleMatrix.zeros(1, 1);
					for (int i=0; i<numSections-1; i++) {
						disag= disag.add((mean[i].getRange(cellsec-overlap, cellsec,0,1).sub(mean[i+1].getRange(0, overlap,0,1))).transpose().mmul(mean[i].getRange(cellsec-overlap, cellsec,0,1).sub(mean[i+1].getRange(0, overlap,0,1))));
					}
					writerDisagreement.write(disag.get(0,0)+",");
					for (int i=0; i<numSections-1; i++) {
						writerDisagreement.write(((mean[i].getRange(cellsec-overlap, cellsec,0,1).sub(mean[i+1].getRange(0, overlap,0,1))).transpose().mmul(mean[i].getRange(cellsec-overlap, cellsec,0,1).sub(mean[i+1].getRange(0, overlap,0,1)))).get(0,0)+",");
					}
					writerDisagreement.write("\n");
					
					for(int i=0; i<numSections; i++){
						trueSolution[i].update();
					}
//					System.out.print(trueSolution[5].trueStates.get(3,0));
					
					for(int i=0; i<numSections; i++){
						trueSolution[i].refinetrueSolution();
					}
					
//					double inflow=0.1125+0.1125*Math.sin(k*(Math.PI/4000)+(Math.PI));
//					if (inflow>trueSolution[0].receiving(trueSolution[0].trueStatesPrior.get(0,0))){
//						inflow=trueSolution[0].receiving(trueSolution[0].trueStatesPrior.get(0,0));
//					}
					
//					double outflow=trueSolution[numSections-1].receiving(trueSolution[numSections-1].trueStatesPrior.get(cellsec-1,0));
//					double outflow=0.1125+0.1125*Math.sin(k*(Math.PI/1000));
//					if (outflow>trueSolution[numSections-1].sending(trueSolution[numSections-1].trueStatesPrior.get(cellsec-1,0))){
//						outflow=trueSolution[numSections-1].sending(trueSolution[numSections-1].trueStatesPrior.get(cellsec-1,0));
//					}
					
//					double upOut=trueSolution[0].computeFlux(trueSolution[0].trueStatesPrior.get(0,0),trueSolution[0].trueStatesPrior.get(1,0));
//				    trueSolution[0].trueStates.put(0,0,trueSolution[0].trueStatesPrior.get(0,0)+(trueSolution[0].dt/trueSolution[0].dx)*(inflow-upOut));

//					double downIn=trueSolution[numSections-1].computeFlux(trueSolution[numSections-1].trueStatesPrior.get(cellsec-2,0),trueSolution[numSections-1].trueStatesPrior.get(cellsec-1,0));
//					trueSolution[numSections-1].trueStates.put(cellsec-1,0,trueSolution[numSections-1].trueStatesPrior.get(cellsec-1,0)+(trueSolution[0].dt/trueSolution[0].dx)*(downIn-outflow));
					

						
					
					
//					trueSolution[0].trueStates.put(0,0,0.5+0.5*Math.sin(k*(Math.PI/1000)+(Math.PI)));
//					trueSolution[numSections-1].trueStates.put(cellsec-1,0,0.5+0.5*Math.sin(k*(Math.PI/1000)));
					
//					trueSolution[0].trueStates.put(0,0,0.1+0.1*Math.sin(k*(Math.PI/1000)+(Math.PI/2)));
//					trueSolution[numSections-1].trueStates.put(cellsec-1,0,0.7+0.3*Math.sin(k*(Math.PI/1000)));
					
//					if((k)%2==0){
//						trueSolution[0].trueStates.put(0,0,0.7);
//					    trueSolution[numSections-1].trueStates.put(cellsec-1,0,0.2);
//					}
//					else{
//						trueSolution[0].trueStates.put(0,0,0.4);
//						trueSolution[numSections-1].trueStates.put(cellsec-1,0,0.1);
//				    }
					
//					if((k/20)%2==0){
///						trueSolution[10].trueStates.put(cellsec-1,0,0.4);
//					}
//					else{
//						trueSolution[10].trueStates.put(cellsec-1,0,0.8);
//					}
					
			//		trueSolution.numUp=k+1;
					
					for(int i=0; i<numSections; i++){
						trueSolution[i].updatemeasurement();
					}
					
					double[] tr=new double [numSections];
					for (int i=0; i<numSections; i++) {
						
						for (int j=0;j<mean[i].length;j++){
							tr[i]=tr[i]+estimations[i].filter.var.get(j,j);						
						}
						writerKVar[i].write(tr[i]+",");
					}
					for (int i=0; i<numSections; i++) {
						writerKVar[i].write("\n");
					}
					
					
					
					for (int i=0; i<numSections; i++) {
//						estimations[i].nextStepNoDataWithoutUpdateTrue();
						estimations[i].nextStepWithoutUpdateTrue();
					}
					//------
//WRITE DOWN KALMAN GAIN					
//					for (int i=0; i<numSections; i++) {
//						if(i==1){
//							for(int l=0;l<2;l++){
//								for(int j=0;j<cellsec;j++){
//									writerKgain.write(estimations[i].filter.Kgain.get(j,l)+",");
//								}
//								writerKgain.write("\n");	
//							}
//							
//							
//						}
//					}
					
			//		estimations[1].filter.Kgain.print();
					if(numSections==1){
						DoubleMatrix G1=DoubleMatrix.zeros(cellsec, cellsec);
						DoubleMatrix F1=DoubleMatrix.zeros(cellsec, cellsec);
						DoubleMatrix zero1=DoubleMatrix.zeros(cellsec, cellsec);
						DoubleMatrix zero2=DoubleMatrix.zeros(cellsec, cellsec);
						DoubleMatrix I11=DoubleMatrix.eye(cellsec);
						
	

					}
					
					else{
					
						DoubleMatrix[] G=new DoubleMatrix[numSections];
						for (int i=0; i<numSections; i++) {
							G[i]=estimations[i].roadModel.section.ModelA.mmul(estimations[i].filter.priorVar).mmul(estimations[i].roadModel.section.ModelA.transpose()).add(estimations[i].roadModel.modelVar).add(estimations[i].filter.f_var.mmul(S[i]).mmul(estimations[i].filter.f_var));
						}
						
						
						DoubleMatrix BigG=G[0];
						for (int i=1;i<numSections;i++){
							BigG=Concat.Diagonal(BigG, G[i]);
						}
						
						DoubleMatrix BigLambda=InverseMatrix.invPoSym(estimations[0].filter.var).mmul(2);
						for (int i=1;i<numSections;i++){
							BigLambda=Concat.Diagonal(BigLambda, InverseMatrix.invPoSym(estimations[i].filter.var).mmul(2));
						}
																	
						DoubleMatrix A_hat=estimations[0].roadModel.section.ModelA;
						for (int i=1;i<numSections;i++){
							A_hat=Concat.Diagonal(A_hat, estimations[i].roadModel.section.ModelA);
						}
						
						DoubleMatrix Lower=A_hat.transpose().mmul(L_tilde.transpose()).mmul(H_hat).mmul(BigG).mmul(H_hat.transpose()).mmul(L_tilde).mmul(A_hat);
						double[][] _BigLambda=new double[BigLambda.rows][BigLambda.columns];
						double[][] _Lower=new double[Lower.rows][Lower.columns];
						for (int i=0;i<BigLambda.rows;i++){
							for (int j=0;j<BigLambda.columns;j++){
								if (BigLambda.get(i,j)<=0){
									BigLambda.put(i,j,1);
								}
								_BigLambda[i][j]=BigLambda.get(i, j);
							}
						}
						for (int i=0;i<Lower.rows;i++){
							for (int j=0;j<Lower.columns;j++){
								_Lower[i][j]=Lower.get(i, j)+5;
							}
						}
//						Matrix bigLambda=new Matrix(_BigLambda);
//						Matrix lower=new Matrix(_Lower);
						DenseMatrix64F bigLambdaSimple=new DenseMatrix64F (_BigLambda);
						DenseMatrix64F lowerSimple=new DenseMatrix64F (_Lower);
						EigenOps bigLambdaEig=new EigenOps();
						EigenOps lowerEig=new EigenOps();
						double[] temp1={0,0};
						double[] temp2={0,0};
						bigLambdaEig.boundLargestEigenValue(bigLambdaSimple, temp1);
						lowerEig.boundLargestEigenValue(lowerSimple, temp2);
						double _gamma=Math.sqrt(1/((temp1[1])*(temp2[1])));
						
						
						
						
						
						
						
					System.out.print(_gamma+"\n");
					
			
					
//					for (int i=0; i<numSections; i++) {
//						estimations[i].filter.var.print();
//					}
					
					for (int i=0; i<numSections; i++) {
						if(estimations[i].roadModel.section.getClass().getSimpleName().equals("FC")){
							estimations[i].filter.gamma=_gamma;
						}
						else{
						estimations[i].filter.gamma=0;
//			            estimations[i].filter.gamma=0;
						}
						
					}
					
					//------
					
					DoubleMatrix[] mean1=new DoubleMatrix[numSections];
					mean1[0]=estimations[0].filter.Consensus2(estimations[1].filter);
					for (int i=1; i<numSections-1; i++) {
	                    mean1[i]=estimations[i].filter.Consensus(estimations[i-1].filter,estimations[i+1].filter);				
					}
					mean1[numSections-1]=estimations[numSections-1].filter.Consensus1(estimations[numSections-2].filter);
					for (int i=0; i<numSections; i++) {
						estimations[i].filter.mean=mean1[i];
					}
					}
					
//					for (int i=0; i<numSections; i++) {
//						if(estimations[i].roadModel.section.getClass().getSimpleName().equals("FC")){
//							estimations[i].filter.mean.put(0,0,trueSolution[i].trueStates.get(0,0));
//							estimations[i].filter.mean.put(cellsec-1,0,trueSolution[i].trueStates.get(cellsec-1,0));
//						}
//						
//					}
					DoubleMatrix[] error2=new DoubleMatrix[numSections];
					for (int i=0; i<numSections; i++) {						
						error2[i]=estimations[i].filter.mean.sub(trueSolution[i].trueStates);
					}
					if (numSections==1){
						DoubleMatrix F1=DoubleMatrix.zeros(cellsec, cellsec);
						DoubleMatrix F2=DoubleMatrix.zeros(cellsec, cellsec);

						DoubleMatrix I11=DoubleMatrix.eye(cellsec);
						
					    F1=I11.sub(estimations[0].filter.var.mmul(S[0]));
					    F2=I11.sub(estimations[0].filter.Kgain.mmul(estimations[0].filter.measure));
//					    if(F1.equals(F2)){
//							System.out.print("T");
//						}
//						else{
//							System.out.print("F");
//						}
					    
						if(error2[0].equals(F2.mmul(estimations[0].roadModel.section.ModelA).mmul(error1[0]))){
							System.out.print("T");
						}
						else{
							System.out.print("F");
						}
					}
					
//					System.out.print(estimations[4].filter.measure.getRows()+"\n");
					
					
					
					
//					if (k==370){
//						for (int i=0; i<numSections; i++) {
//							error1[i].transpose().mmul(InverseMatrix.invPoSym(estimations[i].filter.var)).mmul(error1[i]).print();
//						}
//						
//						for(int i=0;i<cellsec;i++){
//							for(int j=0;j<cellsec;j++){
//								InverseVar.write(roundedString((InverseMatrix.invPoSym(estimations[2].filter.var)).get(i,j))+",");
//							}
//							InverseVar.write("\n");					
//						}
//						
//						for (int j=0;j<cellsec;j++){
//							InverseVar.write(roundedString(error1[2].get(j,0))+",");
//						}
//						
//						trueSolution[0].getDiscretizedTrueSolution().print();
//						InverseVar.write("\n");
//						
//						InverseVar.flush(); InverseVar.close();
//					}
//					
//					if(k==limite-1){
//						for(int i=0;i<numSections;i++){
//							trueSolution[i].trueStates.print();
//						}
//						
//					}
//				 if (k==limite-1){
//					 if (trueSolution[1].trueStates.get(0,0)>trueSolution[1].rhoCritical){
//						 System.out.print("C");
//					 }
//					 else{
//						 System.out.print("F");
//					 }
//				 }
					
					

				}
				
				for (int i = 0; i<numSections; i++) {
					writerSection[i].flush(); writerSection[i].close();
					writerError[i].flush(); writerError[i].close();
					Mode[i].flush(); Mode[i].close();
					trueSection[i].flush(); trueSection[i].close();
					writerKVar[i].flush(); writerKVar[i].close();
				}
//				writer.flush(); writer.close();
				writerAvr.flush(); writerAvr.close();
				writerTrue.flush(); writerTrue.close();
				writerLyap.flush(); writerLyap.close();
				writerKgain.flush(); writerKgain.close();
				writerDisagreement.flush(); writerDisagreement.close();
				InverseVar.flush(); InverseVar.close();
				
				System.out.println(" End");	
				
		
		}
		catch (Exception e) {e.printStackTrace();}
		
	}
	

	
//	public static void exportResult(RoadModel[] roadModels, TrueSolution trueSolution, int limite, int numSimulMax) {
//		try {
//			int cells = trueSolution.cells;
//			double spaceStep = trueSolution.dx;
//			
//			int numEstimations = roadModels.length;
//			
//			BufferedWriter[] writerDensity;
//			BufferedWriter[] writerDensityNoData;
//			BufferedWriter[] writerSpeed;
//			BufferedWriter[] writerSpeedNoData;
//			BufferedWriter[] writerTrueDensity;
//			BufferedWriter[] writerTrueSpeed;
//	
//			for (int numSimul=0; numSimul < numSimulMax; numSimul++) {
//				System.out.print("Début de la simulation "+Integer.toString(numSimul+1)+"/"+Integer.toString(numSimulMax));
//				
//				writerDensity = new BufferedWriter[numEstimations];
//				writerDensityNoData = new BufferedWriter[numEstimations];
//				writerSpeed = new BufferedWriter[numEstimations];
//				writerSpeedNoData = new BufferedWriter[numEstimations];
//				writerTrueDensity = new BufferedWriter[numEstimations];
//				writerTrueSpeed = new BufferedWriter[numEstimations];
//				
//				Estimation[] estimations = new Estimation[numEstimations];
//				trueSolution.newBoundaries(trueSolution.rhoLeft, trueSolution.rhoRight);
//			
//				new File("results/"+Integer.toString(numSimul+1)).mkdir();
//				 
//				for (int i = 0; i<numEstimations; i++) {
//					roadModels[i].newBoundaries(trueSolution.rhoLeft, trueSolution.rhoRight);
//					estimations[i] = new Estimation(roadModels[i], "EnKF", false);
//
//					String S = "results/"+Integer.toString(numSimul+1)+"/"+roadModels[i].nameModel+"_";
//					writerDensity[i] = new BufferedWriter(new FileWriter(new File(S+"density.csv")));
//					writerDensityNoData[i] = new BufferedWriter(new FileWriter(new File(S+"densityNoData.csv")));
//					writerSpeed[i] = new BufferedWriter(new FileWriter(new File(S+"speed.csv")));
//					writerSpeedNoData[i] = new BufferedWriter(new FileWriter(new File(S+"speedNoData.csv")));		
//					writerTrueDensity[i] = new BufferedWriter(new FileWriter(new File(S+"trueDensity.csv")));
//					writerTrueSpeed[i] = new BufferedWriter(new FileWriter(new File(S+"trueSpeed.csv")));	
//				}
//	
//				for (int k=0; k<limite; k++) {
//					trueSolution.update();
//					
//					for (int i=0; i<numEstimations; i++) {
//						Estimation estim = estimations[i];
//						estim.nextStepWithoutUpdateTrue();
//	
//						DoubleMatrix dm = estim.getDensityMean();
//						DoubleMatrix dmNoData = estim.getDensityMeanWithNoData();
//						
//						DoubleMatrix sm = estim.getSpeedMean();
//						DoubleMatrix smNoData = estim.getSpeedMeanWithNoData();
//						
//						for (int j=0; j<cells ; j++) {
//							double pos = ((double)j)*spaceStep;
//							writerDensity[i].write(roundedString(dm.get(j))+",");
//							writerTrueDensity[i].write(roundedString(trueSolution.getDensityValueFromPos(pos))+",");
//							writerSpeed[i].write(roundedString(sm.get(j))+",");
//							writerTrueSpeed[i].write(roundedString(trueSolution.getSpeedValueFromPos(pos))+",");
//							writerDensityNoData[i].write(roundedString(dmNoData.get(j))+",");
//							writerSpeedNoData[i].write(roundedString(smNoData.get(j))+",");
//						}
//						writerDensity[i].write("\n");
//						writerTrueDensity[i].write("\n");
//						writerSpeed[i].write("\n");
//						writerTrueSpeed[i].write("\n");
//						writerDensityNoData[i].write("\n");
//						writerSpeedNoData[i].write("\n");
//					}
//				}
//				
//				for (int i = 0; i<numEstimations; i++) {
//					writerDensity[i].flush(); writerDensity[i].close();
//					writerDensityNoData[i].flush(); writerDensityNoData[i].close();
//					writerSpeed[i].flush(); writerSpeed[i].close(); 
//					writerSpeedNoData[i].flush(); writerSpeedNoData[i].close(); 
//					writerTrueDensity[i].flush(); writerTrueDensity[i].close();
//					writerTrueSpeed[i].flush(); writerTrueSpeed[i].close();
//				}
//				
//				System.out.println(" Fin");			
//			}
//		}
//		catch (Exception e) {e.printStackTrace();}
//		
//	}
	
//	private static String roundedString(double d){
//		return Double.toString(Math.floor(d*100000000)/100000000);
//	}
	
}
