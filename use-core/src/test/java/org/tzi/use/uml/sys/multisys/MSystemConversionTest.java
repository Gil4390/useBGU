package org.tzi.use.uml.sys.multisys;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.TestModelUtil;
import org.tzi.use.uml.sys.MMultiSystem;
import org.tzi.use.uml.sys.MSystem;

public class MSystemConversionTest extends TestCase {

    public void testEmptyMultiModelConversion() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionNoObjects() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionNoObjects() {
        try{
            MModel model1 = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocClass();
            MSystem system1 = new MSystem( model1 );

            MModel model2 = TestModelUtil.getInstance()
                    .createModelWithClassAndAssocClass();
            MSystem system2 = new MSystem( model2 );


            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            MMultiSystem multiSystem = new MMultiSystem(multiModel);

            MSystem cSystem = multiSystem.toMSystem();


            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversion() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionWithLinks() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionOneEmpty() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversion() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testThreeModelsConversion() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testThreeModelsConversionOneEmpty() {
        try{
            //TODO

        } catch (Exception e) {
            throw new Error(e);
        }
    }
}

