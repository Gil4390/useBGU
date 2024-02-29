package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MMediator {

    private String fName;
    private final List<MClabject> fClabjects;
    private final List<MAssoclink> fAssocLinks;
//    private final List<MRestrictionClass> fClabjects;

    public MMediator(String name) {
        this.fName = name;
        fClabjects = new ArrayList<>();
        fAssocLinks = new ArrayList<>();
    }

    public void addClabjectInstance(MClabject clabject) {
        fClabjects.add(clabject);
    }

    public void addAssocLinkInstance(MAssoclink assoclink) {
        fAssocLinks.add(assoclink);
    }

    public String name(){
        return fName;
    }

    public List<MClabject> clabjects(){
        return fClabjects;
    }

    public List<MAssoclink> assocLinks(){
        return fAssocLinks;
    }

}
