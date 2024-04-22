package org.tzi.use.uml.mm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MClabject {

    private final String fName;

    private MClass fParent;
    private MClass fChild;

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;


    public MClabject(MClass child, MClass parent) {
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

    public MAttributeRenaming getRenamedAttribute(String oldName) {
        for(MAttributeRenaming removedAttribute : fAttributeRenaming) {
            if(removedAttribute.attribute().name().equals(oldName)) {
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
        return fAttributeRenaming.stream().map(MAttributeRenaming::attribute).collect(Collectors.toList());
    }

    public List<MAttribute> getAllAttributes(){
        //TODO
        return null;
    }

    public String name() {
        return fName;
    }
}
