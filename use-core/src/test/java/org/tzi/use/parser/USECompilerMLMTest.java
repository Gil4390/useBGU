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
    private static File TEST_PATH_PAPER;

    private static File TEST_PATH_REGULAR;
    private static File EXAMPLES_PATH;
    private static File TEST_EXPR_FILE;

    static {
        try {
            TEST_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/mlmParser").toURI());
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

        List<File> fileList = getFilesMatchingSuffix(".use", 33);
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


    public void testCompile_mlm8_multipleLevels_1_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm8_multipleLevels_1.use");
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

    public void testCompile_mlm8_multipleLevels_2_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm8_multipleLevels_2.use");
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

    public void testCompile_mlm9_role_renaming_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm9_role_renaming.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);

            System.out.println(api.getClassSafe("CD@C").navigableEnds());


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm9_role_renaming2_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm9_role_renaming2.use");
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


            systemApi.setAttributeValue("b1", "b1", "'x'");
            Assert.assertFalse(systemApi.checkState());

            systemApi.setAttributeValue("b1", "b1", "'b'");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm9_role_renaming3_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm9_role_renaming3.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);

            assertEquals("{bb=bb, bb1=bb1, bb2=bb2}", api.getClassSafe("CD@C").navigableEnds().toString());

            System.out.println(api.getClassSafe("CD@C").navigableEnds());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm9_role_renaming4_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm9_role_renaming4.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);

            assertEquals("{bb1=bb1, bb2=bb2}", api.getClassSafe("CD@C").navigableEnds().toString());

            System.out.println(api.getClassSafe("CD@C").navigableEnds());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm9_role_renaming5_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm9_role_renaming5.use");
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

    public void testCompile_mlm9_role_renaming6_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm9_role_renaming6.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();
            assertEquals("{dd=dd, dd2=bb}", mlmResult.getClass("CD", "C").navigableEnds().toString());
            System.out.println(mlmResult.getClass("CD", "C").navigableEnds());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm10_clabject_role_renaming1_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm10_role_renaming.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);

            assertEquals("{dd=dd, ff=bb}", api.getClassSafe("CD@C").navigableEnds().toString());
            assertEquals("{bb=bb}", api.getClassSafe("AB@A").navigableEnds().toString());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm10_clabject_role_renaming2_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm10_role_renaming2.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);

//            assertEquals("{dd=dd, ff=bb}", api.getClassSafe("CD@C").navigableEnds().toString());
//            assertEquals("{bb=bb}", api.getClassSafe("AB@A").navigableEnds().toString());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_clabject_figure_3_test_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH_PAPER + "/mlm-figure-1-test.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);
            System.out.println(api);
//            assertEquals("{dd=dd, ff=bb}", api.getClassSafe("CD@C").navigableEnds().toString());
//            assertEquals("{bb=bb}", api.getClassSafe("AB@A").navigableEnds().toString());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm_graph_toString() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH_PAPER + "/mlm-figure-1-test.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);


            System.out.println(mlmResult.generalizationGraph().toString());
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

    public void testCompile_mlm20_Specification() {
        MMultiLevelModel mlmResult = null;

        File multiFile = new File(TEST_PATH + "/mlm24.use");
        USECompilerMLMTest.StringOutputStream errStr = new USECompilerMLMTest.StringOutputStream();
        PrintWriter newErr = new PrintWriter(System.out);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            mlmResult = USECompilerMLM.compileMLMSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiLevelModelFactory());
            specStream1.close();

            UseMLMApi api = new UseMLMApi(mlmResult);
        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }


}
