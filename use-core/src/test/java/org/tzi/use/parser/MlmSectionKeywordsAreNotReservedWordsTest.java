package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Regression test for the same class of bug already found and fixed for
 * "roles" (see {@link RolesIsNotAReservedWordTest}): "MLM", "mediator",
 * "clabject", and "assoclink" were all written as bare ANTLR3 literals in
 * USEBase.gpart's multi_level_model/mediator/clabject/assoclink rules
 * (and "MLM"/"clabject" again, independently, in CatMLM.gpart's
 * catUSE_model/catClabjectDefinition rules). A bare literal auto-promotes
 * to a keyword reserved *everywhere* in its composed grammar's shared
 * lexer, not just inside the one multi-level-modeling section it actually
 * belongs to -- breaking any plain, single-level USE model that happened
 * to use one of these words as an ordinary identifier, even though none
 * of them have any meaning outside an "MLM ..." file.
 *
 * <p>Fixed the same way "roles" was: each is now a soft keyword (a
 * semantic predicate reclassifying the IDENT only in its own grammar
 * position), matching the existing keyRole/keyRoles/keyUnion/keyClass
 * pattern.
 */
public class MlmSectionKeywordsAreNotReservedWordsTest extends TestCase {

    private void assertUsableAsAttributeName(String name) {
        String src =
                "model M\n" +
                "\n" +
                "class Foo\n" +
                "attributes\n" +
                name + ": Integer\n" +
                "end\n";

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MModel model = USECompiler.compileSpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "mlm-keyword-test.use", err, new ModelFactory());
        err.flush();

        assertNotNull("'" + name + "' must be usable as an ordinary attribute name " +
                "in a plain, non-MLM model:\n" + errBuf, model);
    }

    public void testMlmUsableAsAttributeName() {
        assertUsableAsAttributeName("MLM");
    }

    public void testMediatorUsableAsAttributeName() {
        assertUsableAsAttributeName("mediator");
    }

    public void testClabjectUsableAsAttributeName() {
        assertUsableAsAttributeName("clabject");
    }

    public void testAssoclinkUsableAsAttributeName() {
        assertUsableAsAttributeName("assoclink");
    }
}
