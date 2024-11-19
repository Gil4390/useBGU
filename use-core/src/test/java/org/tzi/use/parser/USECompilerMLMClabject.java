package org.tzi.use.parser;

import junit.framework.TestCase;
import org.assertj.core.api.Assertions;
import org.tzi.use.config.Options;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MNavigableElement;
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

    private List<File> getFilesMatchingSuffix(String suffix, int expected) {
        List<File> fileList = new ArrayList<File>();
        File dir = new File(TEST_PATH.toURI());
        File[] files = dir.listFiles( new SuffixFileFilter(suffix) );
        assertNotNull(files);
        fileList.addAll(Arrays.asList(files));

        // make sure we don't silently miss the input files
        assertEquals(
                "make sure that all test files can be found "
                        + " (or update expected number if you have added test files)",
                expected,
                fileList.size());

        return fileList;
    }

    private File getFailFileFromUseFile(String specFileName) {
        // check for a failure file
        String failFileName =
                specFileName.substring(0, specFileName.length() - 4) + ".fail";
        File failFile = new File(TEST_PATH, failFileName);
        return failFile;
    }

    private void failCompileSpecFailedWithoutFailFile(String specFileName, USECompilerMLMClabject.StringOutputStream errStr, File failFile) {
        // unexpected failure
        System.err.println("#######################");
        System.err.print(errStr.toString());
        System.err.println("#######################");
        fail(
                "compilation of "
                        + specFileName
                        + " had errors, but there is "
                        + "no file `"
                        + failFile.getName()
                        + "'.");
    }

    private void failCompileSpecFailedFailFileDiffers(String specFileName, USECompilerMLMClabject.StringOutputStream errStr, File failFile) {
        System.err.println("Expected: #############");

        try (BufferedReader failReader = new BufferedReader(new FileReader(failFile))){
            while (true) {
                String line = failReader.readLine();
                if (line == null) {
                    break;
                }
                System.err.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        System.err.println("Got: ##################");
        System.err.print(errStr.toString());
        System.err.println("#######################");
        fail(
                "compilation of "
                        + specFileName
                        + " had errors, "
                        + "but the expected error output differs.");
    }

    private void failCompileSpecSucceededButErrorsExpected(String specFileName, File failFile) {
        fail(
                "compilation of "
                        + specFileName
                        + " succeeded, "
                        + "but errors were expected (see "
                        + failFile
                        + ").");
    }

    private boolean isErrorMessageAsExpected(File failFile, USECompilerMLMClabject.StringOutputStream errStr) throws FileNotFoundException {
        // check whether error output equals expected output
        String[] expect = errStr.toString().split("\n|(\r\n)");
        //                        for (int i = 0; i < expect.length; i++) {
        //                            System.out.println("[" + expect[i] + "]");
        //                        }
        int j = 0;
        boolean ok = true;
        try (BufferedReader failReader = new BufferedReader(new FileReader(failFile))){
            while (ok) {
                String line = failReader.readLine();
                if (line == null) {
                    ok = j == expect.length;
                    break;
                }
                ok = line.equals(expect[j]);
                j++;
            }
        } catch (IOException ex) {
            ok = false;
        } catch (IndexOutOfBoundsException ex) {
            ok = false;
        }
        return ok;
    }

    class StringOutputStream extends OutputStream {
        private StringBuilder fBuffer = new StringBuilder();


        public void write(int b) {
            fBuffer.append((char) b);
        }


        public void reset() {
            fBuffer = new StringBuilder();
        }


        public String toString() {
            return fBuffer.toString();
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

    public void testMLMClabjectSpecification() {
        Options.explicitVariableDeclarations = false;

        List<File> fileList = getFilesMatchingSuffix(".use", 14);

        // create a new stream for capturing output on stderr
        USECompilerMLMClabject.StringOutputStream errStr = new USECompilerMLMClabject.StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);
        // compile each file and compare with expected result
        for (File specFile : fileList) {
            String specFileName = specFile.getName();
            try {
                MMultiLevelModel multi_level_model = compileMLMSpecification(specFile, newErr);
                File failFile = getFailFileFromUseFile(specFileName);

                if (failFile.exists()) {
                    if (multi_level_model != null) {
                        failCompileSpecSucceededButErrorsExpected(specFileName, failFile);
                    } else {
                        if (!isErrorMessageAsExpected(failFile, errStr)) {
                            failCompileSpecFailedFailFileDiffers(specFileName, errStr, failFile);
                        }
                    }
                } else {
                    if (multi_level_model == null) {
                        failCompileSpecFailedWithoutFailFile(specFileName, errStr, failFile);
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

//    private Set<String> getAttributes(MMultiLevelModel mlm, String modelName, String ClassName) {
//        return mlm.getClass(modelName, ClassName)
//                .allAttributes()
//                .stream()
//                .map(MAttribute::name)
//                .collect(Collectors.toSet());
//    }

    private Map<String, String> getAttributes(MMultiLevelModel mlm, String modelName, String className) {
        return mlm.getClass(modelName, className)
                .allAttributes()
                .stream()
                .collect(Collectors.toMap(
                        MAttribute::name,
                        attribute -> attribute.type().toString()
                ));
    }

    private Map<String, String> getRoles(MMultiLevelModel mlm, String modelName, String className) {
        return mlm.getClass(modelName, className)
                .navigableEnds()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().cls().name()
                ));
    }


    private void assertRolesEqual(String modelName, String className ,Map<String,String> expectedRoles, MMultiLevelModel mlmResult ) {
        Map<String, String> actualRoles = getRoles(mlmResult, modelName, className);
        Assertions.assertThat(expectedRoles).containsExactlyInAnyOrderEntriesOf(actualRoles);
    }

    private void assertAttributesEqual(String modelName, String className, Map<String, String> expectedAttributes, MMultiLevelModel mlmResult) {
//        Set<String> actualAttributes = getAttributes(mlmResult, modelName, className);
        Map<String, String> actualAttributes = getAttributes(mlmResult, modelName, className);
        Assertions.assertThat(actualAttributes).containsExactlyInAnyOrderEntriesOf(expectedAttributes);
    }

    /**
     * Testing default clabject attribute inheritance
     * because the clabject is empty all the attributes are inherited
     */
    public void testCompile_Clabject_default_attribute_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Clabject_default_attribute_inheritance.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should inherit all the attributes from D
        assertAttributesEqual("M2", "C", Map.of("attr2", "String","attr1","String"), mlmResult);

    }

    /**
     * Testing attribute removal in clabject
     */
    public void testCompile_Clabject_attribute_removal_Spec() {
        File mlmFile = new File(TEST_PATH + "/Clabject_attribute_removal.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should NOT inherit the removed attribute from D
        assertAttributesEqual("M2", "C", Map.of("attr2", "String"), mlmResult);
    }

    public void testCompile_Clabject_attribute_renaming_Spec() {
        File mlmFile = new File(TEST_PATH + "/Clabject_attribute_renaming.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        //class C should inherit the renamed attribute from D
        assertAttributesEqual("M2", "C", Map.of("attr2", "String", "attr3", "Integer"), mlmResult);
    }

    public void testCompile_Assoclink_inheritance_overrides_Spec() {
        File mlmFile = new File(TEST_PATH + "/Assoclink_inheritance_overrides.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));
        assertRolesEqual("M2", "C", Map.of("r", "M2@F"), mlmResult);
    }

    public void testCompile_Default_role_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Default_inheritance.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        assertRolesEqual("M2", "C", Map.of("ff1","M2@F","r","M1@E"), mlmResult);
        assertRolesEqual("M2", "F", Map.of("cc1", "M2@C","dd1","M1@D"), mlmResult);
    }

    public void testCompile_Duplicated_role_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Duplicated_role_inheritance.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        assertRolesEqual("M2", "C", Map.of("r","M2@F"), mlmResult);
        assertRolesEqual("M2", "F", Map.of("cc1","M2@C"), mlmResult);

    }

    //TODO: bug should be fixed -- throws an error although there is renaming
    public void testCompile_Role_renaming_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Role_renaming_inheritance.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

//            assertRolesEqual("M2", "C", List.of("r"), mlmResult);
//            assertRolesEqual("M2", "F", List.of("cc1"), mlmResult);

    }

    public void testCompile_Role_removing_inheritance_Spec() {
        File mlmFile = new File(TEST_PATH + "/Role_removing_inheritance.use");
        MMultiLevelModel mlmResult = compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        assertRolesEqual("M2", "C", Map.of("r","M2@F"), mlmResult );
        assertRolesEqual("M2", "F", Map.of("cc1","M2@C", "dd1","M1@D" ), mlmResult);

    }

}
