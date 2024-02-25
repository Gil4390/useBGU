package org.tzi.use.uml.mm;

public class MClassLevelImpl extends MClassImpl {

    MClassLevelImpl(String name, boolean isAbstract) {
        super(name, isAbstract);
    }

    @Override
    public MAttribute attribute(String name, boolean searchInherited) {
        MAttribute attribute = super.attribute(name, searchInherited);
        if(attribute != null) {
            return attribute;
        }
        MClassifier parent = this.parents().iterator().next();
        MGeneralization edge = this.model.generalizationGraph().edgesBetween(this,parent).iterator().next();
        if( edge instanceof MRestrictionClass ) {
            attribute = ((MRestrictionClass) edge).searchAttribute(name);
        }
        return attribute;
    }
}
