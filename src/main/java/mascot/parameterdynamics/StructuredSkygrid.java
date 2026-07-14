package mascot.parameterdynamics;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.StateNode;
import beast.base.inference.StateNodeInitialiser;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealVectorParam;

import java.util.List;

@Description("Skygrid style dynamics for MASCOT. These assume constant effective population sizes within one state "+
				"and that these effective population sizes only change within one interval")
public class StructuredSkygrid extends NeDynamics implements StateNodeInitialiser {
	
    public Input<RealVectorParam<? extends Real>> NeLogInput = new Input<>(
    		"NeLog", "input of the log effective population sizes", Validate.REQUIRED);

    RealVectorParam<? extends Real> NeLog;
    
	@Override
	public void initAndValidate() {
		// should be called by an interval
		isTime = false;
		NeLog = NeLogInput.get();
	}
	
	@Override
	public void setNrIntervals(int intervals) {
		if (NeLogInput.get().size()!=intervals)
			NeLogInput.get().setDimension(intervals);		
	}

	@Override
	public void recalculate() {
	}
	
	
	public double getNeInterval(int i) {
		// get in which interval the current time falls		
		return Math.exp(NeLog.get(i));
	}

	@Override
	public boolean isDirty() {
		for (int i = 0; i < NeLog.size(); i++)
			if(NeLogInput.get().isDirty(i))
				return true;

		return false;
	}

	@Override
	public void initStateNodes() {
	}

	@Override
	public void getInitialisedStateNodes(List<StateNode> stateNodes) {
		stateNodes.add(NeLogInput.get());
	}

}
