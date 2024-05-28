package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MInternalAssociationEnd fAssocEndP;
    private MInternalAssociationEnd fAssocEndC;
    private String newName;

    public MRoleRenaming(MInternalAssociationEnd aendp, MInternalAssociationEnd aendc, String newName) {
        this.fAssocEndP = aendp;
        this.fAssocEndC = aendc;
        this.newName = newName;

        this.fAssocEndC.newName = newName;

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
