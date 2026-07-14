package mascot.parameterdynamics;

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.StateNode;
import beast.base.inference.StateNodeInitialiser;
import beast.base.spec.inference.parameter.BoolVectorParam;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealScalarParam;
import beast.base.spec.inference.parameter.RealVectorParam;
import mascot.glmmodel.CovariateList;

import java.util.List;

public class LogLinearGLM extends NeDynamics implements StateNodeInitialiser {
    public Input<CovariateList> covariateListInput = new Input<>("covariateList", "input of covariates", Validate.REQUIRED);
    public Input<RealVectorParam<? extends Real>> scalerInput = new Input<>("scaler", "input of covariates scaler", Validate.REQUIRED);
    public Input<BoolVectorParam> indicatorInput = new Input<>("indicator", "input of covariates scaler", Validate.REQUIRED);
    public Input<RealScalarParam<Real>> clockInput = new Input<>("clock", "clock rate of the parameter",Validate.REQUIRED);
    public Input<RealVectorParam<? extends Real>> errorInput = new Input<>("error", "time variant error term in the GLM model for the rates");
    public Input<RealVectorParam<? extends Real>> constantErrorInput = new Input<>("constantError", "time invariant error term in the GLM model for the rates");

    final public Input<RealVectorParam<? extends Real>> rateShiftsInput = new Input<>("rateShifts","When to switch between elements of Ne", Input.Validate.REQUIRED);

    RealVectorParam<? extends Real> rateShifts;

    boolean valuesKnown = false;
    double[] rates;


	@Override
	public void initAndValidate() {
		// set the dimension of the scalers, indicators and potentially the error term
    	scalerInput.get().setDimension(covariateListInput.get().size());
    	indicatorInput.get().setDimension(covariateListInput.get().size());

    	if (errorInput.get()!=null)
    		errorInput.get().setDimension(covariateListInput.get().get(0).getDimension());

		isTime = true;
    	rateShifts = rateShiftsInput.get();
    	rates = new double[covariateListInput.get().get(0).getDimension()];
	}


	@Override
	public double getNeTime(double t) {
//		if (!valuesKnown)
			recalculate();

		int intervalnr = getIntervalNr(t);
		if (intervalnr>=rateShifts.size()) {
			return rates[rateShifts.size()-1];
		}
		return rates[intervalnr];
	}


	private int getIntervalNr(double t) {
		// check which interval t + offset is in
		for (int i = 0; i < rateShifts.size(); i++)
			if (t<rateShifts.get(i))
				return i;

		// after the last interval, just keep using the last element
		return rateShifts.size();
	}



	@Override
	public void recalculate() {
		for (int i = 0; i < rates.length; i++) {
			double logrates = 0;

			for (int j = 0; j < covariateListInput.get().size(); j++){
				if (indicatorInput.get().get(j)){
					logrates += scalerInput.get().get(j)
							*covariateListInput.get().get(j).getArrayValue(i);
				}
			}

	    	if (errorInput.get()!=null)
	   			logrates += errorInput.get().get(i);

	    	if (constantErrorInput.get()!=null)
	   			logrates += constantErrorInput.get().get(0);

	    	rates[i] = clockInput.get().get()*Math.exp(logrates);
		}

		valuesKnown = true;
	}


	@Override
	public boolean isDirty() {
		for (int i = 0; i < scalerInput.get().size(); i++)
			if(scalerInput.get().isDirty(i)){
				valuesKnown = false;
				return true;
			}

		for (int i = 0; i < indicatorInput.get().size(); i++)
			if(indicatorInput.get().isDirty(i)){
				valuesKnown = false;
				return true;
			}

		if (errorInput.get() != null)
			for (int i = 0; i < errorInput.get().size(); i++)
				if(errorInput.get().isDirty(i)){
					valuesKnown = false;
					return true;
				}

		if (constantErrorInput.get() != null)
			for (int i = 0; i < constantErrorInput.get().size(); i++)
				if(constantErrorInput.get().isDirty(i)) {
					valuesKnown = false;
					return true;
				}


		if (clockInput.get().somethingIsDirty()) {
			valuesKnown = false;
			return true;
		}

		return false;
	}

	@Override
	public void restore() {
		valuesKnown = false;
	}

	@Override
	public void initStateNodes() {
	}

	@Override
	public void getInitialisedStateNodes(List<StateNode> stateNodes) {
		stateNodes.add(scalerInput.get());
		stateNodes.add(indicatorInput.get());
		if (errorInput.get() != null) stateNodes.add(errorInput.get());
	}

}
