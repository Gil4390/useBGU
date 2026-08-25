package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompilerCatUSE;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Coverage for CatMLM's fused classifier/clabject header
 * ("category C2 &lt; D : C3 ... end", Java-"implements"-style fusion of
 * cross-level instantiation onto the classifier's own declaration,
 * alongside its pre-existing same-level "&lt;" superclassifier clause),
 * and for the catRefList rule both clauses now share: a bare identifier
 * for a single reference, or a parenthesized comma list for two or more
 * -- applied consistently to catClassifierDefinition's "&lt;" and ":"
 * clauses and to catClabjectDefinition's ":" powerclass list, all three
 * of which used to accept a bare comma list with no parentheses at all.
 *
 * <p>The fused header is pure sugar: it desugars to exactly the same
 * ASTCatClabject a separate "clabject NAME : ..." statement would, added
 * to the same level. Declaring the same class both via the fused header
 * and a separate clabject/superclass statement is not an error -- their
 * reference lists union, which relies on {@code ASTClassifier
 * .addSuperClassifiers} accumulating rather than replacing (a genuine
 * pre-existing bug found while building this: it used to silently
 * overwrite on a second call).
 */
public class CatMLMFusedClassifierClabjectHeaderTest extends TestCase {

    private MMultiLevelModel compile(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "fused-header-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("CatUSE compilation failed:\n" + errBuf, mlm);
        return mlm;
    }

    private void assertFailsToCompile(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "fused-header-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNull("expected a parse failure, but compilation succeeded", mlm);
    }

    private Set<String> attrNames(MMultiLevelModel mlm, String model, String cls) {
        return mlm.getClass(model, cls).allAttributes().stream()
                .map(a -> a.name()).collect(Collectors.toSet());
    }

    public void testFusedHeaderSingleSuperAndSinglePowerclassNoParens() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category C3\n" +
                "attributes\n" +
                "a3: Integer\n" +
                "end\n" +
                "\n" +
                "model M2 < M3\n" +
                "class D\n" +
                "attributes\n" +
                "d: Integer\n" +
                "end\n" +
                "\n" +
                "category C2 < D : C3\n" +
                "attributes\n" +
                "a2: Integer\n" +
                "end\n");

        assertEquals(Set.of("a2", "a3", "d"), attrNames(mlm, "M2", "C2"));
    }

    public void testFusedHeaderMultipleSupersAndPowerclassesRequireParens() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category C3a\n" +
                "attributes\n" +
                "a3a: Integer\n" +
                "end\n" +
                "\n" +
                "category C3b\n" +
                "attributes\n" +
                "a3b: Integer\n" +
                "end\n" +
                "\n" +
                "model M2 < M3\n" +
                "class D1\n" +
                "attributes\n" +
                "d1: Integer\n" +
                "end\n" +
                "\n" +
                "class D2\n" +
                "attributes\n" +
                "d2: Integer\n" +
                "end\n" +
                "\n" +
                "category C2 < (D1, D2) : (C3a, C3b)\n" +
                "attributes\n" +
                "a2: Integer\n" +
                "end\n");

        assertEquals(Set.of("a2", "a3a", "a3b", "d1", "d2"), attrNames(mlm, "M2", "C2"));
    }

    public void testBareMultiItemPowerclassListInFusedHeaderIsRejected() {
        assertFailsToCompile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category C3a end\n" +
                "category C3b end\n" +
                "\n" +
                "model M2 < M3\n" +
                "category C2 : C3a, C3b\n" +
                "end\n");
    }

    public void testBareMultiItemSuperListInFusedHeaderIsRejected() {
        assertFailsToCompile(
                "MLM T\n" +
                "\n" +
                "model M2\n" +
                "class D1 end\n" +
                "class D2 end\n" +
                "category C2 < D1, D2\n" +
                "end\n");
    }

    public void testBareMultiItemPowerclassListInStandaloneClabjectIsRejected() {
        assertFailsToCompile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category C3a end\n" +
                "category C3b end\n" +
                "\n" +
                "model M2 < M3\n" +
                "clabject C2 : C3a, C3b\n" +
                "end\n");
    }

    /** A single reference wrapped in parentheses is not a valid alternate spelling. */
    public void testSingleReferenceMayNotBeParenthesized() {
        assertFailsToCompile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category C3 end\n" +
                "\n" +
                "model M2 < M3\n" +
                "clabject C2 : (C3)\n" +
                "end\n");
    }

    /**
     * The fused header's powerclass list and a separate standalone
     * clabject statement for the same class name union rather than
     * conflict: C2 ends up instantiating both powerclasses.
     */
    public void testFusedHeaderPowerclassAndStandaloneClabjectUnion() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category Vehicle\n" +
                "attributes\n" +
                "wheels: Integer\n" +
                "end\n" +
                "\n" +
                "category Product\n" +
                "attributes\n" +
                "listPrice: Integer\n" +
                "end\n" +
                "\n" +
                "model M2 < M3\n" +
                "category Car : Vehicle\n" +
                "attributes\n" +
                "seats: Integer\n" +
                "end\n" +
                "\n" +
                "clabject Car : Product\n" +
                "end\n");

        assertEquals(Set.of("wheels", "seats", "listPrice"), attrNames(mlm, "M2", "Car"));
    }

    /**
     * The fused header's own same-level "&lt;" list and a separate
     * standalone clabject's own local-parent clause for the same class
     * name also union, rather than the second silently overwriting the
     * first. This is a regression test for a genuine pre-existing bug in
     * ASTClassifier.addSuperClassifiers, which used to replace
     * (fSuperClassifiers = idList) instead of accumulate: before the fix,
     * only whichever declaration ran last would survive.
     */
    public void testFusedHeaderSuperclassAndStandaloneClabjectLocalParentUnion() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model M3\n" +
                "category Vehicle\n" +
                "end\n" +
                "\n" +
                "model M2 < M3\n" +
                "class D\n" +
                "attributes\n" +
                "dAttr: Integer\n" +
                "end\n" +
                "\n" +
                "class E\n" +
                "attributes\n" +
                "eAttr: Integer\n" +
                "end\n" +
                "\n" +
                "category Car < D\n" +
                "attributes\n" +
                "seats: Integer\n" +
                "end\n" +
                "\n" +
                "clabject Car < E; Car : Vehicle\n" +
                "end\n");

        assertEquals("Car must inherit from both D (via the fused header) and E " +
                "(via the separate clabject's local-parent clause), not just whichever ran last",
                Set.of("dAttr", "eAttr", "seats"), attrNames(mlm, "M2", "Car"));
    }
}
