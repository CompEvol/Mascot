package mascot.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import beast.base.core.Description;
import beast.base.core.Input;
import beast.base.core.Input.Validate;
import beast.base.inference.*;
import beast.base.spec.domain.Real;
import beast.base.spec.type.RealVector;


@Description("returns 0 if condition is met and negative infinity if not")
public class LargerThan extends Distribution {
    final public Input<RealVector<? extends Real>> largerInput = new Input<>("larger", "argument for which the differences for entries is calculated", Validate.REQUIRED);
    final public Input<RealVector<? extends Real>> smallerInput = new Input<>("smaller", "argument for which the differences for entries is calculated", Validate.REQUIRED);


    @Override
    public void initAndValidate() {
        calculateLogP();
    }

    @Override
    public double calculateLogP() {
        RealVector<? extends Real> larger = largerInput.get();
        RealVector<? extends Real> smaller = smallerInput.get();
        for (int i = 0; i < larger.size(); i++) {
            if (larger.get(i) <= smaller.get(i)) {
                logP = Double.NEGATIVE_INFINITY;
                return Double.NEGATIVE_INFINITY;
            }
        }
        logP=0;
        return 0;
    }


    @Override
    public void sample(State state, Random random) {
		// do nothing


    }

    @Override
    public List<String> getConditions() {
        List<String> conditions = new ArrayList<>();
        return conditions;
    }

    @Override
    public List<String> getArguments() {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }
}
