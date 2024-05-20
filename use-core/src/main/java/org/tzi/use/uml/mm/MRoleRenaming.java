package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MAssociationEnd fAssocEndP;
    private MAssociationEnd fAssocEndC;
    private String newName;

    public MRoleRenaming(MAssociationEnd aendp, MAssociationEnd aendc, String newName) {
        this.fAssocEndP = aendp;
        this.fAssocEndC = aendc;
        this.newName = newName;
    }

    public MAssociationEnd assocEndP() {
        return fAssocEndP;
    }

    public MAssociationEnd assocEndC() {
        return fAssocEndC;
    }

    public String newName() {
        return newName;
    }
}
