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
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();
            assertEquals(1, multiModel.size());

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelSingleModel2();
            assertEquals(1, multiModel2.size());

            assertEquals(multiModel, multiModel2);

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();
            assertEquals(2, multiModel.size());

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
            assertEquals(2, multiModel2.size());

            assertEquals(multiModel, multiModel2);
        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsAssociationClass() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsAssociationClass();
            assertEquals(2, multiModel.size());

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelTwoModelsAssociationClass2();
            assertEquals(2, multiModel2.size());

            assertEquals(multiModel, multiModel2);
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
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelThreeModels();
            assertEquals(3, multiModel.size());

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelThreeModels2();
            assertEquals(3, multiModel.size());

            assertEquals(multiModel, multiModel2);
        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelTwoModelsInvSimple() {
        try {
            MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSimple();
            assertEquals(2, multiModel.size());

            MMultiModel multiModel2 = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSimple2();
            assertEquals(2, multiModel2.size());

            assertEquals(multiModel, multiModel2);
        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

//    public void testCreateMultiModelWithTwoModels_InvSameFail() {
//        try {
//            TestMultiModelUtil.getInstance().createMultiModelTwoModelsInvSameName2();
//            fail("Same model name not handled!");
//        } catch (Exception e) {
//            assertEquals(e.getMessage(), "MultiModel already contains a model `PersonCompany1'.");
//        }
//    }



    //endregion

    //region Add/Remove model

    public void testAddModelToMultiModel() {
        try{
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();
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
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();

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

    public void testCreateMultiModelWithInterAssociations() {
        MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInterAssociation();
        UseMultiModelApi api = new UseMultiModelApi(multimodel);

        assertEquals(1,api.getMultiModel().fAssociations.size());

    }

    public void testCreateMultiModelFromCompile() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
        assertTrue( multiModel.models().isEmpty() );
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
}
