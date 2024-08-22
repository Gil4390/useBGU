package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MInternalAssociationEnd fAssocEnd;
    private String newName;

    public MRoleRenaming(MInternalAssociationEnd aendp, String newName) {
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
