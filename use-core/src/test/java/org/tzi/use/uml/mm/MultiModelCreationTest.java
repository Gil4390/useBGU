package org.tzi.use.uml.mm;

import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;

public class MultiModelCreationTest extends TestCase {

    //region Simple Cases: testing creation using two ways and comparing them

    public void testCreateEmptyMultiModel() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
        assertTrue( multiModel.models().isEmpty() );
    }

    public void testCreateMultiModelWithSingleModel() {
        try {
            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelSingleModel2();
            assertEquals(1, multiModel2.size());
        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels() {
        try {
            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
            assertEquals(2, multiModel2.size());
        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsAssociationClass() {
        try {
            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelTwoModelsAssociationClass2();
            assertEquals(2, multiModel2.size());
        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels_SameNameFail() {
        try {
            TestMultiModelUtil.getInstance().createMultiModelTwoModels_SameNameFail();
            fail("Same model name not handled!");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "MultiModel already contains a model `PersonCompany1'.");
        }
    }

    public void testCreateMultiModelWithThreeModels() {
        try {

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelThreeModels2();
            assertEquals(3, multiModel2.size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsInvSimple() {
        try {

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSimple2();
            assertEquals(2, multiModel2.size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    //endregion

    //region Add/Remove model

    public void testAddModelToMultiModel() {
        try{
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
            MModel modelToAdd = TestModelUtil.getInstance().createModelWithClassAndAssocs();

            UseMultiModelApi api = new UseMultiModelApi(multimodel);
            api.addModel(modelToAdd);

            assertEquals(3, api.getMultiModel().size());
        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testRemoveModelFromMultiModel() {
        try{
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();

            UseMultiModelApi api = new UseMultiModelApi(multimodel);
            api.removeModel(((MModel)api.getMultiModel().models().toArray()[0]).name());

            assertEquals(1, api.getMultiModel().size());
        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    public void testAddAndRemoveToEmptyMultiModel() {
        try{
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
            UseMultiModelApi api = new UseMultiModelApi(multimodel);
            MModel modelToAdd = TestModelUtil.getInstance().createModelWithClasses();

            api.addModel(modelToAdd);
            assertEquals(1, api.getMultiModel().size());

            api.removeModel(modelToAdd.name());
            assertEquals(0, api.getMultiModel().size());
        } catch (Exception e) {
            throw (new Error(e));
        }
    }

    //endregion

    //region inter
    public void testCreateMultiModelWithInterAssociations() {
        MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInterAssociation();
        UseMultiModelApi api = new UseMultiModelApi(multimodel);

        assertEquals(1,api.getMultiModel().fAssociations.size());

    }


    public void testCreateMultiModelWithInterClass() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithInterClass();
        assertEquals(5, multiModel.numOfClasses());
        assertEquals(4, multiModel.numOfAssociations());
    }

    public void testCreateMultiModelWithInterAssociationClass() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithInterAssociationClass();
        assertEquals(9, multiModel.numOfClasses());
        assertEquals(4, multiModel.numOfAssociations());
    }

    public void testCreateMultiModelWithOperations() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelWithOperations();
        assertEquals(2, multiModel.getClass("model1@Person").operations().size());
        assertEquals(2, multiModel.getClass("Person").operations().size());
    }

    //endregion

}
