package mascot.glmmodel;


import beast.base.core.*;
import beast.base.core.Input.Validate;
import beast.base.inference.Distribution;
import beast.base.inference.State;
import beast.base.inference.StateNode;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.distribution.ScalarDistribution;
import beast.base.spec.inference.parameter.IntVectorParam;
import beast.base.spec.inference.parameter.RealVectorParam;
import beast.base.spec.type.RealVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Description("Produces prior (log) probability of value x." +
        "If x is multidimensional, the components of x are assumed to be independent, " +
        "so the sum of log probabilities of all elements of x is returned as the prior.")
public class ErrorSmoothing extends Distribution {
    final public Input<RealVector<? extends Real>> m_x = new Input<>("x", "point at which the density is calculated", Validate.REQUIRED);

    final public Input<ScalarDistribution<?, Double>> distInput = new Input<>("distr", "distribution used to calculate prior, e.g. normal, beta, gamma.", Validate.REQUIRED);

    /**
     * shadows distInput *
     */
    protected ScalarDistribution<?, Double> dist;

    @Override
    public void initAndValidate() {
        dist = distInput.get();
        calculateLogP();
    }

    @Override
    public double calculateLogP() {
        RealVector<? extends Real> x = m_x.get();
        // Components of x are treated as iid draws from `dist` — sum the
        // scalar log-densities. The spec ScalarDistribution doesn't have a
        // single-shot logP-over-a-vector method; loop manually.
        logP = 0;
        for (int i = 0; i < x.size(); i++) {
            logP += dist.logDensity(x.get(i));
        }
        if (logP == Double.POSITIVE_INFINITY) {
            logP = Double.NEGATIVE_INFINITY;
        }
        return logP;
    }

    /**
     * return name of the parameter this prior is applied to *
     */
    public String getParameterName() {
        if (m_x.get() instanceof BEASTObject) {
            return ((BEASTObject) m_x.get()).getID();
        }
        return m_x.get() + "";
    }

    @Override
    public void sample(State state, Random random) {

        if (sampledFlag)
            return;

        sampledFlag = true;

        // Cause conditional parameters to be sampled
        sampleConditions(state, random);

        // sample distribution parameters
        RealVector<? extends Real> x = m_x.get();

        try {
            // Spec ScalarDistribution.sample() returns one value per call;
            // for a vector x we draw size(x) iid samples. m_x is now typed
            // RealVector<? extends Real>, so only the real-vector branch
            // applies (the legacy code had an IntVectorParam branch that's
            // unreachable under the new Input type).
            if (x instanceof RealVectorParam<?> rvp) {
                for (int i = 0; i < rvp.size(); i++) {
                    rvp.set(i, dist.sample().get(0));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to sample!");
        }
    }

    @Override
    public List<String> getConditions() {
        List<String> conditions = new ArrayList<>();
        conditions.add(dist.getID());
        return conditions;
    }

    @Override
    public List<String> getArguments() {
        List<String> arguments = new ArrayList<>();

        String id = null;
        if (m_x.get() != null && m_x.get() instanceof BEASTInterface) {
            arguments.add(((BEASTInterface)m_x.get()).getID());
        }

        return arguments;
    }
}
