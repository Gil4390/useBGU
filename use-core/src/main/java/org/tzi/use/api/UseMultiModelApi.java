package org.tzi.use.api;

import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.SrcPos;
import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.util.NullPrintWriter;
import org.tzi.use.util.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UseMultiModelApi {

    private MMultiModel mMultiModel;

    ModelFactory mFactory = new ModelFactory();

    public UseMultiModelApi() {
        mMultiModel = mFactory.createMultiModel("unnamed");
    }

    public UseMultiModelApi(String modelName) {
        mMultiModel = mFactory.createMultiModel(modelName);
    }

    public UseMultiModelApi(MMultiModel multimodel) {
        mMultiModel = multimodel;
    }

    public MMultiModel getMultiModel() {
        return mMultiModel;
    }

    public void addModel(MModel model) throws Exception {
        mMultiModel.addModel(model);
    }

    public void removeModel(String modelName) throws Exception {
        mMultiModel.removeModel(modelName);
    }

    public MInterAssociation createInterAssociation(String associationName, String end1ModelName, String end2ModelName,
                                       String end1ClassName, String end1RoleName, String end1Multiplicity, int end1Aggregation,
                                       String end2ClassName, String end2RoleName, String end2Multiplicity, int end2Aggregation) throws UseApiException {

        MModel model1 = mMultiModel.getModel(end1ModelName);
        MModel model2 = mMultiModel.getModel(end2ModelName);

        MInterAssociation assoc = new MInterAssociation(associationName);

        try {
            MAssociationEnd end1 = createAssociationEnd(model1, model1.getClass(end1ClassName),
                    end1RoleName, end1Multiplicity, end1Aggregation, false, new String[0][]);

            MAssociationEnd end2 = createAssociationEnd(model2, model2.getClass(end2ClassName),
                    end2RoleName, end2Multiplicity, end2Aggregation, false, new String[0][]);

            assoc.addAssociationEnd(end1);
            assoc.addAssociationEnd(end2);

            model1.addAssociation(assoc);
            mMultiModel.addInterAssociation(assoc);
        } catch (MInvalidModelException e) {
            throw new UseApiException("Association creation failed", e);
        }

        return assoc;

    }

    private MAssociationEnd createAssociationEnd(MModel model, MClass classEnd,
                                                 String endRoleName, String endMultiplicity, int endAggregation, boolean isOrdered, String[][] qualifier)
            throws UseApiException {


        MMultiplicity m = USECompiler.compileMultiplicity(endMultiplicity,
                "Use Api", NullPrintWriter.getInstance(), mFactory);

        List<VarDecl> qualifierDecl;

        if (qualifier.length > 0) {
            qualifierDecl = new ArrayList<VarDecl>(qualifier.length);
            for (int i = 0; i < qualifier.length; ++i) {
                if (qualifier[i].length != 2) {
                    throw new UseApiException("Qualifiers must be defined with a name and a type");
                }

                Type t = getType(qualifier[i][1], model);
                qualifierDecl.add(new VarDecl(qualifier[i][0], t));
            }
        } else {
            qualifierDecl = Collections.emptyList();
        }

        MAssociationEnd end = new MAssociationEnd(classEnd, endRoleName, m, endAggregation, isOrdered, qualifierDecl);
        return end;
    }

    public MClassInvariant createInterInvariant(String invName, String modelName, String contextName,
                                                String invBody, boolean isExistential) throws UseApiException {

         MModel mModel = mMultiModel.getModel(modelName);
        UseModelApi api = new UseModelApi(mModel);

        MClass cls = api.getClassSafe(contextName);

        Symtable vars = new Symtable();
        try {
            vars.add("self", cls, new SrcPos("self", 1, 1));
        } catch (SemanticException e1) {
            throw new UseApiException("Could not add " + StringUtil.inQuotes("self") + " to symtable.", e1);
        }

        StringWriter errBuffer = new StringWriter();
        PrintWriter errorPrinter = new PrintWriter(errBuffer, true);

        Expression invExp = OCLCompiler.compileExpression(mMultiModel, mModel, invBody, "UseApi", errorPrinter, vars);

        if (invExp == null) {
            throw new UseApiException(errBuffer.toString());
        }

        MClassInvariant inv = api.createInvariantEx(invName, contextName, invExp, isExistential);
        mMultiModel.addInterConstraint(inv);
        return inv;
    }

    public Type getType(String typeExpr, MModel mModel) throws UseApiException {
        Type type;
        type = OCLCompiler.compileType(mModel, typeExpr, "UseApi", NullPrintWriter.getInstance());

        if (type == null) {
            throw new UseApiException("Invalid type expression "
                    + StringUtil.inQuotes(typeExpr) + ".");
        }

        return type;
    }

    public MInterAssociation getInterAssociationSafe(String interAssociationName) throws UseApiException {
        MInterAssociation association =  this.mMultiModel.getInterAssociations(interAssociationName);

        if (association == null) {
            throw new UseApiException("Unknown inter association named " + StringUtil.inQuotes(interAssociationName) +".");
        }

        return association;
    }

}
