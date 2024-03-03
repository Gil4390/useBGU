package org.tzi.use.uml.mm;

public class MultiLevelModelFactory extends MultiModelFactory {


    public MMultiLevelModel createMLM(String name) {
        return new MMultiLevelModel(name);
    }

    public MLevel createLevel(String name, String parentName) {
        return new MLevel(name, parentName);
    }

    public MMediator createMediator(String name) {
        return new MMediator(name);
    }

    public MClabject createClabjectInstance(MClassifier child, MClassifier parent) {
        return new MClabject(child, parent);
    }

    public MAssoclink createAssocLinkInstance(MAssociation child, MAssociation parent) {
        return new MAssoclink(child, parent);
    }

    @Override
    public MMultiModel createMultiModel(String name) {
        return createMLM(name);
    }
}
