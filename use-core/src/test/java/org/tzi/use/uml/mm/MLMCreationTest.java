package org.tzi.use.uml.mm;

import junit.framework.TestCase;

public class MLMCreationTest extends TestCase {


    public void testCreateMLMWithEmptyMediators() {
        MMultiLevelModel multiLevelModel = TestMLMUtil.getInstance().createMLMWithEmptyMediator();
        assertEquals("Mediator1", multiLevelModel.getMediator("Mediator1").name());
        assertEquals("Mediator2", multiLevelModel.getMediator("Mediator2").name());

    }

    public void testCreateMLMWithClabjects_AttributeRenaming() {
        MMultiLevelModel multiLevelModel = TestMLMUtil.getInstance().createMLMWithClabjects_AttributeRenaming();
        MClabject clabject = multiLevelModel.getClabject("Mediator2", "PersonCompany2@Person");
        assertEquals(1, clabject.getAttributes().size());
    }

}
