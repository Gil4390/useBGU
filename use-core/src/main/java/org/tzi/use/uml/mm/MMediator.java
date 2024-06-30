package org.tzi.use.uml.mm;

import java.util.*;

public class MMediator extends MModelElementImpl {

    private String fName;

    private MModel currentModel;
    private MModel parentModel;
    private final Map<String, MClabject> fClabjects;
    private final Map<String, MAssoclink> fAssocLinks;

    public MMediator(String name) {
        super(name);
        this.fName = name;
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
            if(clabject.child().equals(child) && clabject.parent().equals(parent)){
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

    public String name(){
        return fName;
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

}
