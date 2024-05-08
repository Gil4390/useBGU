package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MRestrictionClass extends MGeneralization{

    //private final HashMap<String, MAttribute> fAttributeRenaming;

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;

    public MRestrictionClass(MClassifier child, MClassifier parent) {
        super(child, parent);
        this.fRemovedAttributes = new ArrayList<>();
        this.fAttributeRenaming = new ArrayList<>();
    }

    public void addAttributeRenaming(MAttributeRenaming attributeRenaming) {
        fAttributeRenaming.add(attributeRenaming);
    }

    public void addRemovedAttribute(MAttribute attribute) {
        fRemovedAttributes.add(attribute);
    }

    public MAttribute getRemovedAttribute(String name) {
        for(MAttribute removedAttribute : fRemovedAttributes) {
            if(removedAttribute.name().equals(name)) {
                return removedAttribute;
            }
        }
        return null;
    }

    public MAttributeRenaming getRenamedAttribute(String oldName) {
        for(MAttributeRenaming removedAttribute : fAttributeRenaming) {
            if(removedAttribute.attribute().name().equals(oldName)) {
                return removedAttribute;
            }
        }
        return null;
    }

    public List<MAttributeRenaming> getAttributeRenaming() {
        return fAttributeRenaming;
    }

    public List<MAttribute> getRemovedAttributes() {
        return fRemovedAttributes;
    }


    public MAttributeRenaming addAttributeRenamingApi(String oldAttributeName, String newAttributeName) {
        MAttribute oldAttribute = fParent.attribute(oldAttributeName, false);
        if (oldAttribute == null) {
            throw new NullPointerException("Attribute: " + oldAttributeName + " is invalid");
        }
        if (fChild.attribute(newAttributeName, false) != null) {
            throw new NullPointerException("Attribute: " + newAttributeName + " already exists");
        }
        MAttributeRenaming attributeRenaming = new MAttributeRenaming(oldAttribute, newAttributeName);
        fAttributeRenaming.add(attributeRenaming);
        return attributeRenaming;
    }

}
