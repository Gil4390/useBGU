package org.tzi.use.tools.catuselatex;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.expr.ExpressionPrintVisitor;
import org.tzi.use.uml.ocl.expr.ExpressionVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Pattern;

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
 *
 * <p><b>Name disambiguation:</b> a class's internal name is "Level@Class"
 * (level first) -- but plain USE identifiers can't contain "@" at all, and
 * a flattened model merges every level's classes into one single
 * {@code model} block, so two different levels' classes sharing a short
 * name (legitimate and common in MLM-USE, since each level has its own
 * independent namespace) would otherwise both print as the same bare
 * {@code class X}, producing invalid, non-re-parseable output ("Redefinition
 * of X"). Classes are therefore printed under their bare short name only
 * when that name is unique across the whole flattened model; a colliding
 * name is printed instead as {@code ClassName__AT__LevelName} (class first,
 * synthesized separator last -- see {@link #flatten}), a legal plain-USE
 * identifier. "__AT__" is reserved for this purpose: see
 * {@link #checkNoReservedTokens}.
 */
public class FlatUseTextRenderer {

    /**
     * "__AT__" is reserved for this renderer's own synthesized
     * disambiguation names. A user's own identifier may still contain the
     * substring as long as it isn't exactly this token -- i.e. it's fine
     * if flanked by an extra underscore on either side (that makes the
     * underscore run three-or-more long, no longer an exact match):
     * "this___AT__is_ok" and "this__AT__is_ok__" are fine, "this__AT__is_not"
     * is not, because there its underscore runs immediately around "AT" are
     * exactly two on both sides.
     */
    private static final Pattern RESERVED_AT_TOKEN = Pattern.compile("(?<!_)__AT__(?!_)");

    public static String render(MMultiLevelModel mlm, String displayName) {
        checkNoReservedTokens(mlm);

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

        Map<String, Long> shortNameCounts = new HashMap<>();
        for (MClass cls : allClasses) {
            shortNameCounts.merge(classPart(cls.name()), 1L, Long::sum);
        }

        for (MClass cls : allClasses) {
            printClass(out, cls, mlm, shortNameCounts);
        }

        for (MModel level : orderedLevels(mlm)) {
            List<MAssociation> assocs = new ArrayList<>(level.associations());
            assocs.sort(Comparator.comparing(MAssociation::name));
            for (MAssociation assoc : assocs) {
                printAssociation(out, assoc, shortNameCounts);
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
                printInvariant(out, inv, shortNameCounts);
            }
        }

        out.flush();
        return sw.toString();
    }

    /**
     * Rejects a model that already uses the "__AT__" token this renderer
     * reserves for its own synthesized disambiguation names (see the class
     * doc comment) -- in a level name, or in any class's own short name.
     * Checking this once up front, before rendering anything, means a
     * violation is reported as one clear error rather than surfacing later
     * as a confusing double meaning in the flattened output.
     */
    private static void checkNoReservedTokens(MMultiLevelModel mlm) {
        for (MModel level : mlm.models()) {
            if (RESERVED_AT_TOKEN.matcher(level.name()).find()) {
                throw new IllegalArgumentException("Level name \"" + level.name()
                        + "\" uses \"__AT__\", which is reserved for FlatUseTextRenderer's own "
                        + "disambiguation names. Use e.g. \"_\" or \"___AT__\"/\"__AT___\" instead.");
            }
            for (MClass cls : level.classes()) {
                String shortName = classPart(cls.name());
                if (RESERVED_AT_TOKEN.matcher(shortName).find()) {
                    throw new IllegalArgumentException("Class name \"" + shortName
                            + "\" (in level \"" + level.name() + "\") uses \"__AT__\", which is reserved "
                            + "for FlatUseTextRenderer's own disambiguation names. Use e.g. \"_\" or "
                            + "\"___AT__\"/\"__AT___\" instead.");
                }
            }
        }
    }

    private static void printClass(PrintWriter out, MClass cls, MMultiLevelModel mlm,
                                    Map<String, Long> shortNameCounts) {
        out.println("class " + flatten(cls.name(), shortNameCounts));

        List<MAttribute> attrs = new ArrayList<>(cls.allAttributes());
        if (!attrs.isEmpty()) {
            attrs.sort(Comparator.comparing(MAttribute::name));
            out.println("attributes");
            for (MAttribute attr : attrs) {
                out.println("  " + attr.name() + " : " + flatten(attr.type().toString(), shortNameCounts));
            }
        }
        out.println("end");

        Set<String> inheritedRoles = new TreeSet<>(cls.navigableEnds().keySet());
        if (cls instanceof MInternalClassImpl) {
            inheritedRoles.removeAll(((MInternalClassImpl) cls).navigableElements().keySet());
        }
        if (!inheritedRoles.isEmpty()) {
            out.println("-- " + flatten(cls.name(), shortNameCounts) + " also inherits navigable role(s) "
                    + String.join(", ", inheritedRoles)
                    + " from its powerclass (not restated as a separate association here)");
        }

        List<String> inheritedInvariants = new ArrayList<>();
        for (MClassInvariant inv : mlm.allClassInvariants(cls)) {
            if (!inv.cls().equals(cls)) {
                inheritedInvariants.add(flatten(inv.name(), shortNameCounts));
            }
        }
        if (!inheritedInvariants.isEmpty()) {
            Collections.sort(inheritedInvariants);
            out.println("-- " + flatten(cls.name(), shortNameCounts) + " also inherits invariant(s) "
                    + String.join(", ", inheritedInvariants)
                    + " from its powerclass (printed once, at its origin, below)");
        }
        out.println();
    }

    private static void printAssociation(PrintWriter out, MAssociation assoc, Map<String, Long> shortNameCounts) {
        out.println("association " + flatten(assoc.name(), shortNameCounts) + " between");
        for (MAssociationEnd end : assoc.associationEnds()) {
            out.print("  " + flatten(end.cls().name(), shortNameCounts) + "[" + end.multiplicity() + "] role " + end.name());
            out.println();
        }
        out.println("end");
        out.println();
    }

    private static void printInvariant(PrintWriter out, MClassInvariant inv, Map<String, Long> shortNameCounts) {
        out.print("context " + flatten(inv.cls().name(), shortNameCounts) + " inv " + flatten(inv.name(), shortNameCounts) + ":");
        out.println();
        out.print("  ");
        StringWriter bodySw = new StringWriter();
        PrintWriter bodyPw = new PrintWriter(bodySw);
        ExpressionVisitor visitor = new ExpressionPrintVisitor(bodyPw);
        inv.bodyExpression().processWithVisitor(visitor);
        bodyPw.flush();
        out.println(flatten(bodySw.toString(), shortNameCounts));
        out.println();
    }

    /** The class-name half of an internal "Level@Class" name; unchanged if there is no "@". */
    private static String classPart(String qualifiedName) {
        int at = qualifiedName.indexOf('@');
        return at < 0 ? qualifiedName : qualifiedName.substring(at + 1);
    }

    /**
     * Resolves one already-split "Level@Class" pair to its flattened,
     * plain-USE-legal spelling: the bare class name if it's unique across
     * the whole flattened model, or "Class__AT__Level" (class first) if
     * some other level also has a class with this same short name.
     */
    private static String resolve(String level, String className, Map<String, Long> shortNameCounts) {
        if (shortNameCounts.getOrDefault(className, 0L) > 1) {
            return className + "__AT__" + level;
        }
        return className;
    }

    /**
     * Scans arbitrary text (a type name, an OCL expression body, ...) for
     * every "Level@Class" occurrence and replaces each with its resolved,
     * plain-USE-legal spelling (see {@link #resolve}), leaving everything
     * else untouched.
     */
    private static String flatten(String qualifiedName, Map<String, Long> shortNameCounts) {
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
            String level = qualifiedName.substring(start, at);
            String className = qualifiedName.substring(at + 1, end);
            sb.append(resolve(level, className, shortNameCounts));
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
