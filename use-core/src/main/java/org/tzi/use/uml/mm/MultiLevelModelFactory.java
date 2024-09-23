package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.expr.VarDecl;

import java.util.List;

public class MultiLevelModelFactory extends MultiModelFactory {


    public MMultiLevelModel createMLM(String name) {
        return new MMultiLevelModel(name);
    }
    public MMultiLevelModel createMLM(MMultiModel multiModel){
        return new MMultiLevelModel(multiModel);
    }

    public MMediator createMediator(String name) {
        return new MMediator(name);
    }

    public MClabject createClabject(MClass child, MClass parent) {
        return new MClabject(child, parent);
    }

    public MAttributeRenaming createAttributeRenaming(MAttribute attr, String newName){
        return new MAttributeRenaming(attr, newName);
    }

    public MRoleRenaming createRoleRenaming(MAssociationEnd endp,String newName){
        return new MRoleRenaming(endp, newName);
    }

    public MRoleBinding createRoleBinding(MAssociationEnd childAssociationEnd, MAssociationEnd parentAssociationEnd){
        return new MRoleBinding(childAssociationEnd, parentAssociationEnd);
    }

    public MAssoclink createAssoclink(MAssociation child, MAssociation parent) {
        return new MAssoclink(child, parent);
    }

    @Override
    public MMultiModel createMultiModel(String name) {
        return createMLM(name);
    }

}
