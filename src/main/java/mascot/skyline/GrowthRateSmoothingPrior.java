package mascot.skyline;

import beast.base.core.Citation;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.Distribution;
import beast.base.inference.State;
import beast.base.inference.distribution.ParametricDistribution;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealVectorParam;
import mascot.dynamics.RateShifts;

import java.util.List;
import java.util.Random;

@Citation(	"Nicola F. Müller, Remco R. Bouckaert, Chieh-Hsi Wu, Trevor Bedford (2025)\n"+
			"  MASCOT-Skyline integrates population and migration dynamics\n"+
			"  to enhance phylogeographic reconstructions\n"+
			"  PLOS Computational Biology 21(9):e1013421,\n"+
			"  https://doi.org/10.1371/journal.pcbi.1013421")
public class GrowthRateSmoothingPrior extends Distribution {
	
    public Input<RealVectorParam<? extends Real>> NeLogInput = new Input<>(
    		"NeLog", "input of effective population sizes");        
    
    final public Input<ParametricDistribution> distInput = new Input<>("distr", 
    		"distribution used to calculate prior on the difference between intervals, e.g. normal, beta, gamma.", 
    		Validate.REQUIRED);
    
    final public Input<ParametricDistribution> initDistrInput = new Input<>("initialDistr", 
    		"distribution used to calculate prior on the difference between intervals, e.g. normal, beta, gamma.",
    		Input.Validate.OPTIONAL);
        
    final public Input<ParametricDistribution> finalDistrInput = new Input<>("finalDistr", 
    		"distribution used to calculate prior on the difference between intervals, e.g. normal, beta, gamma.", 
    		Input.Validate.OPTIONAL);
    
    final public Input<ParametricDistribution> meanDistrInput = new Input<>("meanDistr", 
    		"distribution used to calculate prior on the difference between intervals, e.g. normal, beta, gamma.", 
    		Input.Validate.OPTIONAL);
    
    public Input<RateShifts> rateShiftsInput = new Input<>(
    		"rateShifts", "timing of the rate shifts", Validate.REQUIRED);   


   
    
    private RealVectorParam<? extends Real> NeLog;
    
    protected ParametricDistribution dist;
    protected ParametricDistribution initDistr;
    protected ParametricDistribution finalDistr;
    protected ParametricDistribution meanDistr;
    
    RateShifts rateShifts;
    
    @Override
    public void initAndValidate() {
    	NeLog = NeLogInput.get();    	
        dist = distInput.get();
        rateShifts = rateShiftsInput.get();
        
        if (initDistrInput.get()!=null)
        	initDistr = initDistrInput.get();
        if (finalDistrInput.get()!=null)
        	finalDistr = finalDistrInput.get();
        if (meanDistrInput.get()!=null)
        	meanDistr = meanDistrInput.get();
        

    }


	@Override
	public List<String> getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getConditions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sample(State state, Random random) {
		// TODO Auto-generated method stub
		
	}
	
    public double calculateLogP() {
        logP = 0;
        
        double[] growthRates = new double[NeLog.size()-1];


        //loop over all time points
    	for (int j = 1; j < NeLog.size(); j++){
    		double timediff = rateShifts.getValue(j) - rateShifts.getValue(j-1);
    		double logdiff = NeLog.get(j) - NeLog.get(j-1); 
    		growthRates[j-1] = logdiff/timediff;    		
    	}
    	
    	
    	for (int j = 1; j < growthRates.length; j++){
    		double diff = growthRates[j]-growthRates[j-1];
        	logP += dist.logDensity(diff);
    	}
        
        // add contribution from first or last entry
        if (initDistrInput.get()!=null)
    		logP += initDistr.logDensity(growthRates[0]);
        if (finalDistrInput.get()!=null) {
    		logP += finalDistr.logDensity(growthRates[growthRates.length-1]);
        }
        
        if (meanDistrInput.get()!=null) {
        	double mean=0.0;
        	for (int j = 0; j < growthRates.length; j++){
        		mean += growthRates[j];    		
        	}
        	mean /= growthRates.length;
    		logP += meanDistr.logDensity(mean);
        }
        
        return logP;
    }
}
