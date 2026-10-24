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

	/**
	 * Interval from a 4 deme analysis where Euler2ndOrderVec4F returned "too many iterations":
	 * lineages at probability 1 in deme 3 (which has no outgoing migration) got a slightly positive
	 * derivative from float rounding, and the step search, done in double, shrank the step to zero.
	 */
	@Test
	public void testFloatStepSearchTerminates() throws Exception {
		Assume.assumeTrue("needs --add-modules jdk.incubator.vector",
				ModuleLayer.boot().findModule("jdk.incubator.vector").isPresent());

		double[] migrationRates = {
				0.0, 0.09999999999999999, 0.09999999999999999, 14.841315910257661,
				0.09999999999999999, 0.0, 0.09999999999999999, 14.841315910257661,
				0.09999999999999999, 0.09999999999999999, 0.0, 14.841315910257661,
				0.0, 0.0, 0.0, 0.0};
		int[] indicators = {0, 1, 0, 2, 0, 3, 1, 0, 1, 2, 1, 3, 2, 0, 2, 1, 2, 3};
		double[] coalescentRates = {382.5946188183, 382.5946188183, 382.5946188183, 2.5779022637330122};
		double[][] lineages = {
				{0.0, 0.0, 0.0, 1.0},
				{6.469518965654626E-23, 4.041682778422989E-22, 1.578950270586893E-19, 1.0},
				{0.0, 0.0, 0.0, 1.0},
				{1.1846216920542889E-18, 1.1219786507929642E-16, 2.859827598935801E-15, 1.0},
				{1.0617019416403473E-12, 2.9639732890274217E-9, 3.432358997718099E-12, 1.0},
				{0.564485490322113, 8.574212552048266E-4, 2.655136922840029E-4, 0.4343915283679962},
				{0.564485490322113, 8.574212552048266E-4, 2.655136922840029E-4, 0.4343915283679962},
				{0.564485490322113, 8.574212552048266E-4, 2.655136922840029E-4, 0.4343915283679962},
				{0.564485490322113, 8.574212552048266E-4, 2.655136922840029E-4, 0.4343915283679962},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964},
				{0.9960840344429016, 2.4186927475966513E-5, 2.163138924515806E-5, 0.0038701705634593964}};
		double duration = 2.875577560459419E-4;

		double expected = logPAfterInterval(new Euler2ndOrder4(), migrationRates, indicators, coalescentRates, lineages, duration);
		Euler2ndOrderBase floatSolver = (Euler2ndOrderBase) Class.forName("mascot.ode.Euler2ndOrderVec4F").getDeclaredConstructor().newInstance();
		double logP = logPAfterInterval(floatSolver, migrationRates, indicators, coalescentRates, lineages, duration);
		Assert.assertEquals(expected, logP, 1e-5);
	}

	private double logPAfterInterval(Euler2ndOrderBase euler, double[] migrationRates, int[] indicators,
			double[] coalescentRates, double[][] lineages, double duration) {
		int states = coalescentRates.length;
		double[] p = new double[lineages.length * states + 1];
		for (int i = 0; i < lineages.length; i++) {
			System.arraycopy(lineages[i], 0, p, i * states, states);
		}
		euler.setup(p.length, states, 0.001, Double.POSITIVE_INFINITY);
		euler.setUpDynamics(new double[][] {coalescentRates}, new double[][] {migrationRates}, new int[][] {indicators},
				new double[] {Double.POSITIVE_INFINITY});
		euler.initAndcalculateValues(0, lineages.length, duration, p, p.length);
		return p[p.length - 1];
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
