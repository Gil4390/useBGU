package org.tzi.use.tools.catuselatex;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompilerCatUSE;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class PlantUmlDiagramGeneratorTest extends TestCase {

    private static final String CATMLM_SIMPLE =
            "MLM Simple\n" +
            "\n" +
            "model Meta < NONE\n" +
            "category Animal\n" +
            "attributes\n" +
            "legs: Integer\n" +
            "end\n" +
            "\n" +
            "model Instances < Meta\n" +
            "clabject Dog : category Animal\n" +
            "end\n";

    public void testDiagramMarksCategoryAndClabjectStereotypesAndInstanceOfEdge() {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(CATMLM_SIMPLE.getBytes(StandardCharsets.UTF_8)),
                "diagram-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("CatUSE compilation failed:\n" + errBuf, mlm);

        String puml = PlantUmlDiagramGenerator.generate(mlm);

        assertTrue(puml.startsWith("@startuml"));
        assertTrue(puml.trim().endsWith("@enduml"));
        assertTrue("Animal must be stereotyped as a category (it is Dog's powerclass)",
                puml.contains("\"Animal\" as Meta_Animal <<category>>"));
        assertTrue("Dog must be stereotyped as a clabject",
                puml.contains("\"Dog\" as Instances_Dog <<clabject>>"));
        assertTrue("the clabject link must render as a dashed instanceOf arrow",
                puml.contains("Instances_Dog ..> Meta_Animal : <<instanceOf>>"));
        assertTrue("levels must render as separate packages",
                puml.contains("package \"Meta\"") && puml.contains("package \"Instances\""));
    }
}
