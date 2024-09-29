package org.tzi.use.uml.mm;

public class MRoleBinding {

    private MAssociationEnd childAssociationEnd;

    private MAssociationEnd parentAssociationEnd;

    public MRoleBinding(MAssociationEnd childAssociationEnd, MAssociationEnd parentAssociationEnd) {
        this.childAssociationEnd = childAssociationEnd;
        this.parentAssociationEnd = parentAssociationEnd;
    }

    public MAssociationEnd getChildAssociationEnd() {
        return childAssociationEnd;
    }

    public MAssociationEnd getParentAssociationEnd() {
        return parentAssociationEnd;
    }

}
