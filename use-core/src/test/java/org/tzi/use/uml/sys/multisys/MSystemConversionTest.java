package org.tzi.use.uml.sys.multisys;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.api.UseMultiSystemApi;
import org.tzi.use.api.UseSystemApi;
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
            UseSystemApi useSystemApi1 = UseSystemApi.create(system1, true);
            Assert.assertFalse(useSystemApi1.checkState());


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
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            Assert.assertFalse(useSystemApic.checkState());

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

            //assert initial model is satisfactory
            UseSystemApi useSystemApi1 = UseSystemApi.create(system1, true);
            Assert.assertTrue(useSystemApi1.checkState());


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
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            Assert.assertTrue(useSystemApic.checkState());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionComplexConstraintUnsatisfactory() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().
                    createComplexModelWithConstraintsUnSatisfactory("model1");
            MModel model1 = system1.model();

            //assert initial model is Unsatisfactory
            UseSystemApi useSystemApi1 = UseSystemApi.create(system1, true);
            Assert.assertFalse(useSystemApi1.checkState());


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
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            Assert.assertFalse(useSystemApic.checkState());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void testSingleModelConversionComplexConstraintSatisfactory() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().
                    createComplexModelWithConstraintsSatisfactory("model1");
            MModel model1 = system1.model();

            //assert initial model is satisfactory
            UseSystemApi useSystemApi1 = UseSystemApi.create(system1, true);
            Assert.assertTrue(useSystemApi1.checkState());


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

            //assert multi-model is also satisfactory
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            Assert.assertTrue(useSystemApic.checkState());

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    // TODO:
    // convert a mm with object and add new objects to the converted, test for satisfiability

    public void testSingleModelConversionAndAdditionSimple() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().
                    createModelWithObjectsAndConstraintsSimpleSatisfactory("model1");
            MModel model1 = system1.model();

            //assert initial model is satisfactory
            UseSystemApi useSystemApi1 = UseSystemApi.create(system1, true);
            Assert.assertTrue(useSystemApi1.checkState());


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
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            Assert.assertTrue(useSystemApic.checkState());

            //now add new objects that break the constraints
            useSystemApic.createObject("model1_Person", "newP");
            Assert.assertEquals(multiSystem.numObjects() + 1, cSystem.state().numObjects());

            useSystemApic.setAttributeValue("newP", "salary", "6000");
            Assert.assertTrue(useSystemApic.checkState());

            useSystemApic.setAttributeValue("newP", "salary", "1000");
            Assert.assertFalse(useSystemApic.checkState());


        } catch (Exception e) {
            throw new Error(e);
        }
    }



    //====================================== inter assoc ====================================
    //TODO:
    // make instances of the mm in tests from the multi conversion tests
    // start with 2 models each with object diagram, combine them into a mm, add inter assoc, and convert,
    // then add links between the new objects in the new diagram

    public void testMultiModelTwoModelsInterAssociationAddingInterLinks() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInterAssociation();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("PersonCompany1", "Person", "p1");
            multiSystemApi.createObjects("PersonCompany2", "Company", "c1");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Job", "PersonCompany1_p1", "PersonCompany2_c1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem.state().allLinks().size());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelMultipleInterAssociationAddingInterLinks() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelMultipleInterAssociation();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Person", "p1");
            multiSystemApi.createObjects("model2", "Company", "c1");
            multiSystemApi.createObjects("model3", "School", "s1");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Job", "model1_p1", "model2_c1");
            useSystemApic.createLink("Studies", "model1_p1", "model3_s1");
            useSystemApic.createLink("Students", "model2_c1", "model3_s1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 3, cSystem.state().allLinks().size());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelTwoModelsInterAssociationSameClassAddingInterLinks() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoInterAssociationSameClass();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Person", "p1");
            multiSystemApi.createObjects("model2", "Company", "c1");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Job", "model1_p1", "model2_c1");
            useSystemApic.createLink("Study", "model2_c1", "model1_p1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 2, cSystem.state().allLinks().size());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelInterConstraintSimple() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSimple();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model2", "Student", "s1");

            multiSystemApi.setAttributeValue("model1", "e1", "salary", "123");
            multiSystemApi.setAttributeValue("model2", "s1", "salary", "1234");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Job", "model2_s1", "model1_e1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem.state().allLinks().size());

            //useSystemApic.setAttributeValue("model2_s1", "salary", "1234");

            Assert.assertTrue(useSystemApic.checkState());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

}

