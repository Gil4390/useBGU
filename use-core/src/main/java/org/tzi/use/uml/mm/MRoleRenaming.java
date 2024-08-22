package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MAssociationEnd fAssocEnd;
    private String newName;

    public MRoleRenaming(MAssociationEnd aendp, String newName) {
        this.fAssocEnd = aendp;
        this.newName = newName;
    }

    public MAssociationEnd assocEnd() {
        return fAssocEnd;
    }

    public String newName() {
        return newName;
    }
}
