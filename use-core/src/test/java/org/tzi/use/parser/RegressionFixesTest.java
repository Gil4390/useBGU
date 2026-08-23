package org.tzi.use.parser;

import junit.framework.TestCase;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.uml.mm.MClabject;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MMediator;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * One regression test per bug fixed during the scope-1/scope-2 code review
 * ("Fix false-positive attribute/role conflict..." and the "fix-now" batch
 * applied afterward). Each test targets exactly the mechanism the review
 * identified, not just the symptom, so a regression re-introducing the
 * underlying mistake -- not just the exact original reproduction -- would
 * still be caught.
 */
public class RegressionFixesTest extends TestCase {

    private MMultiLevelModel compile(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerMLM.compileMLMSpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "regression-fixes-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("compilation failed:\n" + errBuf, mlm);
        return mlm;
    }

    // -----------------------------------------------------------------
    // 1. ASTMultiLevelModel.gen(): the abort-on-error check tested
    //    ctx.errorCount() instead of mlmContext.errorCount(), and since
    //    MultiContext.reportError forwards every error to mainContext
    //    once set, ctx's own counter never moves -- the check was dead
    //    code. There's no reportError call currently reachable from
    //    within mediator.gen()'s own call graph to reproduce this
    //    end-to-end, so this pins down the forwarding mechanism itself
    //    directly: exactly the fact the fix (checking mlmContext instead
    //    of ctx) depends on.
    // -----------------------------------------------------------------
    public void testMultiContextForwardsErrorsToMainContextNotItself() {
        StringWriter outerBuf = new StringWriter();
        MLMContext mainCtx = new MLMContext("x", new PrintWriter(outerBuf), null, new MultiLevelModelFactory());

        StringWriter innerBuf = new StringWriter();
        MLMContext ctx = new MLMContext("x", new PrintWriter(innerBuf), null, new MultiLevelModelFactory());
        ctx.setMainContext(mainCtx);

        Token t = new CommonToken(0, "dummy");
        ctx.reportError(t, "synthetic error");

        assertEquals("ctx's own counter must stay untouched once it forwards to a main context",
                0, ctx.errorCount());
        assertEquals("the error must land on the context that actually owns error reporting",
                1, mainCtx.errorCount());
    }

    // -----------------------------------------------------------------
    // 2. MMultiLevelModel.checkState(): iterated this.models(), which is
    //    backed by a TreeMap<String,MModel> (alphabetical by name), while
    //    pairing each model with "whatever was previous in that
    //    iteration" as if it were the true parent level. Levels named so
    //    hierarchy order != alphabetical order exposed the mismatch.
    //    "Alpha" (child) sorts before "Zeta" (its parent) -- exactly the
    //    inversion that broke the old code.
    // -----------------------------------------------------------------
    public void testCheckStatePairsLevelsByTrueParentNotAlphabeticalOrder() {
        String src =
                "MLM AlphaZeta\n" +
                "\n" +
                "model Zeta\n" +
                "class Q\n" +
                "end\n" +
                "\n" +
                "model Alpha\n" +
                "class C\n" +
                "end\n" +
                "\n" +
                "mediator Zeta < NONE\n" +
                "end\n" +
                "\n" +
                "mediator Alpha < Zeta\n" +
                "clabject C : Q\n" +
                "end\n" +
                "\n" +
                "end\n";

        MMultiLevelModel mlm = compile(src);
        // Under the old alphabetical-order bug this either throws (systemApi
        // rooted at the wrong model) or returns false for a model that is
        // trivially satisfiable (one object, no constraints).
        assertTrue("a trivially satisfiable MLM must check out regardless of how level names sort alphabetically",
                mlm.checkState());
    }

    // -----------------------------------------------------------------
    // 3. MMediator.getClabject(MClass,MClass): a first-match search over
    //    fClabjects.values() (a HashMap) could return a subclass match
    //    even when an exact match for the queried parent also exists,
    //    depending on unspecified hash-bucket order. C has clabjects to
    //    both Q and its subclass P; querying for Q must always return
    //    the exact C:Q clabject, never the C:P one.
    // -----------------------------------------------------------------
    public void testGetClabjectPrefersExactParentMatchOverSubclassMatch() {
        String src =
                "MLM ExactMatch\n" +
                "\n" +
                "model Meta\n" +
                "class Q\n" +
                "end\n" +
                "\n" +
                "class P < Q\n" +
                "end\n" +
                "\n" +
                "model Instances\n" +
                "class C\n" +
                "end\n" +
                "\n" +
                "mediator Meta < NONE\n" +
                "end\n" +
                "\n" +
                "mediator Instances < Meta\n" +
                "clabject C : Q\n" +
                "end\n" +
                "\n" +
                "clabject C : P\n" +
                "end\n" +
                "\n" +
                "end\n";

        MMultiLevelModel mlm = compile(src);
        MMediator mediator = mlm.getMediator("Instances");
        MClass c = mlm.getClass("Instances", "C");
        MClass q = mlm.getClass("Meta", "Q");

        MClabject result = mediator.getClabject(c, q);
        assertNotNull(result);
        assertEquals("querying for Q must return the clabject whose parent IS Q, not the one whose parent merely subclasses Q",
                q, result.parent());
    }

    // -----------------------------------------------------------------
    // 4. MInternalClassImpl.clabjectsFromParents/clabjectsFromChildren and
    //    MMultiLevelModel.allClassInvariants/subClassesOfClassForInvariant
    //    all called edgesBetween(...).iterator().next() unguarded. These
    //    are defensive fixes -- a well-formed compiled model never
    //    actually produces the empty-edge-set case they guard against --
    //    so there is no natural trigger for the crash itself. What *is*
    //    testable, and worth locking in, is that the normal/valid-input
    //    behavior of all four methods is unchanged by switching to the
    //    guarded lookup.
    // -----------------------------------------------------------------
    public void testInvariantAndClabjectQueriesStillWorkOnValidHierarchy() {
        String src =
                "MLM ValidHierarchy\n" +
                "\n" +
                "model Meta\n" +
                "class Q\n" +
                "end\n" +
                "\n" +
                "constraints\n" +
                "context Q inv QPositive:\n" +
                "1 > 0\n" +
                "\n" +
                "model Instances\n" +
                "class C\n" +
                "end\n" +
                "\n" +
                "mediator Meta < NONE\n" +
                "end\n" +
                "\n" +
                "mediator Instances < Meta\n" +
                "clabject C : Q\n" +
                "end\n" +
                "\n" +
                "end\n";

        MMultiLevelModel mlm = compile(src);
        MClass q = mlm.getClass("Meta", "Q");
        MClass c = mlm.getClass("Instances", "C");

        Set<MClassInvariant> invs = mlm.allClassInvariants(c);
        assertTrue("C should still inherit Q's invariant through its clabject edge",
                invs.stream().anyMatch(inv -> inv.name().contains("QPositive")));

        Set<MClass> subclasses = mlm.subClassesOfClassForInvariant(q, invs.iterator().next());
        assertTrue("C should still be found as a clabject-derived subclass of Q for invariant purposes",
                subclasses.contains(c));
    }
}
