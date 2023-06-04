package org.tzi.use.uml.mm;

import junit.framework.TestCase;
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
            assertEquals(1, multimodel.models().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels();
            assertEquals(2, multimodel.models().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

    public void testCreateMultiModelWithTwoModels_SameNameFail() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelTwoModels_SameNameFail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "MultiModel already contains a model `PersonCompany1'.");
        }
    }


    public void testCreateMultiModelWithThreeModels() {
        try {
            MMultiModel multimodel = TestMultiModelUtil.getInstance().createMultiModelThreeModels();
            assertEquals(3, multimodel.models().size());

        } catch (Exception e) {
            throw ( new Error( e ) );
        }
    }

}
