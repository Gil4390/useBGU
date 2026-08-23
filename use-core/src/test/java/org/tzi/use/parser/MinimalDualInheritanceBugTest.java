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
 * Minimal, standalone reproduction of the "dual inheritance path" conflict
 * discussed while testing CatUSE against the ABCD example -- isolated
 * here with zero CatUSE code involved, to show it was a pre-existing
 * MLM-USE property, not something introduced by the grammar work.
 *
 * Fido reaches Animal.name via two converging paths: same-level
 * subclassing (Fido &lt; Rex, Rex : Animal) and its own instance-of edge
 * (Fido : Dog, Dog &lt; Animal). Both terminate at the same declared
 * attribute, so this must compile -- see the equivalent
 * {@code Attribute_diamond_inheritance_via_subclass_and_clabject.use}
 * fixture under {@code org/tzi/use/mlmTests} for the same case in the
 * project's usual fixture-driven test style; this class keeps a
 * self-contained, single-file version of the same regression.
 */
public class MinimalDualInheritanceBugTest extends TestCase {

    private static final String SPEC =
            "MLM MetaModel\n" +
            "\n" +
            "model Meta\n" +
            "\n" +
            "class Animal\n" +
            "attributes\n" +
            "name: String\n" +
            "end\n" +
            "\n" +
            "class Dog < Animal\n" +
            "end\n" +
            "\n" +
            "model Model\n" +
            "\n" +
            "class Rex\n" +
            "end\n" +
            "\n" +
            "class Fido < Rex\n" +
            "end\n" +
            "\n" +
            "mediator Meta < NONE\n" +
            "end\n" +
            "\n" +
            "mediator Model < Meta\n" +
            "clabject Rex : Animal\n" +
            "end\n" +
            "\n" +
            "clabject Fido : Dog\n" +
            "end\n" +
            "\n" +
            "end\n";

    public void testMinimalDualInheritanceConflict() {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);

        MMultiLevelModel mlm = USECompilerMLM.compileMLMSpecification(
                new ByteArrayInputStream(SPEC.getBytes(StandardCharsets.UTF_8)),
                "minimal-dual-inheritance.use", err, new MultiLevelModelFactory());

        err.flush();
        assertNotNull("diamond convergence on the same attribute declaration must not be rejected:\n" + errBuf, mlm);
    }
}
