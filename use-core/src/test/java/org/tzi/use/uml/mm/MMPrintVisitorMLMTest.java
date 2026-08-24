package org.tzi.use.uml.mm;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompilerMLM;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Regression tests for {@link MMPrintVisitor#visitMLM}. It used to flatten
 * every level's classes/associations/invariants into one fake
 * "model &lt;mlmName&gt;" block, printing every identifier fully qualified
 * as "Level@Name" -- not valid input for a "class" declaration or a
 * "mediator X &lt; Y" header, and losing the level boundaries entirely,
 * despite this class's own documented promise that its output "can be
 * directly fed back into the specification parser". It also printed
 * "mediator X : Y" instead of the real "mediator X &lt; Y" separator, and
 * let a class's clabject (cross-level, instance-of) edge leak into its
 * "class X &lt; Y" header as if it were a same-level superclass.
 */
public class MMPrintVisitorMLMTest extends TestCase {

    private MMultiLevelModel compile(String filename) throws IOException, URISyntaxException {
        File f = new File(ClassLoader.getSystemResource(
                "org/tzi/use/mlm_seminar/" + filename).toURI());
        try (FileInputStream in = new FileInputStream(f)) {
            MMultiLevelModel mlm = (MMultiLevelModel) USECompilerMLM.compileMLMSpecification(
                    in, f.getAbsolutePath(), new PrintWriter(System.err), new MultiLevelModelFactory());
            assertNotNull("fixture failed to compile: " + filename, mlm);
            return mlm;
        }
    }

    private String render(MMultiLevelModel mlm) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        mlm.processWithVisitor(new MMPrintVisitor(pw));
        pw.flush();
        return sw.toString();
    }

    /**
     * The core promise of the class's own doc comment: the printed text
     * is not just cosmetically plausible, it is valid MLM-USE that a
     * fresh compiler run accepts, and it preserves the same clabject
     * structure (attribute cancellation via "clabject X : Y ... ~attr
     * ... end") as the original.
     */
    public void testRenderedMlmIsReparseableAndPreservesClabjectStructure() throws Exception {
        MMultiLevelModel original = compile("mlm-figure-1-test.use");
        String rendered = render(original);

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel reparsed = (MMultiLevelModel) USECompilerMLM.compileMLMSpecification(
                new ByteArrayInputStream(rendered.getBytes(StandardCharsets.UTF_8)),
                "rendered.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("rendered MLM-USE text failed to re-parse:\n" + errBuf + "\n---\n" + rendered, reparsed);

        assertEquals(
                attrNames(original, "PC", "PC"),
                attrNames(reparsed, "PC", "PC"));
        assertEquals(
                attrNames(original, "PC", "Device"),
                attrNames(reparsed, "PC", "Device"));
    }

    public void testNoFullyQualifiedNameLeaksIntoAClassHeader() throws Exception {
        String rendered = render(compile("mlm-figure-1-test.use"));
        Pattern qualifiedClassHeader = Pattern.compile("^class \\S*@\\S*", Pattern.MULTILINE);
        assertFalse("a 'class X < Y' header must never contain a 'Level@' qualifier:\n" + rendered,
                qualifiedClassHeader.matcher(rendered).find());
    }

    public void testClabjectPowerclassNotPrintedAsClassSuperclass() throws Exception {
        String rendered = render(compile("mlm-figure-1-test.use"));
        assertFalse("PC's clabject powerclass (Computer) must not appear as 'class PC < ...'",
                rendered.contains("class PC <"));
    }

    public void testMediatorHeaderUsesLessThanNotColon() throws Exception {
        String rendered = render(compile("mlm-figure-1-test.use"));
        assertTrue(rendered.contains("mediator PC < Computer_product"));
        assertTrue(rendered.contains("mediator Computer_product < NONE"));
        assertFalse(rendered.contains("mediator PC : Computer_product"));
    }

    public void testLevelsPrintedAsSeparateModelBlocks() throws Exception {
        String rendered = render(compile("mlm-figure-1-test.use"));
        assertTrue(rendered.contains("model Computer_product"));
        assertTrue(rendered.contains("model PC"));
    }

    public void testInterElementsStayFullyQualified() throws Exception {
        String rendered = render(compile("mlm-figure-1-test.use"));
        assertTrue("inter-associations legitimately cross levels and should stay qualified",
                rendered.contains("Computer_product@Computer[1] role handler"));
    }

    private java.util.Set<String> attrNames(MMultiLevelModel mlm, String model, String cls) {
        java.util.Set<String> names = new java.util.HashSet<>();
        for (MAttribute attr : mlm.getClass(model, cls).allAttributes()) {
            names.add(attr.name());
        }
        return names;
    }
}
