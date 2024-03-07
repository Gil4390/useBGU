package org.tzi.use.uml.mm;

import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;

import java.util.HashMap;
import java.util.Map;

public class MMultiLevelModel extends MMultiModel {

    private final Map<String, MMediator> fMediators;
    protected MMultiLevelModel(String name) {
        super(name);
        fMediators = new HashMap<>();
    }

    public void addMediator(MMediator mediator) throws Exception {
        if (fMediators.containsKey(mediator.name()))
            throw new Exception("MLM already contains a mediator `"
                    + mediator.name() + "'.");
        this.fMediators.put(mediator.name(), mediator);
    }
    public void removeMediator(String name){
        this.fMediators.remove(name);
    }

    public MMediator getMediator(String name){
        return fMediators.get(name);
    }


    public boolean isValid(){
        //BIG TODO


        for (MModel model : this.models()){
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

            systemApi.checkState();

        }

        return true;
    }


}
