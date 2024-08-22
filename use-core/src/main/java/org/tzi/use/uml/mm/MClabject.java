package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.NonNull;
import org.tzi.use.graph.DirectedEdge;

import java.util.*;
import java.util.stream.Collectors;

public class MClabject extends MGeneralization {

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;

    private final List<MRoleRenaming> fRoleRenaming;
    private final List<MAssociationEnd> fRemovedRoles;


    public MClabject(MClass child, MClass parent) {
        super(child, parent);
        this.fRemovedAttributes = new ArrayList<>();
        this.fAttributeRenaming = new ArrayList<>();
        this.fRoleRenaming = new ArrayList<>();
        this.fRemovedRoles = new ArrayList<>();
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

    public void addRoleRenaming(MRoleRenaming roleRenaming) {
        fRoleRenaming.add(roleRenaming);
    }

    public MAttribute getRemovedAttribute(String name) {
        for(MAttribute removedAttribute : fRemovedAttributes) {
            if(removedAttribute.name().equals(name)) {
                return removedAttribute;
            }
        }
        return null;
    }

    public void addRemovedRole(MAssociationEnd role) {
        fRemovedRoles.add(role);
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

    public Map<String, MAttribute> getAttributes(){
        Map<String, MAttribute> attributes = new HashMap<>();
        fAttributeRenaming.forEach((attributeRenaming) -> {
            attributes.put(attributeRenaming.newName(), attributeRenaming.attribute());
        });
        return attributes;
        //return fAttributeRenaming.stream().map(MAttributeRenaming::attribute).collect(Collectors.toList());
    }

    public List<MRoleRenaming> getRenamedRoles() {
        return fRoleRenaming;
    }

    public List<MAssociationEnd> getRemovedRoles() {
        return fRemovedRoles;
    }


    @Override
    public String name(){
        return "CLABJECT_" + fChild.name() + "_" + fParent.name();
    }

    @Override
    public String toString(){
        return name();
    }

    @Override
    public boolean isReflexive() {
        return false;
    }

    @Override
    public void processWithVisitor(MMVisitor v) {
        v.visitClabject(this);
    }
}
