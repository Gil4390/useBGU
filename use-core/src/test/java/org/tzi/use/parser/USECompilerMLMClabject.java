package org.tzi.use.parser;

import junit.framework.TestCase;
import org.assertj.core.api.Assertions;
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

    private MMultiLevelModel compileMLMSpecification(File specFile, PrintWriter newErr) {
        MMultiLevelModel result = null;

        try (FileInputStream specStream = new FileInputStream(specFile)){
            result = USECompilerMLM.compileMLMSpecification(specStream,
                    specFile.getName(), newErr, new MultiLevelModelFactory());
            specStream.close();
        } catch (IOException e) {
            // This can be ignored
            e.printStackTrace();
        }

        return result;
    }

    private Set<String> getAttributes(MMultiLevelModel mlm, String modelName, String ClassName) {
        return mlm.getClass(modelName, ClassName)
                .allAttributes()
                .stream()
                .map(MAttribute::name)
                .collect(Collectors.toSet());
    }
    private Set<String> getRoles(MMultiLevelModel mlm, String modelName, String className) {
        return mlm.getClass(modelName, className).navigableEnds().keySet();
    }

    /**
     * Testing default clabject attribute inheritance
     * because the clabject is empty all the attributes are inherited
     */
    public void testCompile_Clabject_default_inheritance_1_1_Specification() {
        File mlmFile = new File(TEST_PATH + "/Clabject_default_inheritance_1-1.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should inherit all the attributes from D
        Set<String> classC_Attributes = getAttributes(mlmResult, "M2", "C");
        Assertions.assertThat(classC_Attributes).containsExactlyInAnyOrder("attr1", "attr2");
    }

    /**
     * Testing attribute removal in clabject
     */
    public void testCompile_Clabject_attribute_removal_1_3_Specification() {
        File mlmFile = new File(TEST_PATH + "/Clabject_attribute_removal_1-3.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should NOT inherit the removed attribute from D
        Set<String> classC_Attributes = getAttributes(mlmResult, "M2", "C");
        Assertions.assertThat(classC_Attributes).containsExactlyInAnyOrder("attr2");
    }

    public void testCompile_Clabject_attribute_renaming_1_4_Specification() {
        File mlmFile = new File(TEST_PATH + "/Clabject_attribute_renaming_1-4.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should inherit the renamed attribute from D
        Set<String> classC_Attributes = getAttributes(mlmResult, "M2", "C");
        Assertions.assertThat(classC_Attributes).containsExactlyInAnyOrder("attr2", "attr3");
    }

    public void testCompile_Assoclink_inheritance_overrides_Specification() {
        File mlmFile = new File(TEST_PATH + "/rolesInheritance/Assoclink_inheritance_overrides_2-1.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        Set<String> classC_Roles = getRoles(mlmResult, "M2", "C");
        Assertions.assertThat(classC_Roles).containsExactlyInAnyOrder("r");
    }

    public void testCompile_Default_role_inheritance_Specification() {
        File mlmFile = new File(TEST_PATH + "/rolesInheritance/Default_inheritance_2-2.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        Set<String> classC_Roles = getRoles(mlmResult, "M2", "C");
        Assertions.assertThat(classC_Roles).containsExactlyInAnyOrder("ff1", "r");

        Set<String> ClassF_Roles = getRoles(mlmResult, "M2", "F");
        Assertions.assertThat(ClassF_Roles).containsExactlyInAnyOrder("cc1", "dd1");
    }

    public void testCompile_Duplicated_role_inheritance_2_3_c_Specification() {
        File mlmFile = new File(TEST_PATH + "/rolesInheritance/Duplicated_role_inheritance_2-3-c.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        Set<String> classC_Roles = getRoles(mlmResult, "M2", "C");
        Assertions.assertThat(classC_Roles).containsExactlyInAnyOrder("r");

        Set<String> ClassF_Roles = getRoles(mlmResult, "M2", "F");
        Assertions.assertThat(ClassF_Roles).containsExactlyInAnyOrder("cc1");

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
