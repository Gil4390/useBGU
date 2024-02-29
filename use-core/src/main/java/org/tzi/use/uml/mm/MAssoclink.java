package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MAssoclink {

    private MAssociation fParent;
    private MAssociation fChild;

    private final List<MRoleRenaming> fRoleRenaming;

    public MAssoclink(MAssociation child, MAssociation parent) {
        this.fParent = parent;
        this.fChild = child;
        fRoleRenaming = new ArrayList<>();
    }

    public void addRoleRenaming(MRoleRenaming roleRenaming) {
        fRoleRenaming.add(roleRenaming);
    }

    public MAssociation parent(){
        return fParent;
    }

    public MAssociation child(){
        return fChild;
    }

}
