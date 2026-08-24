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
 * Regression test for a genuine, long-predating-this-work grammar bug: the
 * clabject rule in USEBase.gpart wrote the "roles" section keyword as a
 * bare literal ('roles'), which ANTLR3's shared-lexer testLiterals
 * mechanism auto-promotes into a keyword reserved *everywhere* in the
 * language -- not just inside a "clabject ... roles ~foo end" block. That
 * made "roles" unusable as an ordinary identifier anywhere, including as
 * an OCL operation parameter name, breaking any plain (non-MLM) USE model
 * that happened to use it -- e.g. use-gui's own ShellIT/t109.use fixture
 * ("juniorsAux(roles:Set(Role))"), confirmed failing all the way back to
 * before this repository ever had clabjects touched this session.
 *
 * <p>Fixed by making "roles" a soft keyword (a semantic predicate, exactly
 * like the existing keyRole/keyUnion/keyAssociation rules for "role",
 * "union", "association"), so it's only recognized as the section header
 * in the one syntactic position it can actually appear.
 */
public class RolesIsNotAReservedWordTest extends TestCase {

    public void testRolesUsableAsOperationParameterName() {
        String src =
                "model RolesTest\n" +
                "\n" +
                "class Foo\n" +
                "operations\n" +
                "  bar(roles : Integer) : Integer = roles + 1\n" +
                "end\n";

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MModel model = USECompiler.compileSpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "roles-test.use", err, new ModelFactory());
        err.flush();

        assertNotNull("'roles' must be usable as an ordinary identifier outside a clabject block:\n" + errBuf, model);
    }
}
