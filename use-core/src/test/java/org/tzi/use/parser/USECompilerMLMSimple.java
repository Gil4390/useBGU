package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.config.Options;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.sys.MLMSystem;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;


public class USECompilerMLMSimple extends TestCase {
    private static final boolean VERBOSE = false;

    private static File TEST_PATH;

    static {
        try {
            TEST_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/mlmTests").toURI());
        } catch (NullPointerException | URISyntaxException e) {
            TEST_PATH = null;
            fail("Folders including tests are missing!");
        }
    }

    public void testMLMClabjectSpecification() {
        Options.explicitVariableDeclarations = false;

        List<File> fileList = MLMTestUtil.getInstance().getFilesMatchingSuffix(TEST_PATH,".use", 32);

        // create a new stream for capturing output on stderr
        MLMTestUtil.StringOutputStream errStr = new MLMTestUtil.StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);
        // compile each file and compare with expected result
        for (File specFile : fileList) {
            String specFileName = specFile.getName();
            try {
                MMultiLevelModel multi_level_model = MLMTestUtil.getInstance().compileMLMSpecification(specFile, newErr);
                File failFile = MLMTestUtil.getInstance().getFailFileFromUseFile(TEST_PATH, specFileName);

                if (failFile.exists()) {
                    if (multi_level_model != null) {
                        MLMTestUtil.getInstance().failCompileSpecSucceededButErrorsExpected(specFileName, failFile);
                    } else {
                        if (!MLMTestUtil.getInstance().isErrorMessageAsExpected(failFile, errStr)) {
                            MLMTestUtil.getInstance().failCompileSpecFailedFailFileDiffers(specFileName, errStr, failFile);
                        }
                    }
                } else {
                    if (multi_level_model == null) {
                        MLMTestUtil.getInstance().failCompileSpecFailedWithoutFailFile(specFileName, errStr, failFile);
                    }
                }
                if (VERBOSE) {
                    System.out.println(specFileName + ": PASSED.");
                }
                errStr.reset();
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Testing default clabject attribute inheritance
     * because the clabject is empty all the attributes are inherited
     */
    public void test_Clabject_default_attribute_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Clabject_default_attributes_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should inherit all the attributes from D
        MLMTestUtil.getInstance().assertAttributesEqual("M2", "C", Map.of("attr2", "String","attr1","String"), mlmResult);

    }

    /**
     * Testing attribute removal in clabject
     */
    public void test_Clabject_attribute_removal_Spec() {
        File mlmFile = new File(TEST_PATH + "/Clabject_attribute_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should NOT inherit the removed attribute from D
        MLMTestUtil.getInstance().assertAttributesEqual("M2", "C", Map.of("attr2", "String"), mlmResult);
    }

    public void test_Clabject_attribute_renaming_Spec() {
        File mlmFile = new File(TEST_PATH + "/Clabject_attribute_renaming.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should inherit the renamed attribute from D
        MLMTestUtil.getInstance().assertAttributesEqual("M2", "C", Map.of("attr2", "String", "attr3", "Integer"), mlmResult);
    }

    public void test_Assoclink_inheritance_overrides_Spec() {
        File mlmFile = new File(TEST_PATH + "/Assoclink_inheritance_overrides.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMTestUtil.getInstance().assertRolesEqual("M2", "C", Map.of("r", "M2@F"), mlmResult);
    }

    public void test_Default_role_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Default_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("M2", "C", Map.of("ff1","M2@F","r","M1@E"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("M2", "F", Map.of("cc1", "M2@C","dd1","M1@D"), mlmResult);
    }

    public void test_Duplicated_role_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Duplicated_role_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("M2", "C", Map.of("r","M2@F"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("M2", "F", Map.of("cc1","M2@C"), mlmResult);

    }

    public void test_Role_removing_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("M2", "C", Map.of("dd1","M2@D"), mlmResult );
        MLMTestUtil.getInstance().assertRolesEqual("M2", "D", Map.of("cc1","M2@C", "aa1","M1@A" ), mlmResult);

    }

    public void test_Role_removing_inheritance_link_creation_should_fail() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");

            systemApi.createLink("M2@assoc2", "c1", "d1");

            systemApi.createLink("M1@assoc1", "c1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M2@C", e.getMessage() + " " + e.getCause().getMessage());
        }
    }

    public void test_Role_removing_inheritance_link_creation_should_fail_2() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");

            systemApi.createLink("M2@assoc2", "c1", "d1");

            systemApi.createLink("M1@assoc1", "c1", "d1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M2@C", e.getMessage() + " " + e.getCause().getMessage());
        }
    }

    public void test_Role_removing_inheritance_3_levels_Spec() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance_3_levels.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("M2", "C", Map.of("dd1","M2@D"), mlmResult );
        MLMTestUtil.getInstance().assertRolesEqual("M2", "D", Map.of("cc1","M2@C", "aa1","M1@A" ), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("M3", "E", Map.of("ff1","M3@F"), mlmResult );
        MLMTestUtil.getInstance().assertRolesEqual("M3", "F", Map.of("ee1","M3@E"), mlmResult);

    }

    public void test_Role_removing_inheritance_3_levels_link_creation_should_fail() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance_3_levels.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);

        try {
            systemApi.createObject("M1@A", "a1");
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");
            systemApi.createObject("M3@E", "e1");
            systemApi.createObject("M3@F", "f1");

            systemApi.createLink("M1@assoc1", "a1", "b1");
            systemApi.createLink("M2@assoc2", "c1", "d1");
            systemApi.createLink("M1@assoc1", "a1", "d1");
            systemApi.createLink("M3@assoc3", "e1", "f1");
        }
        catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }


        try {
            systemApi.createLink("M1@assoc1", "e1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

        try {
            systemApi.createLink("M2@assoc2", "e1", "d1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role dd1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

        try {
            systemApi.createLink("M1@assoc1", "e1", "d1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

        try {
            systemApi.createLink("M1@assoc1", "a1", "f1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role aa1 is not accessible from class M3@F", e.getMessage() + " " + e.getCause().getMessage());
        }

        try {
            systemApi.createLink("M2@assoc2", "c1", "f1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role cc1 is not accessible from class M3@F", e.getMessage() + " " + e.getCause().getMessage());
        }
    }

    public void test_Assoclink_inheritance_link_creation_should_fail() {
        File mlmFile = new File(TEST_PATH + "/Assoclink_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@A", "a1");
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");

            systemApi.createLink("M2@assoc2", "c1", "d1");
            systemApi.createLink("M1@assoc1", "a1", "b1");

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }

        try {
            systemApi.createLink("M1@assoc1", "c1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M2@C", e.getMessage() + " " + e.getCause().getMessage());
        }

        try{
            systemApi.createLink("M1@assoc1", "a1", "d1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role aa1 is not accessible from class M2@D", e.getMessage() + " " + e.getCause().getMessage());
        }

    }

    public void test_Assoclink_inheritance_3_levels_link_creation_should_fail() {
        File mlmFile = new File(TEST_PATH + "/Assoclink_inheritance_3_levels.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@A", "a1");
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");
            systemApi.createObject("M3@E", "e1");
            systemApi.createObject("M3@F", "f1");

            systemApi.createLink("M1@assoc1", "a1", "b1");
            systemApi.createLink("M2@assoc2", "c1", "d1");
            systemApi.createLink("M3@assoc3", "e1", "f1");

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }

        try {
            systemApi.createLink("M1@assoc1", "c1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M2@C", e.getMessage() + " " + e.getCause().getMessage());
        }

        try{
            systemApi.createLink("M1@assoc1", "e1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

        try{
            systemApi.createLink("M1@assoc1", "e1", "d1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

        try{
            systemApi.createLink("M2@assoc2", "e1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role dd1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

        try{
            systemApi.createLink("M2@assoc2", "e1", "d1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role dd1 is not accessible from class M3@E", e.getMessage() + " " + e.getCause().getMessage());
        }

    }

    public void test_Assoclink_same_role_name_link_creation_should_fail() {
        File mlmFile = new File(TEST_PATH + "/Assoclink_same_role_names.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@A", "a1");
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");
        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }

        try {
            systemApi.createLink("M1@assoc1", "c1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class M2@C", e.getMessage() + " " + e.getCause().getMessage());
        }

    }


    public void test_assoclink_link_creation_should_fail() {
        File mlmFile = new File(TEST_PATH + "/clabject_with_assoclink.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("AB@B", "b1");
            systemApi.createObject("CD@C", "c1");

            systemApi.createLink("AB@ab1", "c1", "b1");
            fail("Link creation should fail");
        } catch (Exception e) {
            assertEquals("Link creation failed! Role bb1 is not accessible from class CD@C", e.getMessage() + " " + e.getCause().getMessage());
        }
    }

    public void test_Role_removing_check_state() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@A", "a1");
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");
            systemApi.createLink("M1@assoc1", "a1", "b1");
            systemApi.createLink("M1@assoc1", "a1", "d1");
            systemApi.createLink("M2@assoc2", "c1", "d1");
        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }

        assertFalse(systemApi.checkState());
    }

//    public void test_Role_removing_3_levels_check_state() {
//        //TODO: i think this test cant be satisfied -- revisit this
//        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance_3_levels.use");
//        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
//
//        MLMSystem mlmSystem = new MLMSystem(mlmResult);
//        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
//        try {
//            systemApi.createObject("M1@A", "a1");
//            systemApi.createObject("M1@B", "b1");
//            systemApi.createObject("M2@C", "c1");
//            systemApi.createObject("M2@D", "d1");
//            systemApi.createObject("M3@E", "e1");
//            systemApi.createObject("M3@F", "f1");
//            systemApi.createLink("M1@assoc1", "a1", "b1");
//            systemApi.createLink("M1@assoc1", "a1", "d1");
//            systemApi.createLink("M2@assoc2", "c1", "d1");
//            systemApi.createLink("M3@assoc3", "e1", "f1");
//        } catch (Exception e) {
//            fail("Objects and links creation setup should not fail");
//        }
//
//        assertTrue(systemApi.checkState());
//    }

    public void test_assoclink_check_state() {
        File mlmFile = new File(TEST_PATH + "/Assoclink_inheritance.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("M1@A", "a1");
            systemApi.createObject("M1@B", "b1");
            systemApi.createObject("M2@C", "c1");
            systemApi.createObject("M2@D", "d1");
            systemApi.createLink("M1@assoc1", "a1", "b1");
            systemApi.createLink("M2@assoc2", "c1", "d1");
        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }

        assertTrue(systemApi.checkState());
    }

    public void test_constraint_removal() {
        File mlmFile = new File(TEST_PATH + "/constraint_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("AB@B", "b1");
            systemApi.createObject("CD@C", "c1");
            systemApi.createLink("AB@ab1", "c1", "b1");

            systemApi.setAttributeValue("b1", "b1", "4");
            assertTrue(systemApi.checkState());

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

    public void test_constraint_removal_and_attribute_removal() {
        File mlmFile = new File(TEST_PATH + "/constraint_removal_and_attribute_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("AB@A", "a1");
            systemApi.setAttributeValue("a1", "a1", "4");
            assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("a1", "a1", "6");
            assertTrue(systemApi.checkState());

            systemApi.createObject("CD@C", "c1");

            assertTrue(systemApi.checkState(new PrintWriter(System.out)));

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

    public void test_constraint_removal_and_attribute_renaming() {
        File mlmFile = new File(TEST_PATH + "/constraint_removal_with_attribute_renaming.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("AB@A", "a1");
            systemApi.setAttributeValue("a1", "a1", "4");
            assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("a1", "a1", "6");
            assertTrue(systemApi.checkState());

            systemApi.createObject("CD@C", "c1");
            systemApi.setAttributeValue("c1", "a3", "4");
            assertTrue(systemApi.checkState());

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

    public void test_constraint_removal_and_role_removal() {
        File mlmFile = new File(TEST_PATH + "/constraint_removal_and_role_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("AB@A", "a1");
            systemApi.createObject("AB@B", "b1");
            systemApi.createLink("AB@ab1", "a1", "b1");
            systemApi.setAttributeValue("b1", "b1", "4");
            assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b1", "b1", "6");
            assertTrue(systemApi.checkState());

            systemApi.createObject("CD@C", "c1");
            assertTrue(systemApi.checkState());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    public void test_constraint_opposite_powerclass_with_role_and_attribute_removal() {
        File mlmFile = new File(TEST_PATH + "/constraint_opposite_powerclass_with_role_and_attribute_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("AB@A", "a1");
            systemApi.createObject("CD@D", "d1");

            assertTrue(systemApi.checkState(new PrintWriter(System.out)));

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

}
