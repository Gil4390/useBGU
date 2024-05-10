package org.tzi.use.uml.mm.statemachines;

import junit.framework.TestCase;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.TestMultiModelUtil;

public class MultiModelPropertiesTest extends TestCase {

    public void testNumOfClasses_EmptyMultiModel() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
        assertEquals(multiModel.numOfClasses(), 0);
    }

    public void testNumOfClasses_SingleModel() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();
        assertEquals(multiModel.numOfClasses(), 2);
    }

    public void testNumOfClasses_TwoModels() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
        assertEquals(multiModel.numOfClasses(), 4);
    }

    public void testNumOfClasses_ThreeModels() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelThreeModels();
        assertEquals(multiModel.numOfClasses(), 7);
    }


    public void testMaxNumOfClasses_EmptyMultiModel() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
        assertEquals(multiModel.maxNumOfClasses(), -1);
    }

    public void testMaxNumOfClasses_TwoModels() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
        assertEquals(multiModel.maxNumOfClasses(), 2);
    }

    public void testMaxNumOfClasses_ThreeModels() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelThreeModels();
        assertEquals(multiModel.maxNumOfClasses(), 3);
    }

    public void testContainsDuplicateClassNames_EmptyMultiModel() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createEmptyMultiModel();
        assertFalse(multiModel.containsDuplicateClassNames());
    }

    public void testContainsDuplicateClassNames_SingleModel() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelSingleModel();
        assertFalse(multiModel.containsDuplicateClassNames());
    }

    public void testContainsDuplicateClassNames_TwoModels() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
        assertFalse(multiModel.containsDuplicateClassNames());
    }

    public void testContainsDuplicateClassNames_TwoModels2() {
        MMultiModel multiModel = TestMultiModelUtil.getInstance().createMultiModelTwoModelsDifferentClassNames();
        assertFalse(multiModel.containsDuplicateClassNames());
    }

}
