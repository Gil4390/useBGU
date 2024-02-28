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

    public MClabjectInstance createClabjectInstance(MClassifier child, MClassifier parent) {
        return new MClabjectInstance(child, parent);
    }

    public MAssocLinkInstance createAssocLinkInstance(MClassifier child, MClassifier parent) {
        return new MAssocLinkInstance(child, parent);
    }


//    public MMLMClass createClass()

}
