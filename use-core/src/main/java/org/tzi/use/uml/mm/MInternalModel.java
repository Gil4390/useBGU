package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.Nullable;

public class MInternalModel extends MModel {
    protected MInternalModel(String name) {
        super(name);
    }

    @Override
    public MClass getClass(String name) {
        if(name.contains("@")) {
            return super.getClass(name);
        }
        return super.getClass(this.name()+"@"+name);
    }

    @Override
    public MAssociationClass getAssociationClass(String name) {
        if(name.contains("@")) {
            return super.getAssociationClass(name);
        }
        return super.getAssociationClass(this.name()+"@"+name);
    }

    @Override
    public @Nullable MAssociation getAssociation(String name) {
        if(name.contains("@")) {
            return super.getAssociation(name);
        }
        return super.getAssociation(this.name()+"@"+name);
    }

    @Override
    public MClassInvariant getClassInvariant(String name) {
        if(name.contains("@")) {
            return super.getClassInvariant(name);
        }
        return super.getClassInvariant(this.name()+"@"+name);
    }
}
