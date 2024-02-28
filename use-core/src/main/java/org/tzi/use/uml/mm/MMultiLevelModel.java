package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MMultiLevelModel extends MMultiModel {

    private MMultiModel fMultiModel;
    private final List<MMediator> fMediators;
    protected MMultiLevelModel(String name) {
        super(name);
        fMediators = new ArrayList<>();
    }

    public void setMultiModel(MMultiModel multiModel) {
        this.fMultiModel = multiModel;
    }

    public void addMediator(MMediator mediator) {
        this.fMediators.add(mediator);
    }

    @Override
    public void addGeneralization(MGeneralization gen) throws MInvalidModelException {
        super.addGeneralization(gen);
        gen.validateInheritance();
    }

    public boolean isValid(){
        //BIG TODO

        return false;
    }


}
