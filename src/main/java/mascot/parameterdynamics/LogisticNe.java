package mascot.parameterdynamics;

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealScalarParam;

public class LogisticNe extends NeDynamics {

    public Input<RealScalarParam<Real>> carryingProportionInput = new Input<>(
    		"carryingProportion", "the proportion of the current Ne of the maximial Ne (capactity)", Validate.REQUIRED);
    public Input<RealScalarParam<Real>> capacityInput = new Input<>(
    		"capacity", "input of the maximal Ne", Validate.REQUIRED);
    public Input<RealScalarParam<Real>> growthRateInput = new Input<>(
    		"growthRate", "input of the growth rate", Validate.REQUIRED);

    public Input<Double> minNeInput = new Input<>(
    		"minNe", "input of the minimal Ne", 0.0);

    RealScalarParam<Real> cP;
    RealScalarParam<Real> capacity;
    RealScalarParam<Real> growthRate;

	@Override
	public void initAndValidate() {
		// should be called by a time
		isTime = true;

		cP = carryingProportionInput.get();

		capacity = capacityInput.get();
		growthRate = growthRateInput.get();
	}

	@Override
	public void recalculate() {
	}

	@Override
	public double getNeTime(double t) {
		return Math.max(minNeInput.get(),  Math.exp(capacity.get())/(1 + (1-cP.get())/cP.get() * Math.exp(t*growthRate.get())));
	}

	@Override
	public boolean isDirty() {
		if (cP.somethingIsDirty())
			return true;

		if (capacity.somethingIsDirty())
			return true;

		if (growthRate.somethingIsDirty())
			return true;

		return false;
	}

}
