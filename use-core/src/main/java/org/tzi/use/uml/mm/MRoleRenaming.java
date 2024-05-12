package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MAssociationEnd fAssocEnd;
    private String newName;

    public MRoleRenaming(MAssociationEnd aend, String newName) {
        this.fAssocEnd = aend;
        this.newName = newName;
    }

    public MAssociationEnd assocEnd() {
        return fAssocEnd;
    }

    public String newName() {
        return newName;
    }
}
