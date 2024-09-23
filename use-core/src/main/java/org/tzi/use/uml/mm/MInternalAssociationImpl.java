package org.tzi.use.uml.mm;

import java.util.Arrays;
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
        List<MAssociation> assocList = classes[0].associations().stream()
                .filter(ass -> ass.associationEnds().containsAll(Arrays.asList(classes[0], classes[1]))).collect(Collectors.toList());
        // You should handle the case where no association is found
        for(MAssociation assoc : assocList) {
            MGeneralization assoclink = this.model.generalizationGraph().edgesBetween(this,assoc).iterator().next();
            if(assoclink instanceof MAssoclink || (assoclink == null)) {
                continue;
            }
            if(assoclink.fParent.equals(this)) {
                return false;
            }
        }

        return true;
    }


}
