package mascot.glmmodel;


import beast.base.core.*;
import beast.base.core.Input.Validate;
import beast.base.inference.Distribution;
import beast.base.inference.State;
import beast.base.inference.StateNode;
import beast.base.inference.distribution.ParametricDistribution;
import beast.base.spec.inference.parameter.IntVectorParam;
import beast.base.spec.inference.parameter.RealVectorParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Description("Produces prior (log) probability of value x." +
        "If x is multidimensional, the components of x are assumed to be independent, " +
        "so the sum of log probabilities of all elements of x is returned as the prior.")
public class ErrorSmoothing extends Distribution {
    final public Input<Function> m_x = new Input<>("x", "point at which the density is calculated", Validate.REQUIRED);

    final public Input<ParametricDistribution> distInput = new Input<>("distr", "distribution used to calculate prior, e.g. normal, beta, gamma.", Validate.REQUIRED);

    /**
     * shadows distInput *
     */
    protected ParametricDistribution dist;

    @Override
    public void initAndValidate() {
        dist = distInput.get();
        calculateLogP();
    }

    @Override
    public double calculateLogP() {
        Function x = m_x.get();
        // spec types enforce bounds via domain, so no explicit check needed
        logP = dist.calcLogP(x);
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
        Function x = m_x.get();

        Double[] newx;
        try {
            newx = dist.sample(1)[0];

            if (x instanceof RealVectorParam<?> rvp) {
                for (int i = 0; i < newx.length; i++) {
                    rvp.set(i, newx[i]);
                }
            } else if (x instanceof IntVectorParam<?> ivp) {
                for (int i = 0; i < newx.length; i++) {
                    ivp.set(i, (int)Math.round(newx[i]));
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
