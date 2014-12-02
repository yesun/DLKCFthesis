package filters;

import filters.KCF;
import section.Section;








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
	Filter filter;
	
	public Estimation(RoadModel _roadModel, String _nameFilter) {
		roadModel = _roadModel;		
		filter = Filter.createFilter(this);	
	}
	
	public void nextStepWithoutUpdateTrue() {
		filter.nextStep();
	}
	public void nextStepNoDataWithoutUpdateTrue() {
		filter.nextStepNoData();
	}
		
	public void updateTrueSolution() {
		roadModel.updateTrueSolution();
	}

	public DoubleMatrix propagate(DoubleMatrix _density) {		
      return roadModel.propagate(_density);
	}
	
	public DoubleMatrix getDensityMean() {
		return roadModel.getDensityMean(filter.mean);
	}
	
	public void setSection(Section _section){		
			roadModel.setSetion(_section);
			roadModel.updateModelVar();
	}
	
	public static void exportResult(TrueSolution[] trueSolution, int limite, String folder) {
		try {		
			int numSections = trueSolution[0].numSections;
			BufferedWriter[] writerSection = new BufferedWriter[numSections]; //write estimates of each section
			BufferedWriter writerTrue;//write true solution of the entire network
			BufferedWriter writerAvr;//write averaged estimates of the entire network
			BufferedWriter writerLyap;//write the common Lyapunov function
			BufferedWriter[] writerError= new BufferedWriter[numSections];//write estimation errors of each section
			BufferedWriter[] Mode= new BufferedWriter[numSections];//write modes of each section
			BufferedWriter writerDisagreement;//write disagreements among neighbors
			

			System.out.print("Beginning of simulation ");
				
			    new File("results/"+folder).mkdir();				
				RoadModel[] roadModels=new RoadModel[numSections];
				
				for (int i=0;i<numSections;i++){					
					trueSolution[i].section=trueSolution[i].setSections();
				}
				for (int i=0;i<numSections;i++){					
					roadModels[i]=trueSolution[i].setRoadModels();
				}
				writerTrue = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"trueState.csv")));
				writerAvr = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"writerAvr.csv")));
				writerLyap = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Lyapfun.csv")));
				writerDisagreement = new BufferedWriter(new FileWriter(new File("results/"+folder+"/"+"Disagreement.csv")));
				
				
				
			    Estimation[] estimations = new Estimation[numSections];
				double[] deltaV=new double[numSections];
				
				for (int i = 0; i<numSections; i++) {
					estimations[i] = new Estimation(roadModels[i], "KCF");
					String S = "results/"+folder+"/"+roadModels[i].nameModel;
					writerSection[i] = new BufferedWriter(new FileWriter(new File(S+".csv")));
					writerError[i]=new BufferedWriter(new FileWriter(new File(S+"error.csv")));
					Mode[i]=new BufferedWriter(new FileWriter(new File(S+"Mode.csv")));
				}            
				

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
					
					
					DoubleMatrix[] mean = new DoubleMatrix[numSections];					
					for (int i=0; i<numSections; i++) {
						mean [i] = estimations[i].getDensityMean();
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
						

    					for (int j=0; j<mean[i].length ; j++) {
							writerError[i].write(mean[i].get(j)-trueSolution[i].trueStates.get(j)+",");	
						}
    					double sum=0;
    					for (int j=0; j<mean[i].length ; j++) {
							sum=sum+mean[i].get(j)-trueSolution[i].trueStates.get(j);							
						}
    					writerError[i].write(sum+",");

						writerError[i].write("\n");
						writerSection[i].write("\n");
					}
					writerAvr.write("\n");
					
										
					Section[] secs=new Section[numSections];
					for(int i=0;i<numSections;i++){
						secs[i]=trueSolution[i].setSections();//every step, update section (i.e. update mode)
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
							secs[i].getModelA();
				 		    secs[i].getModelB1();
						    secs[i].getModelB2();							
						}						
					}

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

						roadModels[i].updateMeasurement();
						estimations[i].filter.getNewParametersFromModel();
					}
					
					
					DoubleMatrix[] error1=new DoubleMatrix[numSections];
					for (int i=0; i<numSections; i++) {
						error1[i]=mean[i].sub(trueSolution[i].trueStates);				
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
					
					for(int i=0; i<numSections; i++){
						trueSolution[i].refinetrueSolution();
					}
					
					
					for(int i=0; i<numSections; i++){
						trueSolution[i].updatemeasurement();
					}
					

					
					
					
					for (int i=0; i<numSections; i++) {
//						estimations[i].nextStepNoDataWithoutUpdateTrue();
						estimations[i].nextStepWithoutUpdateTrue();
					}

					if(numSections==1){
                    //no consensus
					}
					
					else{					
					DoubleMatrix[] G=new DoubleMatrix[numSections];
					for (int i=0; i<numSections; i++) {
						G[i]=estimations[i].roadModel.section.ModelA.mmul(estimations[i].filter.priorVar).mmul(estimations[i].roadModel.section.ModelA.transpose()).add(estimations[i].roadModel.modelVar).add(estimations[i].filter.f_var.mmul(S[i]).mmul(estimations[i].filter.f_var));
					}
					
					DoubleMatrix[] Lambda=new DoubleMatrix[numSections];
					for (int i=0; i<numSections; i++) {
						Lambda[i]=InverseMatrix.invPoSym(estimations[i].filter.priorVar).sub(estimations[i].roadModel.section.ModelA.transpose().mmul(InverseMatrix.invPoSym(G[i])).mmul(estimations[i].roadModel.section.ModelA));
					}
					
					DoubleMatrix BigG=G[0];
					for (int i=1;i<numSections;i++){
						BigG=Concat.Diagonal(BigG, G[i]);
					}
					
					DoubleMatrix BigLambda=Lambda[0];
					for (int i=1;i<numSections;i++){
						BigLambda=Concat.Diagonal(BigLambda, Lambda[i]);
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
							_BigLambda[i][j]=BigLambda.get(i, j);
						}
					}
					for (int i=0;i<Lower.rows;i++){
						for (int j=0;j<Lower.columns;j++){
							_Lower[i][j]=Lower.get(i, j);
						}
					}
					Matrix bigLambda=new Matrix(_BigLambda);
					Matrix lower=new Matrix(_Lower);
					EigenvalueDecomposition bigLambdaEig=new EigenvalueDecomposition(bigLambda);
					EigenvalueDecomposition lowerEig=new EigenvalueDecomposition(lower);
					Matrix _bigLambda=bigLambdaEig.getD();
					Matrix _lower=lowerEig.getD();
					
					double temp1=_bigLambda.get(0, 0);
					for(int i=1;i<_bigLambda.getColumnDimension();i++){
						if(_bigLambda.get(i, i)<temp1){
							temp1=_bigLambda.get(i, i);
						}						
					}
					
					double temp2=_lower.get(0, 0);
					for(int i=1;i<_lower.getColumnDimension();i++){
						if(_lower.get(i, i)>temp2){
							temp2=_lower.get(i, i);
						}					
					}
					double _gamma=Math.sqrt(temp1/temp2);
					
					
				    System.out.print(_gamma+"\n");
					
					for (int i=0; i<numSections; i++) {
						if(estimations[i].roadModel.section.getClass().getSimpleName().equals("FC")){
							estimations[i].filter.gamma=0;
						}
						else{
						estimations[i].filter.gamma=0.9*_gamma;
//			            estimations[i].filter.gamma=0;
						}
						
					}

					
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
				}
				
				for (int i = 0; i<numSections; i++) {
					writerSection[i].flush(); writerSection[i].close();
					writerError[i].flush(); writerError[i].close();
					Mode[i].flush(); Mode[i].close();
				}

				writerAvr.flush(); writerAvr.close();
				writerTrue.flush(); writerTrue.close();
				writerLyap.flush(); writerLyap.close();
				writerDisagreement.flush(); writerDisagreement.close();
				
				System.out.println(" End");	
				
		
		}
		catch (Exception e) {e.printStackTrace();}		
	}
	
}
