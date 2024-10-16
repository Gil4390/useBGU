package org.tzi.use.uml.mm;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MInternalAssociationImpl extends MAssociationImpl {
    /**
     * Creates a new association. Connections to classes are
     * established by adding association ends. The kind of association
     * will be automatically determined by the kind of association
     * ends.
     *
     * @param name
     */
    MInternalAssociationImpl(String name) {
        super(name);
    }

    @Override
    public boolean isAssignableFrom(MClass[] classes) {
        int i=0;
        for (MAssociationEnd end : associationEnds()) {
            if (!classes[i].isSubClassOf(end.cls())) return false;
            ++i;
        }
        // handle self associations
        if(classes.length == 2 && classes[0].equals(classes[1])) {
            MClass class1 = classes[0];
            MClass class2 = classes[1];
            if(class1 instanceof MInternalClassImpl && class2 instanceof MInternalClassImpl) {
                MClabject clabject1 = ((MInternalClassImpl)class1).getClabjectEdge();
                MClabject clabject2 = ((MInternalClassImpl)class2).getClabjectEdge();
                if(clabject1 != null) {
                    boolean isRoleRemoved = clabject1.getRemovedRoles().stream().anyMatch((role) -> associationEnds().contains(role));
                    if(isRoleRemoved)
                        return false;
                }
                else if(clabject2 != null) {
                    boolean isRoleRemoved = clabject2.getRemovedRoles().stream().anyMatch((role) -> associationEnds().contains(role));
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
