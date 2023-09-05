package org.tzi.use.uml.mm;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;

public class MultiModelCreationTest extends TestCase {

    public void testCreateEmptyMultiModel() {
        MMultiModel multimodel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
        assertTrue( multimodel.models().isEmpty() );
    }

    public void testCreateMultiModelWithSingleModel() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();
            assertEquals(1, multimodel.size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();
            assertEquals(2, multimodel.size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels_SameNameFail() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels_SameNameFail();
            fail("Same model name not handled!");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "MultiModel already contains a model `PersonCompany1'.");
        }
    }


    public void testCreateMultiModelWithThreeModels() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelThreeModels();
            assertEquals(3, multimodel.size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

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

    public void testRemoveModelWithInterAssociationFail() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsInterAssociation();
            UseMultiModelApi api = new UseMultiModelApi(multimodel);

            api.removeModel("PersonCompany1");

        } catch (Exception e) {
            assertEquals("PersonCompany1 cannot be removed due to related inter-association called: 'Job'.",e.getMessage());
        }
    }

}
