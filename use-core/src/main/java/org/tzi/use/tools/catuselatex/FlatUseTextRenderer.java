package org.tzi.use.tools.catuselatex;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.expr.ExpressionPrintVisitor;
import org.tzi.use.uml.ocl.expr.ExpressionVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Renders the fully resolved, flattened plain-USE view of a compiled
 * {@link MMultiLevelModel} -- the third stage, after CatUSE and desugared
 * MLM-USE: what the underlying OCL evaluator actually sees.
 *
 * <p>Every class (category or clabject, at any level) becomes an ordinary
 * flat {@code class} declaration listing its fully resolved attributes
 * ({@link MClass#allAttributes()}, cancellations already applied) -- with
 * no {@code class X < Y} inheritance at all. Plain USE inheritance has no
 * way to express the removal a clabject performs: an inherited
 * attribute/role/constraint can be added to, but never subtracted from, a
 * plain USE subclass. That asymmetry is exactly why the clabject construct
 * exists; reusing "extends" here would silently put the cancelled features
 * back. This is the same flattening
 * {@code MInternalClassImpl#allAttributes()}/{@code #navigableEnds()}
 * already perform on demand for OCL evaluation -- this renderer just makes
 * it visible as text.
 *
 * <p><b>Scope of the association reconstruction:</b> each association is
 * printed once, at its own originally declared endpoint classes -- always
 * correct for those classes, since a clabject's cancellation list can only
 * ever target the clabject itself, never the category/class an association
 * was declared on. A descendant clabject that inherits one of these roles
 * without cancelling it (so, without redeclaring it locally either) is
 * noted with a comment instead of a duplicated association block, to keep
 * this a bounded, transparent tool rather than attempting a fully general
 * n-ary/multi-clabject reconstruction.
 */
public class FlatUseTextRenderer {

    public static String render(MMultiLevelModel mlm, String displayName) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);

        out.println("model " + displayName);
        out.println();

        List<MClass> allClasses = new ArrayList<>();
        for (MModel level : orderedLevels(mlm)) {
            List<MClass> classes = new ArrayList<>(level.classes());
            classes.sort(Comparator.comparing(MClass::name));
            allClasses.addAll(classes);
        }

        for (MClass cls : allClasses) {
            printClass(out, cls, mlm);
        }

        for (MModel level : orderedLevels(mlm)) {
            List<MAssociation> assocs = new ArrayList<>(level.associations());
            assocs.sort(Comparator.comparing(MAssociation::name));
            for (MAssociation assoc : assocs) {
                printAssociation(out, assoc);
            }
        }
        out.println();

        List<MClassInvariant> localInvariants = new ArrayList<>();
        for (MClass cls : allClasses) {
            for (MClassInvariant inv : mlm.allClassInvariants(cls)) {
                if (inv.cls().equals(cls)) {
                    localInvariants.add(inv);
                }
            }
        }
        if (!localInvariants.isEmpty()) {
            out.println("constraints");
            for (MClassInvariant inv : localInvariants) {
                printInvariant(out, inv);
            }
        }

        out.flush();
        return sw.toString();
    }

    private static void printClass(PrintWriter out, MClass cls, MMultiLevelModel mlm) {
        out.println("class " + shortName(cls.name()));

        List<MAttribute> attrs = new ArrayList<>(cls.allAttributes());
        if (!attrs.isEmpty()) {
            attrs.sort(Comparator.comparing(MAttribute::name));
            out.println("attributes");
            for (MAttribute attr : attrs) {
                out.println("  " + attr.name() + " : " + shortName(attr.type().toString()));
            }
        }
        out.println("end");

        Set<String> inheritedRoles = new TreeSet<>(cls.navigableEnds().keySet());
        if (cls instanceof MInternalClassImpl) {
            inheritedRoles.removeAll(((MInternalClassImpl) cls).navigableElements().keySet());
        }
        if (!inheritedRoles.isEmpty()) {
            out.println("-- " + shortName(cls.name()) + " also inherits navigable role(s) "
                    + String.join(", ", inheritedRoles)
                    + " from its powerclass (not restated as a separate association here)");
        }

        List<String> inheritedInvariants = new ArrayList<>();
        for (MClassInvariant inv : mlm.allClassInvariants(cls)) {
            if (!inv.cls().equals(cls)) {
                inheritedInvariants.add(shortName(inv.name()));
            }
        }
        if (!inheritedInvariants.isEmpty()) {
            Collections.sort(inheritedInvariants);
            out.println("-- " + shortName(cls.name()) + " also inherits invariant(s) "
                    + String.join(", ", inheritedInvariants)
                    + " from its powerclass (printed once, at its origin, below)");
        }
        out.println();
    }

    private static void printAssociation(PrintWriter out, MAssociation assoc) {
        out.println("association " + shortName(assoc.name()) + " between");
        for (MAssociationEnd end : assoc.associationEnds()) {
            out.print("  " + shortName(end.cls().name()) + "[" + end.multiplicity() + "] role " + end.name());
            out.println();
        }
        out.println("end");
        out.println();
    }

    private static void printInvariant(PrintWriter out, MClassInvariant inv) {
        out.print("context " + shortName(inv.cls().name()) + " inv " + shortName(inv.name()) + ":");
        out.println();
        out.print("  ");
        StringWriter bodySw = new StringWriter();
        PrintWriter bodyPw = new PrintWriter(bodySw);
        ExpressionVisitor visitor = new ExpressionPrintVisitor(bodyPw);
        inv.bodyExpression().processWithVisitor(visitor);
        bodyPw.flush();
        out.println(shortName(bodySw.toString()));
        out.println();
    }

    private static String shortName(String qualifiedName) {
        if (qualifiedName.indexOf('@') < 0) {
            return qualifiedName;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < qualifiedName.length()) {
            int at = qualifiedName.indexOf('@', i);
            if (at < 0) {
                sb.append(qualifiedName, i, qualifiedName.length());
                break;
            }
            int start = at;
            while (start > i && Character.isJavaIdentifierPart(qualifiedName.charAt(start - 1))) {
                start--;
            }
            sb.append(qualifiedName, i, start);
            int end = at + 1;
            while (end < qualifiedName.length() && Character.isJavaIdentifierPart(qualifiedName.charAt(end))) {
                end++;
            }
            sb.append(qualifiedName, at + 1, end);
            i = end;
        }
        return sb.toString();
    }

    /**
     * Parent levels before children, using each mediator's parent link.
     * Levels form a simple chain (each mediator has at most one parent
     * model) in this system, so a depth-by-walking-parents sort is enough.
     */
    private static List<MModel> orderedLevels(MMultiLevelModel mlm) {
        Map<String, MModel> parentOf = new HashMap<>();
        for (MMediator mediator : mlm.mediators()) {
            if (mediator.getParentModel() != null) {
                parentOf.put(mediator.getCurrentModel().name(), mediator.getParentModel());
            }
        }

        Map<String, Integer> depth = new HashMap<>();
        for (MModel model : mlm.models()) {
            int d = 0;
            String curName = model.name();
            Set<String> seen = new HashSet<>();
            while (parentOf.containsKey(curName) && seen.add(curName)) {
                curName = parentOf.get(curName).name();
                d++;
            }
            depth.put(model.name(), d);
        }

        List<MModel> levels = new ArrayList<>(mlm.models());
        levels.sort(Comparator.comparingInt(m -> depth.getOrDefault(m.name(), 0)));
        return levels;
    }
}
