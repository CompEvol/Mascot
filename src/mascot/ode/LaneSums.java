package mascot.ode;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.FloatVector;

/**
 * Sums the lanes of a vector in a fixed order (lane 0, 1, 2, ...) in double precision.
 * reduceLanes(VectorOperators.ADD) leaves the order of the additions unspecified, and the
 * interpreter and the JIT-compiled code add the lanes in different orders. The result for the
 * same input then changes once a solver gets compiled, which BEAST reports as an incorrectly
 * calculated likelihood. A fixed order gives the same result in every run on the same machine.
 * Requires the JVM option --add-modules jdk.incubator.vector.
 */
public final class LaneSums {

	private LaneSums() {
	}

	public static double sum(final FloatVector v) {
		double s = 0;
		for (int i = 0; i < v.length(); i++) s += v.lane(i);
		return s;
	}

	public static double sum(final DoubleVector v) {
		double s = 0;
		for (int i = 0; i < v.length(); i++) s += v.lane(i);
		return s;
	}
}
