package org.tzi.use.uml.mm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * this class is needed to distinguish between internal and regular associations
 * in the isAssignableFrom method
 */
public class MInternalAssociationImpl extends MAssociationImpl {

    MInternalAssociationImpl(String name) {
        super(name);
    }

    @Override
    public boolean isAssignableFrom(MClass[] classes) {
        int i=0;
        for (MAssociationEnd end : associationEnds()) {
            if (!classes[i].isSubClassifierOf(end.cls())) return false;
            ++i;
        }
        // handle self associations
        if(classes.length == 2 && classes[0].equals(classes[1])) {
            MClass cls = classes[0];
            if(cls instanceof MInternalClassImpl) {
                Set<MClabject> clabjects = ((MInternalClassImpl)cls).clabjectsFromParents();
                for (MClabject clabject : clabjects) {
                    boolean isRoleRemoved = clabject.getRemovedRoles().stream().anyMatch((role) -> associationEnds().contains(role));
                    if(isRoleRemoved)
                        return false;
                }
            }
        }

        // handle assoclinks
        List<MAssociation> assocList = classes[0].associations().stream()
                .filter(ass -> classes[1].associations().contains(ass)).collect(Collectors.toList());
        for(MAssociation assoc : assocList) {
            Iterator<MGeneralization> it = this.model.generalizationGraph().edgesBetween(assoc, this).iterator();
            if (it.hasNext()){
                MGeneralization assoclink = it.next();
                if(assoclink instanceof MAssoclink && assoclink.fParent.equals(this)) {
                    return false;
                }
            }
        }

        return true;
    }



}
