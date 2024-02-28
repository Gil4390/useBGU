package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MAssocLinkInstance {

    private MClassifier fParent;
    private MClassifier fChild;

    private final List<MRoleRenaming> fRoleRenaming;

    public MAssocLinkInstance(MClassifier child, MClassifier parent) {
        this.fParent = parent;
        this.fChild = child;
        fRoleRenaming = new ArrayList<>();
    }

    public void addRoleRenaming(MRoleRenaming roleRenaming) {
        fRoleRenaming.add(roleRenaming);
    }



}
