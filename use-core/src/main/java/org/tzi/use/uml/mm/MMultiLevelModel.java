package org.tzi.use.uml.mm;

import org.tzi.use.api.UseMLMSystemApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MLMSystemState;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.util.NullPrintWriter;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class MMultiLevelModel extends MMultiModel {

    private final List<MModel> fModelsList; //ordered list of models
    private final Map<String, MMediator> fMediators;
    protected MMultiLevelModel(String name) {
        super(name);
        fModelsList = new ArrayList<>();
        fMediators = new HashMap<>();
    }
    protected MMultiLevelModel(MMultiModel multiModel){
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
            for (MClass mClass: multiModel.interClasses()) {
                this.addClass(mClass);
            }
            //look for inter associations
            for (MAssociation association : multiModel.interAssociations()) {
                this.fAssociations.put(association.name(), association);
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
        model.classes().forEach(cls -> ((MInternalClassImpl)cls).setMultiModel(this));
        fModelsList.add(model);

        for (MClassInvariant inv : model.classInvariants()){
            inv.calculateExpandedExpression();
        }
    }

    public MModel getParentModel(String modelName) {
        if (!fModels.containsKey(modelName)){
            return null;
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

    public MModel getNextModel(String modelName) {
        if (!fModels.containsKey(modelName)){
            return null;
        }
        int i = 0;
        for (MModel model : fModelsList){
            if (model.name().equals(modelName) && i<fModelsList.size()-1){
                return fModelsList.get(i+1);
            }
            i++;
        }
        return null;
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

    public List<MClabject> clabjects() {
        List<MClabject> clabjects = new ArrayList<>();
        for (MMediator mediator : fMediators.values()){
            clabjects.addAll(mediator.clabjects());
        }
        return clabjects;
    }

    public List<MAssoclink> assoclinks() {
        List<MAssoclink> assoclinks = new ArrayList<>();
        for (MMediator mediator : fMediators.values()){
            assoclinks.addAll(mediator.assocLinks());
        }
        return assoclinks;
    }

    public List<MMediator> mediators(){
        return new ArrayList<>(fMediators.values());
    }

    @Override
    public void addGeneralization(MGeneralization gen) throws MInvalidModelException {
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
                        //fGenGraph.removeEdge(gen);
                        throw new MInvalidModelException("Attribute "+childAttr.name()+" is present in both parent and child classes");
                    }
                }
            }
        }
        super.addGeneralization(gen);

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
                    String obj1 = ((MAssociation)assoclink.child()).associationEnds().get(0).cls().name();
                    String obj2 = ((MAssociation)assoclink.child()).associationEnds().get(1).cls().name();

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

    public String checkWellDefinednessState() {
        return checkWellDefinednessState(NullPrintWriter.getInstance());
    }

    public String checkWellDefinednessState(PrintWriter error){
        MLMSystemState.Definedness result = MLMSystemState.Definedness.WellDefined;
        MModel previousModel = fModelsList.get(0);
        Collection<MModel> models = this.models().stream().skip(1).collect(Collectors.toList());
        for (MModel model : models){
            MMediator mediator = fMediators.get(model.name());
            UseMLMSystemApi systemApi = new UseMLMSystemApi(previousModel);

            //for each clabject, we create an object of the instance type
            for (MClabject clabject : mediator.clabjects()){
                try {
                    systemApi.createObject(clabject.parent().name(), clabject.child().name());

                }catch (Exception e){
                    error.println(e.getMessage());
                    return MLMSystemState.Definedness.NotWellDefined.toString();
                }
            }

            for (MAssoclink assoclink : mediator.assocLinks()){
                try {
                    String obj1 = ((MAssociation)assoclink.child()).associationEnds().get(0).cls().name();
                    String obj2 = ((MAssociation)assoclink.child()).associationEnds().get(1).cls().name();

                    systemApi.createLink(assoclink.parent().name(), obj1, obj2);

                }catch (Exception e){
                    error.println(e.getMessage());
                    return MLMSystemState.Definedness.NotWellDefined.toString();
                }
            }

            MLMSystemState.Definedness currRes = systemApi.checkWellDefinedness(error);
            if (currRes.equals(MLMSystemState.Definedness.NotWellDefined)){
                return MLMSystemState.Definedness.NotWellDefined.toString();
            }
            else if (currRes.equals(MLMSystemState.Definedness.PartiallyDefined) && result.equals(MLMSystemState.Definedness.WellDefined)){
                result = MLMSystemState.Definedness.PartiallyDefined;
            }
            previousModel = model;
        }
        if (result.equals(MLMSystemState.Definedness.PartiallyDefined)){
            return MLMSystemState.Definedness.WellDefined.toString();
        }
        else return result.toString();
    }

    @Override
    public void processWithVisitor(MMVisitor v) {
        v.visitMLM(this);
    }

    public List<MClass> powerTypes(){
        List<MClass> res = new ArrayList<>();
        for (MMediator mediator : this.mediators()){
            res.addAll(mediator.powerTypes());
        }
        return res;
    }

    public List<MClass> powerTypes(String levelName) {
        MModel prevModel = this.getNextModel(levelName);
        if (prevModel == null){
            return new ArrayList<>();
        }
        MMediator nextMediator = this.getMediator(prevModel.name());
        return nextMediator.powerTypes();
    }

    public List<MClass> powerTypesOfClass(String className) {
        List<MClass> res = new ArrayList<>();
        ((MInternalClassImpl) getClass(className)).clabjectsFromParents().forEach(
                clab -> res.add((MClass) clab.parent())
        );
        return res;
    }

    public Set<MClass> subClassesOfClassForInvariant(MClass cls, MClassInvariant inv){
        Set<MClass> res = new HashSet<>();
        Set<MClass> children = ((MInternalClassImpl) cls).children();

        //clabjects connect classes from different levels
        for (MClass child : children) {
            if (!child.model().equals(cls.model())) {
                MGeneralization edge = cls.model().generalizationGraph().edgesBetween(child, cls).iterator().next();
                MClabject clabject = ((MClabject) edge);
                if (clabject.getRemovedConstraints().contains(inv)){
                    continue;
                }
                res.add(child);
                subClassesOfClassForInvariant(child, inv);
            }
        }
        return res;
    }
}
