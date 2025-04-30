package org.tzi.use.uml.mm;

import java.util.*;
import java.util.stream.Collectors;

public class MAssoclink extends MGeneralization {

    private final List<MRoleBinding> fRoleBinding;

    public MAssoclink(MAssociation child, MAssociation parent) {
        super(child, parent);
        fRoleBinding = new ArrayList<>();
    }

    public void addRoleBinding(MRoleBinding roleBinding) {
        fRoleBinding.add(roleBinding);
    }

    public List<MRoleBinding> roleBindings() {
        return fRoleBinding;
    }
    @Override
    public String name() {
        return "ASSOCLINK___" + fChild.name() + "___" + fParent.name();
    }

    @Override
    public String toString(){
        return name();
    }

    @Override
    public void processWithVisitor(MMVisitor v) {
        v.visitAssoclink(this);
    }
}
