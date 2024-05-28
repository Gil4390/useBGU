package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.expr.VarDecl;

import java.util.List;

public class MInternalAssociationEnd extends MAssociationEnd{
    String newName;
    public MInternalAssociationEnd(MClass cls, String rolename, MMultiplicity mult, int kind, boolean isOrdered, List<VarDecl> qualifiers) {
        super(cls, rolename, mult, kind, isOrdered, qualifiers);
    }

    public MInternalAssociationEnd(MClass cls, String rolename, MMultiplicity mult, int kind, boolean isOrdered, boolean isExplicitNavigable) {
        super(cls, rolename, mult, kind, isOrdered, isExplicitNavigable);
    }

    public void setNewName(String newName){
        this.newName = newName;
    }

    public String name(){
        if (newName == null){
            return super.name();
        }
        return newName;
    }

    public String nameAsRolename(){
        if (newName == null){
            return super.nameAsRolename();
        }
        return newName;
    }

    public String getOriginalName(){
        return super.name();
    }
}
