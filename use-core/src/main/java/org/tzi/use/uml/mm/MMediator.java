package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MMediator {

    private String fName;
    private final List<MClabjectInstance> fClabjects;
    private final List<MAssocLinkInstance> fAssocLinks;
//    private final List<MRestrictionClass> fClabjects;

    public MMediator(String name) {
        this.fName = name;
        fClabjects = new ArrayList<>();
        fAssocLinks = new ArrayList<>();
    }

    public void addClabjectInstance(MClabjectInstance clabject) {
        fClabjects.add(clabject);
    }

    public void addAssocLinkInstance(MAssocLinkInstance assoclink) {
        fAssocLinks.add(assoclink);
    }


}
