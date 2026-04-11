package mascot.util;

import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.CalculationNode;
import beast.base.spec.domain.Real;
import beast.base.spec.type.RealVector;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Description("calculates the differences between consecutive entries of a real vector")
public class Difference extends CalculationNode implements RealVector<Real> {
    final public Input<RealVector<? extends Real>> functionInput = new Input<>("arg", "argument for which the differences for entries is calculated", Validate.REQUIRED);

    boolean needsRecompute = true;
    double[] difference;
    double[] storedDifference;

    @Override
    public void initAndValidate() {
    	difference = new double[functionInput.get().size()];
    	storedDifference = new double[functionInput.get().size()];
    }

    @Override
    public Real getDomain() {
        return Real.INSTANCE;
    }

    @Override
    public int size() {
        return difference.length;
    }

    @Override
    public double get(int i) {
        if (needsRecompute) {
            compute();
        }
        return difference[i];
    }

    @Override
    public List<Double> getElements() {
        if (needsRecompute) compute();
        return Arrays.stream(difference).boxed().collect(Collectors.toList());
    }

    void compute() {
        for (int i = 1; i < functionInput.get().size(); i++) {
        	difference[i-1] = functionInput.get().get(i-1) - functionInput.get().get(i);
        }
        needsRecompute = false;
    }

    @Override
    public void store() {
    	System.arraycopy(difference, 0, storedDifference, 0, difference.length);
        super.store();
    }

    @Override
    public void restore() {
    	double[] tmp = storedDifference;
    	storedDifference = difference;
    	difference = tmp;
        super.restore();
    }

    @Override
    public boolean requiresRecalculation() {
        needsRecompute = true;
        return true;
    }
}
