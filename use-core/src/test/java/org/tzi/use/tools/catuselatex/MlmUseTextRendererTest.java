package org.tzi.use.tools.catuselatex;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompilerCatUSE;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Regression tests for {@link MlmUseTextRenderer}: it must produce text
 * that is both structurally correct (no cross-level clabject edge leaking
 * into a same-level "class X &lt; Y" header -- see
 * {@link LevelScopedPrintVisitor#visitClass}) and genuinely re-parseable,
 * unlike the inherited {@code MMPrintVisitor.visitMLM}, which flattens
 * every level into one fake "model &lt;mlmName&gt;" block using
 * fully-qualified "Level@Name" identifiers throughout.
 */
public class MlmUseTextRendererTest extends TestCase {

    private static final String CATMLM_ABCD =
            "MLM ABCD\n" +
            "\n" +
            "model AB < NONE\n" +
            "category A\n" +
            "attributes\n" +
            "attr1: Integer; catAtt\n" +
            "attr2: Real\n" +
            "end\n" +
            "\n" +
            "class A1\n" +
            "attributes\n" +
            "attr1: String\n" +
            "end\n" +
            "\n" +
            "category B\n" +
            "attributes\n" +
            "attr1: Integer\n" +
            "attr2: String; catAtt\n" +
            "end\n" +
            "\n" +
            "class B1\n" +
            "attributes\n" +
            "attr1: Integer\n" +
            "end\n" +
            "\n" +
            "catAssociation aa1 between\n" +
            "A[1] role a1\n" +
            "A1[1] role r1\n" +
            "end\n" +
            "\n" +
            "association bb1 between\n" +
            "B[1] role b1\n" +
            "B1[1] role r1\n" +
            "end\n" +
            "\n" +
            "constraints\n" +
            "context A inv ABMLM: catConstr\n" +
            "self.attr2 > 0\n" +
            "\n" +
            "model CD < AB\n" +
            "clabject C : (A, B)\n" +
            "end\n" +
            "\n" +
            "clabject D : B\n" +
            "end\n";

    private MMultiLevelModel compileCatUse(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "renderer-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("CatUSE compilation failed:\n" + errBuf, mlm);
        return mlm;
    }

    /**
     * The core promise of this tool: the rendered text is not just
     * cosmetically plausible, it is valid MLM-USE that a completely fresh
     * compiler run accepts and turns into a model with the same clabject
     * attribute sets as the original CatUSE compilation.
     */
    public void testRenderedTextIsReparseableAndPreservesStructure() {
        MMultiLevelModel original = compileCatUse(CATMLM_ABCD);
        String rendered = MlmUseTextRenderer.render(original, "ABCD");

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel reparsed = USECompilerMLM.compileMLMSpecification(
                new ByteArrayInputStream(rendered.getBytes(StandardCharsets.UTF_8)),
                "rendered.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("rendered MLM-USE text failed to re-parse:\n" + errBuf + "\n---\n" + rendered, reparsed);

        assertEquals(attrNames(original, "C"), attrNames(reparsed, "C"));
        assertEquals(attrNames(original, "D"), attrNames(reparsed, "D"));
    }

    /**
     * Regression: MClass.parents() mixes same-level generalization with
     * cross-level clabject instance-of edges. A naive reuse of the
     * inherited MMPrintVisitor.visitClass would print the clabject's
     * powerclass as if it were a same-level superclass ("class C < A, B").
     */
    public void testClabjectPowerclassIsNotPrintedAsClassSuperclass() {
        String rendered = MlmUseTextRenderer.render(compileCatUse(CATMLM_ABCD), "ABCD");
        assertFalse("clabject C's powerclasses must not appear as 'class C < ...'",
                rendered.contains("class C <"));
        assertTrue("clabject C must still appear declared plainly in its own level",
                rendered.contains("class C\n") || rendered.contains("class C \n") || rendered.contains("class C{"));
    }

    public void testMediatorHeaderUsesRealSyntaxNotColon() {
        String rendered = MlmUseTextRenderer.render(compileCatUse(CATMLM_ABCD), "ABCD");
        assertTrue(rendered.contains("mediator CD < AB"));
        assertTrue(rendered.contains("mediator AB < NONE"));
    }

    private Set<String> attrNames(MMultiLevelModel mlm, String cls) {
        return mlm.getClass("CD", cls).allAttributes().stream()
                .map(a -> a.name()).collect(Collectors.toSet());
    }
}
