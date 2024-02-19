package org.tzi.use.uml.mm;

public class MultiLevelModelFactory extends ModelFactory {


    public MMultiLevelModel createMLM(String name) {
        return new MMultiLevelModel(name);
    }

    public MLevel createLevel(String name, String parentName) {
        return new MLevel(name, parentName);
    }

    public MMediator createMediator(String name) {
        return new MMediator(name);
    }

    public MClabjectInstance createClabjectInstance(MClassifier child, MClassifier parent) {
        return new MClabjectInstance(child, parent);
    }

}
