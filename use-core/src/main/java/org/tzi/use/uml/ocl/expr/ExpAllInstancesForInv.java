package org.tzi.use.uml.ocl.expr;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystemState;

import java.util.HashSet;
import java.util.Set;

/**
 * this class is needed to calculate the correct classes that related to an invariant,
 * since role removals and invariant removals can change the relevant objects that are related to an invariant
 */
public class ExpAllInstancesForInv extends ExpAllInstances{

    MClassInvariant invariant;
    public ExpAllInstancesForInv(Type sourceType, MClassInvariant inv) throws ExpInvalidException {
        super(sourceType);
        this.invariant = inv;
    }

    @Override
    public Value eval(EvalContext ctx) {
        ctx.enter(this);
        MSystemState systemState = isPre() ? ctx.preState() : ctx.postState();

        // the result set will contain all instances of the specified
        // class plus all instances of subclasses that the invariant was not removed for them

        // get set of objects
        SetValue res;

        if(this.getSourceType().isTypeOfClass()) {
            MClass cls = (MClass) this.getSourceType();
            Set<MObject> objSet = new HashSet<>(systemState.objectsOfClass(cls));
            MMultiLevelModel mlm = ((MMultiLevelModel)((MInternalClassImpl) cls).getMultiModel());
            Set<MClass> subClassesForInvariant = mlm.subClassesOfClassForInvariant(cls, this.invariant);
            for(MClass subClass : subClassesForInvariant) {
                objSet.addAll(systemState.objectsOfClass(subClass));
            }

            Value[] objValues = new Value[objSet.size()];

            int i = 0;
            for (MObject obj : objSet) {
                objValues[i++] = new ObjectValue(obj.cls(), obj);
            }

            // create result set with object references
            res = new SetValue(this.getSourceType(), objValues);
        } else {
            throw new IllegalArgumentException("allInstancesForInv() is only supported on classes.");
        }

        ctx.exit(this, res);
        return res;
    }

    public void setInvariant(MClassInvariant invariant) {
        this.invariant = invariant;
    }
}
