package mascot.glmmodel;


import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.Distribution;
import beast.base.inference.State;
import beast.base.spec.inference.distribution.ScalarDistribution;
import mascot.dynamics.GLM;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Description("Produces prior (log) probability of value x." +
        "If x is multidimensional, the components of x are assumed to be independent, " +
        "so the sum of log probabilities of all elements of x is returned as the prior.")
public class MaxRate extends Distribution {
    final public Input<GLM> GLMStepwiseModelInput = new Input<>("GLMmodel", "glm model input");
    final public Input<ScalarDistribution<?, Double>> distInput = new Input<>("distr", "distribution used to calculate prior, e.g. normal, beta, gamma.", Validate.REQUIRED);
    final public Input<Boolean> migrationOnlyInput = new Input<>("migrationOnly", "put prior only on migration rates", false);
    final public Input<Boolean> NeOnlyInput = new Input<>("NeOnly", "put prior only on migration rates", false);

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
    	Double[] mig = GLMStepwiseModelInput.get().getAllCoalescentRate();
		Double[] coal = GLMStepwiseModelInput.get().getAllBackwardsMigration();

    	logP = 0.0;

    	if (migrationOnlyInput.get()){
	        logP += sumLogDensity(mig);
    	}else{
	        logP += sumLogDensity(coal);
	        logP += sumLogDensity(mig);
    	}
        if (logP == Double.POSITIVE_INFINITY) {
            logP = Double.NEGATIVE_INFINITY;
        }
        return logP;
    }

    private double sumLogDensity(Double[] values) {
        // Each entry is treated as an iid draw from `dist` (matches what
        // legacy ParametricDistribution.calcLogP(Function) did over a
        // multidimensional sample). Spec ScalarDistribution exposes
        // logDensity per scalar; sum manually.
        double sum = 0;
        for (Double v : values) sum += dist.logDensity(v);
        return sum;
    }

    /**
     * return name of the parameter this prior is applied to *
     */
    public String getParameterName() {
        return "";
    }

    @Override
    public void sample(State state, Random random) {

        if (sampledFlag)
            return;

        sampledFlag = true;

        // Cause conditional parameters to be sampled
        sampleConditions(state, random);
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
        return arguments;
    }
}
