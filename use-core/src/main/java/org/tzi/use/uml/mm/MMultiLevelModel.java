package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MMultiLevelModel extends MMultiModel {

    private final List<MLevel> fLevels;
    protected MMultiLevelModel(String name) {
        super(name);
        fLevels = new ArrayList<>();
    }

    public void addLevel(MLevel level) throws Exception {
        fLevels.add(level);
        this.addModel(level.model());
    }

    @Override
    public void addGeneralization(MGeneralization gen) throws MInvalidModelException {
        super.addGeneralization(gen);
        gen.validateInheritance();
    }




}
