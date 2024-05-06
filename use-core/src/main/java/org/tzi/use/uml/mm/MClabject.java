package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.NonNull;
import org.tzi.use.graph.DirectedEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MClabject implements DirectedEdge<MClassifier> {

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

    public List<MAttributeRenaming> getAttributeRenaming() {
        return fAttributeRenaming;
    }

    public List<MAttribute> getRemovedAttributes() {
        return fRemovedAttributes;
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

    public Set<String> getParentAttributes() {
        Set<String> result = new HashSet<>();
        for(MAttribute attribute : fParent.attributes()) {
            if(!fRemovedAttributes.contains(attribute) && fAttributeRenaming.stream().noneMatch(ar -> ar.attribute().equals(attribute)))
                result.add(attribute.name());
            else if(fAttributeRenaming.stream().anyMatch(ar -> ar.attribute().equals(attribute)))
                result.add(fAttributeRenaming.stream().filter(ar -> ar.attribute().equals(attribute)).findFirst().get().newName());
        }
        return result;
    }

    @Override
    public @NonNull MClassifier source() {
        return fParent;
    }

    @Override
    public @NonNull MClassifier target() {
        return fChild;
    }

    @Override
    public boolean isReflexive() {
        return false;
    }
}
