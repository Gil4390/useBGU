package org.tzi.use.parser;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompilerCatUSE;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Regression test for a question raised while discussing whether CatMLM's
 * grammar needs a level-qualified spelling for {@code instanceOf} (the
 * clabject powerclass reference) to handle a class name that is reused at
 * more than one level -- see the discussion recorded in known-issues.org.
 *
 * <p>Same-level subclassing ({@code <}) can never be ambiguous across
 * levels: {@link org.tzi.use.parser.use.USECompilerCatUSE#desugar} resolves
 * a {@code <} reference only against the declaring level's own class table
 * ({@code classesByLevel.get(level.name())}).
 *
 * <p>{@code instanceOf} (a clabject's {@code :} powerclass reference) is
 * resolved only against the level's own declared parent
 * ({@code classesByLevel.get(level.parentName())}) -- never any other
 * level, regardless of what other levels declare. So today, reusing a
 * class name at multiple levels causes no ambiguity for either relation:
 * CatMLM has no syntax at all for a clabject to target a powerclass other
 * than its level's single declared parent, so there is nothing to
 * disambiguate. This test reuses the name "Node" at all three levels of a
 * chain (plus a same-level subclass also named "Node") and checks that
 * every clabject still resolves to exactly its own declared parent's
 * class, not any other same-named one.
 */
public class CatMLMLevelNameDuplicationTest extends TestCase {

    private static final String CATMLM_LEVEL_NAME_DUP =
            "MLM CatLevelNameDup\n" +
            "\n" +
            "model M3\n" +
            "class Node\n" +
            "attributes\n" +
            "  marker: Integer\n" +
            "end\n" +
            "\n" +
            "model M2 < M3\n" +
            "class Node\n" +
            "attributes\n" +
            "  mid: String\n" +
            "end\n" +
            "clabject Node : Node end\n" +
            "\n" +
            "model M1 < M2\n" +
            "class Base\n" +
            "end\n" +
            "class Node < Base\n" +
            "end\n" +
            "clabject Node : Node end\n";

    public void testSameNameAtEveryLevelResolvesToOwnParent() {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);

        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(CATMLM_LEVEL_NAME_DUP.getBytes(StandardCharsets.UTF_8)),
                "cat-level-name-dup.use", err, new MultiLevelModelFactory());

        err.flush();
        if (mlm == null) {
            fail("CatMLM compilation failed:\n" + errBuf);
        }

        // M2@Node's clabject must resolve to M3@Node specifically (the only
        // class named "Node" at M2's declared parent, M3), inheriting
        // "marker" -- not fail, and not silently resolve to nothing.
        assertEquals(Set.of("marker", "mid"), attrNames(mlm, "M2", "Node"));

        // M1@Node's clabject must resolve to M2@Node specifically (M1's
        // declared parent), not to M3@Node directly (which would skip
        // "mid") and not to itself (the same-level "Node < Base" edge is
        // an entirely separate relation, resolved within M1 alone).
        assertEquals(Set.of("marker", "mid"), attrNames(mlm, "M1", "Node"));
    }

    private Set<String> attrNames(MMultiLevelModel mlm, String modelName, String className) {
        return mlm.getClass(modelName, className).allAttributes().stream()
                .map(MAttribute::name)
                .collect(Collectors.toSet());
    }
}
