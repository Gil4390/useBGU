package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;

public class MClabject {

    private MClassifier fParent;
    private MClassifier fChild;

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;


    public MClabject(MClassifier child, MClassifier parent) {
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
}
