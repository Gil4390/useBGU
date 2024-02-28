package org.tzi.use.uml.mm;

public class MRoleRenaming {

    private MAssociationEnd fAssocEnd;
    private String newName;

    public MRoleRenaming(MAssociationEnd maend, String newName) {
        this.fAssocEnd = maend;
        this.newName = newName;
    }

}
