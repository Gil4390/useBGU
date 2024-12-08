package org.tzi.use.parser;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMLMApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.config.Options;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.parser.use.USECompilerMulti;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;
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
public class USECompilerMLMTest extends TestCase {
    private static final boolean VERBOSE = false;

    private static File TEST_PATH;
    private static File TEST_PATH_SEMINAR;
    private static File TEST_PATH_PAPER;

    private static File TEST_PATH_REGULAR;
    private static File EXAMPLES_PATH;
    private static File TEST_EXPR_FILE;

    static {
        try {
            TEST_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/mlmParser").toURI());
            TEST_PATH_SEMINAR = new File(ClassLoader.getSystemResource("org/tzi/use/mlm_seminar").toURI());
            TEST_PATH_PAPER = new File(ClassLoader.getSystemResource("org/tzi/use/mlmPaper").toURI());
            TEST_PATH_REGULAR = new File(ClassLoader.getSystemResource("org/tzi/use/parser").toURI());
            EXAMPLES_PATH = new File(ClassLoader.getSystemResource("examples").toURI());
            TEST_EXPR_FILE = new File(ClassLoader.getSystemResource("org/tzi/use/parser/test_expr.in").toURI());
        } catch (NullPointerException | URISyntaxException e) {
            TEST_PATH = null;
            EXAMPLES_PATH = null;
            TEST_EXPR_FILE = null;
            fail("Folders including tests are missing!");
        }
    }

    // java.io has a StringWriter but we need an OutputStream for
    // System.err
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

    public void testMLMSpecification() {
        Options.explicitVariableDeclarations = false;

        List<File> fileList = getFilesMatchingSuffix(".use", 43);
        // add all the example files which should have no errors
        File[] files = EXAMPLES_PATH.listFiles( new SuffixFileFilter(".use") );
        assertNotNull(files);
        fileList.addAll(Arrays.asList(files));

        // create a new stream for capturing output on stderr
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
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


    private File getFailFileFromUseFile(String specFileName) {
        // check for a failure file
        String failFileName =
                specFileName.substring(0, specFileName.length() - 4) + ".fail";
        File failFile = new File(TEST_PATH, failFileName);
        return failFile;
    }


    private void failCompileSpecFailedWithoutFailFile(String specFileName, USECompilerMLMTest.StringOutputStream errStr, File failFile) {
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


    private void failCompileSpecFailedFailFileDiffers(String specFileName, USECompilerMLMTest.StringOutputStream errStr, File failFile) {
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

    private boolean isErrorMessageAsExpected(File failFile, USECompilerMLMTest.StringOutputStream errStr) throws FileNotFoundException {
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

    private List<File> getFilesMatchingPrefixWithSuffix(String prefix, String suffix, int expectedPrefix, int expectedSuffix) {
        List<File> filteredSuffix = getFilesMatchingSuffix(suffix,expectedSuffix);
        List<File> filteredPrefix = new ArrayList<File>();
        for(File f : filteredSuffix) {
            if(f.getName().startsWith(prefix)) {
                filteredPrefix.add(f);
            }
        }
        assertEquals(
                "make sure that all test files can be found "
                        + " (or update expected number if you have added test files)",
                expectedPrefix,
                filteredPrefix.size());
        return filteredPrefix;
    }


    private MMultiLevelModel compileMLMSpecification(File specFile, PrintWriter newErr) throws FileNotFoundException {
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

    /**
     * testing basic parsing rules of mlm with empty mediators
     */
    public void test_empty_mediators_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/empty_mediators.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            //empty mediators, classes should behave like in a multi-model
            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a1", "a2")), classA_Attributes);
            Set<String> classB_Attributes = mlmResult.getClass("AB", "B").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("b1", "b2")), classB_Attributes);

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb1")), classA_Roles);
            Set<String> classB_Roles = mlmResult.getClass("AB", "B").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("aa1", "dd3")), classB_Roles);

            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("c")), classC_Attributes);
            Set<String> classD_Attributes = mlmResult.getClass("CD", "D").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("d")), classD_Attributes);

            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("dd1", "dd2")), classC_Roles);
            Set<String> classD_Roles = mlmResult.getClass("CD", "D").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1", "cc2", "bb3")), classD_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * testing basic parsing rules of mlm with empty clabjects
     */
    public void test_empty_clabjects_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/empty_clabjects.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a1", "a2")), classA_Attributes);
            Set<String> classB_Attributes = mlmResult.getClass("AB", "B").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("b1", "b2")), classB_Attributes);

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb1")), classA_Roles);
            Set<String> classB_Roles = mlmResult.getClass("AB", "B").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("aa1", "dd3")), classB_Roles);

            //classes C & D should inherit the roles and attributes from A & B
            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("c", "a1", "a2")), classC_Attributes);
            Set<String> classD_Attributes = mlmResult.getClass("CD", "D").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("d", "b1", "b2")), classD_Attributes);

            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("dd1", "dd2", "bb1")), classC_Roles);
            Set<String> classD_Roles = mlmResult.getClass("CD", "D").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1", "cc2", "bb3", "aa1", "dd3")), classD_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * testing basic parsing rules of attribute removing and renaming
     */
    public void test_attribute_removing_renaming_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/attribute_removing_renaming.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a1", "a2")), classA_Attributes);
            Set<String> classB_Attributes = mlmResult.getClass("AB", "B").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("b1", "b2")), classB_Attributes);

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb1")), classA_Roles);
            Set<String> classB_Roles = mlmResult.getClass("AB", "B").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("aa1", "dd3")), classB_Roles);

            //classes C & D should inherit the roles and attributes from A & B with renaming and removing of attributes
            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("c", "c1")), classC_Attributes);
            Set<String> classD_Attributes = mlmResult.getClass("CD", "D").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("d", "b1", "b2")), classD_Attributes);

            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("dd1", "dd2", "bb1")), classC_Roles);
            Set<String> classD_Roles = mlmResult.getClass("CD", "D").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1", "cc2", "bb3", "aa1", "dd3")), classD_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * testing basic parsing rules of role removing and renaming
     */
    public void test_roles_removing_renaming_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/roles_removing_renaming.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a1", "a2")), classA_Attributes);
            Set<String> classB_Attributes = mlmResult.getClass("AB", "B").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("b1", "b2")), classB_Attributes);

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb1", "bb2")), classA_Roles);
            Set<String> classB_Roles = mlmResult.getClass("AB", "B").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("aa1", "aa2", "dd3")), classB_Roles);

            //classes C & D should inherit the roles and attributes from A & B with renaming and removing of attributes and roles
            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("c", "c1")), classC_Attributes);
            Set<String> classD_Attributes = mlmResult.getClass("CD", "D").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("d", "b1", "b2")), classD_Attributes);

            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("dd1", "dd2", "bb3")), classC_Roles);
            Set<String> classD_Roles = mlmResult.getClass("CD", "D").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1", "cc2", "bb3", "aa1", "aa2", "dd3")), classD_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * testing basic parsing rules of assoclink
     */
    public void test_assoclink_basic_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/assoclink_basic.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a1", "a2")), classA_Attributes);
            Set<String> classB_Attributes = mlmResult.getClass("AB", "B").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("b1", "b2")), classB_Attributes);

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb1")), classA_Roles);
            Set<String> classB_Roles = mlmResult.getClass("AB", "B").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("aa1", "dd3")), classB_Roles);

            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("c", "c1")), classC_Attributes);
            Set<String> classD_Attributes = mlmResult.getClass("CD", "D").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("d", "b1", "b2")), classD_Attributes);

            //the association cd1 is defined as assoclink of ab1
            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("dd1", "dd2")), classC_Roles);
            Set<String> classD_Roles = mlmResult.getClass("CD", "D").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1", "cc2", "bb3", "dd3")), classD_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * testing the attribute inheritance of a 3-level MLM.
     */
    public void test_3_levels_empty_clabjects_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/3_levels_empty_clabjects.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("aa")), classA_Attributes);
            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("aa", "cc")), classC_Attributes);
            Set<String> classE_Attributes = mlmResult.getClass("EF", "E").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("aa", "cc", "ee")), classE_Attributes);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * testing the attribute inheritance of a 3-level MLM, when removing and renaming attributes.
     */
    public void test_3_levels_attributes_renaming_removing_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/3_levels_attributes_renaming_removing.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("aa1", "aa2")), classA_Attributes);
            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("aa3", "cc")), classC_Attributes);
            Set<String> classE_Attributes = mlmResult.getClass("EF", "E").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("aa3", "cc", "ee")), classE_Attributes);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when defining a clabject, all the attributes and roles are inherited and are accessible through inter-constraints
     */
    public void test_inter_constraints_with_empty_clabjects_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/inter_constraints_with_empty_clabjects.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseSystemApi systemApi = new UseSystemApiUndoable(mlmResult);
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

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when renaming an attribute in a clabject, the attribute should be accessible using the new attribute name through inter-constraints
     */
    public void test_clabject_attribute_renaming_with_inter_constraints_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/clabject_attribute_renaming_with_inter_constraints.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseSystemApi systemApi = new UseSystemApiUndoable(mlmResult);
            systemApi.createObject("CD@C", "c1");
            systemApi.createObject("AB@B", "b1");
            systemApi.createLink("AB@ab1", "c1", "b1");

            systemApi.setAttributeValue("c1", "c5", "3");

            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("c1", "c5", "10");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when renaming a role in a clabject, the role should be accessible using the new role name through inter-constraints.
     */
    public void testCompile_inter_constraints_with_role_renaming_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/inter_constraints_with_role_renaming.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseSystemApi systemApi = new UseSystemApiUndoable(mlmResult);
            systemApi.createObject("CD@C", "c1");
            systemApi.createObject("AB@B", "b1");
            systemApi.createLink("AB@ab1", "c1", "b1");

            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("b1", "b1", "'b'");

            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * Attribute renaming in 3-level MLM, each renamed attribute should only be accessible in its corresponding level
     */
    public void test_3_levels_attribute_renaming_inter_constraints_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/3_levels_attribute_renaming_inter_constraints.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Attributes = mlmResult.getClass("AB", "A").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a1", "a2")), classA_Attributes);
            Set<String> classC_Attributes = mlmResult.getClass("CD", "C").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a2", "c", "c5")), classC_Attributes);
            Set<String> classE_Attributes = mlmResult.getClass("EF", "E").allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet());
            assertEquals(new HashSet<>(List.of("a2", "c", "e", "e9")), classE_Attributes);


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * Role renaming in 3-level MLM, each renamed role should only be accessible in its corresponding level
     */
    public void test_3_levels_roles_renaming_inter_constraints_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/3_levels_roles_renaming_inter_constraints.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb1")), classA_Roles);

            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb5", "dd1")), classC_Roles);

            Set<String> classE_Roles = mlmResult.getClass("EF", "E").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("bb9", "dd1","ff1")), classE_Roles);


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * defining assoclink between inherited classes
     */
    public void test_assoclink_between_inherited_classes_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/assoclink_between_inherited_classes.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * self-association in the upper level turns to an association in the lower level through assoclink
     */
    public void test_assoclink_self_assoc_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/assoclink_self_assoc.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)) {
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> classA_Roles = mlmResult.getClass("AB", "A").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("aaa1", "aaa2")), classA_Roles);
            Set<String> classC_Roles = mlmResult.getClass("CD", "C").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("dd1")), classC_Roles);
            Set<String> classD_Roles = mlmResult.getClass("CD", "D").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1")), classD_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when removing an attribute in a clabject, the attribute shouldn’t be accessible at the current level.
     */
    public void testCompile_mlm23_clabject_attribute_removing_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm23.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);
            System.out.println(api.getClassSafe("CD@C").allAttributes().toString());
            assertFalse(api.getClassSafe("CD@C").allAttributes().stream().anyMatch(a -> a.name().equals("a1")));
//            assertEquals("[c : String]", api.getClassSafe("CD@C").allAttributes().toString());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when defining an assoclink, all parent roles related to that assoclink are not accessible using the parent end name
     * instead hey should be accessible using the child end name
     */
    public void testCompile_mlm21_assoclink_role_not_accessible_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm21.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);
            System.out.println(api.getClassSafe("CD@C").navigableEnds().toString());
            assertFalse(api.getClassSafe("CD@C").navigableEnds().containsKey("bb1"));
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when defining two assoclinks in two separate levels, all parent roles are inaccessible on both levels.
     */
    public void test_3_levels_assoclink_roles_should_be_accessible_on_correct_levels_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/3_levels_assoclink.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);
            assertFalse(api.getClassSafe("CD@C").navigableEnds().containsKey("bb1"));
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
            assertFalse(api.getClassSafe("EF@E").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when defining an assoclink, that is related to a deleted role, the assoclink overrules.
     */
    public void test_assoclink_overrules_role_removing_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/assoclink_overrules_role_removing.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);
            assertFalse(api.getClassSafe("CD@C").navigableEnds().containsKey("bb1"));
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     *
     */
    public void test_role_removing_solves_self_inheritance_satisfiability_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/role_removing_solves_self_inheritance_satisfiability.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseSystemApi systemApi = new UseSystemApiUndoable(mlmResult);
            systemApi.createObject("AB@B", "b1");
            systemApi.createObject("CD@D", "d1");
            systemApi.createLink("bd1", "b1", "d1");

            Assert.assertTrue(systemApi.checkState(newErr));

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when defining an assoclink, inherited classes shouldn’t have access to parent roles.
     */
    public void test_assoclink_removes_access_to_old_role_names_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/assoclink_removes_access_to_old_role_names.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> class_D1_Roles = mlmResult.getClass("CD", "D1").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("cc1")), class_D1_Roles);
            List<String> class_D1_RolesType = mlmResult.getClass("CD", "D1").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new ArrayList<>(List.of( "CD@C")), class_D1_RolesType);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    /**
     * when defining an assoclink and role removing to that assoclink in a 3-Level MLM, roles shouldn’t be accessible.
     */
    public void test_3_level_assoclink_with_role_removal_Spec() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/3_level_assoclink_with_role_removal.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            Set<String> class_E_Roles = mlmResult.getClass("EF", "E").navigableEnds().keySet();
            assertEquals(new HashSet<>(List.of("ff1")), class_E_Roles);

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }


    public void testCompile_mlm_figure_1_test_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH_SEMINAR + "/mlm-figure-1-test.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();



            Set<String> class_Hardware_Roles = mlmResult.getClass("Computer_product", "Hardware").navigableEnds().keySet();
            List<String> class_Hardware_RolesType = mlmResult.getClass("Computer_product", "Hardware").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("part","parent","softw")), class_Hardware_Roles);
            assertEquals(new ArrayList<>(List.of("Computer_product@Hardware","Computer_product@Hardware","Computer_product@Software")), class_Hardware_RolesType);

            Set<String> class_Peripheral_Roles = mlmResult.getClass("Computer_product", "Peripheral").navigableEnds().keySet();
            List<String> class_Peripheral_RolesType = mlmResult.getClass("Computer_product", "Peripheral").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("part","parent","softw")), class_Peripheral_Roles);
            assertEquals(new ArrayList<>(List.of("Computer_product@Hardware","Computer_product@Hardware","Computer_product@Software")), class_Peripheral_RolesType);

            Set<String> class_Computer_Roles = mlmResult.getClass("Computer_product", "Computer").navigableEnds().keySet();
            List<String> class_Computer_RolesType = mlmResult.getClass("Computer_product", "Computer").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("maintained", "part","parent","softw")), class_Computer_Roles);
            assertEquals(new ArrayList<>(List.of("PC@PC", "Computer_product@Hardware", "Computer_product@Hardware", "Computer_product@Software")), class_Computer_RolesType);

            Set<String> class_Software_Roles = mlmResult.getClass("Computer_product", "Software").navigableEnds().keySet();
            List<String> class_Software_RolesType = mlmResult.getClass("Computer_product", "Software").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("hardw")), class_Software_Roles);
            assertEquals(new ArrayList<>(List.of("Computer_product@Hardware")), class_Software_RolesType);

            Set<String> class_System_Roles = mlmResult.getClass("Computer_product", "System").navigableEnds().keySet();
            List<String> class_System_RolesType = mlmResult.getClass("Computer_product", "System").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("hardw","appl")), class_System_Roles);
            assertEquals(new ArrayList<>(List.of("Computer_product@Application", "Computer_product@Hardware")), class_System_RolesType);

            Set<String> class_Application_Roles = mlmResult.getClass("Computer_product", "Application").navigableEnds().keySet();
            List<String> class_Application_RolesType = mlmResult.getClass("Computer_product", "Application").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("installed","syst","hardw")), class_Application_Roles);
            assertEquals(new ArrayList<>(List.of("Computer_product@Hardware", "PC@PCOS", "Computer_product@System")), class_Application_RolesType);


            Set<String> class_PC_Roles = mlmResult.getClass("PC", "PC").navigableEnds().keySet();
            List<String> class_PC_RolesType = mlmResult.getClass("PC", "PC").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("handler", "os", "part", "softw")), class_PC_Roles);
            assertEquals(new ArrayList<>(List.of("Computer_product@Computer", "PC@PCOS", "PC@Device", "PC@PCAppl")), class_PC_RolesType);

            Set<String> class_Device_Roles = mlmResult.getClass("PC", "Device").navigableEnds().keySet();
            List<String> class_Device_RolesType = mlmResult.getClass("PC", "Device").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("parent","softw")), class_Device_Roles);
            assertEquals(new ArrayList<>(List.of("PC@PC", "Computer_product@Software")), class_Device_RolesType);

            Set<String> class_PCAppl_Roles = mlmResult.getClass("PC", "PCAppl").navigableEnds().keySet();
            List<String> class_PCAppl_RolesType = mlmResult.getClass("PC", "PCAppl").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("hardw","syst","installed")), class_PCAppl_Roles);
            assertEquals(new ArrayList<>(List.of("PC@PC", "PC@PCOS", "PC@PCOS")), class_PCAppl_RolesType);

            Set<String> class_PCOS_Roles = mlmResult.getClass("PC", "PCOS").navigableEnds().keySet();
            List<String> class_PCOS_RolesType = mlmResult.getClass("PC", "PCOS").navigableEnds().values().stream().map(element -> element.cls().name()).collect(Collectors.toList());
            assertEquals(new HashSet<>(List.of("appl", "installer","pc")), class_PCOS_Roles);
            assertEquals(new ArrayList<>(List.of( "PC@PCAppl", "Computer_product@Application", "PC@PC")), class_PCOS_RolesType);


            UseSystemApi systemApi = new UseSystemApiUndoable(mlmResult);
            systemApi.createObject("Computer_product@System", "sys1");
            systemApi.createObject("Computer_product@Application", "app1");
            systemApi.createObject("Computer_product@Computer", "comp1");
            systemApi.createObject("PC@PC", "pc1");
            systemApi.createObject("PC@PCOS", "pcos1");

            systemApi.createLink("Computer_product@compatibility", "sys1", "app1");
            systemApi.createLink("Computer_product@hardwSoftw", "comp1", "sys1");

            systemApi.createLink("Computer_product@compatibility", "pcos1", "app1");
            systemApi.createLink("installation", "app1", "pcos1");

            systemApi.createLink("PC@pcOs", "pc1", "pcos1");
            systemApi.createLink("connection", "comp1", "pc1");

            Assert.assertTrue(systemApi.checkState(newErr));

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }


}
