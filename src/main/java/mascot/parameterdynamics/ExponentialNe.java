package mascot.parameterdynamics;

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealScalarParam;

public class ExponentialNe extends NeDynamics {

    public Input<RealScalarParam<Real>> logNeNullInput = new Input<>(
    		"NeNull", "input of the Ne at the time of the most recent sampled ancestor", Validate.REQUIRED);
    public Input<RealScalarParam<Real>> growthRateInput = new Input<>(
    		"growthRate", "input of the growth rate", Validate.REQUIRED);

    public Input<Double> minNeInput = new Input<>(
    		"minNe", "input of the minimal Ne", 0.0);

    RealScalarParam<Real> logNeNull;
    RealScalarParam<Real> growthRate;

	@Override
	public void initAndValidate() {
		// should be called by a time
		isTime = true;

		logNeNull = logNeNullInput.get();
		growthRate = growthRateInput.get();
	}

	@Override
	public void recalculate() {
	}

	@Override
	public double getNeTime(double t) {

		return Math.max(minNeInput.get(), Math.exp(logNeNull.get()-t*growthRate.get()));
	}

	@Override
	public boolean isDirty() {
		if (logNeNull.somethingIsDirty())
			return true;

		if (growthRate.somethingIsDirty())
			return true;

		return false;
	}


}
