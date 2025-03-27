package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.api.UseMLMSystemApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.parser.shell.ShellCommandCompiler;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.sys.MLMSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.soil.MStatement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;

public class SoilCompilerMLMTest extends TestCase {

    private static File SOIL_PATH;
    private static File MLM_PATH;

    static {
        try {
            SOIL_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/mlmSoil").toURI());
            MLM_PATH = new File(ClassLoader.getSystemResource("org/tzi/use/mlmParser").toURI());
        } catch (NullPointerException | URISyntaxException e) {
            SOIL_PATH = null;
            MLM_PATH = null;
            fail("Folders including tests are missing!");
        }
    }

    public void test_multi_2024_challenge_MLM() {
        File mlmFile = new File(MLM_PATH + "/multi-2024-challenge.use");
        MMultiLevelModel mlmResult = MLMTestUtil.getInstance().compileMLMSpecification(mlmFile, new PrintWriter(System.out));

        MLMSystem mlmSystem = new MLMSystem(mlmResult);
        UseMLMSystemApi systemApi = new UseMLMSystemApi(mlmSystem);

        File soilFile = new File(SOIL_PATH + "/multi-2024-challenge.soil");

        try (FileReader fileReader = new FileReader(soilFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.isEmpty()) continue;
                evaluateStatement(systemApi, line.substring(1).trim());
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public void evaluateStatement(UseMLMSystemApi systemApi, String statement) throws MSystemException {
        systemApi.getSystem().execute(generateStatement(systemApi, statement));
    }

    private MStatement generateStatement(UseMLMSystemApi systemApi,String input) {

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