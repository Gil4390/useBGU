package org.tzi.use.uml.mm;

import java.util.*;
import java.util.stream.Collectors;

public class MMediator extends MModelElementImpl {

    private MModel currentModel;
    private MModel parentModel;
    private final Map<String, MClabject> fClabjects;
    private final Map<String, MAssoclink> fAssocLinks;

    public MMediator(String name) {
        super(name);
        fClabjects = new HashMap<>();
        fAssocLinks = new HashMap<>();
    }

    public void setCurrentModel(MModel currentModel) {
        this.currentModel = currentModel;
    }

    public void setParentModel(MModel parentModel) {
        this.parentModel = parentModel;
    }

    public MModel getCurrentModel() {
        return currentModel;
    }

    public MModel getParentModel() {
        return parentModel;
    }

    public void addClabject(MClabject clabject) {
        fClabjects.put(clabject.name(), clabject);
    }

    public MClabject getClabject(String name){
        return this.fClabjects.get(name);
    }

    public MClabject getClabject(MClass child, MClass parent){
        for(MClabject clabject : fClabjects.values()){
            if(clabject.child().equals(child) && clabject.parent().isSubClassifierOf(parent)){
                return clabject;
            }
        }
        return null;
    }

    public void addAssocLink(MAssoclink assoclink) {
        fAssocLinks.put(assoclink.name(), assoclink);
    }

    public MAssoclink getAssoclink(String name){
        return this.fAssocLinks.get(name);
    }

    public String parentModelName() {
        if(parentModel == null) {
            return "NONE";
        }
        return parentModel.name();
    }

    @Override
    public void processWithVisitor(MMVisitor v) {
        v.visitMediator(this);
    }

    public Collection<MClabject> clabjects(){
        return fClabjects.values();
    }

    public Collection<MAssoclink> assocLinks(){
        return fAssocLinks.values();
    }

    public List<MClass> powerTypes(){
        ArrayList<MClass> powerTypes = new ArrayList<>();
        for(MClabject clabject : fClabjects.values()){
            powerTypes.add((MClass)clabject.parent());
        }
        return powerTypes;
    }

    public List<MClabject> clabjectsOfAssoclink(String assoclinkName){
        return null;
    }

    public MAssoclink assoclinkOfClabject(String clabjectName){
        MClabject clabject = getClabject(clabjectName);
        for(MAssoclink assoclink : fAssocLinks.values()){
            MAssociation childAssoc = (MAssociation) assoclink.child();
            MAssociation parentAssoc = (MAssociation) assoclink.parent();
            if(childAssoc.associationEnds().stream().map(MAssociationEnd::cls).collect(Collectors.toList()).contains(clabject.child())
                    && parentAssoc.associationEnds().stream().map(MAssociationEnd::cls).collect(Collectors.toList()).contains(clabject.parent()) ){
                return assoclink;
            }
        }
        return null;
    }

}
