package org.tzi.use.uml.mm;

import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;

import java.util.HashMap;
import java.util.Map;

public class MMultiLevelModel extends MMultiModel {

    private MMultiModel fMultiModel;
    private final Map<String, MMediator> fMediators;
    protected MMultiLevelModel(String name) {
        super(name);
        fMediators = new HashMap<>();
    }

    public void setMultiModel(MMultiModel multiModel) {
        this.fMultiModel = multiModel;
    }

    public void addMediator(MMediator mediator) {
        this.fMediators.put(mediator.name(), mediator);
    }

    @Override
    public void addGeneralization(MGeneralization gen) throws MInvalidModelException {
        super.addGeneralization(gen);
        gen.validateInheritance();
    }

    public boolean isValid(){
        //BIG TODO


        for (MModel model : fMultiModel.models()){
            MMediator mediator = fMediators.get(model.name());
            UseSystemApi systemApi = new UseSystemApiUndoable(model);

            //for each clabject, we create an object of the instance type
            for (MClabject clabject : mediator.clabjects()){
                try {
                    systemApi.createObject(clabject.parent().name(), clabject.child().name());

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return false;
                }
            }

            for (MAssoclink assoclink : mediator.assocLinks()){
                try {
                    String obj1 =  assoclink.child().associationEnds().get(0).name();
                    String obj2 =  assoclink.child().associationEnds().get(1).name();

                    systemApi.createLink(assoclink.parent().name(), obj1, obj2);

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return false;
                }
            }

        }

        return true;
    }


}
