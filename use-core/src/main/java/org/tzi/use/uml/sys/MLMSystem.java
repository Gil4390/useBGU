package org.tzi.use.uml.sys;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.value.Value;

import java.util.List;

public class MLMSystem extends MSystem {
    /**
     * constructs a new MLMSystem
     * sets the current state to a new MLMSystemState
     *
     * @param model the model of this system
     */
    public MLMSystem(MModel model) {
        super(model);
//        fCurrentState = new MSystemState(getUniqueNameGenerator().generate("state#"), this);
    }

    @Override
    public MLink createLink(StatementEvaluationResult result, MAssociation association, List<MObject> participants, List<List<Value>> qualifierValues) throws MSystemException {
        //check if role was removed or overridden in assoclink
        if (association.associationEnds().size() <= 2){
            for (int i = 0; i < participants.size(); i++){
                MObject participant = participants.get(i);
                MAssociationEnd end = association.associationEnds().get(1-i);
                MNavigableElement role = participant.cls().navigableEnd(end.nameAsRolename());
                if (role == null || !role.cls().equals(end.cls())) {
                    throw new MSystemException("Role " + end.name() + " is not accessible from class " + participant.cls().name());
                }
            }

        }

        return super.createLink(result, association, participants, qualifierValues);
    }
}
