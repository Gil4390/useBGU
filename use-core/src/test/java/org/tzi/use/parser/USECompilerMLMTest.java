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

        List<File> fileList = getFilesMatchingSuffix(".use", 39);
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


    public void testCompile_mlm1_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm1.use");
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

    public void testCompile_mlm2_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm2.use");
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

    public void testCompile_mlm3_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm3.use");
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

    public void testCompile_mlm3b_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm3b.use");
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


    public void testCompile_mlm4_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm4.use");
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

    public void testCompile_mlm7a_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm7a.use");
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

    public void testCompile_mlm7b_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm7b.use");
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

    public void testCompile_mlm8_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm8.use");
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

    public void testCompile_mlm12_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm12.use");
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


//    public void testCompile_mlm_graph_toString() {
//        MMultiLevelModel mlmResult = null;
//
//        File multiFile = new File(TEST_PATH_PAPER + "/mlm-figure-1-test.use");
//        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
//        PrintWriter newErr = new PrintWriter(System.out);
//
//        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
//            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
//                    multiFile.getName(), newErr, new MultiLevelModelFactory());
//            specStream1.close();
//
//            UseMLMApi api = new UseMLMApi(mlmResult);
//
//
//            System.out.println(mlmResult.generalizationGraph().toString());
//        } catch (Exception e) {
//            // This can be ignored
//            e.printStackTrace();
//            fail("Unexpected exception");
//        }
//    }

    public void testCompile_mlm19_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm19.use");
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
    public void testCompile_mlm20_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm20.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
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
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm21_assoclink_accessible_role_Specification() {
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
            assertTrue(api.getClassSafe("CD@C").navigableEnds().containsKey("dd1"));
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm22_assoclink_three_levels_roles_not_accessible_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm22.use");
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

    public void testCompile_mlm26_assoclink_overrules_roles_deletion_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm26.use");
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

    public void testCompile_mlm27_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm27.use");
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


    public void testCompile_mlm_figure_1_test_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH_SEMINAR + "/mlm-figure-1-test.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

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
