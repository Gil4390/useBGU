package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;

import java.util.List;

public class MultiModelFactory extends ModelFactory{

    private String modelName;

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public MMultiModel createMultiModel(String name) {
        return new MMultiModel(name);
    }

    @Override
    public MModel createModel(String name) {
        return new MInternalModel(name);
    }

    @Override
    public MClass createClass(String name, boolean isAbstract) {
        return super.createClass(modelName + name, isAbstract);
    }

    @Override
    public MAssociation createAssociation(String name) {
        return super.createAssociation(modelName + name);
    }

    @Override
    public MAssociationEnd createAssociationEnd(MClass cls, String rolename, MMultiplicity mult, int kind, boolean isOrdered, List<VarDecl> qualifiers) {
        return super.createAssociationEnd(cls, rolename, mult, kind, isOrdered, qualifiers);
    }

    @Override
    public MAssociationClass createAssociationClass(String name, boolean isAbstract) {
        return super.createAssociationClass(modelName + name, isAbstract);
    }

    @Override
    public MClassInvariant createClassInvariant(String name, List<String> vars, MClass cls, Expression inv, boolean isExistential) throws ExpInvalidException {
        return super.createClassInvariant(modelName + name, vars, cls, inv, isExistential);
    }
}
