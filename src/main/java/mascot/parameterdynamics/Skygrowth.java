package mascot.parameterdynamics;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.inference.StateNode;
import beast.base.inference.StateNodeInitialiser;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealVectorParam;
import mascot.dynamics.RateShifts;

import java.util.List;


/**
 * @author Nicola F. Mueller
 */
@Description("Populaiton function with values at certain time points that are interpolated in between. Parameter has to be in log space")
public class Skygrowth extends NeDynamics implements StateNodeInitialiser {
	
    final public Input<RealVectorParam<? extends Real>> NeInput = new Input<>("logNe",
            "Nes over time in log space", Input.Validate.REQUIRED);
    final public Input<RateShifts> rateShiftsInput = new Input<>("rateShifts",
            "When to switch between elements of Ne", Input.Validate.REQUIRED);

    //
    // Public stuff
    //
    RealVectorParam<? extends Real> Ne;
    RateShifts rateShifts;
    
    boolean NesKnown = false;
    double[] growth;
    double[] growth_stored;


    @Override
	public void initAndValidate() {
    	Ne = NeInput.get();    	    	
    	rateShifts = rateShiftsInput.get();
    	Ne.setDimension(rateShifts.getDimension()+1);
    	growth = new double[rateShifts.getDimension()];
    	recalculateNe();
		isTime = true;

    }


	@Override
	public double getNeTime(double t) {	
//		if (!NesKnown)
//			recalculateNe();

		int intervalnr = getIntervalNr(t);
		if (intervalnr>=rateShifts.getDimension()) {
			return Math.exp(Ne.get(Ne.size()-1));
		}
		double timediff = t;
		if (intervalnr>0)
			timediff -= rateShifts.getValue(intervalnr-1);
				
		return Math.exp(Ne.get(intervalnr)-growth[intervalnr]*timediff);
	}


	
	private int getIntervalNr(double t) {
		// check which interval t + offset is in
		for (int i = 0; i < rateShifts.getDimension(); i++)
			if (t<rateShifts.getValue(i))
				return i;
		
		// after the last interval, just keep using the last element
		return rateShifts.getDimension();					
	}

	
	// computes the Ne's at the break points
	private void recalculateNe() {
		growth = new double[rateShifts.getDimension()];
		double curr_time = 0.0;
		for (int i = 1; i < Ne.size(); i++) {
			growth[i-1] = (Ne.get(i-1)- Ne.get(i))/(rateShifts.getValue(i-1)-curr_time);
			curr_time = rateShifts.getValue(i-1);
		}
		NesKnown = true;
	}

	@Override
	public boolean requiresRecalculation() {
		recalculateNe();
		return super.requiresRecalculation();
	}
	
	@Override
	public void store() {
		growth_stored = new double[growth.length];
		System.arraycopy(growth, 0, growth_stored, 0, growth.length);
		super.store();
	}
	
	@Override
	public void restore() {
		System.arraycopy(growth_stored, 0, growth, 0, growth_stored.length);
		super.restore();
	}


	@Override
	public void recalculate() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isDirty() {
		if (Ne.isDirty(0))
			return true;

		return false;
	}

	// Dimension fixing is already done in initAndValidate; nothing further
	// needed here. The interface is implemented so the framework knows this
	// class owns the dimension of NeInput.
	@Override
	public void initStateNodes() {
	}

	@Override
	public void getInitialisedStateNodes(List<StateNode> stateNodes) {
		stateNodes.add(NeInput.get());
	}

}