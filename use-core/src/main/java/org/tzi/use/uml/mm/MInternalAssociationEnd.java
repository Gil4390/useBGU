package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.expr.VarDecl;

import java.util.List;

public class MInternalAssociationEnd extends MAssociationEnd{
    String newName;
    boolean endRemoved;
    private MClass renamedClass;
    public MInternalAssociationEnd(MClass cls, String rolename, MMultiplicity mult, int kind, boolean isOrdered, List<VarDecl> qualifiers) {
        super(cls, rolename, mult, kind, isOrdered, qualifiers);
    }

    public MInternalAssociationEnd(MClass cls, String rolename, MMultiplicity mult, int kind, boolean isOrdered, boolean isExplicitNavigable) {
        super(cls, rolename, mult, kind, isOrdered, isExplicitNavigable);
    }

    public void setNewName(String newName, MClass renamedClass){
        this.newName = newName;
        this.renamedClass = renamedClass;
    }

    public void setEndRemoved(boolean endRemoved){
        this.endRemoved = endRemoved;
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

    public boolean isRenamed(MClass cls){
        return newName != null && renamedClass.equals(cls) && !newName.equals(super.name());
    }

    public boolean isEndRemoved(){
        return endRemoved;
    }
}
