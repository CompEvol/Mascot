package mascot.util;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.CalculationNode;
import beast.base.spec.domain.Real;
import beast.base.spec.type.RealScalar;
import beast.base.spec.type.RealVector;


@Description("returns the last entry of a real vector")
public class Final extends CalculationNode implements RealScalar<Real> {
    final public Input<RealVector<? extends Real>> functionInput = new Input<>("arg", "argument for which the last entry is returned", Validate.REQUIRED);

    @Override
    public void initAndValidate() {
    }

    @Override
    public Real getDomain() {
        return Real.INSTANCE;
    }

    @Override
    public double get() {
    	return functionInput.get().get(functionInput.get().size() - 1);
    }
}
