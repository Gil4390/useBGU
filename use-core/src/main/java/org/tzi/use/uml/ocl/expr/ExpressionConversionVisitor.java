package org.tzi.use.uml.ocl.expr;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.ocl.type.Type;

import java.io.PrintWriter;
import java.util.Map;

public class ExpressionConversionVisitor implements ExpressionVisitor {


    private Map<String, MClass> classes;

    public ExpressionConversionVisitor(Map<String, MClass> classes) {
        this.classes = classes;
    }

    private void visitCollectionLiteral(ExpCollectionLiteral exp) {
        for (Expression elemExp : exp.getElemExpr()) {
            elemExp.processWithVisitor(this);
        }
    }

    @Override
    public void visitAllInstances(ExpAllInstances exp) {

    }

    @Override
    public void visitAny(ExpAny exp) {
        visitQuery(exp);
    }

    @Override
    public void visitAsType(ExpAsType exp) {
        exp.getSourceExpr().processWithVisitor(this);
    }

    @Override
    public void visitAttrOp(ExpAttrOp exp) {
        exp.objExp().processWithVisitor(this);
    }

    @Override
    public void visitBagLiteral(ExpBagLiteral exp) {
        visitCollectionLiteral(exp);
    }

    @Override
    public void visitCollect(ExpCollect exp) {
        visitQuery(exp);
    }

    @Override
    public void visitCollectNested(ExpCollectNested exp) {
        visitQuery(exp);
    }

    @Override
    public void visitConstBoolean(ExpConstBoolean exp) {

    }

    @Override
    public void visitConstEnum(ExpConstEnum exp) {

    }

    @Override
    public void visitConstInteger(ExpConstInteger exp) {
    }

    @Override
    public void visitConstReal(ExpConstReal exp) {
    }

    @Override
    public void visitConstString(ExpConstString exp) {

    }

    @Override
    public void visitEmptyCollection(ExpEmptyCollection exp) {

    }

    @Override
    public void visitExists(ExpExists exp) {
        visitQuery(exp);
    }

    @Override
    public void visitForAll(ExpForAll exp) {
        visitQuery(exp);
    }

    @Override
    public void visitIf(ExpIf exp) {
        exp.getCondition().processWithVisitor(this);
        exp.getThenExpression().processWithVisitor(this);
        exp.getElseExpression().processWithVisitor(this);
    }

    @Override
    public void visitIsKindOf(ExpIsKindOf exp) {
        exp.getSourceExpr().processWithVisitor(this);
    }

    @Override
    public void visitIsTypeOf(ExpIsTypeOf exp) {
        exp.getSourceExpr().processWithVisitor(this);
    }

    @Override
    public void visitIsUnique(ExpIsUnique exp) {
        visitQuery(exp);
    }

    @Override
    public void visitIterate(ExpIterate exp) {
        visitQuery(exp, exp.getAccuInitializer());
    }

    @Override
    public void visitLet(ExpLet exp) {
        exp.getVarExpression().processWithVisitor(this);
        exp.getInExpression().processWithVisitor(this);
    }

    @Override
    public void visitNavigation(ExpNavigation exp) {
        exp.getObjectExpression().processWithVisitor(this);

        MNavigableElement src = exp.getSource();
        MNavigableElement dest = exp.getDestination();

        Map<String, MClass> classes = this.classes;
        // in conversion

        if (!classes.containsKey(src.cls().name())) {
            String srcModelName = src.cls().model().name();
            String srcClassName = src.cls().name();
            String convertedSrcClassName = srcModelName + "@" + srcClassName;
            Map<String, ? extends MNavigableElement> ends1 = classes.get(convertedSrcClassName).navigableEnds();

            String dstModelName = dest.cls().model().name();
            String dstClassName = dest.cls().name();
            String convertedDstClassName = dstModelName + "@" + dstClassName;
            Map<String, ? extends MNavigableElement> ends2 = classes.get(convertedDstClassName).navigableEnds();

            for (Map.Entry<String, ? extends MNavigableElement> entry : ends1.entrySet()) {
                MNavigableElement value = entry.getValue();
                if (value.cls().name().equals(convertedDstClassName)
                        && dest.nameAsRolename().equals(value.nameAsRolename())) {
                    dest = value;
                    break;
                }
            }

            for (Map.Entry<String, ? extends MNavigableElement> entry : ends2.entrySet()) {
                MNavigableElement value = entry.getValue();
                if (value.cls().name().equals(convertedSrcClassName)
                        && src.nameAsRolename().equals(value.nameAsRolename())) {
                    src = value;
                    break;
                }
            }

            exp.setSource(src);
            exp.setDestination(dest);
        }

        if(exp.getQualifierExpression().length > 0){
            // qualifier values
            for(Expression e : exp.getQualifierExpression()){
                e.processWithVisitor(this);
            }
        }
    }

    @Override
    public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
        exp.getObjectExpression().processWithVisitor(this);
    }

    @Override
    public void visitObjAsSet(ExpObjAsSet exp) {
        exp.getObjectExpression().processWithVisitor(this);
    }

    @Override
    public void visitObjOp(ExpObjOp exp) {
        exp.getArguments()[0].processWithVisitor(this);

        for (int i = 1; i < exp.getArguments().length; ++i) {

            exp.getArguments()[i].processWithVisitor(this);
        }
    }

    @Override
    public void visitObjRef(ExpObjRef exp) {
    }

    @Override
    public void visitOne(ExpOne exp) {
        visitQuery(exp);
    }

    @Override
    public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
        visitCollectionLiteral(exp);
    }

    private void visitQuery(ExpQuery exp, VarInitializer accuInitializer) {
        exp.getRangeExpression().processWithVisitor(this);

        exp.getVariableDeclarations().processWithVisitor(this);
        if (accuInitializer != null) {
            accuInitializer.getVarDecl().processWithVisitor(this);
            accuInitializer.initExpr().processWithVisitor(this);
        }
        exp.getQueryExpression().processWithVisitor(this);
    }

    @Override
    public void visitQuery(ExpQuery exp) {
        visitQuery(exp, null);
    }

    @Override
    public void visitReject(ExpReject exp) {
        visitQuery(exp);
    }

    @Override
    public void visitWithValue(ExpressionWithValue exp) {
    }

    @Override
    public void visitSelect(ExpSelect exp) {
        visitQuery(exp);
    }

    @Override
    public void visitSequenceLiteral(ExpSequenceLiteral exp) {
        visitCollectionLiteral(exp);
    }

    @Override
    public void visitSetLiteral(ExpSetLiteral exp) {
        visitCollectionLiteral(exp);
    }

    @Override
    public void visitSortedBy(ExpSortedBy exp) {
        visitQuery(exp);
    }

    @Override
    public void visitStdOp(ExpStdOp exp) {
        Expression[] args = exp.args();

        if(exp.getOperation().isInfixOrPrefix()){
            if(args.length == 1){
                args[0].processWithVisitor(this);
            } else { // Infix has two arguments
                args[0].processWithVisitor(this);
                args[1].processWithVisitor(this);
            }
        } else {
            if(args.length == 0){
            } else {
                args[0].processWithVisitor(this);

                if(args.length > 1){
                    for(int i = 1; i < args.length; i++){

                        args[i].processWithVisitor(this);
                    }
                }
            }
        }
    }

    @Override
    public void visitTupleLiteral(ExpTupleLiteral exp) {
        for(ExpTupleLiteral.Part p : exp.getParts()){
            p.getExpression().processWithVisitor(this);
        }
    }

    @Override
    public void visitTupleSelectOp(ExpTupleSelectOp exp) {
        exp.getTupleExp().processWithVisitor(this);
    }

    @Override
    public void visitUndefined(ExpUndefined exp) {
    }

    @Override
    public void visitVariable(ExpVariable exp) {

    }

    @Override
    public void visitClosure(ExpClosure exp) {
        visitQuery(exp);
    }

    @Override
    public void visitOclInState(ExpOclInState exp) {
        exp.getSourceExpr().processWithVisitor(this);
    }

    @Override
    public void visitVarDeclList(VarDeclList varDeclList) {
        for (int i = 0; i < varDeclList.size(); ++i) {
            varDeclList.varDecl(i).processWithVisitor(this);
        }
    }

    @Override
    public void visitVarDecl(VarDecl varDecl) {

    }

    @Override
    public void visitObjectByUseId(ExpObjectByUseId expObjectByUseId) {

        expObjectByUseId.processWithVisitor(this);

    }

    @Override
    public void visitConstUnlimitedNatural(
            ExpConstUnlimitedNatural expressionConstUnlimitedNatural) {
    }

    @Override
    public void visitSelectByKind(ExpSelectByKind expSelectByKind) {
        expSelectByKind.getSourceExpression().processWithVisitor(this);
    }

    @Override
    public void visitExpSelectByType(ExpSelectByType expSelectByType) {
        visitSelectByKind(expSelectByType);
    }

    @Override
    public void visitRange(ExpRange exp) {
        exp.getStart().processWithVisitor(this);
        exp.getEnd().processWithVisitor(this);
    }
}
