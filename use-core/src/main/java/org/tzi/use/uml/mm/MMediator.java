package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MMediator {

    private String fName;

    private final List<MClabjectInstance> fClabjects;

    public MMediator(String name) {
        this.fName = name;
        fClabjects = new ArrayList<>();
    }

    public void addClabjectInstance(MClabjectInstance clabject) {
        fClabjects.add(clabject);
    }


}
