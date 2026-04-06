package mascot.util;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.CalculationNode;
import beast.base.spec.domain.Real;
import beast.base.spec.type.RealScalar;
import beast.base.spec.type.RealVector;


@Description("returns the first entry of a real vector")
public class First extends CalculationNode implements RealScalar<Real> {
    final public Input<RealVector<? extends Real>> functionInput = new Input<>("arg", "argument for which the first entry is returned", Validate.REQUIRED);

    @Override
    public void initAndValidate() {
    }

    @Override
    public Real getDomain() {
        return Real.INSTANCE;
    }

    @Override
    public double get() {
    	return functionInput.get().get(0);
    }
}
