package org.tzi.use.parser;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseMultiModelApi;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class USECompilerMultiTest extends TestCase {
    // Set this to true to see more details about what is tested.
    private static final boolean VERBOSE = false;

    private static File TEST_PATH;
    private static File TEST_PATH_PAPER;
    private static File TEST_PATH_REGULAR;
    private static File EXAMPLES_PATH;
    private static File TEST_EXPR_FILE;

    static {
        try {
            TEST_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/multiParser").toURI());
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

    public void testMultiSpecification() {
        Options.explicitVariableDeclarations = false;

        List<File> fileList = getFilesMatchingSuffix(".use", 24);
        // add all the example files which should have no errors
        File[] files = EXAMPLES_PATH.listFiles( new SuffixFileFilter(".use") );
        assertNotNull(files);
        fileList.addAll(Arrays.asList(files));

        // create a new stream for capturing output on stderr
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);
        // compile each file and compare with expected result
        for (File specFile : fileList) {
            String specFileName = specFile.getName();
            try {
                MMultiModel multi_model = compileMultiSpecification(specFile, newErr);
                File failFile = getFailFileFromUseFile(specFileName);

                if (failFile.exists()) {
                    if (multi_model != null) {
                        failCompileSpecSucceededButErrorsExpected(specFileName, failFile);
                    } else {
                        if (!isErrorMessageAsExpected(failFile, errStr)) {
                            failCompileSpecFailedFailFileDiffers(specFileName, errStr, failFile);
                        }
                    }
                } else {
                    if (multi_model == null) {
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


    public void testExpression() throws IOException {
        MModel model = new ModelFactory().createModel("Test");
        // read expressions and expected results from file
        BufferedReader in = new BufferedReader(new FileReader(TEST_EXPR_FILE));
        int lineNr = 0;

        while (true) {
            String line = in.readLine();
            lineNr++;

            if (line == null) {
                break;
            }
            if (line.startsWith("##")) {
                if (VERBOSE) {
                    System.out.println("testing " + line.substring(2).trim());
                }
                continue;
            }
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }

            String expStr = line;
            while (true) {
                line = in.readLine();
                lineNr++;

                if (line == null) {
                    in.close();
                    throw new RuntimeException("missing result line");
                }
                if (line.startsWith("-> ")) {
                    break;
                }
                expStr += " " + line.trim();
            }
            String resultStr = line.substring(3);

            if (VERBOSE) {
                System.out.println("expression: " + expStr);
            }

            InputStream stream = new ByteArrayInputStream(expStr.getBytes());

            Expression expr =
                    OCLCompiler.compileExpression(
                            model,
                            stream,
                            TEST_EXPR_FILE.toString(),
                            new PrintWriter(System.err),
                            new VarBindings());
            assertNotNull(expr + " compiles", expr);

            MSystemState systemState = new MSystem(model).state();

            Value val = new Evaluator().eval(expr, systemState);
            assertEquals(TEST_EXPR_FILE + ":" + lineNr + " evaluate: " + expStr, resultStr, val.toStringWithType());
        }

        in.close();
    }

    private File getFailFileFromUseFile(String specFileName) {
        // check for a failure file
        String failFileName =
                specFileName.substring(0, specFileName.length() - 4) + ".fail";
        File failFile = new File(TEST_PATH, failFileName);
        return failFile;
    }


    private void failCompileSpecFailedWithoutFailFile(String specFileName, StringOutputStream errStr, File failFile) {
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


    private void failCompileSpecFailedFailFileDiffers(String specFileName, StringOutputStream errStr, File failFile) {
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

    private boolean isErrorMessageAsExpected(File failFile, StringOutputStream errStr) throws FileNotFoundException {
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


    private MModel compileSpecification(File specFile, PrintWriter newErr) throws FileNotFoundException {
        MModel result = null;

        try (FileInputStream specStream = new FileInputStream(specFile)){
            result = USECompiler.compileSpecification(specStream,
                    specFile.getName(), newErr, new ModelFactory());
            specStream.close();
        } catch (IOException e) {
            // This can be ignored
            e.printStackTrace();
        }

        return result;
    }

    private MMultiModel compileMultiSpecification(File specFile, PrintWriter newErr) throws FileNotFoundException {
        MMultiModel result = null;

        try (FileInputStream specStream = new FileInputStream(specFile)){
            result = USECompilerMulti.compileMultiSpecification(specStream,
                    specFile.getName(), newErr, new MultiModelFactory());
            specStream.close();
        } catch (IOException e) {
            // This can be ignored
            e.printStackTrace();
        }

        return result;
    }

    public void testCompileMultiAddModelSpecification() {
        MMultiModel multiModelResult = null;
        MModel modelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_add_model.use");
        File modelFile = new File(TEST_PATH_REGULAR + "/t4.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();


            try (FileInputStream specStream2 = new FileInputStream(modelFile)){
                modelResult = USECompiler.compileSpecification(specStream2,
                        modelFile.getName(), newErr, new ModelFactory());
                specStream2.close();
                multiModelResult.addModel(modelResult);
            }

            assertEquals(multiModelResult.models().size(), 3);
            assertEquals(((MModel)multiModelResult.models().toArray()[2]).name(), modelResult.name());


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
        }
    }


    public void testCompileMultiRemoveModelSpecification1() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_add_model.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            multiModelResult.removeModel("model1");

            assertEquals(multiModelResult.size(), 1);
            assertEquals(((MModel)multiModelResult.models().toArray()[0]).name(), "model2");


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
        }
    }

    public void testCompileMultiModelInterAssoc() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_assoc.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObjects("model1@Employee", "e1");
            api.createObjects("model2@Student", "s1");
            api.setAttributeValue("e1","salary","50");
            api.setAttributeValue("s1","grade","60");
            assertFalse(api.checkState());

            api.createLink("Job","e1","s1");

            assertTrue(api.checkState());
            assertEquals(2, api.getSystem().state().numObjects());
            assertEquals(1, api.getSystem().state().allLinks().size());


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
        }
    }

    public void testCompileMultiModelInterAssoc2() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_assoc.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObjects("model1@Employee", "e1");
            api.createObjects("model2@Student", "s1");

            api.createLink("Job","e1","s1");
            api.createLink("Job","e1","s1");

            fail("double link creation");


        } catch (Exception e) {
            // This can be ignored
            assertEquals(e.getMessage(), "Link creation failed!");
        }
    }

    public void testCompileMultiModelInterClass1() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_class_1.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObjects("A", "x1");
            api.createObjects("B", "y1");
            api.createObjects("model1@A", "x2");
            api.createObjects("model2@B", "y2");

            api.createLink("A1","x1","x2");
            api.createLink("A2","x2","x1");
            api.createLink("A3","x1","y1");

            assertFalse(api.checkState());

            api.setAttributeValue("x2","number","6");
            api.setAttributeValue("y1","salary","60");

            assertTrue(api.checkState());


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testCompileMultiModelInterClass2() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_class_2.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObjects("A","a1","a2","a3");
            api.createLink("A1","a1","a2");
            api.createLink("A1","a2","a3");
            api.setAttributeValue("a1","salary","100");
            api.setAttributeValue("a2","salary","80");
            api.setAttributeValue("a3","salary","90");

            assertFalse(api.checkState());

            api.setAttributeValue("a3","salary","70");

            assertTrue(api.checkState(new PrintWriter(System.out)));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testCompileMultiModelInterAssocClass1() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_association_class_1.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObject("model1@A","a1");
            api.createObject("model1@A","a2");
            api.createObject("model2@C","c1");
            api.createObject("E","e1");
            api.createObject("F","f1");


            api.createLinkObject("A","ac1","a1","c1");
            api.createLinkObject("B","ac2","e1","f1");
            api.createLinkObject("C","ac3","a1","e1");

            assertFalse(api.checkState());

            api.createLinkObject("C","ac4","a2","e1");

            assertTrue(api.checkState());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testCompileMultiModelInvariant() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_const.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObjects("model1@Employee", "e1");
            api.createObjects("model2@Student", "s1");
            api.createObjects("model2@Student", "s2");
            api.createLink("Job","e1","s1");
            api.createLink("Job","e1","s2");

            api.setAttributeValue("e1","salary","100");
            api.setAttributeValue("s1","salary","50");
            api.setAttributeValue("s1","grade","95");
            api.setAttributeValue("s2","salary","70");
            api.setAttributeValue("s2","grade","95");

            assertTrue(api.checkState());

            api.setAttributeValue("e1","salary","45");

            assertFalse(api.checkState());

        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
        }
    }

    public void testCompileMultiModelInvariant2() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_complex.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObjects("model1@Employee", "e1");
            api.createObjects("model1@Company", "c1");
            api.createObjects("model2@Student", "s1");
            api.createObjects("model2@Student", "s2");
            api.createObjects("model2@School", "school1");
            api.createObjects("model2@School", "school2");


            api.setAttributeValue("e1","salary","100");
            api.setAttributeValue("s1","grade","95");
            api.setAttributeValue("s1","salary","50");
            api.setAttributeValue("s2","grade","95");
            api.setAttributeValue("s2","salary","50");

            api.createLink("model2@Studies","s1","school1");
            api.createLink("model2@Studies","s2","school2");

            api.createLink("Graduates","e1","school1");
            api.createLink("Job","e1","s2");
            api.createLink("Job","e1","s1");

            assertFalse(api.checkState());

            api.deleteLink("model2@Studies", new String[]{"s2", "school2"});
            api.createLink("model2@Studies","s2","school1");

            assertTrue(api.checkState());


        } catch (Exception e) {
            // This can be ignored
            fail(e.getMessage());
        }
    }

    public void testCompileMultiModelEmployeesFile() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/Employees.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);




        } catch (Exception e) {
            // This can be ignored
            fail(e.getMessage());
        }
    }

    public void testCompileMultiRemoveModelSpecification2() {
        MMultiModel multiModelResult = null;
        MModel modelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_add_model.use");
        File modelFile = new File(TEST_PATH_REGULAR + "/t4.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();


            try (FileInputStream specStream2 = new FileInputStream(modelFile)){
                modelResult = USECompiler.compileSpecification(specStream2,
                        modelFile.getName(), newErr, new ModelFactory());
                specStream2.close();
                multiModelResult.addModel(modelResult);
            }

            assertEquals(multiModelResult.models().size(), 3);
            assertEquals(((MModel)multiModelResult.models().toArray()[2]).name(), modelResult.name());

            multiModelResult.removeModel("model1");

            assertEquals(multiModelResult.size(), 2);
            assertEquals(((MModel)multiModelResult.models().toArray()[0]).name(), "model2");

            multiModelResult.removeModel("model2");

            assertEquals(multiModelResult.size(), 1);
            assertEquals(((MModel)multiModelResult.models().toArray()[0]).name(), modelResult.name());


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
        }
    }


    public void testCompileMultiModelOperations() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_operations.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

        } catch (Exception e) {
            // This can be ignored
            fail(e.getMessage());
        }
    }

    public void testCompileMultiModelEnums() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH + "/multi_inter_Enum.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            UseMultiModelApi multiApi = new UseMultiModelApi(multiModelResult);
            UseSystemApiUndoable api = new UseSystemApiUndoable(multiApi);

            api.createObject("model1@A","a1");
            api.createObject("model2@B","b1");
            api.createObject("A","a2");

            assertFalse(api.checkState());

            api.setAttributeValue("a1", "c", "model1@Color::red");
            api.setAttributeValue("b1", "c", "model2@Color::green");

            api.setAttributeValue("a2", "c1", "model1@Color::red");
            api.setAttributeValue("a2", "c2", "model2@Color::yellow");
            api.setAttributeValue("a2", "c3", "Color::black");

            assertTrue(api.checkState());

        } catch (Exception e) {
            // This can be ignored
            fail(e.getMessage());
        }
    }

    public void testCompile_multi_figure3_Specification() {
        MMultiModel multiModelResult = null;

        File multiFile = new File(TEST_PATH_PAPER + "/multi-model-figure-3-test.use");
        StringOutputStream errStr = new StringOutputStream();
        PrintWriter newErr = new PrintWriter(errStr);

        try (FileInputStream specStream1 = new FileInputStream(multiFile)){
            multiModelResult = USECompilerMulti.compileMultiSpecification(specStream1,
                    multiFile.getName(), newErr, new MultiModelFactory());
            specStream1.close();

            MSystem system = new MSystem( multiModelResult );

            UseSystemApi systemApi = UseSystemApi.create(system, false);

            boolean res = systemApi.checkState();
             systemApi.checkState();


        } catch (Exception e) {
            // This can be ignored
            e.printStackTrace();
            fail("Unexpected exception");
        }
    }

}
