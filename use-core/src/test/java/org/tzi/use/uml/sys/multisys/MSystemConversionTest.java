package org.tzi.use.uml.sys.multisys;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.TestModelUtil;
import org.tzi.use.uml.mm.TestMultiModelUtil;
import org.tzi.use.uml.sys.MMultiSystem;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.ObjectCreation;
import org.tzi.use.util.NullWriter;

import java.io.PrintWriter;
import java.util.ArrayList;

public class MSystemConversionTest extends TestCase {

    public void testEmptyMultiModelConversion() {
        try{
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
            MMultiSystem multiSystem = new MMultiSystem(multiModel);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert no errors occurred
            Assert.assertEquals(0, cSystem.state().numObjects());
            Assert.assertEquals(0, cSystem.state().allLinks().size());


        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionNoObjects() {
        try{
            //create the multi-model with 1 model
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();
            MMultiSystem multiSystem = new MMultiSystem(multiModel);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert no errors occurred
            Assert.assertEquals(0, cSystem.state().numObjects());
            Assert.assertEquals(0, cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionNoObjects() {
        try{
            //create the multi-model with 2 models
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();
            MMultiSystem multiSystem = new MMultiSystem(multiModel);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert no errors occurred
            Assert.assertEquals(0, cSystem.state().numObjects());
            Assert.assertEquals(0, cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionOnlyObjects() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().createModelWithOnlyObjects("model1");
            MModel model1 = system1.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);

            //set the previous model1 system that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionWithLinks() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);

            //set the previous model1 system that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionOneEmpty() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithOnlyObjects("model1");
            MModel model1 = system1.model();
            MModel model2 = TestModelUtil.getInstance().createModelWithClassAndAssocs("model2");
            MSystem system2 = new MSystem( model2 );

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1 system that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionOnlyObjects() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithOnlyObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithOnlyObjects("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionWithLinks() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjects("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testThreeModelsConversionOneEmpty() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjects("model2");
            MModel model2 = system2.model();
            MModel model3 = TestModelUtil.getInstance().createModelWithClassAndAssocs("model3");
            MSystem system3 = new MSystem( model3 );

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);
            multiModel.addModel(model3);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);
            multiSystem.setModelSystem(model2.name(), system3);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testThreeModelsConversionOnlyObjects() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjects("model2");
            MModel model2 = system2.model();
            MSystem system3 = ObjectCreation.getInstance().createModelWithObjects("model3");
            MModel model3 = system3.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);
            multiModel.addModel(model3);

            //set the previous model1, model2, model3 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);
            multiSystem.setModelSystem(model2.name(), system3);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    //=================================== more complex cases ==================================

    public void testTwoModelsConversionComplex1() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithManyObjects("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionComplex2() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjectsAndLinkObject("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjects("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionComplex3() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjectsAndLinkObject("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjectsOfSameClassAndLinkObject("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionComplex4() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjectsAndTenaryLinkObject("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testTwoModelsConversionComplex5() {
        try{
            //create the initial models and their systems
            MSystem system1 = ObjectCreation.getInstance().createModelWithObjects("model1");
            MModel model1 = system1.model();
            MSystem system2 = ObjectCreation.getInstance().createModelWithObjectsAndLinkObject2("model2");
            MModel model2 = system2.model();

            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);
            multiModel.addModel(model2);

            //set the previous model1, model2 systems that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);
            multiSystem.setModelSystem(model2.name(), system2);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    //=================================== unsatisfactory tests ====================================

    public void testSingleModelConversionSimpleConstraintUnsatisfactory() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().
                    createModelWithObjectsAndConstraintsSimpleUnsatisfactory("model1");
            MModel model1 = system1.model();

            //assert initial model is Unsatisfactory
            Assert.assertFalse(system1.state().check(new PrintWriter(new NullWriter()),
                    false,
                    false,
                    true, new ArrayList<>()));


            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);

            //set the previous model1 system that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

            //assert multi-model is also Unsatisfactory
            Assert.assertFalse(cSystem.state().check(new PrintWriter(new NullWriter()),
                    false,
                    false,
                    true, new ArrayList<>()));

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionSimpleConstraintSatisfactory() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().
                    createModelWithObjectsAndConstraintsSimpleSatisfactory("model1");
            MModel model1 = system1.model();

            //assert initial model is Unsatisfactory
            Assert.assertTrue(system1.state().check(new PrintWriter(new NullWriter()),
                    false,
                    false,
                    true, new ArrayList<>()));


            //create the multi-model that holds the model
            MMultiModel multiModel = new MMultiModel("multi");
            multiModel.addModel(model1);

            //set the previous model1 system that contains the objects
            MMultiSystem multiSystem = new MMultiSystem(multiModel);
            multiSystem.setModelSystem(model1.name(), system1);

            //convert
            MSystem cSystem = multiSystem.toMSystem();

            //assert
            Assert.assertEquals(multiSystem.numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystem.numLinks(), cSystem.state().allLinks().size());

            //assert multi-model is also Satisfactory
            Assert.assertTrue(cSystem.state().check(new PrintWriter(new NullWriter()),
                    false,
                    false,
                    true, new ArrayList<>()));

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    //TODO:
    // 1. check class with a circular association
    // (the conversion to from multi model to model fails for some reason)
    // 2. add check() function to MMultiSystem
    // 3. add tests for more complex constraints

}

