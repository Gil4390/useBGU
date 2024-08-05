package org.tzi.use.uml.mm;

import java.util.*;
import java.util.stream.Collectors;

public class MAssoclink extends MGeneralization {
//    private final String fName;
//
//    private MAssociation fParent;
//    private MAssociation fChild;
    private final List<MRoleBinding> fRoleBinding;

    public MAssoclink(MAssociation child, MAssociation parent) {
        super(child, parent);
        fRoleBinding = new ArrayList<>();
    }

    public void addRoleBinding(MRoleBinding roleBinding) {
        fRoleBinding.add(roleBinding);
    }

    @Override
    public String name() {
        return "ASSOCLINK_" + fChild.name() + "_" + fParent.name();
    }

    @Override
    public String toString(){
        return name();
    }
}
