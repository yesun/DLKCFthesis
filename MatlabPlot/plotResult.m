classdef plotResult

    
    properties


       rhoEst 

       rhoTrue 


       isSave
       plotWidth
       plotHeight

       cMin=0;
       cMax=1.00;
       rhoMeasLoc

       measInterval=1; 
    end

    
    methods

        function plotRhoEst(sim)
            imagesc(sim.rhoEst)
            xlabel('space','FontSize',15)
            ylabel('time','FontSize',15)
            title('Density estimation','FontSize',15)
            set(gca, 'YDir', 'normal');
            colorbar
            caxis([sim.cMin, sim.cMax])


        end

        function plotRhoTrue(sim)
            imagesc(sim.rhoTrue)
            xlabel('space','FontSize',15)
            ylabel('time','FontSize',15)
            title('True density','FontSize',15)
            set(gca, 'YDir', 'normal');
            colorbar
            caxis([sim.cMin, sim.cMax])
        end






        function plotAll(sim)
            subplot(2,3,4);
            sim.plotRhoTrue;
            subplot(2,3,6);
            sim.plotRhoEst;
        end


        
    end
    
end

