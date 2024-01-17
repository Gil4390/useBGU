package org.tzi.use.parser;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseMultiModelApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.parser.shell.ShellCommandCompiler;
import org.tzi.use.parser.soil.SoilCompiler;
import org.tzi.use.parser.use.USECompilerMulti;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.util.NullPrintWriter;

import java.io.*;
import java.net.URISyntaxException;

public class SoilCompilerMultiTest extends TestCase {

    private UseSystemApi systemApi;
    private static File TEST_PATH;
    private static File TEST_PATH_REGULAR;
    private static File EXAMPLES_PATH;
    private static File TEST_EXPR_FILE;

    static {
        try {
            TEST_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/multiSoil").toURI());
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

    public void testComplexModel() {
        MModel model = TestModelUtil.getInstance().createComplexModelWithConstraints("model1");
        systemApi = UseSystemApi.create(model, true);

        File soilFile = new File(TEST_PATH + "/Employees.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(line.substring(1).trim());
            }

            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("cs", "budget", "13000");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void testSimpleMultiModel() {
        MMultiModel model = TestMultiModelUtil.getInstance().createMultiModelTwoModels2();
        systemApi = UseSystemApi.create(model, true);

        File soilFile = new File(TEST_PATH + "/SimpleMultiModel.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(line.substring(1).trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(4, systemApi.getSystem().state().numObjects());
        Assert.assertEquals(1, systemApi.getSystem().state().allLinks().size());

    }

    public void testMultiModel_InterObject() {
        MMultiModel model = TestMultiModelUtil.getInstance().createMultiModelWithInterClass();
        systemApi = UseSystemApi.create(model, true);

        File soilFile = new File(TEST_PATH + "/InterClass.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(line.substring(1).trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(5, systemApi.getSystem().state().numObjects());
        Assert.assertEquals(4, systemApi.getSystem().state().allLinks().size());

    }

    public void testComplexMultiModel() {
        MMultiModel model = TestMultiModelUtil.getInstance().createMultiModelInterConstraintComplex2();
        systemApi = UseSystemApi.create(model, true);

        File soilFile = new File(TEST_PATH + "/ComplexMultiModel.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(line.substring(1).trim());
            }

            Assert.assertFalse(systemApi.checkState());
            systemApi.setAttributeValue("m2", "level", "'A'");
            Assert.assertTrue(systemApi.checkState());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void testMultiModel_LinkObjectsInInternalModel() {
        MMultiModel model = TestMultiModelUtil.getInstance().createMultiModelTwoModelsAssociationClass2();
        systemApi = UseSystemApi.create(model, true);

        File soilFile = new File(TEST_PATH + "/AssociationClass.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(line.substring(1).trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(3, systemApi.getSystem().state().numObjects());
        Assert.assertEquals(1, systemApi.getSystem().state().allLinks().size());

    }
    public void testMultiModel_InterLinkObjects() {
        MMultiModel model = TestMultiModelUtil.getInstance().createMultiModelWithInterAssociationClass();
        systemApi = UseSystemApi.create(model, true);

        File soilFile = new File(TEST_PATH + "/InterAssociationClass.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(line.substring(1).trim());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Assert.assertEquals(9, systemApi.getSystem().state().numObjects());
        Assert.assertEquals(4, systemApi.getSystem().state().allLinks().size());

    }


    public void evaluateStatement(String statement) throws MSystemException {
        systemApi.getSystem().execute(generateStatement(statement));
    }

    private MStatement generateStatement(String input) {

        return ShellCommandCompiler.compileShellCommand(
                systemApi.getSystem().model(),
                systemApi.getSystem().state(),
                systemApi.getSystem().getVariableEnvironment(),
                input,
                "<input>",
                new PrintWriter(System.err),
                true);
    }
}