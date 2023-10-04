package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A InternalModel is part of a multi-model, containing all
 * specific model related elements
 *
 * @author Gil Khais
 * @author Amiel Saad
 */

public class MInternalModel extends MModel {
    protected MInternalModel(String name) {
        super(name);
    }

    /**
     * validates that there is a '@' sign in the given class name,
     * and call super method to retrieve the desired class.
     * @param name
     */
    @Override
    public MClass getClass(String name) {
        if(name.contains("@")) {
            return super.getClass(name);
        }
        return super.getClass(this.name()+"@"+name);
    }

    /**
     * validates that there is a '@' sign in the given association class name,
     * and call super method to retrieve the desired association class.
     * @param name
     */
    @Override
    public MAssociationClass getAssociationClass(String name) {
        if(name.contains("@")) {
            return super.getAssociationClass(name);
        }
        return super.getAssociationClass(this.name()+"@"+name);
    }

    /**
     * validates that there is a '@' sign in the given association name,
     * and call super method to retrieve the desired association.
     * @param name
     */
    @Override
    public @Nullable MAssociation getAssociation(String name) {
        if(name.contains("@")) {
            return super.getAssociation(name);
        }
        return super.getAssociation(this.name()+"@"+name);
    }

    /**
     * validates that there is a '@' sign in the given class invariant name,
     * and call super method to retrieve the desired class invariant.
     * @param name
     */
    @Override
    public MClassInvariant getClassInvariant(String name) {
        if(name.contains("@")) {
            return super.getClassInvariant(name);
        }
        return super.getClassInvariant(this.name()+"@"+name);
    }
}
