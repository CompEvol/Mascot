package mascot.glmmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CovariateTest {

    @Test
    public void testDoubleArrayConstructorPopulatesValuesInput() {
        Double[] vals = new Double[]{1.0, 2.0, 3.0};
        Covariate c = new Covariate(vals, "test");
        assertEquals(3, c.valuesInput.get().size());
        assertEquals(1.0, c.valuesInput.get().get(0));
        assertEquals(2.0, c.valuesInput.get().get(1));
        assertEquals(3.0, c.valuesInput.get().get(2));
    }

    @Test
    public void testInitAndValidateAfterDoubleArrayConstructorPreservesValues() {
        // Regression for the bug where initAndValidate() rebuilt `values` from an
        // empty valuesInput, clobbering the values set by the constructor.
        Double[] vals = new Double[]{4.0, 5.0, 6.0};
        Covariate c = new Covariate(vals, "test");
        c.initAndValidate();
        assertEquals(3, c.getDimension());
        assertEquals(4.0, c.getArrayValue(0));
        assertEquals(5.0, c.getArrayValue(1));
        assertEquals(6.0, c.getArrayValue(2));
    }

    @Test
    public void testInitAndValidateViaValuesInputStillWorks() {
        // Existing code path: default constructor + valuesInput populated externally.
        Covariate c = new Covariate();
        c.valuesInput.get().add(7.0);
        c.valuesInput.get().add(8.0);
        c.setID("test");
        c.initAndValidate();
        assertEquals(2, c.getDimension());
        assertEquals(7.0, c.getArrayValue(0));
        assertEquals(8.0, c.getArrayValue(1));
    }
}
