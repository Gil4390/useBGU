package org.tzi.use.tools.catuselatex;

import junit.framework.TestCase;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.parser.use.USECompilerCatUSE;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Regression tests for {@link FlatUseTextRenderer}: the third pipeline
 * stage (CatUSE -&gt; MLM-USE -&gt; plain USE), which flattens every class's
 * fully resolved (post-cancellation) structure into ordinary, inheritance-
 * free plain-USE syntax.
 */
public class FlatUseTextRendererTest extends TestCase {

    private static final String CATMLM_VEHICLE =
            "MLM VehicleCatalog\n" +
            "\n" +
            "model Taxonomy < NONE\n" +
            "category Vehicle\n" +
            "attributes\n" +
            "wheels: Integer\n" +
            "internalCode: String; catAtt\n" +
            "end\n" +
            "\n" +
            "category Product\n" +
            "attributes\n" +
            "listPrice: Integer\n" +
            "skuPrefix: String; catAtt\n" +
            "end\n" +
            "\n" +
            "catAssociation classifiedAs between\n" +
            "Vehicle[1] role vehicleSide\n" +
            "Product[1] role productSide\n" +
            "end\n" +
            "\n" +
            "constraints\n" +
            "context Vehicle inv PositiveWheels: catConst\n" +
            "self.wheels > 0\n" +
            "\n" +
            "model Fleet < Taxonomy\n" +
            "clabject Car : category Vehicle, Product\n" +
            "end\n" +
            "\n" +
            "clabject Bicycle : category Vehicle\n" +
            "end\n";

    private MMultiLevelModel compileCatUse(String src) {
        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)),
                "flat-renderer-test.use", err, new MultiLevelModelFactory());
        err.flush();
        assertNotNull("CatUSE compilation failed:\n" + errBuf, mlm);
        return mlm;
    }

    /**
     * The core promise: the flattened text is genuinely plain, vanilla
     * USE -- accepted by USECompiler (not USECompilerMLM), with no MLM
     * syntax (no "MLM" header, no "clabject"/"mediator" left over).
     */
    public void testFlattenedTextIsPlainReparseableUse() {
        String rendered = FlatUseTextRenderer.render(compileCatUse(CATMLM_VEHICLE), "VehicleCatalog");

        assertFalse(rendered.contains("clabject"));
        assertFalse(rendered.contains("mediator"));
        assertFalse(rendered.startsWith("MLM"));

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MModel reparsed = USECompiler.compileSpecification(
                new ByteArrayInputStream(rendered.getBytes(StandardCharsets.UTF_8)),
                "flat.use", err, new ModelFactory());
        err.flush();
        assertNotNull("flattened text failed to re-parse as plain USE:\n" + errBuf + "\n---\n" + rendered, reparsed);
    }

    /**
     * The whole point of not using "class X < Y" here: a cancelled
     * (catAtt) attribute must be genuinely absent from the clabject's
     * flat attribute list, not just hidden behind inheritance.
     */
    public void testCancelledAttributesAreGenuinelyAbsentFromClabject() {
        String rendered = FlatUseTextRenderer.render(compileCatUse(CATMLM_VEHICLE), "VehicleCatalog");
        int carStart = rendered.indexOf("class Car");
        int carEnd = rendered.indexOf("end", carStart);
        String carBlock = rendered.substring(carStart, carEnd);

        assertTrue(carBlock.contains("wheels"));
        assertTrue(carBlock.contains("listPrice"));
        assertFalse("catAtt-cancelled internalCode must not appear on Car", carBlock.contains("internalCode"));
        assertFalse("catAtt-cancelled skuPrefix must not appear on Car", carBlock.contains("skuPrefix"));
    }

    /** catConst cancellation: the invariant must print once, only under its surviving owner. */
    public void testCatConstInvariantOnlyAppearsOnceUnderItsOrigin() {
        String rendered = FlatUseTextRenderer.render(compileCatUse(CATMLM_VEHICLE), "VehicleCatalog");
        assertTrue(rendered.contains("context Vehicle inv PositiveWheels"));
        assertFalse(rendered.contains("context Car inv PositiveWheels"));
        assertFalse(rendered.contains("context Bicycle inv PositiveWheels"));
        assertEquals(1, countOccurrences(rendered, "inv PositiveWheels"));
    }

    /** catAssociation cancellation: neither far-end role survives on the dual-powerclass clabject. */
    public void testCatAssociationRolesAreCancelledOnClabject() {
        String rendered = FlatUseTextRenderer.render(compileCatUse(CATMLM_VEHICLE), "VehicleCatalog");
        assertFalse(rendered.contains("Car also inherits navigable role"));
        assertTrue("Bicycle only instantiates Vehicle, so it loses productSide without ever gaining it",
                !rendered.contains("Bicycle also inherits navigable role(s) productSide"));
    }

    /**
     * An association role a clabject *does* still inherit (untagged,
     * survives cancellation) is noted with a comment rather than a
     * duplicated association block -- and the result must still reparse.
     */
    public void testSurvivingInheritedRoleIsNotedNotDuplicated() {
        String src =
                "MLM InheritedRoleDemo\n" +
                "\n" +
                "model Meta < NONE\n" +
                "category Animal\n" +
                "end\n" +
                "\n" +
                "class Habitat\n" +
                "end\n" +
                "\n" +
                "association livesIn between\n" +
                "Animal[1] role home\n" +
                "Habitat[*] role residents\n" +
                "end\n" +
                "\n" +
                "model Instances < Meta\n" +
                "clabject Dog : category Animal\n" +
                "end\n";

        String rendered = FlatUseTextRenderer.render(compileCatUse(src), "InheritedRoleDemo");
        assertTrue(rendered.contains("Dog also inherits navigable role(s) residents"));
        assertEquals(1, countOccurrences(rendered, "association livesIn"));

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MModel reparsed = USECompiler.compileSpecification(
                new ByteArrayInputStream(rendered.getBytes(StandardCharsets.UTF_8)),
                "flat.use", err, new ModelFactory());
        err.flush();
        assertNotNull("flattened text with an inherited-role comment failed to re-parse:\n" + errBuf, reparsed);
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0, idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) >= 0) {
            count++;
            idx += needle.length();
        }
        return count;
    }
}
