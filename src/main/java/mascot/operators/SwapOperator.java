package mascot.operators;


import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.Operator;
import beast.base.spec.domain.Real;
import beast.base.spec.inference.parameter.RealVectorParam;
import beast.base.util.Randomizer;



@Description("A generic operator swapping a one or more pairs in a multi-dimensional parameter")
public class SwapOperator extends Operator {
    final public Input<RealVectorParam<? extends Real>> realparameterInput = new Input<>("parameter", "a real parameter to swap individual values for", Validate.OPTIONAL);
    final public Input<Integer> howManyInput = new Input<>("howMany", "number of items to swap, default 1, must be less than half the dimension of the parameter", 1);


    RealVectorParam<? extends Real> parameter;

    @Override
    public void initAndValidate() {
    	parameter = realparameterInput.get();
    }

    @Override
    public double proposal() {
        int left = Randomizer.nextInt(parameter.size());
    	int right = Randomizer.nextInt(parameter.size());

    	while (left==right)
    		right = Randomizer.nextInt(parameter.size());
        
        parameter.swap(left, right);
        
        return 0.0;
    }

}
