package mascot.glmmodel;

import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.core.Loggable;
import beast.base.inference.CalculationNode;
import beast.base.spec.inference.parameter.BoolVectorParam;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealScalarParam;
import beast.base.spec.inference.parameter.RealVectorParam;

public abstract class GlmModel extends CalculationNode implements Loggable {

    public Input<CovariateList> covariateListInput = new Input<>("covariateList", "input of covariates", Validate.REQUIRED);
    public Input<RealVectorParam<? extends Real>> scalerInput = new Input<>("scaler", "input of covariates scaler", Validate.REQUIRED);
    public Input<BoolVectorParam> indicatorInput = new Input<>("indicator", "input of covariates scaler", Validate.REQUIRED);
    public Input<RealScalarParam<Real>> clockInput = new Input<>("clock", "clock rate of the parameter",Validate.REQUIRED);
    public Input<RealVectorParam<? extends Real>> errorInput = new Input<>("error", "time variant error term in the GLM model for the rates");
    public Input<RealVectorParam<? extends Real>> constantErrorInput = new Input<>("constantError", "time invariant error term in the GLM model for the rates");

    public int nrIntervals;
    public int verticalEntries;


	public abstract double[] getRates(int i);

	public boolean isDirty(){
		for (int i = 0; i < scalerInput.get().size(); i++)
			if(scalerInput.get().isDirty(i))
					return true;

		for (int i = 0; i < indicatorInput.get().size(); i++)
			if(indicatorInput.get().isDirty(i))
					return true;

		if (errorInput.get() != null)
			for (int i = 0; i < errorInput.get().size(); i++)
				if(errorInput.get().isDirty(i))
						return true;

		if (constantErrorInput.get() != null)
			for (int i = 0; i < constantErrorInput.get().size(); i++)
				if(constantErrorInput.get().isDirty(i))
						return true;


		if (clockInput.get().somethingIsDirty())
			return true;

		return false;
	}

	public void setNrIntervals(int i, int dim, boolean isMigration){
		nrIntervals = i;
		if (isMigration){
			// calc the two possible lengths of the covariates
			int l2 = i*(dim*(dim-1));

			// check that the dimension of the covariates are correct
			for (int j = 0; j < covariateListInput.get().size(); j++){
				if (covariateListInput.get().get(j).getDimension()!=l2)
				throw new RuntimeException("The dimension of the the covariate \"" + covariateListInput.get().get(j).getID() + "\" is wrong.\n" +
						"The current dimension is " + covariateListInput.get().get(j).getDimension() +
						", but should be equal to the number of rate shifts\n"+
						"i.e. it should be " +l2 + "\n");
			}
		}else{
			// calc the two possible lengths of the covariates
			int l2 = i*dim;
			for (int j = 0; j < covariateListInput.get().size(); j++){
				if (covariateListInput.get().get(j).getDimension()!=l2)
				throw new RuntimeException("The dimension of the the covariate \"" + covariateListInput.get().get(j).getID() + "\" is wrong.\n" +
						"The current dimension is " + covariateListInput.get().get(j).getDimension() +
						", but should be equal to the number of rate shifts\n"+
						"i.e. it should be " +l2 + "\n");
			}
		}

		verticalEntries = covariateListInput.get().get(0).getDimension()/(nrIntervals);

	}

	public void setNrDummy(){
		verticalEntries = 0;
	}



}
