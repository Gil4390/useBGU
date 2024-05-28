package org.tzi.use.uml.mm;

import java.util.*;
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
//        //check conflicts
//        if (!roleRenaming.assocEndC().name().equals(roleRenaming.newName())){
//            MClassImpl originalClass = (MClassImpl) roleRenaming.assocEndC().cls();
//            MClassImpl secondClass = (MClassImpl) roleRenaming.assocEndC().association().associationEnds().stream().filter(e -> !e.cls().name().equals(originalClass.name())).collect(Collectors.toList()).get(0).cls();
//            Map<String, MNavigableElement> ends = secondClass.navigableEnds();
//            Set<String> takenNames = new HashSet<>();
//            for (MNavigableElement end : ends.values()) {
//                Map<String, MNavigableElement> ends2 = ((MClassImpl)end.cls()).navigableEnds();
//                for (MNavigableElement end2 : ends2.values()) {
//                    if (end2.cls().name().equals(secondClass.name())){
//                        takenNames.add(end.nameAsRolename());
//                    }
//                }
//            }
//            if (takenNames.contains(roleRenaming.newName())) {
//                throw new NullPointerException("Role: " + roleRenaming.newName() + " already exists" + " in association: " + roleRenaming.assocEndC().association().name());
//            }
//        }


        fRoleRenaming.add(roleRenaming);
    }

    public List<MRoleRenaming> getRoleRenaming() {
        return fRoleRenaming;
    }

    @Override
    public String name() {
        return "ASSOCLINK_" + fChild.name() + "_" + fParent.name();
    }

}
