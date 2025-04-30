package org.tzi.use.uml.mm;

import java.util.*;
import java.util.stream.Collectors;

public class MClabject extends MGeneralization {

    private final List<MAttributeRenaming> fAttributeRenaming;
    private final List<MAttribute> fRemovedAttributes;
    private final List<MAssociationEnd> fRemovedRoles;
    private final List<MAssociationEnd> fRemovedRolesAssoclink;
    private final List<MClassInvariant> fRemovedConstraints;


    public MClabject(MClass child, MClass parent) {
        super(child, parent);
        this.fRemovedAttributes = new ArrayList<>();
        this.fAttributeRenaming = new ArrayList<>();
        this.fRemovedRoles = new ArrayList<>();
        this.fRemovedRolesAssoclink = new ArrayList<>();
        this.fRemovedConstraints = new ArrayList<>();
    }

    public void addAttributeRenaming(MAttributeRenaming attributeRenaming) {
        Set<String> taken = fAttributeRenaming.stream().map(MAttributeRenaming::newName).collect(Collectors.toSet());
        taken.addAll(child().allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet()));
        taken.addAll(parent().allAttributes().stream().map(MAttribute::name).collect(Collectors.toSet()));
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
    }

    public void addRemovedRole(MAssociationEnd role) {
        fRemovedRoles.add(role);
    }
    public void addRemovedRoleAssoclink(MAssociationEnd role) {
        fRemovedRolesAssoclink.add(role);
    }

    public List<MAssociationEnd> getRemovedRoles() {
        List<MAssociationEnd> allRemovedRoles = new ArrayList<>(fRemovedRoles);
        allRemovedRoles.addAll(fRemovedRolesAssoclink);
        return allRemovedRoles;
    }

    public List<MAssociationEnd> getOnlyClabjectRemovedRoles() {
        return fRemovedRoles;
    }

    public List<MAssociationEnd> getOnlyAssoclinkRemovedRoles() {
        return fRemovedRolesAssoclink;
    }

    public void addRemovedConstraint(MClassInvariant constraint) {
        fRemovedConstraints.add(constraint);
    }

    public List<MClassInvariant> getRemovedConstraints() {
        return fRemovedConstraints;
    }


    @Override
    public String name(){
        return "CLABJECT___" + fChild.name() + "___" + fParent.name();
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
