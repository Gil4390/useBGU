package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompilerMLM;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Runs the docx's own column-1 (plain MLMUse) text -- transcribed
 * verbatim, zero CatUSE code involved -- through the completely
 * unmodified USECompilerMLM. Originally used to confirm the
 * "Attribute Duplication" / duplicate-role conflict seen while testing
 * the CatUSE desugarer was pre-existing MLM-USE behavior rather than an
 * artifact of the new grammar work (it was: E's same-level "E &lt; D"
 * edge converges with its own "E : B2" instance-of edge on the same
 * inherited role). Kept as a regression test now that the underlying
 * diamond-detection bug is fixed in MMultiLevelModel/MInternalClassImpl:
 * the documented example should compile cleanly, exactly as written.
 */
public class DocxColumn1LiteralTest extends TestCase {

    private static final String MLMUSE_COLUMN1 =
            "MLM ABCD\n" +
            "\n" +
            "model AB\n" +
            "\n" +
            "class A\n" +
            "attributes\n" +
            "attr1: Integer\n" +
            "attr2: Real\n" +
            "end\n" +
            "\n" +
            "class A1\n" +
            "attributes\n" +
            "attr1: String\n" +
            "end\n" +
            "\n" +
            "class B\n" +
            "attributes\n" +
            "attr1: Integer\n" +
            "attr2: String\n" +
            "end\n" +
            "\n" +
            "class B1\n" +
            "attributes\n" +
            "attr1: Integer\n" +
            "end\n" +
            "\n" +
            "class B2 < B\n" +
            "end\n" +
            "\n" +
            "association aa1 between\n" +
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
            "context A inv ABMLM:\n" +
            "self.b.attr2 = 'MLM'\n" +
            "\n" +
            "context A1 inv A1B1MLM:\n" +
            "self.a1.b.r1.attr1 > 5\n" +
            "\n" +
            "model CD\n" +
            "\n" +
            "class C\n" +
            "end\n" +
            "\n" +
            "class D\n" +
            "end\n" +
            "\n" +
            "class E < D\n" +
            "end\n" +
            "\n" +
            "association cd between\n" +
            "C[*] role c\n" +
            "D[*] role d\n" +
            "end\n" +
            "\n" +
            "inter-associations\n" +
            "association ac between\n" +
            "CD@C[1..*] role ic\n" +
            "AB@A1[1..*] role ia\n" +
            "end\n" +
            "\n" +
            "inter-constraints\n" +
            "context CD@C inv CB1MLM:\n" +
            "self.ia->forAll( it | it.attr1= 'MLM')\n" +
            "\n" +
            "mediator AB < NONE\n" +
            "end\n" +
            "\n" +
            "mediator CD < AB\n" +
            "clabject C : A\n" +
            "attributes\n" +
            "~attr1\n" +
            "roles\n" +
            "~r1\n" +
            "constraints\n" +
            "~ABMLM\n" +
            "end\n" +
            "\n" +
            "clabject C : B\n" +
            "attributes\n" +
            "~attr2\n" +
            "end\n" +
            "\n" +
            "clabject E : B2\n" +
            "attributes\n" +
            "~attr2\n" +
            "end\n" +
            "\n" +
            "clabject D : B\n" +
            "attributes\n" +
            "~attr2\n" +
            "end\n" +
            "\n" +
            "assoclink cd : ab\n" +
            "a -> c\n" +
            "b -> d\n" +
            "end\n" +
            "\n" +
            "end\n";

    public void testDocxColumn1AsWritten() {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);

        MMultiLevelModel mlm = USECompilerMLM.compileMLMSpecification(
                new ByteArrayInputStream(MLMUSE_COLUMN1.getBytes(StandardCharsets.UTF_8)),
                "docx-column1-literal.use", err, new MultiLevelModelFactory());

        err.flush();
        assertNotNull("the docx's own column-1 ABCD example should compile as written:\n" + errBuf, mlm);
    }
}
