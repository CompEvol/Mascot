package mascot.util;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.CalculationNode;
import beast.base.spec.domain.Real;
import beast.base.spec.type.RealScalar;
import beast.base.spec.type.RealVector;


@Description("calculates the mean of the entries of a real vector")
public class Mean extends CalculationNode implements RealScalar<Real> {
    final public Input<RealVector<? extends Real>> functionInput = new Input<>("arg", "argument for which the mean is calculated", Validate.REQUIRED);

    boolean needsRecompute = true;
    double mean;
    double storedMean;

    @Override
    public void initAndValidate() {
    }

    @Override
    public Real getDomain() {
        return Real.INSTANCE;
    }

    @Override
    public double get() {
        if (needsRecompute) {
            compute();
        }
        return mean;
    }

    void compute() {
    	mean = 0.0;
        for (int i = 0; i < functionInput.get().size(); i++) {
        	mean += functionInput.get().get(i);
        }
        mean /= functionInput.get().size();
        needsRecompute = false;
    }

    @Override
    public void store() {
    	storedMean = mean;
        super.store();
    }

    @Override
    public void restore() {
    	mean = storedMean;
        super.restore();
    }

    @Override
    public boolean requiresRecalculation() {
        needsRecompute = true;
        return true;
    }
}
