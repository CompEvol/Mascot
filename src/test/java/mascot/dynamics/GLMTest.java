package mascot.dynamics;

import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.BoolVectorParam;
import beast.base.spec.inference.parameter.RealScalarParam;
import beast.base.spec.inference.parameter.RealVectorParam;
import mascot.glmmodel.Covariate;
import mascot.glmmodel.CovariateList;
import mascot.glmmodel.LogLinear;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GLMTest {

    /**
     * Regression for the single-epoch GLM intervalNr bug.
     * <p>
     * In a single-epoch model (rateShifts = [Infinity]), firstlargerzero == 0
     * and dimension == 1, so the boundary branch in getCoalescentRate / getNe /
     * getMig is always taken for any i. The old hardcoded fallback
     * intervalNr = dim - 2 evaluated to -1 for dim=1, causing
     * ArrayIndexOutOfBoundsException when rates were looked up. The fix
     * uses dim - firstlargerzero - 1, which is the correct last valid
     * interval index for any rateShifts configuration.
     */
    @Test
    public void testSingleEpochGLMRatesDoNotThrow() {
        int dim = 2;

        // single-epoch rate shifts: dim = 1 with the only value > 0, so firstlargerzero = 0
        RateShifts rateShifts = new RateShifts();
        rateShifts.initByName("value", "1.0");

        GLM glm = buildGLM(dim, rateShifts);

        double[] coalRate = glm.getCoalescentRate(0);
        assertEquals(dim, coalRate.length);
        for (double v : coalRate)
            assertTrue(Double.isFinite(v), "coalescent rate must be finite, got " + v);

        for (int s = 0; s < dim; s++) {
            double ne = glm.getNe(s, 0);
            assertTrue(Double.isFinite(ne), "Ne must be finite, got " + ne);
        }

        double mig = glm.getMig(0, 1, 0);
        assertTrue(Double.isFinite(mig), "migration rate must be finite, got " + mig);
    }

    private GLM buildGLM(int dim, RateShifts rateShifts) {
        LogLinear migGLM = buildLogLinear(dim * (dim - 1));
        LogLinear neGLM = buildLogLinear(dim);

        GLM glm = new GLM();
        glm.initByName(
                "dimension", dim,
                "rateShifts", rateShifts,
                "migrationGLM", migGLM,
                "NeGLM", neGLM,
                "types", "a b");
        return glm;
    }

    private LogLinear buildLogLinear(int covariateDim) {
        Double[] vals = new Double[covariateDim];
        for (int i = 0; i < covariateDim; i++)
            vals[i] = 1.0;

        Covariate cov = new Covariate(vals, "cov");
        cov.initAndValidate();

        CovariateList covList = new CovariateList();
        covList.initByName("covariates", cov);

        RealVectorParam<Real> scaler = new RealVectorParam<>(new double[]{0.0}, Real.INSTANCE);
        BoolVectorParam indicator = new BoolVectorParam(new boolean[]{true});
        RealScalarParam<Real> clock = new RealScalarParam<>();
        clock.initByName("value", "1.0");

        LogLinear glm = new LogLinear();
        glm.initByName(
                "covariateList", covList,
                "scaler", scaler,
                "indicator", indicator,
                "clock", clock);
        return glm;
    }
}
