package org.tzi.use.uml.mm;

public class MLevel {

    private String fName;
    private String fParentName;
    private MModel fModel;
    private MMediator fMediator;

    public MLevel(String fName, String fParentName) {
            this.fName = fName;
            this.fParentName = fParentName;
    }

    public void setModel(MModel model) {
        this.fModel = model;
    }

    public void setMediator(MMediator mediator) {
        this.fMediator = mediator;
    }

    public MModel model() {
        return this.fModel;
    }

    public String name() {
        return fName;
    }

}
