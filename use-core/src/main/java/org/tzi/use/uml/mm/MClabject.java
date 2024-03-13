package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MClabject {

    private final String fName;

    private MClassifier fParent;
    private MClassifier fChild;

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;


    public MClabject(MClassifier child, MClassifier parent) {
        fName = child.name();
        this.fParent = parent;
        this.fChild = child;
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

    public MClassifier parent(){
        return fParent;
    }

    public MClassifier child(){
        return fChild;
    }

    public List<MAttribute> getAttributes(){
        //TODO
        return null;
    }

    public List<MAttribute> getAllAttributes(){
        //TODO
        return null;
    }

    public String name() {
        return fName;
    }
}
