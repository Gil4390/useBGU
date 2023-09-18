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

public class UseMultiModelApi extends UseModelApi{

    private MMultiModel mMultiModel;
    MultiModelFactory mFactory = new MultiModelFactory();

    public UseMultiModelApi() {
        mMultiModel = mFactory.createMultiModel("unnamed");
    }

    public UseMultiModelApi(String modelName) {
        mMultiModel = mFactory.createMultiModel(modelName);
    }

    public UseMultiModelApi(MModel multimodel) {
        mMultiModel = (MMultiModel) multimodel;
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

    @Override
    public MModel getModel() {
        return mMultiModel;
    }

    //======================================================================================
    @Override
    public MClass createClass(String name, boolean isAbstract) throws UseApiException {
        if (!name.contains("@")){
            throw new UseApiException("In a MultiModel a class name must contain @");
        }

        String modelName = name.split("@")[0];
        String className = name.split("@")[1];

        MModel model = mMultiModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        if (className == null || className.equals("")) {
            throw new UseApiException("A class must be named");
        }

        MClass cls = mFactory.createClass(className, isAbstract);

        try {
            model.addClass(cls);
        } catch (MInvalidModelException e) {
            throw new UseApiException("Add class failed!", e);
        }

        return cls;
    }

    @Override
    public MAssociation createAssociation(String associationName, String[] classNames, String[] roleNames,
            String[] multiplicities, int[] aggregationKinds, boolean[] orderedInfo,
            String[][][] qualifier) throws UseApiException {

        String end1Name = classNames[0];
        String end2Name = classNames[1];
        if (!end1Name.contains("@"))
            throw new UseApiException("Class " + end1Name + " does not contain @");
        if (!end2Name.contains("@"))
            throw new UseApiException("Class " + end2Name + " does not contain @");

        String end1ModelName = end1Name.split("@")[0];
        String end2ModelName = end2Name.split("@")[0];


        if (associationName == null || associationName.equals("")) {
            throw new UseApiException("Associations must be named!");
        }

        int numEnds = classNames.length;

        if (numEnds != roleNames.length ||
                numEnds != multiplicities.length ||
                numEnds != aggregationKinds.length ||
                numEnds != orderedInfo.length ||
                (qualifier.length > 0 && qualifier.length != numEnds)) {
            throw new UseApiException("All association end information must be provided for each association end.");
        }

        MAssociation assoc = mFactory.createAssociation(associationName);
        MAssociationEnd end;

        try {
            for (int i = 0; i < numEnds; ++i) {
                end = createAssociationEnd(classNames[i],
                        roleNames[i],
                        multiplicities[i],
                        aggregationKinds[i],
                        orderedInfo[i],
                        (qualifier.length == 0 ? new String[0][] : qualifier[i]));

                assoc.addAssociationEnd(end);
            }

            if (end1ModelName.equals(end2ModelName)){
                //regular association
                MModel model = mMultiModel.getModel(end1ModelName);
                model.addAssociation(assoc);
            }
            else{
                //inter association
                mMultiModel.addAssociation(assoc);
            }

        } catch (MInvalidModelException e) {
            throw new UseApiException("Association creation failed", e);
        }

        return assoc;
    }

    @Override
    public MAssociationClass createAssociationClass(String associationClassName, boolean isAbstract, String[] parents,
            String[] classNames, String[] roleNames, String[] multiplicities, int[] aggregationKinds,
            boolean[] orderedInfo, String[][][] qualifier) throws UseApiException {

        String end1Name = classNames[0];
        String end2Name = classNames[1];
        if (!end1Name.contains("@"))
            throw new UseApiException("Class " + end1Name + "does not contain @");
        if (!end2Name.contains("@"))
            throw new UseApiException("Class " + end2Name + "does not contain @");
        if (!associationClassName.contains("@"))
            throw new UseApiException("Class " + associationClassName + "does not contain @");

        String end1ModelName = end1Name.split("@")[0];
        String end2ModelName = end2Name.split("@")[0];
        String assocModelName = associationClassName.split("@")[0];
        String assocClassName = associationClassName.split("@")[1];

        int numEnds = classNames.length;

        if ( numEnds != roleNames.length ||
                numEnds != multiplicities.length ||
                numEnds != aggregationKinds.length ||
                numEnds != orderedInfo.length ||
                (qualifier.length > 0 && qualifier.length != numEnds)) {
            throw new UseApiException("The number of class names, role names, multiplicities and aggregation kinds must match.");
        }

        MAssociationClass associationClass = mFactory.createAssociationClass(assocClassName, isAbstract);

        try {
            for (int i = 0; i < numEnds; ++i) {
                associationClass.addAssociationEnd(createAssociationEnd(
                        classNames[i], roleNames[i], multiplicities[i],
                        aggregationKinds[i], orderedInfo[i], (qualifier.length == 0 ? new String[0][] : qualifier[i])));
            }

            if (assocModelName.equals(end1ModelName) && assocModelName.equals(end2ModelName)){
                //regular association class
                MModel model = mMultiModel.getModel(assocModelName);
                model.addClass(associationClass);

                for(String p : parents){
                    createGeneralization(assocClassName, p);
                }
                model.addAssociation(associationClass);
            }
            else{
                //inter association class
                throw new UseApiException("NOT IMPLEMENTED YET");
            }

        } catch (MInvalidModelException e) {
            throw new UseApiException(e.getMessage(), e);
        }

        return associationClass;
    }

    public MClassInvariant createInvariant(String invName, String contextName,
            String invBody, boolean isExistential) throws UseApiException {

        MClass cls = getClassSafe(contextName);

        Symtable vars = new Symtable();
        try {
            vars.add("self", cls, new SrcPos("self", 1, 1));
        } catch (SemanticException e1) {
            throw new UseApiException("Could not add " + StringUtil.inQuotes("self") + " to symtable.", e1);
        }

        StringWriter errBuffer = new StringWriter();
        PrintWriter errorPrinter = new PrintWriter(errBuffer, true);

        Expression invExp = OCLCompiler.compileExpression(cls.model(), invBody, "UseApi", errorPrinter, vars);

        if (invExp == null) {
            throw new UseApiException(errBuffer.toString());
        }

        return createRegularInvariantEx(invName, contextName, invExp, isExistential);
    }

    public MClassInvariant createRegularInvariantEx(String invName, String contextName,
                                             Expression invBody, boolean isExistential) throws UseApiException {
        MClass cls = getClassSafe(contextName);

        MClassInvariant mClassInvariant = null;
        try {
            mClassInvariant = mFactory.createClassInvariant(invName, null,
                    cls, invBody, isExistential);

            cls.model().addClassInvariant(mClassInvariant);
        } catch (ExpInvalidException e) {
            throw new UseApiException("Invalid invariant expression!", e);
        } catch (MInvalidModelException e) {
            throw new UseApiException("Invariant creation failed!", e);
        }

        return mClassInvariant;
    }

    public MClassInvariant createInterInvariant(String invName, String contextName,
                                                String invBody, boolean isExistential) throws UseApiException {

        MClass cls = getClassSafe(contextName);

        Symtable vars = new Symtable();
        try {
            vars.add("self", cls, new SrcPos("self", 1, 1));
        } catch (SemanticException e1) {
            throw new UseApiException("Could not add " + StringUtil.inQuotes("self") + " to symtable.", e1);
        }

        StringWriter errBuffer = new StringWriter();
        PrintWriter errorPrinter = new PrintWriter(errBuffer, true);

        Expression invExp = OCLCompiler.compileExpression(mMultiModel, invBody, "UseApi", errorPrinter, vars);

        if (invExp == null) {
            throw new UseApiException(errBuffer.toString());
        }

        return createInvariantEx(invName, contextName, invExp, isExistential);
    }

    public MClassInvariant createInvariantEx(String invName, String contextName,
                                             Expression invBody, boolean isExistential) throws UseApiException {
        MClass cls = getClassSafe(contextName);

        MClassInvariant mClassInvariant = null;
        try {
            mClassInvariant = mFactory.createClassInvariant(invName, null,
                    cls, invBody, isExistential);

            mMultiModel.addClassInvariant(mClassInvariant);
        } catch (ExpInvalidException e) {
            throw new UseApiException("Invalid invariant expression!", e);
        } catch (MInvalidModelException e) {
            throw new UseApiException("Invariant creation failed!", e);
        }

        return mClassInvariant;
    }



    @Override
    public MClass getClassSafe(String name) throws UseApiException {
        if (!name.contains("@")){
            throw new UseApiException("Class " + name + "does not contain @");
        }

        String modelName = name.split("@")[0];
        String className = name.split("@")[1];

        MModel model = mMultiModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        MClass cls = model.getClass(className);
        if (cls == null) {
            throw new UseApiException("Unknown class " + StringUtil.inQuotes(className) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }

    @Override
    public MAssociation getAssociationSafe(String name) throws UseApiException {
        if (!name.contains("@") && mMultiModel.getInterAssociations(name) == null){
            throw new UseApiException("Association name " + name + " does not exists");
        }
        if(!name.contains("@") && mMultiModel.getInterAssociations(name) != null) {
            return mMultiModel.getInterAssociations(name);
        }
        String modelName = name.split("@")[0];
        String assocName = name.split("@")[1];

        MModel model = mMultiModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        MAssociation cls = model.getAssociation(assocName);
        if (cls == null) {
            throw new UseApiException("Unknown association named " + StringUtil.inQuotes(assocName) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }

    @Override
    public MAssociationClass getAssociationClassSafe(String name) throws UseApiException {
        if (!name.contains("@")){
            throw new UseApiException("Association name " + name + "does not contain @");
        }

        String modelName = name.split("@")[0];
        String assocClassName = name.split("@")[1];

        MModel model = mMultiModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        MAssociationClass cls = model.getAssociationClass(assocClassName);
        if (cls == null) {
            throw new UseApiException("Unknown association class " + StringUtil.inQuotes(assocClassName) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }
}
