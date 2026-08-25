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
 * Smoke test for the CatMLM front-end: runs the ABCD worked example
 * (attributes/constraints/associations/clabject-shorthand subset) through
 * USECompilerCatUSE and checks the resulting MMultiLevelModel against
 * what the plain MLMUse column-1 syntax is known to produce for the same
 * model.
 */
public class CatUSESmokeTest extends TestCase {

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
            "category B2 < B\n" +
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
            "association ab between\n" +
            "A[*] role a\n" +
            "B[1] role b\n" +
            "end\n" +
            "\n" +
            "constraints\n" +
            "context A inv ABMLM: catConstr\n" +
            "self.b.attr2 = 'MLM'\n" +
            "\n" +
            "context A1 inv A1B1MLM:\n" +
            "self.a1.b.r1.attr1 > 5\n" +
            "\n" +
            "model CD < AB\n" +
            "clabject C : (A, B)\n" +
            "end\n" +
            "\n" +
            "clabject D : B\n" +
            "end\n" +
            "\n" +
            "clabject E : B2\n" +
            "end\n";

    public void testAbcdWorkedExample() {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);

        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(CATMLM_ABCD.getBytes(StandardCharsets.UTF_8)),
                "abcd-catmlm.use", err, new MultiLevelModelFactory());

        err.flush();
        if (mlm == null) {
            fail("CatMLM compilation failed:\n" + errBuf);
        }

        Set<String> cAttrs = attrNames(mlm, "C");
        Set<String> dAttrs = attrNames(mlm, "D");
        Set<String> eAttrs = attrNames(mlm, "E");

        System.out.println("CD@C attributes = " + cAttrs);
        System.out.println("CD@D attributes = " + dAttrs);
        System.out.println("CD@E attributes = " + eAttrs);

        // Ground truth, from the docx's column 1 (plain MLMUse) rendering
        // of the same model:
        //   clabject C : A  cancels A.attr1 (catAtt)  -> A.attr2 survives
        //   clabject C : B  cancels B.attr2 (catAtt)  -> B.attr1 survives
        //   clabject D : B  cancels B.attr2 (catAtt)  -> B.attr1 survives
        //   clabject E : B2 cancels B.attr2 (catAtt, inherited via B2 < B)
        assertEquals(Set.of("attr2", "attr1"), cAttrs); // A.attr2 + B.attr1
        assertEquals(Set.of("attr1"), dAttrs);            // B.attr1 only
        assertEquals(Set.of("attr1"), eAttrs);             // via B2 < B, same as D

        Set<String> cInvariants = mlm.allClassInvariants(mlm.getClass("CD", "C")).stream()
                .map(inv -> inv.name())
                .collect(Collectors.toSet());
        System.out.println("CD@C invariants = " + cInvariants);
        assertTrue("catConstr-tagged ABMLM should have been cancelled for clabject C",
                cInvariants.stream().noneMatch(n -> n.contains("ABMLM")));

        System.out.println("CatMLM ABCD smoke test: PASSED");
    }

    private Set<String> attrNames(MMultiLevelModel mlm, String className) {
        return mlm.getClass("CD", className).allAttributes().stream()
                .map(a -> a.name())
                .collect(Collectors.toSet());
    }
}
