package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.expr.*;

import java.util.List;

/**
 *  This Class is used to override the calculateExpandedExpression method of the MClassInvariant class.
 *  This class wil only be created as part of a Multi-Level Model.
 *
 */
public class MInternalClassInvariant extends MClassInvariant{

    MInternalClassInvariant(String name, List<String> vars, MClassifier cls, Expression inv, boolean isExistential) throws ExpInvalidException {
        super(name, vars, cls, inv, isExistential);
    }

    public void calculateExpandedExpression() throws ExpInvalidException {
        Expression allInstances = new ExpAllInstancesForInv(cls(), this);

        if (isExistential()) {
            fExpanded = new ExpExists(vars(), allInstances, bodyExpression());
        } else {
            fExpanded = new ExpForAll(vars(), allInstances, bodyExpression());
        }
    }
}
