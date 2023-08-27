package org.tzi.use.uml.sys.multisys;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.api.UseMultiSystemApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
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

    //region Simple Cases
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
    //endregion

    //=================================== more complex cases ==================================

    //region Complex Cases
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


            //add objects to converted system
            UseSystemApi cSystemApi = UseSystemApi.create(cSystem, true);
            assertTrue(cSystemApi.checkState());
            cSystemApi.createObjects("model1_Person", "p5");
            cSystemApi.createLink("model1_Job","p5","model1_c1");
            assertFalse(cSystemApi.checkState());

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

            //check converted system
            UseSystemApi cSystemApi = UseSystemApi.create(cSystem, true);
            assertTrue(cSystemApi.checkState());
            cSystemApi.createObjects("model2_Person", "p5");
            cSystemApi.createObjects("model2_Company", "c5");
            cSystemApi.createObjects("model2_Salary", "s5");

            cSystemApi.createLinkObject("model2_Job","j2","p5","c5","s5");
            assertTrue(cSystemApi.checkState());


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

            //check converted system
            UseSystemApi cSystemApi = UseSystemApi.create(cSystem, true);
            assertTrue(cSystemApi.checkState(new PrintWriter(System.out)));
            cSystemApi.createObjects("model2_Person", "p5");
            cSystemApi.createLink("model2_isBoss","model2_p1", "p5");
            assertFalse(cSystemApi.checkState());
            cSystemApi.undo();
            assertTrue(cSystemApi.checkState());
            cSystemApi.createLink("model2_Job", "p5","model2_c1");
            assertFalse(cSystemApi.checkState());

        } catch (Exception e) {
            throw new Error(e);
        }
    }
    //endregion

    //=================================== unsatisfactory tests ====================================

    //region Satisfiability Tests
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
    //endregion


    public void testMultiModelGeneralization() {
        try{
            //create the initial model and its system
            MSystem system1 = ObjectCreation.getInstance().createModelWithGeneralization("model1");
            MModel model1 = system1.model();

            UseSystemApi useSystemApi1 = UseSystemApi.create(system1, true);
            assertTrue(useSystemApi1.checkState());

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


    //====================================== inter assoc ====================================

    //region Inter-Association & Inter-Constraints
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

    public void testMultiModelInterConstraintSimpleSatisfactory() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSimple();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model1", "Employee", "e2");
            multiSystemApi.createObjects("model2", "Student", "s1");

            multiSystemApi.setAttributeValue("model1", "e1", "salary", "100");
            multiSystemApi.setAttributeValue("model1", "e2", "salary", "110");
            //multiSystemApi.setAttributeValue("model2", "s1", "salary", "50");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Job", "model2_s1", "model1_e1");
            useSystemApic.createLink("Job", "model2_s1", "model1_e2");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 2, cSystem.state().allLinks().size());

            useSystemApic.setAttributeValue("model2_s1", "salary", "50");

            Assert.assertTrue(useSystemApic.checkState());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelInterConstraintSimpleUnsatisfactory() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSimple();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model2", "Student", "s1");

            multiSystemApi.setAttributeValue("model1", "e1", "salary", "100");
            multiSystemApi.setAttributeValue("model2", "s1", "salary", "200");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Job", "model2_s1", "model1_e1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem.state().allLinks().size());

            Assert.assertFalse(useSystemApic.checkState());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelInterConstraintComplexUnsatisfactory() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model2", "Student", "s1");
            multiSystemApi.createObjects("model3", "Company", "c1");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Interns", "model3_c1", "model2_s1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem.state().allLinks().size());

            Assert.assertFalse(useSystemApic.checkState());

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelInterConstraintComplexSatisfactory() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model2", "Student", "s1");
            multiSystemApi.createObjects("model3", "Company", "c1");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.createLink("Interns", "model3_c1", "model2_s1");
            useSystemApic.createLink("Supervise", "model2_s1", "model1_e1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 2, cSystem.state().allLinks().size());

            Assert.assertTrue(useSystemApic.checkState(new PrintWriter(System.out)));

        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    //TODO: fix
    public void testMultiModelInterConstraintSelfAssoc() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintSelfAssociation();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model1", "Employee", "e2");
            multiSystemApi.createObjects("model1", "Employee", "e3");

            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);
            useSystemApic.setAttributeValue("model1_e1","salary","100");
            useSystemApic.setAttributeValue("model1_e2","salary","50");
            useSystemApic.setAttributeValue("model1_e3","salary","70");

            useSystemApic.createLink("WorksFor", "model1_e1", "model1_e2");
            useSystemApic.createLink("WorksFor", "model1_e1", "model1_e3");

            Assert.assertTrue(useSystemApic.checkState(new PrintWriter(System.out)));


        } catch ( Exception e ) {
            throw ( new Error( e ) );
        }
    }

    public void testMultiModelInterConstraintComplex2Unsatisfactory() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex2();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);
            Assert.assertTrue(useSystemApi1.checkState());


            multiSystemApi.createObjects("model1", "Manager", "m1");
            multiSystemApi.createObjects("model2", "Student", "s1");
            multiSystemApi.createInterLink("model1", "model2", "supervising", "m1", "s1");


            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem.state().allLinks().size());

            Assert.assertFalse(useSystemApic.checkState());

        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testMultiModelInterConstraintComplex2Satisfactory() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex2();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);
            Assert.assertTrue(useSystemApi1.checkState());


            multiSystemApi.createObjects("model1", "Manager", "m1");
            multiSystemApi.setAttributeValue("model1", "m1", "salary", "1000");
            multiSystemApi.createObjects("model2", "Student", "s1");
            multiSystemApi.setAttributeValue("model2", "s1", "salary", "500");
            multiSystemApi.createInterLink("model1", "model2", "supervising", "m1", "s1");


            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApic = UseSystemApi.create(cSystem, true);

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem.state().allLinks().size());

            Assert.assertFalse(useSystemApic.checkState());

        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testMultiModelWithConstraintOclIsType() {
        try {
            // testing normal single model
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintOclIsType();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.getApiSafe("model1").createObject("Goo", "goo");
            multiSystemApi.getApiSafe("model1").createObject("Foo", "foo");
            multiSystemApi.getApiSafe("model1").createLink("assoc2", "goo", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("A", "a");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "a", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());
            multiSystemApi.getApiSafe("model1").createObject("C", "c");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "c", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("B", "b");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "b", "foo");
            Assert.assertTrue(multiSystemApi.getApiSafe("model1").checkState());

            //testing inter assoc and constraints

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);

            useSystemApi1.createObjects("model2_Bar", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_a", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_c", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_b", "bar");
            Assert.assertTrue(useSystemApi1.checkState());

        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testMultiModelWithConstraintOclIsKind() {
        try {
            // testing normal single model
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintOclIsKind();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.getApiSafe("model1").createObject("Goo", "goo");
            multiSystemApi.getApiSafe("model1").createObject("Foo", "foo");
            multiSystemApi.getApiSafe("model1").createLink("assoc2", "goo", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("A", "a");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "a", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());
            multiSystemApi.getApiSafe("model1").createObject("C", "c");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "c", "foo");
            Assert.assertTrue(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").deleteLink("assoc1", new String[]{"c", "foo"});
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("B", "b");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "b", "foo");
            Assert.assertTrue(multiSystemApi.getApiSafe("model1").checkState());

            //testing inter assoc and constraints

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);

            useSystemApi1.createObjects("model2_Bar", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_a", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_c", "bar");
            Assert.assertTrue(useSystemApi1.checkState());

            useSystemApi1.deleteLink("interAssoc1", new String[]{"model1_c", "bar"});
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_b", "bar");
            Assert.assertTrue(useSystemApi1.checkState());

        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testMultiModelWithConstraintSelectByType() {
        try {
            // testing normal single model
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintSelectByType();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.getApiSafe("model1").createObject("Goo", "goo");
            multiSystemApi.getApiSafe("model1").createObject("Foo", "foo");
            multiSystemApi.getApiSafe("model1").createLink("assoc2", "goo", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("A", "a");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "a", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());
            multiSystemApi.getApiSafe("model1").createObject("C", "c");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "c", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("B", "b");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "b", "foo");
            Assert.assertTrue(multiSystemApi.getApiSafe("model1").checkState());

            //testing inter assoc and constraints

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);

            useSystemApi1.createObjects("model2_Bar", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_a", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_c", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_b", "bar");
            Assert.assertTrue(useSystemApi1.checkState());

        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testMultiModelWithConstraintSelectByKind() {
        try {
            // testing normal single model
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithConstraintSelectByKind();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.getApiSafe("model1").createObject("Goo", "goo");
            multiSystemApi.getApiSafe("model1").createObject("Foo", "foo");
            multiSystemApi.getApiSafe("model1").createLink("assoc2", "goo", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("A", "a");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "a", "foo");
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());
            multiSystemApi.getApiSafe("model1").createObject("C", "c");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "c", "foo");
            Assert.assertTrue(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").deleteLink("assoc1", new String[]{"c", "foo"});
            Assert.assertFalse(multiSystemApi.getApiSafe("model1").checkState());

            multiSystemApi.getApiSafe("model1").createObject("B", "b");
            multiSystemApi.getApiSafe("model1").createLink("assoc1", "b", "foo");
            Assert.assertTrue(multiSystemApi.getApiSafe("model1").checkState());

            //testing inter assoc and constraints

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);

            useSystemApi1.createObjects("model2_Bar", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_a", "bar");
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_c", "bar");
            Assert.assertTrue(useSystemApi1.checkState());

            useSystemApi1.deleteLink("interAssoc1", new String[]{"model1_c", "bar"});
            Assert.assertFalse(useSystemApi1.checkState());

            useSystemApi1.createLink("interAssoc1", "model1_b", "bar");
            Assert.assertTrue(useSystemApi1.checkState());


        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    //endregion


    //TODO
    // test where you add inter assoc \ constraint between objects of the same mode, right now we get an error
    // tests where we instantiate objects and links based on the multi model that azzam made, one test that satisfies and another that doesn't
    // tests for inter links

    //region Inter-Links
    public void testMultiModelInterLinks1() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex2();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Employee", "e1");
            multiSystemApi.createObjects("model2", "Student", "s1");
            multiSystemApi.createInterLink("model1", "model2", "supervising", "e1", "s1");

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();

            UseSystemApi useSystemApi1 = UseSystemApi.create(cSystem1, true);

            useSystemApi1.createObject("model1_Manager", "m1");
            useSystemApi1.createObject("model2_Student", "s1");
            useSystemApi1.createLink("supervising", "m1", "s1");

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects() + 2, cSystem1.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 2, cSystem1.state().allLinks().size());
        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testMultiModelInterLinks2() {
        try {
            // creation of the system
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex2();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);

            multiSystemApi.createObjects("model1", "Manager", "m1");
            multiSystemApi.createObjects("model2", "Student", "s1");
            multiSystemApi.createInterLink("model1", "model2", "supervising", "m1", "s1");

            MSystem cSystem1 = multiSystemApi.getMultiSystem().toMSystem();

            Assert.assertEquals(multiSystemApi.getMultiSystem().numObjects(), cSystem1.state().numObjects());
            Assert.assertEquals(multiSystemApi.getMultiSystem().numLinks() + 1, cSystem1.state().allLinks().size());


        } catch (Exception e) {
            throw (new Error(e));
        }
    }
    //endregion


    //TODO: FIX
    public void testMultiModelInterAssociationGeneralization() {
        try{
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelInterAssocitionWithGeneralization();
            UseMultiSystemApi multiSystemApi = new UseMultiSystemApi(multiModel, false);
            MSystem cSystem = multiSystemApi.getMultiSystem().toMSystem();
            UseSystemApi systemApi = new UseSystemApiUndoable(cSystem);

            systemApi.createObjects("model1_Person","p1");
            systemApi.createObjects("model2_Company","c1");

            systemApi.createLink("Job","p1","c1");

            assertFalse(systemApi.checkState());

            systemApi.createObjects("model1_Adult","a1");
            systemApi.deleteLink("Job", new String[]{"p1","c1"});
            systemApi.deleteObject("p1");
            systemApi.createLink("Job","a1","c1");

            assertTrue(systemApi.checkState(new PrintWriter(System.out)));




        } catch (Exception e) {
            throw new Error(e);
        }
    }


}

