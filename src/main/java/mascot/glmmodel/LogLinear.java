package mascot.glmmodel;

import java.io.PrintStream;

public class LogLinear extends GlmModel {

	@Override
	public void initAndValidate() {
		// set the dimension of the scalers, indicators and potentially the error term
    	scalerInput.get().setDimension(covariateListInput.get().size());
    	indicatorInput.get().setDimension(covariateListInput.get().size());

    	if (errorInput.get()!=null)
    		errorInput.get().setDimension(covariateListInput.get().get(0).getDimension());

    	if (constantErrorInput.get()!=null)
    		if (constantErrorInput.get().size()<1)
    			constantErrorInput.get().setDimension(verticalEntries);
	}


	@Override
	public double[] getRates(int i) {
    	double[] logrates = new double[verticalEntries];

    	for (int j = 0; j < logrates.length; j++)
    		logrates[j] = 0;

		for (int j = 0; j < covariateListInput.get().size(); j++){
			if (indicatorInput.get().get(j)){
				for (int k = 0; k < logrates.length; k++){
					logrates[k] += scalerInput.get().get(j)
						*covariateListInput.get().get(j).getArrayValue(verticalEntries*i + k);
				}
			}
		}

    	if (errorInput.get()!=null)
    		for (int k = 0; k < logrates.length; k++)
    			logrates[k] += errorInput.get().get(verticalEntries*i + k);


    	if (constantErrorInput.get()!=null)
    		for (int k = 0; k < logrates.length; k++)
    			logrates[k] += constantErrorInput.get().get(k);

    	double[] rates = new double[verticalEntries];

		for (int k = 0; k < verticalEntries; k++){
			rates[k] = clockInput.get().get()*Math.exp(logrates[k]);
		}

		return rates;
	}

	@Override
	public void init(PrintStream out) {
		out.print(String.format("%sClock\t", getID()));

		for (int i = 0 ; i < scalerInput.get().size(); i++){
			out.print(String.format("%sscaler.%s\t", getID(), covariateListInput.get().get(i).getID()));
		}
	}

	@Override
	public void log(long sample, PrintStream out) {
		out.print(clockInput.get().get() +"\t");

		for (int i = 0 ; i < scalerInput.get().size(); i++){
			if (indicatorInput.get().get(i)){
				out.print(scalerInput.get().get(i) +"\t");
			}else{
				out.print("0.0\t");
			}
		}
	}

	@Override
	public void close(PrintStream out) {
		// TODO Auto-generated method stub

	}

}
