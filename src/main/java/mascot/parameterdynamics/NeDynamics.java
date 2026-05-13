package mascot.parameterdynamics;

import beast.base.inference.CalculationNode;
import beast.base.inference.StateNode;

public abstract class NeDynamics extends CalculationNode {

	public boolean isTime;

	@Override
	public void initAndValidate() {
	}


    /**
     * recalculate the dynamics
     */
    public void recalculate() {};

    /**
     * returns the effective population size at time t
     * @param t
     * @return
     */
    public double getNeTime(double t) {
		throw new IllegalArgumentException("Function not implemented. Class of parametric function not correctly recognized");
    }


    /**
     * returns the effective population size at interval i
     * @param i
     * @return
     */
    public double getNeInterval(int i) {
		throw new IllegalArgumentException("Function not implemented. Class of parametric function not correctly recognized");
    }

    public void setNrIntervals(int intervals) {}


	public boolean isDirty() {
		return true;
	};

    /**
     * True if the value bound by {@code input} has been marked dirty by the
     * MCMC framework. Spec interface inputs (RealScalar, RealVector, …)
     * don't expose dirtiness directly — they may be backed either by a
     * {@link StateNode} (proposal-dirty signal) or by an upstream
     * {@link CalculationNode} (recalculation-dirty signal). Subclasses use
     * this helper inside {@link #isDirty()} so concrete-spec-param Inputs
     * can be replaced by the interface without losing the dirty check.
     */
    protected static boolean isDirtyInput(Object input) {
        if (input instanceof StateNode s && s.somethingIsDirty()) return true;
        if (input instanceof CalculationNode c && c.isDirtyCalculation()) return true;
        return false;
    }
}
