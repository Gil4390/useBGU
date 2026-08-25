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
 * Coverage for everything that can legally (or must illegally) follow the
 * ":" in a CatMLM clabject declaration, after the optional "category" hint
 * keyword was removed from that position (CatMLM.gpart's
 * catClabjectDefinition rule): "clabject NAME : POWERCLASS (, POWERCLASS)*
 * end" and its fused "clabject NAME &lt; LOCAL ; NAME : POWERCLASS ..."
 * variant. "category" remains a keyword elsewhere in the grammar (the
 * classifier-declaration choice between "category ID ... end" and "class
 * ID ... end"), so these tests also confirm that removing its use in the
 * clabject-target position didn't leave it -- or any of CatMLM's other
 * soft keywords -- accidentally reserved there, the same class of bug
 * previously found and fixed for "roles" in USEBase.gpart.
 */
public class ClabjectPowerclassListSyntaxTest extends TestCase {

    private MMultiLevelModel compile(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "clabject-syntax-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("CatUSE compilation failed:\n" + errBuf, mlm);
        return mlm;
    }

    private void assertFailsToCompile(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "clabject-syntax-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNull("expected a parse failure, but compilation succeeded", mlm);
    }

    private Set<String> attrNames(MMultiLevelModel mlm, String model, String cls) {
        return mlm.getClass(model, cls).allAttributes().stream()
                .map(a -> a.name()).collect(Collectors.toSet());
    }

    public void testSinglePowerclassNoKeyword() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category Vehicle\n" +
                "attributes\n" +
                "wheels: Integer\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "clabject Car : Vehicle\n" +
                "end\n");

        assertEquals(Set.of("wheels"), attrNames(mlm, "Instances", "Car"));
    }

    public void testMultiplePowerclassesNoKeyword() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
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
                "model Instances < Meta\n" +
                "clabject Car : (Vehicle, Product)\n" +
                "end\n");

        assertEquals(Set.of("wheels", "listPrice"), attrNames(mlm, "Instances", "Car"));
    }

    public void testFusedLocalSubclassWithSinglePowerclassNoKeyword() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category Item\n" +
                "attributes\n" +
                "price: Integer\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "class Product\n" +
                "attributes\n" +
                "sku: String\n" +
                "end\n" +
                "\n" +
                "clabject SpecialProduct < Product; SpecialProduct : Item\n" +
                "end\n");

        assertEquals(Set.of("sku", "price"), attrNames(mlm, "Instances", "SpecialProduct"));
    }

    public void testFusedLocalSubclassWithMultiplePowerclassesNoKeyword() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category Item\n" +
                "attributes\n" +
                "price: Integer\n" +
                "end\n" +
                "\n" +
                "category Shippable\n" +
                "attributes\n" +
                "weightKg: Integer\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "class Product\n" +
                "attributes\n" +
                "sku: String\n" +
                "end\n" +
                "\n" +
                "clabject SpecialProduct < Product; SpecialProduct : (Item, Shippable)\n" +
                "end\n");

        assertEquals(Set.of("sku", "price", "weightKg"), attrNames(mlm, "Instances", "SpecialProduct"));
    }

    /**
     * A powerclass literally named "category" -- the exact word that used
     * to be a hint keyword right in this position -- must still work as an
     * ordinary powerclass name, both alone and first in a list.
     */
    public void testPowerclassLiterallyNamedCategoryAsSoleTarget() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category category\n" +
                "attributes\n" +
                "wheels: Integer\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "clabject Car : category\n" +
                "end\n");

        assertEquals(Set.of("wheels"), attrNames(mlm, "Instances", "Car"));
    }

    public void testPowerclassLiterallyNamedCategoryFirstInList() {
        MMultiLevelModel mlm = compile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category category\n" +
                "attributes\n" +
                "wheels: Integer\n" +
                "end\n" +
                "\n" +
                "category Product\n" +
                "attributes\n" +
                "listPrice: Integer\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "clabject Car : (category, Product)\n" +
                "end\n");

        assertEquals(Set.of("wheels", "listPrice"), attrNames(mlm, "Instances", "Car"));
    }

    /**
     * Every other CatMLM soft keyword ("catAtt", "catConstr",
     * "catAssociation") and every plain-USE soft keyword reused from the
     * base grammar ("role", "union", "association", "class") must remain
     * usable as an ordinary powerclass name in this position too -- none
     * of them should have leaked into a reserved word here, the same
     * failure mode already found and fixed for "roles" in USEBase.gpart.
     * "MLM" and "clabject" are included too: they used to be bare literals
     * in both USEBase.gpart and (independently, since CatMLM.gpart is a
     * separately-composed grammar) CatMLM.gpart itself, reserved
     * everywhere rather than just at the start of an MLM file / a clabject
     * declaration -- see MlmSectionKeywordsAreNotReservedWordsTest for the
     * plain-USE side of the same fix.
     */
    public void testOtherSoftKeywordsUsableAsPowerclassNames() {
        for (String name : new String[] { "catAtt", "catConstr", "catAssociation", "role", "union", "association", "class", "MLM", "clabject" }) {
            MMultiLevelModel mlm = compile(
                    "MLM T\n" +
                    "\n" +
                    "model Meta < NONE\n" +
                    "category " + name + "\n" +
                    "attributes\n" +
                    "x: Integer\n" +
                    "end\n" +
                    "\n" +
                    "model Instances < Meta\n" +
                    "clabject Car : " + name + "\n" +
                    "end\n");

            assertEquals("powerclass named '" + name + "' should be usable as a clabject target",
                    Set.of("x"), attrNames(mlm, "Instances", "Car"));
        }
    }

    /**
     * Regression guard: the old "category" hint keyword must no longer be
     * accepted before a powerclass name that isn't itself called
     * "category" -- confirms the grammar change actually took effect
     * rather than "category" merely becoming inert.
     */
    public void testOldCategoryHintKeywordIsNoLongerAccepted() {
        assertFailsToCompile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category Vehicle\n" +
                "attributes\n" +
                "wheels: Integer\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "clabject Car : category Vehicle\n" +
                "end\n");
    }

    public void testOldCategoryHintKeywordIsNoLongerAcceptedInFusedForm() {
        assertFailsToCompile(
                "MLM T\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category Item\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "class Product\n" +
                "end\n" +
                "\n" +
                "clabject SpecialProduct < Product; SpecialProduct : category Item\n" +
                "end\n");
    }
}
