package org.tzi.use.uml.mm;

import org.tzi.use.api.UseMLMSystemApi;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.uml.Definedness;
import org.tzi.use.uml.Satisfiability;
import org.tzi.use.uml.ocl.type.EnumType;
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
        model.classes().forEach(cls -> ((MInternalClassImpl)cls).setMainModel(this));
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
                        throw new MInvalidModelException("Attribute "+childAttr.name()+" is present in both parent: " + gen.parent().name() + " and child: " + gen.child().name());
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
        Definedness result = Definedness.WellDefined;

        for (MMediator mediator : mediators()){
            MModel previousModel = mediator.getParentModel();
            if (previousModel == null) continue;
            UseMLMSystemApi systemApi = new UseMLMSystemApi(previousModel);

            //for each clabject, we create an object of the instance type
            for (MClabject clabject : mediator.clabjects()){
                try {
                    systemApi.createObject(clabject.parent().name(), clabject.child().name());

                }catch (Exception e){
                    error.println(e.getMessage());
                    return Definedness.NotWellDefined.toString();
                }
            }

            for (MAssoclink assoclink : mediator.assocLinks()){
                try {
                    String obj1 = ((MAssociation)assoclink.child()).associationEnds().get(0).cls().name();
                    String obj2 = ((MAssociation)assoclink.child()).associationEnds().get(1).cls().name();

                    systemApi.createLink(assoclink.parent().name(), obj1, obj2);

                }catch (Exception e){
                    error.println(e.getMessage());
                    return Definedness.NotWellDefined.toString();
                }
            }

            Satisfiability currRes = systemApi.checkWellDefinedness(error);
            if (currRes.equals(Satisfiability.NotSatisfied)){
                return Definedness.NotWellDefined.toString();
            }
            else if (currRes.equals(Satisfiability.PartiallySatisfied) && result.equals(Definedness.WellDefined)){
                result = Definedness.PartiallyDefined;
            }
        }
        if (result.equals(Definedness.PartiallyDefined)){
            return Definedness.WellDefined.toString();
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
        for (MMediator med : mediators()){
            if (med.parentModelName().equals(levelName)){
                return fMediators.get(med.name()).powerTypes();
            }
        }
        return new ArrayList<>();
    }

    // given a class and an invariant, calculates the subclasses that the invariant should be checked for
    // if the invariant is a local invariant (meaning it's defined within a model) then unless removed in a clabject it is applied to all subclasses
    // if the invariant is an inter-invariant then it's only applied to subclasses within the scope of the model of the base class
    public Set<MClass> subClassesOfClassForInvariant(MClass cls, MClassInvariant inv){
        Set<MClass> res = new HashSet<>();
        Set<MClass> children = ((MInternalClassImpl) cls).children();

        for (MClass child : children) {
            //check if the inheritance is of type clabject, if so the invariant might have been removed.
            if (!child.model().equals(cls.model())) {
                MGeneralization edge = cls.model().generalizationGraph().edgesBetween(child, cls).iterator().next();
                MClabject clabject = ((MClabject) edge);
                if (clabject.getRemovedConstraints().contains(inv)){
                    continue;
                }
                if (this.interInvariants().contains(inv)){
                    continue;
                }
            }
            res.add(child);
            res.addAll(subClassesOfClassForInvariant(child, inv));
        }
        return res;
    }

    public Set<MClassInvariant> allClassInvariants(MClass cls) {
        //local constraints
        Set<MClassInvariant> res = cls.model().classInvariants(cls);
        //inter-constraints
        res.addAll(this.classInvariants(cls));

         for (MClass parent : cls.parents()){
             Set<MClassInvariant> parentConstraints = this.allClassInvariants(parent);
             res.addAll(parentConstraints);
             //check if the inheritance is of type clabject, if so the invariant might have been removed.
             if (!parent.model().equals(cls.model())) {
                 MGeneralization edge = cls.model().generalizationGraph().edgesBetween(cls, parent).iterator().next();
                 MClabject clabject = ((MClabject) edge);

                 for (MClassInvariant inv : parentConstraints){
                     if (clabject.getRemovedConstraints().contains(inv)){
                         res.remove(inv);
                     }
                     if (this.interInvariants().contains(inv)){
                         res.remove(inv);
                     }
                 }

             }
         }

        return res;
    }
}
