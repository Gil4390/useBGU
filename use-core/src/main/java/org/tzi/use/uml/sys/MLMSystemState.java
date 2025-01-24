package org.tzi.use.uml.sys;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.tzi.use.config.Options;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.util.Log;
import org.tzi.use.util.StringUtil;
import org.tzi.use.util.collections.Bag;
import org.tzi.use.util.collections.Queue;

import java.io.PrintWriter;
import java.util.*;

public class MLMSystemState extends MSystemState{
    MLMSystemState(String name, MSystem system) {
        super(name, system);
    }

    public MLMSystemState(String name, MSystemState x) {
        super(name, x);
    }

    public enum Definedness {
        WellDefined, NotWellDefined, PartiallyDefined
    }

    public Definedness checkWellDefinedness(PrintWriter out, boolean traceEvaluation,
                                            boolean showDetails, boolean allInvariants, final List<String> invNames) {
        Definedness valid = Definedness.WellDefined;
        Evaluator evaluator = new Evaluator();

        // model inherent constraints: check whether cardinalities of
        // association links match their declaration of multiplicities
        valid = checkWellDefinedStructure(out);

        if (Options.EVAL_NUMTHREADS > 1)
            out.println("checking invariants (using " + Options.EVAL_NUMTHREADS
                    + " concurrent threads)...");
        else
            out.println("checking invariants...");

        out.flush();
        int numChecked = 0;
        int numFailed = 0;
        long tAll = System.currentTimeMillis();

        ArrayList<MClassInvariant> invList = new ArrayList<MClassInvariant>();
        ArrayList<Boolean> negatedList = new ArrayList<Boolean>();
        ArrayList<Expression> exprList = new ArrayList<Expression>();
        Collection<MClassInvariant> source;

        if (invNames.isEmpty()) {
            source = system().model().classInvariants();
        } else {
            source = Collections2.filter(system().model().classInvariants(),
                    new Predicate<MClassInvariant>() {
                        @Override
                        public boolean apply(MClassInvariant input) {
                            return invNames.contains(input.name());
                        }
                    });
        }

        for (MClassInvariant inv : source) {

            // Ignore if deactivated and not all should be checked.
            if (!allInvariants && !inv.isActive()) continue;

            Expression expr = inv.expandedExpression();

            if (inv.isNegated()) {
                try {
                    Expression[] args = { expr };
                    Expression expr1 = ExpStdOp.create("not", args);
                    expr = expr1;
                } catch (ExpInvalidException e) {
                    // This cannot happen, since in invariant is a boolean expression
                    // (checked by MClassInvariant constructor)
                }
                negatedList.add(Boolean.TRUE);
            } else {
                negatedList.add(Boolean.FALSE);
            }
            invList.add(inv);
            exprList.add(expr);
        }

        // start (possibly concurrent) evaluation
        Queue resultValues = evaluator.evalList(Options.EVAL_NUMTHREADS,
                exprList, this);

        // receive results
        for (int i = 0; i < exprList.size(); i++) {
            MClassInvariant inv = invList.get(i);
            numChecked++;
            String msg = "checking invariant (" + numChecked + ") `"
                    + inv.cls().name() + "::" + inv.name() + "': ";
            out.print(msg); // + inv.bodyExpression());
            out.flush();
            try {
                Value v = (Value) resultValues.get();

                // if value 'v' is null, the invariant can not be evaluated,
                // therefore it is N/A (not available).
                if (v == null) {
                    out.println("N/A");
                    // if there is a value, the invariant can always be
                    // evaluated and the
                    // result can be printed.
                } else {
                    boolean ok = v.isDefined() && ((BooleanValue) v).isTrue();
                    if (ok)
                        out.println("OK."); // (" + timeStr +").");
                    else {
                        out.println("FAILED."); // (" + timeStr +").");
                        out.println("  -> " + v.toStringWithType());

                        // repeat evaluation with output of all subexpression
                        // results
                        if (traceEvaluation) {
                            out.println("Results of subexpressions:");
                            Expression expr = exprList.get(i);
                            evaluator.eval(expr, this, new VarBindings(), out);
                        }

                        // show instances violating the invariant by using
                        // the OCL expression C.allInstances->reject(self |
                        // <inv>)
                        if (showDetails) {
                            out.println("Instances of " + inv.cls().name()
                                    + " violating the invariant:");
                            Expression expr = inv
                                    .getExpressionForViolatingInstances();
                            Value v1 = evaluator.eval(expr, this,
                                    new VarBindings());
                            out.println("  -> " + v1.toStringWithType());
                        }
                        valid = Definedness.NotWellDefined;
                        numFailed++;
                    }
                }
            } catch (InterruptedException ex) {
                Log.error("InterruptedException: " + ex.getMessage());
            }
        }

        long t = System.currentTimeMillis() - tAll;
        String timeStr = t % 1000 + "s";
        timeStr = (t / 1000) + "." + StringUtil.leftPad(timeStr, 4, '0');
        out.println("checked " + numChecked + " invariant"
                + ((numChecked == 1) ? "" : "s") + (Options.testMode ? "" : " in " + timeStr) + ", "
                + numFailed + " failure" + ((numFailed == 1) ? "" : "s") + '.');
        out.flush();
        return valid;
    }

    public Definedness checkWellDefinedStructure(PrintWriter out) {
        return checkWellDefinedStructure(out, true);
    }

    public Definedness checkWellDefinedStructure(PrintWriter out, boolean reportAllErrors) {
        long start = System.currentTimeMillis();

        Definedness res = Definedness.WellDefined;
        out.println("checking structure of level " + system().model() + "...");
        out.flush();

        updateDerivedValues(true);

        // check the whole/part hierarchy
        if (!checkWholePartLink(out)) {
            if (!reportAllErrors) return Definedness.NotWellDefined;
            res = Definedness.NotWellDefined;
        }

        // check all associations
        for (MAssociation assoc : system().model().associations()) {
            Definedness res2 = checkWellDefinedStructure(assoc, out, reportAllErrors);
            if (res2 == Definedness.NotWellDefined || res == Definedness.NotWellDefined) {
                res = Definedness.NotWellDefined;
            } else if (res2 == Definedness.PartiallyDefined) {
                res = Definedness.PartiallyDefined;
            }
            if (!reportAllErrors && res == Definedness.NotWellDefined) return Definedness.NotWellDefined;
        }

        out.flush();

        if (!Options.testMode) {
            long duration = System.currentTimeMillis() - start;
            out.println(String.format("checked structure in %,dms.", duration));
        }

        return res;
    }

    public Definedness checkWellDefinedStructure(MAssociation assoc, PrintWriter out, boolean reportAllErrors) {
        Definedness res = Definedness.WellDefined;

        boolean valid = validateRedefines(assoc, out, reportAllErrors);
        if (!valid) res = Definedness.NotWellDefined;

        if (assoc.associationEnds().size() != 2) {
            // check for n-ary links
            Definedness res1 = naryAssociationsAreWellDefined(out, assoc, reportAllErrors);
            if (res1 == Definedness.NotWellDefined || res == Definedness.NotWellDefined) {
                res = Definedness.NotWellDefined;
            } else if (res1 == Definedness.PartiallyDefined) {
                res = Definedness.PartiallyDefined;
            }
        } else {
            // check both association ends
            Iterator<MAssociationEnd> it2 = assoc.associationEnds().iterator();
            MAssociationEnd aend1 = it2.next();
            MAssociationEnd aend2 = it2.next();

            Definedness res2 = validateWellDefinedBinaryAssociations(out, assoc, aend1, aend2, reportAllErrors);
            if (res2 == Definedness.NotWellDefined || res == Definedness.NotWellDefined) {
                res = Definedness.NotWellDefined;
            } else if (res2 == Definedness.PartiallyDefined) {
                res = Definedness.PartiallyDefined;
            }
            if (!reportAllErrors && res == Definedness.NotWellDefined) return Definedness.NotWellDefined;


            Definedness res3 = validateWellDefinedBinaryAssociations(out, assoc, aend2, aend1, reportAllErrors);
            if (res3 == Definedness.NotWellDefined || res == Definedness.NotWellDefined) {
                res = Definedness.NotWellDefined;
            } else if (res3 == Definedness.PartiallyDefined) {
                res = Definedness.PartiallyDefined;
            }
        }

        out.flush();
        return res;
    }

    private Definedness naryAssociationsAreWellDefined(PrintWriter out, MAssociation assoc, boolean reportAllErrors) {
        Definedness valid = Definedness.WellDefined;
        Set<MLink> links = linksOfAssociation(assoc).links();

        for (MAssociationEnd selEnd : assoc.associationEnds()) {
            List<MAssociationEnd> otherEnds = selEnd.getAllOtherAssociationEnds();
            List<MClass> classes = new ArrayList<MClass>();

            for (MAssociationEnd end : otherEnds) {
                classes.add(end.cls());
            }

            Bag<MObject[]> crossProduct = getCrossProductOfInstanceSets(classes);

            for (MObject[] tuple : crossProduct) {
                int count = 0;

                for (MLink link : links) {
                    boolean ok = true;
                    int index = 0;

                    for (MAssociationEnd end : otherEnds) {
                        if (link.linkEnd(end).object() != tuple[index]) {
                            ok = false;
                        }
                        ++index;
                    }
                    if (ok)
                        ++count;
                }
                if (!selEnd.multiplicity().contains(count)) {

                    int largestLowerBound = selEnd.multiplicity().getLargestLowerBound();
                    if (count < largestLowerBound){
                        //case 1: num of obj is less the largest lower bound - partial
                        valid = Definedness.PartiallyDefined;
                    }
                    else{
                        //case 2: num of obj is greater than the largest upper bound - illegal
                        valid = Definedness.NotWellDefined;
                        out.println("Multiplicity constraint violation in association `"
                                + assoc.name() + "':");
                        out.println("  Objects `" + StringUtil.fmtSeq(tuple, ", ")
                                + "' are connected to " + count + " object"
                                + ((count == 1) ? "" : "s") + " of class `"
                                + selEnd.cls().name() + "'");
                        out.println("  but the multiplicity is specified as `"
                                + selEnd.multiplicity() + "'.");
                    }
                }
            }
            if (!reportAllErrors && valid == Definedness.NotWellDefined) return Definedness.NotWellDefined;
        }
        return valid;
    }

    private Definedness validateWellDefinedBinaryAssociations(PrintWriter out, MAssociation assoc,
                                                              MAssociationEnd aend1, MAssociationEnd aend2, boolean reportAllErrors) {
        Definedness valid = Definedness.WellDefined;

        // for each object of the association end's type get
        // the number of links in which the object participates
        MClass cls = aend1.cls();
        Set<MObject> objects = objectsOfClassAndSubClasses(cls);

        for (MObject obj : objects) {
            Map<List<Value>,Set<MObject>> linkedObjects = getLinkedObjects(obj, aend1, aend2);

            if (linkedObjects.size() == 0 && !aend2.multiplicity().contains(0)) {
                //reportMultiplicityViolation(out, assoc, aend1, aend2, obj, null);
                valid = Definedness.PartiallyDefined;
                continue;
            }

            for(Map.Entry<List<Value>, Set<MObject>> entry : linkedObjects.entrySet()) {
                if (!aend2.multiplicity().contains(entry.getValue().size())) {
                    int largestLowerBound = aend2.multiplicity().getLargestLowerBound();
                    if (entry.getValue().size() < largestLowerBound){
                        //case 1: num of obj is less the largest lower bound - partial
                        valid = Definedness.PartiallyDefined;
                    }
                    else{
                        //case 2: num of obj is greater than the largest upper bound - illegal
                        reportMultiplicityViolation(out, assoc, aend1, aend2, obj, entry);
                        valid = Definedness.NotWellDefined;
                    }
                }

                if (!aend1.getSubsettedEnds().isEmpty()) {
                    if (!validateSubsets(out, obj, entry.getKey(), entry.getValue(), aend1)){
                        valid = Definedness.NotWellDefined;
                    }
                }
            }

            if (!reportAllErrors && valid == Definedness.NotWellDefined) return Definedness.NotWellDefined;
        }

        return valid;
    }



}
