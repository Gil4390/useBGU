package org.tzi.use.uml.mm;

import java.util.HashMap;

public class MRestrictionClass extends MGeneralization{

    private final HashMap<String, MAttribute> fAttributeRenaming;

    public MRestrictionClass(MClassifier child, MClassifier parent) {
        super(child, parent);
        fAttributeRenaming = new HashMap<>();
    }

    @Override
    public void validateInheritance() {

    }

    public void addAttributeRenaming(String newAttributeName, MAttribute oldAttribute) {
        //TODO: what if someone tries to rename an attribute twice?
        fAttributeRenaming.put(newAttributeName, oldAttribute);
    }

    public MAttribute searchAttribute(String attributeName) {
        return null;
    }


}
