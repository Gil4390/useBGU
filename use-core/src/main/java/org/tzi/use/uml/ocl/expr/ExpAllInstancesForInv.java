package org.tzi.use.uml.ocl.expr;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.LinkValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkSet;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystemState;

import java.util.HashSet;
import java.util.Set;

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
            //Set<MObject> objSet = systemState.objectsOfClassAndSubClasses((MClass)this.getSourceType());
            MClass cls = (MClass) this.getSourceType();
            Set<MObject> objSet = new HashSet<>(systemState.objectsOfClass(cls));
            Set<MClass> subClassesForInvariant = ((MMultiLevelModel) systemState.system().model()).subClassesOfClassForInvariant(cls, this.invariant);
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
        } else if (this.getSourceType().isTypeOfAssociation()) {
            MLinkSet links = systemState.linksOfAssociation((MAssociation)this.getSourceType());
            Value[] linkValues = new Value[links.size()];

            int i = 0;
            for (MLink link : links.links()) {
                linkValues[i++] = new LinkValue(link.association(), link);
            }

            res = new SetValue(this.getSourceType(), linkValues);
        } else {
            throw new IllegalArgumentException("allInstances() is only supported on classes and associations.");
        }

        ctx.exit(this, res);
        return res;
    }
}
