package mascot.dynamics;


import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.core.Loggable;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealVectorParam;

import java.io.PrintStream;


@Description("Constant migration rates and effective population sizes over time.")
public class Constant extends Dynamics implements Loggable  {

    public Input<RealVectorParam<? extends Real>> NeInput = new Input<>("Ne", "input of effective population sizes", Validate.REQUIRED);
    public Input<RealVectorParam<? extends Real>> b_mInput = new Input<>("backwardsMigration", "input of backwards in time migration rates");
    public Input<RealVectorParam<? extends Real>> f_mInput = new Input<>("forwardsMigration", "input of backwards in time migration rates", Validate.XOR, b_mInput);    
    public Input<Double> ploidyInput = new Input<>("ploidy", "Ploidy (copy number) for this gene, typically a whole number or half (default is 1).", 1.0);

	private boolean isBackwardsMigration;
	
	private enum MigrationType {
	    symmetric, asymmetric 
	}
	
	MigrationType migrationType;

    

    @Override
    public void initAndValidate() {
    	super.initAndValidate();
    	
   	
    	if (dimensionInput.get()<getNrTypes())
    		dimensionInput.set(getNrTypes());
    	
    	
    	if (b_mInput.get()!=null)
    		isBackwardsMigration = true;
    	else
    		isBackwardsMigration = false;
    	
    	
    	if (dimensionInput.get()!=NeInput.get().size() || fromBeautiInput.get()){
    		System.err.println("the dimension of " + NeInput.get().getID() + " is set to " + dimensionInput.get());
    		NeInput.get().setDimension(dimensionInput.get());
    	}

    	int migDim = NeInput.get().size()*(NeInput.get().size()-1);
    	
    	if (isBackwardsMigration){
    		if (fromBeautiInput.get()){
    			migrationType = MigrationType.asymmetric;
        		System.err.println("the dimension of " + b_mInput.get().getID() + " is set to " + migDim);
    			b_mInput.get().setDimension(migDim);       		   			
    		}else{
	    		if (migDim == b_mInput.get().size()){
	    			migrationType = MigrationType.asymmetric;
	    		}else if ((int) migDim/2 == b_mInput.get().size()){
	    			migrationType = MigrationType.symmetric;
	    		}else{
	    			migrationType = MigrationType.asymmetric;
	    			System.err.println("Wrong number of migration elements, assume asymmetric migration:");
	        		System.err.println("the dimension of " + b_mInput.get().getID() + " is set to " + migDim);
	    			b_mInput.get().setDimension(migDim);       		
	    		}
    		}
    	}
    	if (!isBackwardsMigration){
    		if (fromBeautiInput.get()){
    			migrationType = MigrationType.asymmetric;
        		System.err.println("the dimension of " + f_mInput.get().getID() + " is set to " + migDim);
        		f_mInput.get().setDimension(migDim);       					
    		}else{
    			if (migDim == f_mInput.get().size()){
    				migrationType = MigrationType.asymmetric;
	    		}else if ((int) migDim/2 == f_mInput.get().size()){
	    			migrationType = MigrationType.symmetric;
	    		}else{
	    			migrationType = MigrationType.asymmetric;
	    			System.err.println("Wrong number of migration elements, assume asymmetric migration:");
	        		System.err.println("the dimension of " + f_mInput.get().getID() + " is set to " + migDim);
	        		f_mInput.get().setDimension(migDim);       		
	    		}
    		}
    	}
   	
    }

    /**
     * Returns the time to the next interval.
     */
    @Override
    public double getInterval(int i) {
    	return Double.POSITIVE_INFINITY;
    }
    
    @Override
	public double [] getIntervals() {
		return new double[]{Double.POSITIVE_INFINITY};
	}

    
    public boolean intervalIsDirty(int i){
    	boolean intervalIsDirty = false;  	    	
    	
    	for (int j = 0; j < dimensionInput.get(); j++)
    		if (NeInput.get().isDirty(j))
    			intervalIsDirty = true;  
    
    	if (isBackwardsMigration){
			for (int j = 0; j < b_mInput.get().size(); j++)
				if (b_mInput.get().isDirty(j))
					intervalIsDirty = true; 
    	}else{
			for (int j = 0; j < f_mInput.get().size(); j++)
				if (f_mInput.get().isDirty(j))
					intervalIsDirty = true;    		
    	}		
    	return intervalIsDirty;

    }   
 
	@Override
    public double[] getCoalescentRate(int i){
    	double[] coal = new double[NeInput.get().size()];
    	int c = 0;
    	for (int j = 0; j < NeInput.get().size(); j++){
    		coal[c] = 1/(ploidyInput.get()*NeInput.get().get(j));
    		c++;
    	}
    	return coal;

    }
    
	@Override    
    public double[] getBackwardsMigration(int i){
		int n = NeInput.get().size();
    	double[] m = new double[n * n];
    	
    	if (isBackwardsMigration){
    		if (migrationType == MigrationType.asymmetric){
		    	int c = 0;
		    	for (int a = 0; a < NeInput.get().size(); a++){
		    		for (int b = 0; b < NeInput.get().size(); b++){
		    			if (a!=b){
		    				m[a * n + b] = b_mInput.get().get(c);
		    				c++;
		    			}
		    		}
		    	}
    		}else{
		    	int c = 0;
		    	for (int a = 0; a < NeInput.get().size(); a++){
		    		for (int b = a+1; b < NeInput.get().size(); b++){
		    			if (a!=b){
		    				m[a * n + b] = b_mInput.get().get(c);
		    				m[b * n + a] = b_mInput.get().get(c);
		    				c++;
		    			}
		    		}
		    	}    			
    		}
    	}else{
    		if (migrationType == MigrationType.asymmetric){
		    	int c = 0;
		    	for (int a = 0; a < NeInput.get().size(); a++){
		    		for (int b = 0; b < NeInput.get().size(); b++){
		    			if (a!=b){
		    				m[a * n + b] = f_mInput.get().get(c)
		    						*NeInput.get().get(b)
		    							/NeInput.get().get(a);
		    				c++;
		    			}
		    		}
		    	} 
    		}else{
		    	int c = 0;
		    	for (int a = 0; a < NeInput.get().size(); a++){
		    		for (int b = a+1; b < NeInput.get().size(); b++){
		    			if (a!=b){
		    				m[a * n + b] = f_mInput.get().get(c)
		    						*NeInput.get().get(b)
		    							/NeInput.get().get(a);
		    				m[b * n + a] = f_mInput.get().get(c)
		    						*NeInput.get().get(a)
		    							/NeInput.get().get(b);
		    				c++;
		    			}
		    		}
		    	}    			
    		}
    	}
    	return m;  	
    }    
	
	@Override
	public void recalculate() {
	}
	
	@Override
	public void init(PrintStream out) {
		for (int i = 0 ; i < NeInput.get().size(); i++){
			out.print(String.format("%s.%s\t", NeInput.get().getID(), getStringStateValue(i)));
		}
		if (migrationType == MigrationType.asymmetric){
	    	int c = 0;
	    	for (int a = 0; a < NeInput.get().size(); a++){
	    		for (int b = 0; b < NeInput.get().size(); b++){
	    			if (a!=b){
	    				if (isBackwardsMigration)
	    					out.print(String.format("b_%s.%s_to_%s\t", b_mInput.get().getID(), getStringStateValue(a), getStringStateValue(b)));
	    				else
	    					out.print(String.format("f_%s.%s_to_%s\t", f_mInput.get().getID(), getStringStateValue(b), getStringStateValue(a)));	    					
	    				c++;
	    			}
	    		}
	    	} 
		}
		else{
			int c = 0;
	    	for (int a = 0; a < NeInput.get().size(); a++){
	    		for (int b = a+1; b < NeInput.get().size(); b++){
	    			if (a!=b){
	    				if (isBackwardsMigration)
	    					out.print(String.format("b_%s.%s_and_%s\t", b_mInput.get().getID(), getStringStateValue(a), getStringStateValue(b)));
	    				else
	    					out.print(String.format("f_%s.%s_and_%s\t", f_mInput.get().getID(), getStringStateValue(b), getStringStateValue(a)));	    					
	    				c++;
	    			}
	    		}
	    	} 
		}

	}

	@Override
	public void log(long sample, PrintStream out) {
		for (int i = 0 ; i < NeInput.get().size(); i++){
			out.print(NeInput.get().get(i) + "\t");
		}
		
		if (migrationType == MigrationType.asymmetric){
	    	int c = 0;
	    	for (int a = 0; a < NeInput.get().size(); a++){
	    		for (int b = 0; b < NeInput.get().size(); b++){
	    			if (a!=b){
	    				if (isBackwardsMigration)
	    					out.print(b_mInput.get().get(c) + "\t");
	    				else
	    					out.print(f_mInput.get().get(c) + "\t");
	    				c++;
	    			}
	    		}
	    	} 
		}
		else{
			int c = 0;
	    	for (int a = 0; a < NeInput.get().size(); a++){
	    		for (int b = a+1; b < NeInput.get().size(); b++){
	    			if (a!=b){
	    				if (isBackwardsMigration)
	    					out.print(b_mInput.get().get(c) + "\t");
	    				else
	    					out.print(f_mInput.get().get(c) + "\t");
	    				c++;
	    			}
	    		}
	    	} 
		}

		
	}

	@Override
	public void close(PrintStream out) {
		// TODO Auto-generated method stub
		
	}


//    @Override
//	protected boolean requiresRecalculation(){
//    	return intervalIsDirty(0);
//    }

    @Override
	public int getEpochCount() {return 1;}


    
}