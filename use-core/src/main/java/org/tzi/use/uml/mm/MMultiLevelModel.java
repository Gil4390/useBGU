package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MMultiLevelModel extends MModel {

    private final List<MLevel> fLevels;
    protected MMultiLevelModel(String name) {
        super(name);
        fLevels = new ArrayList<>();
    }

    public void addLevel(MLevel level) {
        fLevels.add(level);
    }

    @Override
    public void addGeneralization(MGeneralization gen) throws MInvalidModelException {
        super.addGeneralization(gen);
        gen.validateInheritance();
    }




}
