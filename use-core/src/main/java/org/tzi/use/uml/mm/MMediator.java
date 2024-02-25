package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MMediator {

    private String fName;

    private final List<MRestrictionClass> fClabjects;

    public MMediator(String name) {
        this.fName = name;
        fClabjects = new ArrayList<>();
    }

    public void addClabjectInstance(MRestrictionClass clabject) {
        fClabjects.add(clabject);
    }


}
