package org.tzi.use.uml.mm;

import junit.framework.TestCase;
import org.tzi.use.uml.sys.MSystemState;

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

        MAttribute originalAtr = clabject.child().attribute("name", false);
        MAttribute renamedAtr =  multiLevelModel.getClass("PersonCompany2", "Person").attribute("newName", false);

        assertEquals(originalAtr, renamedAtr);
    }

    public void testCreateMLMWithClabjects_AttributeRemoving() {
        MMultiLevelModel multiLevelModel = TestMLMUtil.getInstance().createMLMWithClabjects_AttributeRenaming();
        MClabject clabject = multiLevelModel.getClabject("Mediator2", "PersonCompany2@Person");
        assertEquals(1, clabject.getAttributes().size());

        MAttribute renamedAtr =  multiLevelModel.getClass("PersonCompany2", "Person").attribute("name", false);
        assertNull(renamedAtr);
    }

    public void testValidMLM1(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM1();
        assertFalse(mlm.checkState());
    }
    public void testValidMLM2(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM2();
        assertFalse(mlm.checkState());
    }
    public void testValidMLM3(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM3();
        assertTrue(mlm.checkState());
    }

    public void testValidMLM4(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM4();
        assertFalse(mlm.checkState());
    }

    public void testLegalMLM1(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM1();
        assertEquals(MSystemState.Legality.PartiallyLegal.toString(), mlm.checkLegalState());
    }
    public void testLegalMLM2(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM2();
        assertEquals(MSystemState.Legality.PartiallyLegal.toString(), mlm.checkLegalState());
    }
    public void testLegalMLM3(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM3();
        assertEquals(MSystemState.Legality.Legal.toString(), mlm.checkLegalState());
    }

    public void testLegalMLM4(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM4();
        assertEquals(MSystemState.Legality.Illegal.toString(), mlm.checkLegalState());
    }

}
