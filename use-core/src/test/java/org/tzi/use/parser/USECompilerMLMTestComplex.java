package org.tzi.use.parser;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMLMApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.config.Options;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.sys.MLMSystem;
import org.tzi.use.util.SuffixFileFilter;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Test USECompilerMLM class. The TestDriver reads all files with extension ".use" from the mlm directory
 *  and processes them with the mlm parser. If a file <code>t.use</code> contains
 *  expected errors there should be a file <code>t.fail</code> with the expected
 *  output messages. There are four possible results:
 *  <ol>
 *    <li> file parses ok and no failure file exists: PASSED.</li>
 *    <li> file parses ok and a failure file exists: FAILURE.</li>
 *    <li> file does not parse and a failure file exists: PASSED if the actual
 *    output matches the expected output from the failure file, otherwise
 *    FAILURE.</li>
 *    <li> file does not parse and no failure file exists: FAILURE.</li>
 *  </ol>
 *
 */
public class USECompilerMLMTestComplex extends TestCase {
    private static final boolean VERBOSE = false;

    private static File TEST_PATH;
    private static File TEST_PATH_SEMINAR;

    static {
        try {
            TEST_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/mlmParser").toURI());
            TEST_PATH_SEMINAR = new File(ClassLoader.getSystemResource("org/tzi/use/mlm_seminar").toURI());
        } catch (NullPointerException | URISyntaxException e) {
            TEST_PATH = null;
            fail("Folders including tests are missing!");
        }
    }
    

    public void testMLMSpecification() {
        Options.explicitVariableDeclarations = false;

        List<File> fileList = MLMTestUtil.getInstance().getFilesMatchingSuffix(TEST_PATH, ".use", 48);
        // add all the example files which should have no errors
        File[] files = TEST_PATH.listFiles( new SuffixFileFilter(".use") );
        assertNotNull(files);
        fileList.addAll(Arrays.asList(files));

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
     * testing basic parsing rules of mlm with empty mediators
     */
    public void test_empty_mediators_Spec() {
        File mlmFile = new File(TEST_PATH + "/empty_mediators.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //empty mediators, classes should behave like in a multi-model
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A", "dd3", "CD@D"), mlmResult);

        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "dd2", "CD@D"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C", "cc2", "CD@C", "bb3", "AB@B"), mlmResult);

    }

    /**
     * testing basic parsing rules of mlm with empty clabjects
     */
    public void test_empty_clabjects_Spec() {
        File mlmFile = new File(TEST_PATH + "/empty_clabjects.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A", "dd3", "CD@D"), mlmResult);

        //classes C & D should inherit the roles and attributes from A & B
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String", "b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "dd2", "CD@D", "bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C", "cc2", "CD@C", "bb3", "AB@B", "aa1", "AB@A", "dd3", "CD@D"), mlmResult);

    }

    /**
     * testing basic parsing rules of attribute removing and renaming
     */
    public void test_attribute_removing_renaming_Spec() {
        File mlmFile = new File(TEST_PATH + "/attribute_removing_renaming.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A", "dd3", "CD@D"), mlmResult);

        //classes C & D should inherit the roles and attributes from A & B with renaming and removing of attributes
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "c1", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String", "b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "dd2", "CD@D", "bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C", "cc2", "CD@C", "bb3", "AB@B", "aa1", "AB@A", "dd3", "CD@D"), mlmResult);}

    /**
     * testing basic parsing rules of role and attribute removing
     */
    public void test_attributes_roles_removing_Spec() {
        File mlmFile = new File(TEST_PATH + "/roles_attributes_removing.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B", "bb2", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A", "aa2", "AB@A", "dd3", "CD@D"), mlmResult);

        //classes C & D should inherit the roles and attributes from A & B with renaming and removing of attributes and roles
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "c1", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String", "b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "dd2", "CD@D", "bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C", "cc2", "CD@C", "bb3", "AB@B", "aa1", "AB@A", "aa2", "AB@A", "dd3", "CD@D"), mlmResult);

    }

    /**
     * testing basic parsing rules of assoclink
     */
    public void test_assoclink_basic_Spec() {
        File mlmFile = new File(TEST_PATH + "/assoclink_basic.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A", "dd3", "CD@D"), mlmResult);

        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "c1", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String", "b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "dd2", "CD@D"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C", "cc2", "CD@C", "bb3", "AB@B", "dd3", "CD@D"), mlmResult);

    }

    /**
     * testing the attribute inheritance of a 3-level MLM.
     */
    public void test_3_levels_empty_clabjects_Spec() {
        File mlmFile = new File(TEST_PATH + "/3_levels_empty_clabjects.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("aa", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("aa", "String", "cc", "Integer"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("EF", "E", Map.of("aa", "String", "cc", "Integer", "ee", "Integer"), mlmResult);

    }

    /**
     * testing the attribute inheritance of a 3-level MLM, when removing and renaming attributes.
     */
    public void test_3_levels_attributes_renaming_removing_Spec() {
        File mlmFile = new File(TEST_PATH + "/3_levels_attributes_renaming_removing.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("aa1", "String", "aa2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("aa3", "String", "cc", "Integer"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("EF", "E", Map.of("aa3", "String", "cc", "Integer", "ee", "Integer"), mlmResult);

    }

    //region Double-Clabject Inheritance
    public void test_double_clabject_Spec() {
        File mlmFile = new File(TEST_PATH + "/double_clabject.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A"), mlmResult);

        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "a1", "String", "a2", "String", "b1", "String", "b2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "aa1", "AB@A", "bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C"), mlmResult);
    }

    public void test_double_clabject_attribute_removing_Spec() {
        File mlmFile = new File(TEST_PATH + "/double_clabject_attribute_removing.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A"), mlmResult);

        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "a2", "String", "b2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "aa1", "AB@A", "bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C"), mlmResult);
    }

    public void test_double_clabject_attribute_renaming_Spec() {
        File mlmFile = new File(TEST_PATH + "/double_clabject_attribute_renaming.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A"), mlmResult);

        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "a3", "String", "a2", "String", "b3", "String", "b2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D", "aa1", "AB@A", "bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C"), mlmResult);
    }

    public void test_double_clabject_role_removing_Spec() {
        File mlmFile = new File(TEST_PATH + "/double_clabject_role_removing.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "B", Map.of("b1", "String", "b2", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("bb1", "AB@B"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("AB", "B", Map.of("aa1", "AB@A"), mlmResult);

        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("c", "String", "a1", "String", "a2", "String", "b1", "String", "b2", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "D", Map.of("d", "String"), mlmResult);

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C"), mlmResult);
    }
    //endregion

    /**
     * when defining a clabject, all the attributes and roles are inherited and are accessible through inter-constraints
     */
    public void test_inter_constraints_with_empty_clabjects_Spec() {
        File mlmFile = new File(TEST_PATH + "/inter_constraints_with_empty_clabjects.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);

        try {
            systemApi.createObject("CD@C", "c1");
            systemApi.createObject("AB@B", "b1");
            systemApi.createLink("AB@ab1", "c1", "b1");

            systemApi.setAttributeValue("c1", "a2", "10");

            systemApi.setAttributeValue("b1", "b1", "'x'");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("b1", "b1", "'b'");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c1", "a2", "0");
            Assert.assertFalse(systemApi.checkState());
        }
        catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }

    }

    /**
     * when renaming an attribute in a clabject, the attribute should be accessible using the new attribute name through inter-constraints
     */
    public void test_clabject_attribute_renaming_with_inter_constraints_Spec() {
        File mlmFile = new File(TEST_PATH + "/clabject_attribute_renaming_with_inter_constraints.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            systemApi.createObject("CD@C", "c1");
            systemApi.createObject("AB@B", "b1");
            systemApi.createLink("AB@ab1", "c1", "b1");

            systemApi.setAttributeValue("c1", "c5", "3");

            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("c1", "c5", "10");
            Assert.assertTrue(systemApi.checkState());
        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

    /**
     * Attribute renaming in 3-level MLM, each renamed attribute should only be accessible in its corresponding level
     */
    public void test_3_levels_attribute_renaming_inter_constraints_Spec() {
        File mlmFile = new File(TEST_PATH + "/3_levels_attribute_renaming_inter_constraints.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        MLMTestUtil.getInstance().assertAttributesEqual("AB", "A", Map.of("a1", "String", "a2", "Integer"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("CD", "C", Map.of("a2", "Integer", "c", "String", "c5", "String"), mlmResult);
        MLMTestUtil.getInstance().assertAttributesEqual("EF", "E", Map.of("a2", "Integer", "c", "String", "e", "String", "e9", "String"), mlmResult);

    }

    /**
     * defining assoclink between inherited classes
     */
    public void test_assoclink_between_inherited_classes_Spec() {
        File mlmFile = new File(TEST_PATH + "/assoclink_between_inherited_classes.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C"), mlmResult);
    }

    /**
     * self-association in the upper level turns to an association in the lower level through assoclink
     */
    public void test_assoclink_self_assoc_Spec() {
        File mlmFile = new File(TEST_PATH + "/assoclink_self_assoc.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("AB", "A", Map.of("aaa1", "AB@A", "aaa2", "AB@A"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "C", Map.of("dd1", "CD@D"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("CD", "D", Map.of("cc1", "CD@C"), mlmResult);

    }

    /**
     * when removing an attribute in a clabject, the attribute shouldn’t be accessible at the current level.
     */
    public void testCompile_mlm23_clabject_attribute_removing_Specification() {
        File mlmFile = new File(TEST_PATH + "/mlm23.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        UseMLMApi api = new UseMLMApi(mlmResult);
        try{
            System.out.println(api.getClassSafe("CD@C").allAttributes().toString());
            assertFalse(api.getClassSafe("CD@C").allAttributes().stream().anyMatch(a -> a.name().equals("a1")));
        }
        catch (Exception e){
            fail("test should not fail");
        }
    }

    /**
     * when defining an assoclink, all parent roles related to that assoclink are not accessible using the parent end name
     * instead hey should be accessible using the child end name
     */
    public void testCompile_mlm21_assoclink_role_not_accessible_Specification() {
        File mlmFile = new File(TEST_PATH + "/mlm21.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        UseMLMApi api = new UseMLMApi(mlmResult);
        try{
            System.out.println(api.getClassSafe("CD@C").navigableEnds().toString());
            assertFalse(api.getClassSafe("CD@C").navigableEnds().containsKey("bb1"));
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    /**
     * when defining two assoclinks in two separate levels, all parent roles are inaccessible on both levels.
     */
    public void test_3_levels_assoclink_roles_should_be_accessible_on_correct_levels_Spec() {
        File mlmFile = new File(TEST_PATH + "/3_levels_assoclink.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        UseMLMApi api = new UseMLMApi(mlmResult);
        try{
            assertFalse(api.getClassSafe("CD@C").navigableEnds().containsKey("bb1"));
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
            assertFalse(api.getClassSafe("EF@E").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    /**
     * when defining an assoclink, that is related to a deleted role, the assoclink overrules.
     */
    public void test_assoclink_overrules_role_removing_Spec() {
        File mlmFile = new File(TEST_PATH + "/assoclink_overrules_role_removing.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        UseMLMApi api = new UseMLMApi(mlmResult);
        try{
            assertFalse(api.getClassSafe("CD@C").navigableEnds().containsKey("bb1"));
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    /**
     *
     */
    public void test_role_removing_solves_self_inheritance_satisfiability_Spec() {
        File mlmFile = new File(TEST_PATH + "/role_removing_solves_self_inheritance_satisfiability.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);

        try{
            systemApi.createObject("AB@B", "b1");
            systemApi.createObject("CD@D", "d1");
            systemApi.createLink("bd1", "b1", "d1");

            Assert.assertTrue(systemApi.checkState());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    /**
     * when defining an assoclink, inherited classes shouldn’t have access to parent roles.
     */
    public void test_assoclink_removes_access_to_old_role_names_Spec() {
        File mlmFile = new File(TEST_PATH + "/assoclink_removes_access_to_old_role_names.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("CD", "D1", Map.of("cc1", "CD@C"), mlmResult);
    }

    /**
     * when defining an assoclink and role removing to that assoclink in a 3-Level MLM, roles shouldn’t be accessible.
     */
    public void test_3_level_assoclink_with_role_removal_Spec() {
        File mlmFile = new File(TEST_PATH + "/3_level_assoclink_with_role_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("EF", "E", Map.of("ff1", "EF@F"), mlmResult);
    }


    public void testCompile_mlm_figure_1_test_Specification() {
        File mlmFile = new File(TEST_PATH_SEMINAR + "/mlm-figure-1-test.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertRolesEqual("Computer_product", "Hardware", Map.of("part", "Computer_product@Hardware", "parent", "Computer_product@Hardware", "softw", "Computer_product@Software"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("Computer_product", "Peripheral", Map.of("part", "Computer_product@Hardware", "parent", "Computer_product@Hardware", "softw", "Computer_product@Software"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("Computer_product", "Computer", Map.of("maintained", "PC@PC", "part", "Computer_product@Hardware", "parent", "Computer_product@Hardware", "softw", "Computer_product@Software"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("Computer_product", "Software", Map.of("hardw", "Computer_product@Hardware"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("Computer_product", "System", Map.of("hardw", "Computer_product@Hardware", "appl", "Computer_product@Application"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("Computer_product", "Application", Map.of("installed", "PC@PCOS", "syst", "Computer_product@System", "hardw", "Computer_product@Hardware"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("PC", "PC", Map.of("handler", "Computer_product@Computer", "os", "PC@PCOS", "part", "PC@Device", "softw", "PC@PCAppl", "maintained" , "PC@PC"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("PC", "Device", Map.of("parent", "PC@PC", "softw", "Computer_product@Software"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("PC", "PCAppl", Map.of("hardw", "PC@PC", "syst", "PC@PCOS", "installed", "PC@PCOS"), mlmResult);
        MLMTestUtil.getInstance().assertRolesEqual("PC", "PCOS", Map.of("appl", "PC@PCAppl", "installer", "Computer_product@Application", "pc", "PC@PC"), mlmResult);

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);

        try{
            systemApi.createObject("Computer_product@System", "sys1");
            systemApi.createObject("Computer_product@Application", "app1");
            systemApi.createObject("Computer_product@Computer", "comp1");
            systemApi.createObject("PC@PC", "pc1");
            systemApi.createObject("PC@PCOS", "pcos1");

            systemApi.createLink("Computer_product@compatibility", "sys1", "app1");
            systemApi.createLink("Computer_product@hardwSoftw", "comp1", "sys1");

            systemApi.createLink("installation", "app1", "pcos1");

            systemApi.createLink("PC@pcOs", "pc1", "pcos1");
            systemApi.createLink("connection", "comp1", "pc1");

            // The structure check is false because PC@PC - Computer_Product@Computer association's multiplicity cant be satisfied,
            // and if we remove the role maintained to satisfy it, the inter constraint will fail because maintained is used
            //Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    public void test_multi_2024_challenge_Spec() {
        File mlmFile = new File(TEST_PATH + "/multi-2024-challenge.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);

        MLMTestUtil.getInstance().assertAttributesEqual("Product", "BookCopy", Map.of("currency", "Integer", "SSP", "Integer", "reducedPrice", "Integer"), mlmResult);
        try{
            systemApi.createObject("Product@BookCopy", "bc1");
            systemApi.setAttributeValue("bc1", "currency", "10");
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }


    /**
     * local constraints should be inherited through clabject inheritance like normal inheritance
     */
    public void test_local_constraints_inheritance_with_clabjects_Spec() {
        File mlmFile = new File(TEST_PATH + "/local_constraints_inheritance_with_clabjects.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertInvariantsEqual("M1", "A3", List.of("M1@A1::M1@invA1", "M1@A2::M1@invA2", "M1@A3::M1@invA3"), mlmResult);
        MLMTestUtil.getInstance().assertInvariantsEqual("M2", "B3", List.of("M1@A1::M1@invA1", "M1@A2::M1@invA2", "M1@A3::M1@invA3", "M2@B1::M2@invB1", "M2@B2::M2@invB2", "M2@B3::M2@invB3"), mlmResult);
        MLMTestUtil.getInstance().assertInvariantsEqual("M3", "C3", List.of("M1@A1::M1@invA1", "M1@A2::M1@invA2", "M1@A3::M1@invA3", "M2@B1::M2@invB1", "M2@B2::M2@invB2", "M2@B3::M2@invB3", "M3@C1::M3@invC1", "M3@C2::M3@invC2", "M3@C3::M3@invC3"), mlmResult);

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            // Class M1@A3 invariant checks
            systemApi.createObject("M1@A3", "a3");
            systemApi.setAttributeValue("a3", "attrA1", "3");
            systemApi.setAttributeValue("a3", "attrA2", "3");
            systemApi.setAttributeValue("a3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("a3", "attrA1", "6");
            systemApi.setAttributeValue("a3", "attrA2", "6");
            systemApi.setAttributeValue("a3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

            // Class M2@B3 invariant checks
            systemApi.createObject("M2@B3", "b3");
            systemApi.setAttributeValue("b3", "attrA1", "6");
            systemApi.setAttributeValue("b3", "attrA2", "6");
            systemApi.setAttributeValue("b3", "attrA3", "6");
            systemApi.setAttributeValue("b3", "attrB1", "6");
            systemApi.setAttributeValue("b3", "attrB2", "6");
            systemApi.setAttributeValue("b3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrA1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrA1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrA2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrA2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

            // Class M3@C3 invariant checks
            systemApi.createObject("M3@C3", "c3");
            systemApi.setAttributeValue("c3", "attrA1", "6");
            systemApi.setAttributeValue("c3", "attrA2", "6");
            systemApi.setAttributeValue("c3", "attrA3", "6");
            systemApi.setAttributeValue("c3", "attrB1", "6");
            systemApi.setAttributeValue("c3", "attrB2", "6");
            systemApi.setAttributeValue("c3", "attrB3", "6");
            systemApi.setAttributeValue("c3", "attrC1", "6");
            systemApi.setAttributeValue("c3", "attrC2", "6");
            systemApi.setAttributeValue("c3", "attrC3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrA1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrA1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrA2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrA2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrB1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrB1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrB2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrB2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrB3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

    /**
     * local constraints should be inherited through clabject inheritance like normal inheritance unless removed
     */
    public void test_local_constraints_inheritance_with_clabjects_with_constraint_removal_Spec() {
        File mlmFile = new File(TEST_PATH + "/local_constraints_inheritance_with_clabjects_with_constraint_removal.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertInvariantsEqual("M1", "A3", List.of("M1@A1::M1@invA1", "M1@A2::M1@invA2", "M1@A3::M1@invA3"), mlmResult);
        MLMTestUtil.getInstance().assertInvariantsEqual("M2", "B3", List.of("M1@A1::M1@invA1", "M1@A3::M1@invA3", "M2@B1::M2@invB1", "M2@B2::M2@invB2", "M2@B3::M2@invB3"), mlmResult);
        MLMTestUtil.getInstance().assertInvariantsEqual("M3", "C3", List.of("M1@A1::M1@invA1", "M1@A3::M1@invA3", "M2@B1::M2@invB1", "M2@B3::M2@invB3", "M3@C1::M3@invC1", "M3@C2::M3@invC2", "M3@C3::M3@invC3"), mlmResult);

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            // Class M1@A3 invariant checks
            systemApi.createObject("M1@A3", "a3");
            systemApi.setAttributeValue("a3", "attrA1", "3");
            systemApi.setAttributeValue("a3", "attrA2", "3");
            systemApi.setAttributeValue("a3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("a3", "attrA1", "6");
            systemApi.setAttributeValue("a3", "attrA2", "6");
            systemApi.setAttributeValue("a3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

            // Class M2@B3 invariant checks
            systemApi.createObject("M2@B3", "b3");
            systemApi.setAttributeValue("b3", "attrA1", "6");
            systemApi.setAttributeValue("b3", "attrA3", "6");
            systemApi.setAttributeValue("b3", "attrB1", "6");
            systemApi.setAttributeValue("b3", "attrB2", "6");
            systemApi.setAttributeValue("b3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrA1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrA1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

            // Class M3@C3 invariant checks
            systemApi.createObject("M3@C3", "c3");
            systemApi.setAttributeValue("c3", "attrA1", "6");
            systemApi.setAttributeValue("c3", "attrA3", "6");
            systemApi.setAttributeValue("c3", "attrB1", "6");
            systemApi.setAttributeValue("c3", "attrB3", "6");
            systemApi.setAttributeValue("c3", "attrC1", "6");
            systemApi.setAttributeValue("c3", "attrC2", "6");
            systemApi.setAttributeValue("c3", "attrC3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrA1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrA1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrB1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrB1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrB3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }

    /**
     * inter constraints should not be inherited through clabject inheritance, and should only be inherited in the level in which it's defined
     */
    public void test_inter_constraints_inheritance_with_clabjects_Spec() {
        File mlmFile = new File(TEST_PATH + "/inter_constraints_inheritance_with_clabjects.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMTestUtil.getInstance().assertInvariantsEqual("M1", "A3", List.of("M1@A1::invA1", "M1@A2::invA2", "M1@A3::invA3"), mlmResult);
        MLMTestUtil.getInstance().assertInvariantsEqual("M2", "B3", List.of("M2@B1::invB1", "M2@B2::invB2", "M2@B3::invB3"), mlmResult);
        MLMTestUtil.getInstance().assertInvariantsEqual("M3", "C3", List.of("M3@C1::invC1", "M3@C2::invC2", "M3@C3::invC3"), mlmResult);

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseSystemApi systemApi = new UseSystemApiUndoable(mlmSystem);
        try {
            // Class M1@A3 invariant checks
            systemApi.createObject("M1@A3", "a3");
            systemApi.setAttributeValue("a3", "attrA1", "3");
            systemApi.setAttributeValue("a3", "attrA2", "3");
            systemApi.setAttributeValue("a3", "attrA3", "3");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("a3", "attrA1", "6");
            systemApi.setAttributeValue("a3", "attrA2", "6");
            systemApi.setAttributeValue("a3", "attrA3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

            // Class M2@B3 invariant checks
            systemApi.createObject("M2@B3", "b3");
            systemApi.setAttributeValue("b3", "attrA1", "3");
            systemApi.setAttributeValue("b3", "attrA2", "3");
            systemApi.setAttributeValue("b3", "attrA3", "3");
            systemApi.setAttributeValue("b3", "attrB1", "6");
            systemApi.setAttributeValue("b3", "attrB2", "6");
            systemApi.setAttributeValue("b3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());


            systemApi.setAttributeValue("b3", "attrB1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("b3", "attrB3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("b3", "attrB3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

            // Class M3@C3 invariant checks
            systemApi.createObject("M3@C3", "c3");
            systemApi.setAttributeValue("c3", "attrA1", "3");
            systemApi.setAttributeValue("c3", "attrA2", "3");
            systemApi.setAttributeValue("c3", "attrA3", "3");
            systemApi.setAttributeValue("c3", "attrB1", "3");
            systemApi.setAttributeValue("c3", "attrB2", "3");
            systemApi.setAttributeValue("c3", "attrB3", "3");
            systemApi.setAttributeValue("c3", "attrC1", "6");
            systemApi.setAttributeValue("c3", "attrC2", "6");
            systemApi.setAttributeValue("c3", "attrC3", "6");
            Assert.assertTrue(systemApi.checkState());


            systemApi.setAttributeValue("c3", "attrC1", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC1", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC2", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC2", "6");
            Assert.assertTrue(systemApi.checkState());

            systemApi.setAttributeValue("c3", "attrC3", "3");
            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("c3", "attrC3", "6");
            Assert.assertTrue(systemApi.checkState());
            // ==================================================================================

        } catch (Exception e) {
            fail("Objects and links creation setup should not fail");
        }
    }


}
