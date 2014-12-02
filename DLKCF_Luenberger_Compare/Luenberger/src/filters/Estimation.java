package filters;

import section.Section;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import model.RoadModel;
import org.jblas.DoubleMatrix;
import trueSolution.TrueSolution;

public class Estimation {
	RoadModel roadModel;	
	Filter filter;

	public Estimation(RoadModel _roadModel, String _nameFilter) {
		roadModel = _roadModel;		
		filter = Filter.createFilter(this);	
	}

	
	public void nextStepWithoutUpdateTrue() {
		if (roadModel.section.getClass().getSimpleName().equals("FC")){
			filter.nextStepNoAnalysis();//no output feedback if in FC mode
		}
		else{
			filter.nextStep();
		}		
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
//			filter.measureVar=roadModel.modelVar;
	}

	public static void exportResult(TrueSolution[] trueSolution, int limite, String folder) {
		try {
		
			int numSections = trueSolution[0].numSections;
			
			BufferedWriter[] writerSection = new BufferedWriter[numSections];
			BufferedWriter writerTrue;
			BufferedWriter writerAvr;
			BufferedWriter[] writerError= new BufferedWriter[numSections];
			BufferedWriter[] Mode= new BufferedWriter[numSections];
			BufferedWriter[] trueSection= new BufferedWriter[numSections];
			BufferedWriter writerDisagreement;
			
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
							
				Estimation[] estimations = new Estimation[numSections];

				for (int i = 0; i<numSections; i++) {
					estimations[i] = new Estimation(roadModels[i], "KCF");
					String S = "results/"+folder+"/"+roadModels[i].nameModel;
					writerSection[i] = new BufferedWriter(new FileWriter(new File(S+".csv")));
					writerError[i]=new BufferedWriter(new FileWriter(new File(S+"error.csv")));
					Mode[i]=new BufferedWriter(new FileWriter(new File(S+"Mode.csv")));
					trueSection[i]=new BufferedWriter(new FileWriter(new File(S+"trueSection.csv")));
				}            
				
				int overlap=roadModels[0].trueSolution.overlapsec;				
				int cellsec=roadModels[0].section.cellsec;
	             
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
							
						for (int j=0; j<mean[i].length ; j++) {
							trueSection[i].write(trueSolution[i].trueStates.get(j,0)+",");
								
						}
						trueSection[i].write("\n");
						writerError[i].write("\n");
						writerSection[i].write("\n");
					}
					writerAvr.write("\n");
					
					Section[] secs=new Section[numSections];
					for(int i=0;i<numSections;i++){
						secs[i]=trueSolution[i].setSections();//every step, update mode
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
							else if (secs[i].wavefront==cellsec-1){
								secs[i].wavefront=cellsec-2;
							}					
							if(trueSolution[i].flux(secs[i].Estimates.get(secs[i].wavefront,0))-trueSolution[i].flux(secs[i].Estimates.get(secs[i].wavefront+1,0))>0){							
								secs[i].wavedirection=0;						
						    }
						    else{							
								secs[i].wavedirection=1;						        
							}							
							secs[i].getModelA();
				 		    secs[i].getModelB1();
						    secs[i].getModelB2();
						    secs[i].getModelB3();
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
						Mode[i].write(estimations[i].roadModel.section.wavefront+","); 
						Mode[i].write(estimations[i].roadModel.section._wavefront+",");						
						Mode[i].write(estimations[i].roadModel.section.wavedirection+",");
						Mode[i].write(estimations[i].roadModel.section._wavedirection+",");
						if (estimations[i].roadModel.section.getClass().getSimpleName().equals("FC")){
							if (estimations[i].roadModel.section.wavefront==cellsec-1&&estimations[i].roadModel.section.wavedirection==1){
								Mode[i].write(1+",");
							}
							else if (estimations[i].roadModel.section.wavefront==0&&estimations[i].roadModel.section.wavedirection==0){
								Mode[i].write(2+",");
							}
							else{
								Mode[i].write(3+",");
							}
							if (estimations[i].roadModel.section._wavefront==cellsec-1&&estimations[i].roadModel.section._wavedirection==1){
								Mode[i].write(1+",");
							}
							else if (estimations[i].roadModel.section._wavefront==0&&estimations[i].roadModel.section._wavedirection==0){
								Mode[i].write(2+",");
							}
							else{
								Mode[i].write(3+",");
							}
						}
						else{
							Mode[i].write(0+",");
							Mode[i].write(0+",");
						}				
						Mode[i].write("\n");
					}
					
					for (int i=0; i<numSections; i++) {
						estimations[i].filter.getNewParametersFromModel();
					}			
				
					for(int i=0; i<numSections; i++){
						trueSolution[i].currenttrueStates=trueSolution[i].trueStates;
					}
					
					//**Start setting boundary condition
					double inflow=0.1125+0.1125*Math.sin(k*(Math.PI/4000)+(Math.PI));
					if (inflow>trueSolution[0].receiving(trueSolution[0].currenttrueStates.get(0,0))){
					    inflow=trueSolution[0].receiving(trueSolution[0].currenttrueStates.get(0,0));
					}
					trueSolution[0].boundaryFlux.put(0,0,inflow);
					double outflow=trueSolution[numSections-1].receiving(trueSolution[numSections-1].currenttrueStates.get(cellsec-1,0));
					trueSolution[numSections-1].boundaryFlux.put(1,0,outflow);
//					double outflow=0.1125+0.1125*Math.sin(k*(Math.PI/1000));
//					if (outflow>trueSolution[0].sending(trueSolution[0].trueStatesPrior.get(cellsec-1,0))){
//						outflow=trueSolution[0].sending(trueSolution[0].trueStatesPrior.get(cellsec-1,0));
//					}
					//**End setting boundary condition					

					for(int i=0; i<numSections; i++){
						trueSolution[i].update();
					}
					
					for(int i=0; i<numSections; i++){
						trueSolution[i].updatemeasurement();
					}
										
					for (int i=0; i<numSections; i++) {
						estimations[i].nextStepWithoutUpdateTrue();
					}					
				}
				
				for (int i = 0; i<numSections; i++) {
					writerSection[i].flush(); writerSection[i].close();
					writerError[i].flush(); writerError[i].close();
					Mode[i].flush(); Mode[i].close();
					trueSection[i].flush(); trueSection[i].close();

				}

				writerAvr.flush(); writerAvr.close();
				writerTrue.flush(); writerTrue.close();
				
			System.out.println(" End");	
				
		
		}
		catch (Exception e) {e.printStackTrace();}
		
	}
	
}
