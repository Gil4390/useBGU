package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MInternalAssociationEnd fAssocEndP;
    private String newName;

    public MRoleRenaming(MInternalAssociationEnd aendp, String newName) {
        this.fAssocEndP = aendp;
        this.newName = newName;
    }

    public MAssociationEnd assocEndP() {
        return fAssocEndP;
    }

    public String newName() {
        return newName;
    }
}
