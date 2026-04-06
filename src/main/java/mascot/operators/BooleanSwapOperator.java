package mascot.operators;


import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.Operator;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.BoolVectorParam;
import beast.base.spec.inference.parameter.RealVectorParam;
import beast.base.util.Randomizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



@Description("A generic operator swapping a one or more pairs in a multi-dimensional parameter")
public class BooleanSwapOperator extends Operator {
    final public Input<BoolVectorParam> boolparameterInput = new Input<>("indicator", "an indicator parameter to swap individual values for", Validate.REQUIRED);
    final public Input<RealVectorParam<? extends Real>> realparameterInput = new Input<>("parameter", "a real parameter to swap individual values for", Validate.OPTIONAL);
    final public Input<Integer> howManyInput = new Input<>("howMany", "number of items to swap, default 1, must be less than half the dimension of the parameter", 1);


    int howMany;
    BoolVectorParam indicator;
    RealVectorParam<? extends Real> parameter;
    private List<Integer> masterList = null;

    @Override
    public void initAndValidate() {
    	indicator = boolparameterInput.get();
        if (realparameterInput.get()!=null){
	        parameter = realparameterInput.get();
	        if (indicator.size()!=parameter.size()){
	            throw new IllegalArgumentException("indicator and parameter have different dimensions");
	        }
        }

        howMany = howManyInput.get();
        if (howMany * 2 > indicator.size()) {
            throw new IllegalArgumentException("howMany it too large: must be less than half the dimension of the parameter");
        }

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < indicator.size(); i++) {
            list.add(i);
        }
        masterList = Collections.unmodifiableList(list);
    }

    @Override
    public double proposal() {
        List<Integer> allIndices = new ArrayList<>(masterList);
        int left, right;

        for (int i = 0; i < howMany; i++) {
            left = allIndices.remove(Randomizer.nextInt(allIndices.size()));
        	right = allIndices.remove(Randomizer.nextInt(allIndices.size()));

            // repeat until left and right are different
            if (indicator.get(left)==indicator.get(right)){
            	return Double.NEGATIVE_INFINITY;
            }

            indicator.swap(left, right);
            if (realparameterInput.get()!=null){
            	parameter.swap(left, right);
            }
        }



        return 0.0;
    }

}
