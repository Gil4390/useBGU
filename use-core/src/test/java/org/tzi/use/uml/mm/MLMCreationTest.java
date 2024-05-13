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
        MClabject clabject = multiLevelModel.getClabject("Mediator2", "CLABJECT_PersonCompany2@Person_PersonCompany1@Person");
        assertEquals(1, clabject.getAttributes().size());

        MAttribute originalAtr = clabject.child().attribute("name", false);
        MAttribute renamedAtr =  multiLevelModel.getClass("PersonCompany2", "Person").attribute("newName", false);

        assertEquals(originalAtr, renamedAtr);
    }

    public void testCreateMLMWithClabjects_AttributeRemoving() {
        MMultiLevelModel multiLevelModel = TestMLMUtil.getInstance().createMLMWithClabjects_AttributeRenaming();
        MClabject clabject = multiLevelModel.getClabject("Mediator2", "CLABJECT_PersonCompany2@Person_PersonCompany1@Person");
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

    public void testValidMLM_TwoMultiplicityRange_PartiallyLegal() {
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLMTwoMultiplicityRange();
        assertEquals(MSystemState.Legality.Legal.toString(), mlm.checkLegalState());
    }

    public void testLegalMLM_NoAssocLink_PartiallyLegal(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM1();
        assertEquals(MSystemState.Legality.Legal.toString(), mlm.checkLegalState());
    }
    public void testLegalMLM_OneAssocLink_PartiallyLegal(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM2();
        assertEquals(MSystemState.Legality.Legal.toString(), mlm.checkLegalState());
    }
    public void testLegalMLM_TwoAssocLink_Legal(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM3();
        assertEquals(MSystemState.Legality.Legal.toString(), mlm.checkLegalState());
    }

    public void testLegalMLM_ThreeAssocLink_Illegal(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLM4();
        assertEquals(MSystemState.Legality.Illegal.toString(), mlm.checkLegalState());
    }

    public void testLegalMLM_Constraint_Illegal(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLMWithConstraint();
        assertEquals(MSystemState.Legality.Illegal.toString(), mlm.checkLegalState());
    }

    public void testLegalMLM_Constraint_Legal(){
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLMWithConstraint2();
        assertEquals(MSystemState.Legality.Legal.toString(), mlm.checkLegalState());
    }

    public void testMLM_AttributeRenaming() {
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLMWithAttributeRenaming();
        MClabject clabject = mlm.getClabject("CD", "CLABJECT_CD@C_AB@A");
        String newName = clabject.getRenamedAttribute("name").newName();

        assertEquals("newName", newName);
    }
    public void testMLM_AttributeRenaming_existingNewName() {
        try{
            MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLMWithAttributeRenaming2();
            fail("Should throw exception");
        } catch(Exception e) {
            assertEquals("Attribute: newName already exists", e.getMessage());
        }
    }

    public void testMLM_AttributeRenaming_ThreeLevels_1() {
        MMultiLevelModel mlm = TestMLMUtil.getInstance().createMLMWithAttributeRenaming3();
        MClabject clabject = mlm.getClabject("CD", "CLABJECT_CD@C_AB@A");
        String newName = clabject.getRenamedAttribute("name").newName();

        assertEquals("newName", newName);
    }





}
