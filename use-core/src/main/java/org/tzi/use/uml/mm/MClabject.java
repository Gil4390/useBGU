package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.NonNull;
import org.tzi.use.graph.DirectedEdge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MClabject extends MGeneralization {

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;


    public MClabject(MClass child, MClass parent) {
        super(child, parent);
        this.fRemovedAttributes = new ArrayList<>();
        this.fAttributeRenaming = new ArrayList<>();
    }

    public void addAttributeRenaming(MAttributeRenaming attributeRenaming) {
        Set<String> taken = fAttributeRenaming.stream().map(MAttributeRenaming::newName).collect(Collectors.toSet());
        taken.addAll(((MInternalClassImpl)child()).allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet()));
        taken.addAll(((MInternalClassImpl)parent()).allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet()));
        if(taken.contains(attributeRenaming.newName())) {
            throw new NullPointerException("Attribute: " + attributeRenaming.newName() + " already exists");
        }
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

    @NonNull
    public MClassifier parent(){
        return fParent;
    }

    @NonNull
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

    @Override
    public String name(){
        return "CLABJECT_" + fChild.name() + "_" + fParent.name();
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
