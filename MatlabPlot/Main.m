%This code runs the VCTM examples
clear all 
close all
folderNum='Result_Plot/';
%load([folderNum,'D_S_37.csv']);
load([folderNum,'writerAvr.csv']);
%load([folderNum,'D_S_37_NoData.csv']);
%load([folderNum,'D_S_37.csv']);
%load([folderNum,'DSlinear_S_37.csv']);
%load([folderNum,'DSlinear_S_37_NoData.csv'])
%load([folderNum,'aDSlinear_S_37.csv']);
%load([folderNum,'aDSlinear_S_37_NoData.csv'])
load([folderNum,'TrueState.csv'])

[m,n]=size(writerAvr);
[p,q]=size(TrueState);
Range=1:n;



DLKCF=plotResult;


DLKCF.rhoEst=writerAvr(:,Range);

DLKCF.rhoTrue=TrueState(:,1:q);

figure(3)
DLKCF.plotAll;
%figure(4)
%kcf.plotError;





%generate some plots
close all
DLKCF.plotHeight=2;
DLKCF.plotWidth=3;
DLKCF.isSave=1;
DLKCF.plotRhoEst;

%kcf.plotRhoTrue;


