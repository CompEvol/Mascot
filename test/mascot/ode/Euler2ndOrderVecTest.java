package mascot.ode;

import java.util.Random;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 * Checks that the SIMD solvers return bit-identical results when called repeatedly with the same
 * input. Sums over lanes used to depend on whether a solver was interpreted or JIT-compiled, so
 * the result changed after several hundred calls and BEAST stopped with an incorrectly calculated
 * likelihood. Needs the JVM option --add-modules jdk.incubator.vector, otherwise it is skipped.
 */
public class Euler2ndOrderVecTest {

	// enough calls for the solver methods to be JIT-compiled (results used to change after 150 to 900)
	static final int CALLS = 1500;
	// at least two vectors of lineages for 16 float lanes (AVX-512), otherwise the scalar code is used
	static final int LINEAGES = 64;

	@Test
	public void testRepeatedCallsDouble() throws Exception {
		for (int states : new int[] {2, 3, 4, 5}) {
			checkRepeatedCalls(states, false);
		}
	}

	@Test
	public void testRepeatedCallsFloat() throws Exception {
		for (int states : new int[] {2, 3, 4, 5}) {
			checkRepeatedCalls(states, true);
		}
	}

	private void checkRepeatedCalls(int states, boolean singlePrecision) throws Exception {
		Assume.assumeTrue("needs --add-modules jdk.incubator.vector",
				ModuleLayer.boot().findModule("jdk.incubator.vector").isPresent());

		// loaded by reflection, as in Mascot, so that this class compiles without the vector module
		String name = "mascot.ode.Euler2ndOrderVec" + states + (singlePrecision ? "F" : "");
		Euler2ndOrderBase euler = (Euler2ndOrderBase) Class.forName(name).getDeclaredConstructor().newInstance();
		euler.setup(LINEAGES * states + 1, states, 0.001, 0.2);

		Random random = new Random(127);
		double[] migrationRates = new double[states * states];
		int[] indicators = new int[states * (states - 1) * 2];
		for (int a = 0, k = 0; a < states; a++) {
			for (int b = 0; b < states; b++) {
				if (a == b) continue;
				migrationRates[a * states + b] = 0.1 + random.nextDouble();
				indicators[k++] = a;
				indicators[k++] = b;
			}
		}
		double[] coalescentRates = new double[states];
		for (int a = 0; a < states; a++) {
			coalescentRates[a] = 0.5 + random.nextDouble();
		}
		double[] p0 = new double[LINEAGES * states + 1];
		for (int i = 0; i < LINEAGES; i++) {
			double sum = 0;
			for (int a = 0; a < states; a++) {
				p0[i * states + a] = random.nextDouble();
				sum += p0[i * states + a];
			}
			for (int a = 0; a < states; a++) {
				p0[i * states + a] /= sum;
			}
		}

		double[] first = null;
		double[] p = new double[p0.length];
		for (int call = 0; call < CALLS; call++) {
			System.arraycopy(p0, 0, p, 0, p0.length);
			euler.initWithIndicators(migrationRates, indicators, coalescentRates, LINEAGES);
			euler.calculateValues(0.5, p, p.length);
			if (first == null) {
				first = p.clone();
				Assert.assertTrue(name + " logP should be finite", Double.isFinite(first[first.length - 1]));
				Assert.assertNotEquals(name + " logP should be updated", 0.0, first[first.length - 1], 0.0);
				continue;
			}
			for (int k = 0; k < p.length; k++) {
				if (Double.doubleToRawLongBits(p[k]) != Double.doubleToRawLongBits(first[k])) {
					Assert.fail(name + ": call " + call + " differs from call 0 at index " + k
							+ (k == p.length - 1 ? " (logP)" : "") + ": " + p[k] + " != " + first[k]);
				}
			}
		}
	}
}
