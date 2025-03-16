package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.Expression;

import java.util.List;
/**
 * A MultiModelFactory creates instances of the MultiModel elements.
 *
 * @author Gil Khais
 * @author Amiel Saad
 */
public class MultiModelFactory extends ModelFactory{

    /**
     * indicates the current generated model, followed by '@' sign.
     * Example: 'model1@'
     */
    protected String modelName;

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

    /**
     * creates an MInternalClass instance with the name of current generated model.
     * @return
     */
    @Override
    public MClass createClass(String name, boolean isAbstract) {
        return new MInternalClassImpl(modelName + name, isAbstract);
    }

    /**
     * creates an MAssociation instance with the name of current generated model.
     * @return
     */
    @Override
    public MAssociation createAssociation(String name) {
        return new MInternalAssociationImpl(modelName + name);
    }


    /**
     * creates an MAssociationClass instance with the name of current generated model.
     * @return
     */
    @Override
    public MAssociationClass createAssociationClass(String name, boolean isAbstract) {
        return super.createAssociationClass(modelName + name, isAbstract);
    }

    /**
     * creates an MClassInvariant instance with the name of current generated model.
     * @return
     */
    @Override
    public MClassInvariant createClassInvariant(String name, List<String> vars, MClassifier cls, Expression inv, boolean isExistential) throws ExpInvalidException {
        return super.createClassInvariant(modelName + name, vars, cls, inv, isExistential);
    }
}
