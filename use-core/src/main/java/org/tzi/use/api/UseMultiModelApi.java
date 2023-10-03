package org.tzi.use.api;

import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.SrcPos;
import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.util.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * <p>This class encapsulates access to the USE multi model,
 * All structural modifications of a multi model can be done
 * through this class which acts as a facade to the
 * overall USE system.</p>
 *
 * @see UseModelApi
 * @author Gil Khais
 * @author Amiel Saad
 */
public class UseMultiModelApi extends UseModelApi{

    /**
     * The instance of the encapsulated multi-model.
     */
    private MMultiModel mMultiModel;
    MultiModelFactory mFactory = new MultiModelFactory();

    /**
     * Creates a new UseMultiModelApi instance with an empty multi-model named "unnamed".
     * The new multi-model instance can be retrieved by {@link #getMultiModel()}.
     */
    public UseMultiModelApi() {
        mMultiModel = mFactory.createMultiModel("unnamed");
    }

    /**
     * Creates a new UseMultiModelApi instance with an empty multi-model named <code>name</code>.
     * The new multi-model instance can be retrieved by {@link #getMultiModel()}.
     * @param name The name of the new model.
     */
    public UseMultiModelApi(String name) {
        mMultiModel = mFactory.createMultiModel(name);
    }

    /**
     * Creates a new UseMultiModelApi instance with
     * the provided <code>multiModel</code> as the multi-model instance.
     * This is useful if you want to modify an existing multi-model instance.
     * @param multiModel The multi-model to modify through this API instance.
     */
    public UseMultiModelApi(MMultiModel multiModel) {
        mMultiModel = multiModel;
    }

    /**
     * Returns the multi-model modified through this API instance.
     * @return the multi-model handled by this API instance.
     */
    public MMultiModel getMultiModel() {
        return mMultiModel;
    }

    @Override
    public MModel createModel(String modelName) {
        return mFactory.createModel(modelName);
    }

    /**
     * Adds a model to the multi-model handled by this API instance.
     *
     */
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

    /**
     * Helper method to safely retrieve a model.
     * Safe by the degree, that if no exception is thrown you get a valid class
     * instance. In contrast to the need to handle <code>null</code> as a return value.
     * @param modelName The name of the model to lookup.
     * @return The {@link MModel} with the name <code>modelName</code>.
     * @throws UseApiException If no model with the given name exists in the encapsulated multi-model.
     */
    public MModel getModelSafe(String modelName) throws UseApiException {
        MModel model = mMultiModel.getModel(modelName);

        if (model == null) {
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        return model;
    }
    //======================================================================================
    /**
     * Creates a new class in a USE multi-model.
     * <p>
     *      If the name provided is of the form: <code> modelName@className </code>
     *      then the class is created inside the model named 'modelName'
     * </p>
     * <p>
     *      If the name does not contain an '@' then the class is created
     *      as an 'inter-class' inside the multi-model
     * </p>
     *
     * @param name The name of the class to create.
     * @param isAbstract If <code>true</code>, no instances can be created for this class.
     * @return the newly created class
     * @throws UseApiException If the model given was not found.
     * @throws UseApiException If no class name is given or if the class is invalid.
     */
    @Override
    public MClass createClass(String name, boolean isAbstract) throws UseApiException {
        MModel model;
        String className;

        if (name.contains("@")){
            //regular class
            String modelName = name.split("@")[0];
            className = name.split("@")[1];

            model = mMultiModel.getModel(modelName);
            mFactory.setModelName(model + "@");
            if (model == null){
                throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
            }

            if (className == null || className.equals("")) {
                throw new UseApiException("A class must be named");
            }
        }
        else{
            //inter-class
            model = this.mMultiModel;
            mFactory.setModelName("");
            className = name;
        }
        MClass cls = mFactory.createClass(className, isAbstract);

        try {
            model.addClass(cls);
        } catch (MInvalidModelException e) {
            throw new UseApiException("Add class failed!", e);
        }

        return cls;
    }

    /**
     * This method overrides the method in {@link UseModelApi} to
     * handle the creation of both regular and inter associations
     *
     * <p>
     *      classEnd1 - e1
     *      classEnd2 - e2
     * </p>
     *
     * <li>
     *      Regular Association: If e1 and e2 are in the form of:
     *      <code> modelName@className </code>, and both modelNames are the same,
     *      the association will be created inside the model named 'modelName'
     * </li>
     * <li>
     *      Inter Association-Class:
     *      If the model names differ or if one of the end classes names
     *      does not contain and @ then the association will be created as
     *      an inter association and will be placed inside the multi model
     * </li>
     */
    @Override
    public MAssociation createAssociation(String associationName, String[] classNames, String[] roleNames,
            String[] multiplicities, int[] aggregationKinds, boolean[] orderedInfo,
            String[][][] qualifier) throws UseApiException {

        MModel model = mMultiModel;
        mFactory.setModelName("");

        String end1Name = classNames[0];
        String end2Name = classNames[1];

        if (end1Name.contains("@") && end1Name.contains("@")){
            String end1ModelName = end1Name.split("@")[0];
            String end2ModelName = end2Name.split("@")[0];
            if (end1ModelName.equals(end2ModelName)) {
                //regular association
                model = mMultiModel.getModel(end1ModelName);
                mFactory.setModelName(model + "@");
            }
        }

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

            model.addAssociation(assoc);

        } catch (MInvalidModelException e) {
            throw new UseApiException("Association creation failed", e);
        }

        return assoc;
    }

    /**
     * This method overrides the method in {@link UseModelApi} to
     * handle the creation of both regular and inter association classes
     * <p>
     *      assocClassName - ac1
     *      classEnd1 - e1
     *      classEnd2 - e2
     * </p>

     * <li>
     *      Regular Association-Class: If ac1, e1 and e2 are in the form of:
     *      <code> modelName@className </code>, all three modelNames must be the same and
     *      the assoc-class will be created inside the model named 'modelName'
     * </li>
     * <li>
     *      Inter Association-Class between regular classes: If ac1 does not contain
     *      an '@' and e1, e2 are in the form of: <code> modelName@className </code>,
     *      the assoc-class will be created inside the multi-model as an inter-association-class
     * </li>
     * <li>
     *      Inter Association-Class between inter-classes: If ac1, e1 and e2 does not contain an '@'
     *      the assoc-class will be created inside the multi-model as an inter-association-class
     * </li>
     * <li>
     *      Inter Association-Class between regular class and an inter-class: If ac1 does not contain
     *      an '@' and e1, e2 are in the form of: <code> modelName@className </code>,
     *      the assoc-class will be created inside the multi-model as an inter-association-class
     * </li>
     */
    @Override
    public MAssociationClass createAssociationClass(String associationClassName, boolean isAbstract, String[] parents,
            String[] classNames, String[] roleNames, String[] multiplicities, int[] aggregationKinds,
            boolean[] orderedInfo, String[][][] qualifier) throws UseApiException {

        String assocClassName;
        MModel model;

        if (associationClassName.contains("@")){
            //regular association class
            String end1Name = classNames[0];
            String end2Name = classNames[1];
            if (!end1Name.contains("@"))
                throw new UseApiException("Class " + end1Name + "does not contain @");
            if (!end2Name.contains("@"))
                throw new UseApiException("Class " + end2Name + "does not contain @");

            assocClassName = associationClassName.split("@")[1];
            String assocModelName = associationClassName.split("@")[0];
            String end1ModelName = end1Name.split("@")[0];
            String end2ModelName = end2Name.split("@")[0];

            if (!(assocModelName.equals(end1ModelName) && assocModelName.equals(end2ModelName))){
                throw new UseApiException("Association class model and the class ends models must be the same");
            }
            model = getModelSafe(assocModelName);
            mFactory.setModelName(model + "@");
        }
        else{
            //Inter Association-Class
            assocClassName = associationClassName;
            model = this.mMultiModel;
            mFactory.setModelName("");
        }


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

            model.addClass(associationClass);

            for(String p : parents){
                createGeneralization(assocClassName, p);
            }
            model.addAssociation(associationClass);
        } catch (MInvalidModelException e) {
            throw new UseApiException(e.getMessage(), e);
        }

        return associationClass;
    }

    /**
     * This method overrides the method in {@link UseModelApi} to
     * handle the creation of regular invariants, the only difference is
     * that regular invariants are put inside the correct model
     */
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
            mFactory.setModelName(cls.model().name() + "@");
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

    /**
     * This method handles the creation of inter invariants,
     * the only difference in logic here is that the invariant's body
     * is compiled with the context of the multi-model and not the regular context of a single model.
     */
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

        return createInterInvariantEx(invName, contextName, invExp, isExistential);
    }

    public MClassInvariant createInterInvariantEx(String invName, String contextName,
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

    /**
     * This method creates a generalization relation between two classes.
     * the classes can be either regular or inter-classes
     */
    @Override
    public MGeneralization createGeneralization(String childName, String parentName) throws UseApiException {
        MClass mChild = getClassSafe(childName);
        MClass mParent = getClassSafe(parentName);

        return createGeneralizationEx(mChild, mParent);
    }

    @Override
    public MGeneralization createGeneralizationEx(MClass child, MClass parent) throws UseApiException {
        MModel model;
        if (child.model().equals(parent.model()) && !child.model().equals(this.mMultiModel)) {
            //regular generalization
            model = child.model();
        }
        else{
            //inter-generalization
            throw new UseApiException("INTER GENERALIZATIONS NOT SUPPORTED");
        }

        MGeneralization mGeneralization = mFactory.createGeneralization(child, parent);

        try {
            model.addGeneralization(mGeneralization);
        } catch (MInvalidModelException e) {
            throw new UseApiException("Creation of generalization failed!", e);
        }

        return mGeneralization;
    }


    /**
     * <p>Creates an operation signature with the name <code>operationName</code> for the class
     * identified by <code>ownerName</code>.</p>
     * <p>The return type of the operation is defined by the parameter <code>returnType</code>.
     * It can be any built-in or already created user defined type.
     * </p>
     * <p>The parameters of the operation to create are specified by a two dimensional array.
     * The first dimension defines the parameter position. The second dimension has exactly two entries:
     * <ol>
     *   <li> At index 0 the name of the parameter</li>
     *   <li> At index 1 the type of the parameter</li>
     * </ol>
     * <p>
     *      If the ownerName provided is of the form: <code> modelName@className </code>
     *      then the operation is created inside the model named 'modelName'
     *      The operation is created with the name operationName@modelName
     * </p>
     * <p>
     *      If the ownerName does not contain an '@' then the class is created
     *      as an 'inter-operation' inside the multi-model
     * </p>
     *
     *
     *
     * @param ownerName The class name to create the operation for.
     * @param operationName The name of the operation to create.
     * @param parameter The operation parameters
     * @param returnType The return type of the operation (can be <code>null</code>).
     * @return The created <code>MOperation</code>.
     * @throws UseApiException
     */
    @Override
    public MOperation createOperation(String ownerName, String operationName, String[][] parameter, String returnType) throws UseApiException {

        if (ownerName == null || ownerName.equals("")) {
            throw new UseApiException("Owner name is required!");
        }


        if (ownerName.contains("@")){
            //regular operation
            String modelName = ownerName.split("@")[0];
            String className = ownerName.split("@")[1];

            MModel model = mMultiModel.getModel(modelName);

            if (model == null){
                throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
            }

            if (className == null || className.equals("")) {
                throw new UseApiException("A class must be named");
            }
        }


        if (operationName == null || operationName.equals("")) {
            throw new UseApiException("Operation name is required!");
        }

        MClass owner = getClassSafe(ownerName);

        VarDeclList vars = new VarDeclList(false);
        for (String[] var : parameter) {
            Type t = getType(var[1]);
            vars.add(new VarDecl(var[0], t));
        }

        Type resultType = null;
        if (returnType != null) {
            resultType = getType(returnType);
        }

        return createOperationEx(owner, operationName, vars, resultType);
    }

    /**
     * This method overrides the method in {@link UseModelApi} to
     * handle the creation of MPrePostCondition, the only difference is
     * the model in which the prepost is created in.
     */
    public MPrePostCondition createPrePostConditionEx(String name,
                                                      MOperation op, boolean isPre, Expression condition)
            throws UseApiException {

        MPrePostCondition cond;
        try {
            cond = mFactory.createPrePostCondition(name, op, isPre, condition);
            op.cls().model().addPrePostCondition(cond);
        } catch (ExpInvalidException | MInvalidModelException ex) {
            throw new UseApiException("Could not create pre-/postcondition.", ex);
        }

        return cond;
    }

    /**
     * Helper method to safely retrieve a class or an inter-class.
     * Safe by the degree, that if no exception is thrown you get a valid class
     * instance. In contrast to the need to handle <code>null</code> as a return value.
     *  <p>
     *      In order to retrieve regular classes from a model the name
     *      must be in the format: <code> modelName@className </code>
     *  </p>
     *  <p>
     *      For inter-classes the name must be the name of the inter-class
     *  </p>
     * @param name The name of the class to lookup.
     * @return The {@link MClass} with the name <code>name</code>.
     * @throws UseApiException If no class with the given name exists in the encapsulated multi-model.
     */
    @Override
    public MClass getClassSafe(String name) throws UseApiException {
        if (!name.contains("@")){
            //inter-class
            MClass cls = mMultiModel.getClass(name);
            if (cls == null) {
                throw new UseApiException("Unknown class " + StringUtil.inQuotes(name));
            }
            return cls;
        }

        //regular class
        String modelName = name.split("@")[0];
        String className = name.split("@")[1];

        MModel model = mMultiModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        MClass cls = model.getClass(name);
        if (cls == null) {
            throw new UseApiException("Unknown class " + StringUtil.inQuotes(name) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }

    /**
     * Queries the underlying multi-model for the association with the name <code>name</code>
     * and returns the corresponding meta-class ({@link MAssociation}) instance.
     *
     *  <p>
     *      In order to retrieve regular association from a model the name
     *      must be in the format: <code> modelName@className </code>
     *  </p>
     *  <p>
     *      For inter-associations the name must be the name of the inter-association
     *  </p>
     *
     * @param name The name of the association to query for.
     * @return The meta-class instance with the given name.
     * @throws UseApiException If the association is unknown or if the model name is unknown.
     */
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

        MAssociation cls = model.getAssociation(name);
        if (cls == null) {
            throw new UseApiException("Unknown association named " + StringUtil.inQuotes(name) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }

    /**
     * Queries the underlying multi-model for the association-class with the name <code>name</code>
     * and returns the corresponding meta-class ({@link MAssociationClass}) instance.
     *
     *  <p>
     *      In order to retrieve regular association-class from a model the name
     *      must be in the format: <code> modelName@assocClassName </code>
     *  </p>
     *  <p>
     *      for inter-association-classes the name must be the name of the inter-association-class
     *  </p>
     *
     * @param name The name of the association-class to query for.
     * @return The meta-class instance with the given name.
     * @throws UseApiException If the association-class is unknown or if the model name is unknown.
     */
    @Override
    public MAssociationClass getAssociationClassSafe(String name) throws UseApiException {
        if (!name.contains("@")){
            //inter-association-class
            MAssociationClass cls = mMultiModel.getAssociationClass(name);
            if (cls == null) {
                throw new UseApiException("Unknown association class " + StringUtil.inQuotes(name));
            }
            return cls;
        }

        String modelName = name.split("@")[0];
        String assocClassName = name.split("@")[1];

        MModel model = mMultiModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        MAssociationClass cls = model.getAssociationClass(name);
        if (cls == null) {
            throw new UseApiException("Unknown association class " + StringUtil.inQuotes(name) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }
}
