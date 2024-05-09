package org.tzi.use.uml.mm;

import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.graph.DirectedGraphBase;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.util.NullPrintWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMultiLevelModel extends MMultiModel {

    private final List<MModel> fModelsList; //ordered list of models
    private final Map<String, MMediator> fMediators;
    private final DirectedGraph<MClassifier, MClabject> fClabjectGraph;
    protected MMultiLevelModel(String name) {
        super(name);
        fModelsList = new ArrayList<>();
        fMediators = new HashMap<>();
        fClabjectGraph = new DirectedGraphBase<>();
    }
    protected MMultiLevelModel(MMultiModel multiModel){
        super(multiModel.name());
        fModelsList = new ArrayList<>();
        fClabjectGraph = new DirectedGraphBase<>();

        //steal all the fields from the multiModel
        try {
            for (EnumType enumType : multiModel.enumTypes()) {
                this.addEnumType(enumType);
            }
            for (MModel model : multiModel.models()) {
                this.addModel(model);
            }
            for (MClass mClass: multiModel.interClasses()) {
                this.addClass(mClass);
            }
            //look for inter associations
            for (MAssociation association : multiModel.interAssociations()) {
                this.addAssociation(association);
            }
            //look for inter invariants
            for (MClassInvariant invariant : multiModel.interConstraints()) {
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
        fClabjectGraph.addAll(model.classes());

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

    @Override
    public void addGeneralization(MGeneralization gen) throws MInvalidModelException {
        super.addGeneralization(gen);
        if (gen instanceof MClabject){
            //checks for conflicts
            MInternalClassImpl child = (MInternalClassImpl) gen.child();
            MInternalClassImpl parent = (MInternalClassImpl) gen.parent();
            List<MAttribute> childAttributes = child.allAttributes();
            List<MAttribute> parentAttributes = parent.allAttributes();
            for (MAttribute childAttr : childAttributes){
                for (MAttribute parentAttr : parentAttributes) {
                    if (childAttr.name().equals(parentAttr.name())){
                        //conflict
                        if (((MClabject) gen).getRemovedAttribute(parentAttr.name()) != null){
                            //attribute is removed
                            continue;
                        }
                        else if (((MClabject) gen).getRenamedAttribute(parentAttr.name()) != null){
                            //attribute is renamed
                            continue;
                        }
                        fGenGraph.removeEdge(gen);
                        throw new MInvalidModelException("Attribute "+childAttr.name()+" is present in both parent and child classes");
                    }
                }
            }

        }

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
                    String obj1 = assoclink.child().associationEnds().get(0).cls().name();
                    String obj2 = assoclink.child().associationEnds().get(1).cls().name();

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

    public String checkLegalState() {
        return checkLegalState(NullPrintWriter.getInstance());
    }

    public String checkLegalState(PrintWriter error){
        MSystemState.Legality result = MSystemState.Legality.Legal;
        MModel previousModel = fModelsList.get(0);
        for (MModel model : this.models()){
            MMediator mediator = fMediators.get(model.name());
            UseSystemApi systemApi = new UseSystemApiUndoable(previousModel);

            //for each clabject, we create an object of the instance type
            for (MClabject clabject : mediator.clabjects()){
                try {
                    systemApi.createObject(clabject.parent().name(), clabject.child().name());

                }catch (Exception e){
                    error.println(e.getMessage());
                    return MSystemState.Legality.Illegal.toString();
                }
            }

            for (MAssoclink assoclink : mediator.assocLinks()){
                try {
                    String obj1 = assoclink.child().associationEnds().get(0).cls().name();
                    String obj2 = assoclink.child().associationEnds().get(1).cls().name();

                    systemApi.createLink(assoclink.parent().name(), obj1, obj2);

                }catch (Exception e){
                    error.println(e.getMessage());
                    return MSystemState.Legality.Illegal.toString();
                }
            }

            MSystemState.Legality currRes = systemApi.checkLegality(error);
            if (currRes.equals(MSystemState.Legality.Illegal)){
                return MSystemState.Legality.Illegal.toString();
            }
            else if (currRes.equals(MSystemState.Legality.PartiallyLegal) && result.equals(MSystemState.Legality.Legal)){
                result = MSystemState.Legality.PartiallyLegal;
            }
            previousModel = model;
        }
        if (result.equals(MSystemState.Legality.PartiallyLegal)){
            return MSystemState.Legality.Legal.toString();
        }
        else return result.toString();
    }


}
