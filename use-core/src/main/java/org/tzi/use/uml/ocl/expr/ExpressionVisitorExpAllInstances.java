package org.tzi.use.uml.ocl.expr;

import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.ocl.type.Type;

public class ExpressionVisitorExpAllInstances implements ExpressionVisitor {

    private MClassInvariant inv;
    // collection literal?

    public ExpressionVisitorExpAllInstances(MClassInvariant inv) {
        this.inv = inv;
    }

    public void visitAllInstancesForInv(ExpAllInstancesForInv exp) {
        exp.setInvariant(inv);
    }

    @Override
    public void visitAllInstances(ExpAllInstances exp) {
        if (exp instanceof ExpAllInstancesForInv){
            visitAllInstancesForInv((ExpAllInstancesForInv) exp);
        }
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
        //
    }

    @Override
    public void visitConstEnum(ExpConstEnum exp) {
        //
    }

    @Override
    public void visitConstInteger(ExpConstInteger exp) {
        //
    }

    @Override
    public void visitConstReal(ExpConstReal exp) {
        //
    }

    @Override
    public void visitConstString(ExpConstString exp) {
        //
    }

    @Override
    public void visitEmptyCollection(ExpEmptyCollection exp) {
        //
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
        for (Expression e : exp.getQualifierExpression()) {
            e.processWithVisitor(this);
        }
    }

    @Override
    public void visitObjAsSet(ExpObjAsSet exp) {
        exp.getObjectExpression().processWithVisitor(this);

    }

    @Override
    public void visitInstanceOp(ExpInstanceOp exp) {
        for (Expression expArgs : exp.getArguments()) {
            expArgs.processWithVisitor(this);
        }
    }

    @Override
    public void visitObjRef(ExpObjRef exp) {
        //
    }

    @Override
    public void visitOne(ExpOne exp) {
        visitQuery(exp);
    }

    @Override
    public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
        visitCollectionLiteral(exp);
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
        //
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
        for (Expression e : exp.args()) {
            e.processWithVisitor(this);
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
        //
    }

    @Override
    public void visitVariable(ExpVariable exp) {
        //
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
        //
    }

    @Override
    public void visitObjectByUseId(ExpObjectByUseId exp) {
        exp.processWithVisitor(this);
    }

    @Override
    public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp) {
        //
    }

    @Override
    public void visitSelectByKind(ExpSelectByKind exp) {
        exp.getSourceExpression().processWithVisitor(this);
    }

    @Override
    public void visitExpSelectByType(ExpSelectByType exp) {
        visitSelectByKind(exp);
    }

    @Override
    public void visitRange(ExpRange exp) {
        exp.getStart().processWithVisitor(this);
        exp.getEnd().processWithVisitor(this);
    }

    @Override
    public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
        exp.getObjectExpression().processWithVisitor(this);
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

    private void visitCollectionLiteral(ExpCollectionLiteral exp) {
        for (Expression elemExp : exp.getElemExpr()) {
            elemExp.processWithVisitor(this);
        }
    }
}
