package org.tzi.use.uml.mm;

import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MSystemState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMultiLevelModel extends MMultiModel {

    private final List<MModel> fModelsList; //ordered list of models
    private final Map<String, MMediator> fMediators;
    protected MMultiLevelModel(String name) {
        super(name);
        fModelsList = new ArrayList<>();
        fMediators = new HashMap<>();
    }
    protected  MMultiLevelModel(MMultiModel multiModel){
        super(multiModel.name());
        fModelsList = new ArrayList<>();
        //steal all the fields from the multiModel
        try {
            for (EnumType enumType : multiModel.enumTypes()) {
                this.addEnumType(enumType);
            }
            for (MModel model : multiModel.models()) {
                this.addModel(model);
            }
            for (MClass mClass: multiModel.classes()) {
                this.addClass(mClass);
            }
            for (MAssociation association : multiModel.associations()) {
                this.addAssociation(association);
            }
            for (MClassInvariant invariant : multiModel.classInvariants()) {
                this.addClassInvariant(invariant);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        fMediators = new HashMap<>();
    }

    @Override
    public void addModel(MModel model) throws Exception {
        super.addModel(model);
        fModelsList.add(model);

    }

    public MModel getParentModel(String modelName) throws Exception {
        if (!fModels.containsKey(modelName)){
            throw new Exception("Model `"+modelName+"' does not exist.");
        }

        MModel prevModel = null;
        for (MModel model : fModelsList){
            if (model.name().equals(modelName)){
                return prevModel;
            }
            prevModel = model;
        }
        return prevModel;
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

    public MClabject getClabject(String mediatorName, String clabjectName){
        MMediator mediator = this.getMediator(mediatorName);
        return mediator.getClabject(clabjectName);
    }

    public boolean checkState(){
        boolean result = true;
        MModel previousModel = fModelsList.get(0);
        for (MModel model : this.models()){
            MMediator mediator = fMediators.get(model.name());
            UseSystemApi systemApi = new UseSystemApiUndoable(previousModel);

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
                    //todo need to find the object name based on the assoclink
                    //String name1 = assoclink.child().associationEnds().get(0).cls().name();
                    String obj1 =  assoclink.child().associationEnds().get(0).cls().name();
                    String obj2 =  assoclink.child().associationEnds().get(1).cls().name();

                    systemApi.createLink(assoclink.parent().name(), obj1, obj2);

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return false;
                }
            }

            result = systemApi.checkState() && result;
            previousModel = model;
        }

        return result;
    }

    public String checkLegalState(){
        MSystemState.Legality result = MSystemState.Legality.Legal;
        for (MModel model : this.models()){
            MMediator mediator = fMediators.get(model.name());
            UseSystemApi systemApi = new UseSystemApiUndoable(model);

            //for each clabject, we create an object of the instance type
            for (MClabject clabject : mediator.clabjects()){
                try {
                    systemApi.createObject(clabject.parent().name(), clabject.child().name());

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return "ILLEGAL";
                }
            }

            for (MAssoclink assoclink : mediator.assocLinks()){
                try {
                    String obj1 =  assoclink.child().associationEnds().get(0).name();
                    String obj2 =  assoclink.child().associationEnds().get(1).name();

                    systemApi.createLink(assoclink.parent().name(), obj1, obj2);

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    return "ILLEGAL";
                }
            }

            MSystemState.Legality currRes = systemApi.checkLegality();
            if (currRes.equals(MSystemState.Legality.Illegal)){
                return "ILLEGAL";
            }
            else if (currRes.equals(MSystemState.Legality.PartiallyLegal) && result.equals(MSystemState.Legality.Legal)){
                result = MSystemState.Legality.PartiallyLegal;
            }

        }

        return result.toString();
    }


}
