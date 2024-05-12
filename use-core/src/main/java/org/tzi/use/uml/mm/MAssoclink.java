package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MAssoclink extends MGeneralization {
//    private final String fName;
//
//    private MAssociation fParent;
//    private MAssociation fChild;

    private final List<MRoleRenaming> fRoleRenaming;

    public MAssoclink(MAssociation child, MAssociation parent) {
        super(child, parent);
        fRoleRenaming = new ArrayList<>();
    }

    public void addRoleRenaming(MRoleRenaming roleRenaming) {
        //check conflicts
        Set<String> taken = fRoleRenaming.stream().map(MRoleRenaming::newName).collect(Collectors.toSet());
        //taken.addAll(((MAssociationImpl)child()).associationEnds().stream().map(MAssociationEnd::nameAsRolename).collect(Collectors.toSet()));
        taken.addAll(((MAssociationImpl)parent()).associationEnds().stream()
                .filter(assocEnd -> !assocEnd.equals(roleRenaming.assocEnd()))
                .map(MAssociationEnd::nameAsRolename)
                //.filter(str -> !str.equals(parent().name()))
                .collect(Collectors.toSet()));
        //((MAssociation)child()).getAssociationEnd(roleRenaming.assocEnd().cls(), roleRenaming.assocEnd().name());
        if(taken.contains(roleRenaming.newName())) {
            throw new NullPointerException("Role: " + roleRenaming.newName() + " already exists" + " in association: " + roleRenaming.assocEnd().association().name());
        }
        fRoleRenaming.add(roleRenaming);
    }

    @Override
    public String name() {
        return "ASSOCLINK" + fChild.name() + "_" + fParent.name();
    }

}
