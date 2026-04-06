package mascot.parameterdynamics;

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealScalarParam;

public class ConstantNe extends NeDynamics {

    public Input<RealScalarParam<Real>> NeInput = new Input<>(
    		"logNe", "input of the Ne at the time of the most recent sampled ancestor", Validate.REQUIRED);

    RealScalarParam<Real> Ne;

	@Override
	public void initAndValidate() {
		// should be called by a time
		isTime = true;

		Ne = NeInput.get();
	}

	@Override
	public void recalculate() {
	}

	@Override
	public double getNeTime(double t) {
		return Math.exp(Ne.get());
	}

	@Override
	public boolean isDirty() {
		if (Ne.somethingIsDirty())
			return true;

		return false;
	}
}
