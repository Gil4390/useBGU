package org.tzi.use.uml.mm;

import java.util.*;

public class MMediator {

    private String fName;

    private MModel currentModel;
    private MModel parentModel;
    private final Map<String, MClabject> fClabjects;
    private final Map<String, MAssoclink> fAssocLinks;

    public MMediator(String name) {
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

    public void addAssocLink(MAssoclink assoclink) {
        fAssocLinks.put(assoclink.name(), assoclink);
    }

    public MAssoclink getAssoclink(String name){
        return this.fAssocLinks.get(name);
    }

    public String name(){
        return fName;
    }

    public Collection<MClabject> clabjects(){
        return fClabjects.values();
    }

    public Collection<MAssoclink> assocLinks(){
        return fAssocLinks.values();
    }

}