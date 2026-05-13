package mascot.parameterdynamics;

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.spec.domain.Real;
import beast.base.spec.type.RealScalar;

public class ConstantNe extends NeDynamics {

    public Input<RealScalar<Real>> NeInput = new Input<>(
    		"logNe", "input of the Ne at the time of the most recent sampled ancestor", Validate.REQUIRED);

    RealScalar<Real> Ne;

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
		return isDirtyInput(Ne);
	}
}
