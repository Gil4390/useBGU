package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.config.Options;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;
import org.tzi.use.util.SuffixFileFilter;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;

public class USECompilerMLMClabject extends TestCase {
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

    public void testCompile_mlm1_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/Clabject_default_inheritance_1-1.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();
            //class D should inherit all the attributes from C
            Set<String> classD_Attributes = mlmResult.getClass("M1", "D").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("attr1")), classD_Attributes);
            Set<String> classC_Attributes = mlmResult.getClass("M2", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("attr1", "attr2")), classC_Attributes);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_Assoclink_inheritance_overrides_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/rolesInheritance/Assoclink_inheritance_overrides_2-1.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();
            assertRolesEqual("M2", "C", List.of("r"), mlmResult);
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_Default_role_inheritance_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/rolesInheritance/Default_inheritance_2-2.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();
            assertRolesEqual("M2", "C", List.of("ff1","r"), mlmResult);
            assertRolesEqual("M2", "F", List.of("cc1","dd1"), mlmResult);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_Duplicated_role_inheritance_2_3_c_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/rolesInheritance/Duplicated_role_inheritance_2-3-c.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();
            assertRolesEqual("M2", "C", List.of("r"), mlmResult);
            assertRolesEqual("M2", "F", List.of("cc1"), mlmResult);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_Duplicated_role_inheritance_2_3_e_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/rolesInheritance/Duplicated_role_inheritance_2-3-e.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    //TODO: bug should be fixed -- throws an error although there is renaming
    public void testCompile_Role_renaming_inheritance_2_4_a_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/rolesInheritance/Role_renaming_inheritance_2-4-a.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();
//            assertRolesEqual("M2", "C", List.of("r"), mlmResult);
//            assertRolesEqual("M2", "F", List.of("cc1"), mlmResult);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_Role_removing_inheritance_2_5_a_Specification() {
        MMultiLevelModel mlmResult = null;
        File multiFile = new File(TEST_PATH + "/rolesInheritance/Role_removing_inheritance_2-5-a.use");

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), new PrintWriter(System.out), new MultiLevelModelFactory());
            specStream1.close();
            assertRolesEqual("M2", "C", List.of("r"), mlmResult);
            assertRolesEqual("M2", "F", List.of("cc1","dd1"), mlmResult);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    private void assertRolesEqual(String modelName, String className, List<String> expectedRoles, MMultiLevelModel mlmResult) {
        Set<String> actualRoles = mlmResult.getClass(modelName, className).navigableEnds().keySet();
        assertEquals(new HashSet<>(expectedRoles), actualRoles);
    }

}
