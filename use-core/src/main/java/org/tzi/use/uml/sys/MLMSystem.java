package org.tzi.use.uml.sys;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.value.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MLMSystem extends MSystem {
    /**
     * constructs a new MSystem
     *
     * @param model the model of this system
     */
    public MLMSystem(MModel model) {
        super(model);
    }

    @Override
    public MLink createLink(StatementEvaluationResult result, MAssociation association, List<MObject> participants, List<List<Value>> qualifierValues) throws MSystemException {
        //check role removal
        List<MClabject> clabjects = participants.stream().map(
                p -> ((MInternalClassImpl)p.cls()).getClabjectEdge()
        ).collect(Collectors.toList());

        for (MClabject clabject : clabjects) {
            if (clabject == null) {
                continue;
            }
            for (MAssociationEnd end : clabject.getRemovedRoles()){
                if (association.associationEnds().contains(end)){
                    throw new MSystemException("Role " + end.name() + " is removed from class " + clabject.child().name());
                }
            }
        }


        //check assoclink
//        for (MAssociation childAssoc : association.children()){
//            Set<MGeneralization> assoclinks = model().generalizationGraph().edgesBetween(association, childAssoc);
//            Set<MGeneralization> assoclinks2 = model().generalizationGraph().edgesBetween(childAssoc, association);
//            for (MGeneralization assoclink : assoclinks){
//                if (assoclink instanceof MAssoclink){
//                    for (MRoleBinding rb : ((MAssoclink) assoclink).roleBindings()) {
//                        List<MClass> participantsClasses = participants.stream().map(MObject::cls).collect(Collectors.toList());
//                        if (participantsClasses.contains(rb.getParentAssociationEnd().cls())) {
//                            throw new MSystemException("Role " + rb.getParentAssociationEnd().name() + " is removed from class " + rb.getParentAssociationEnd().cls().name());
//                        }
//                    }
//                }
//            }
//        }

        return super.createLink(result, association, participants, qualifierValues);
    }
}
