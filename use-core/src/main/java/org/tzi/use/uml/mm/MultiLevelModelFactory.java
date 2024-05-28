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

    public MLevel createLevel(String name, String parentName) {
        return new MLevel(name, parentName);
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

    public MRoleRenaming createRoleRenaming(MAssociationEnd endp, MAssociationEnd endc, String newName){
        return new MRoleRenaming((MInternalAssociationEnd)endp, (MInternalAssociationEnd)endc, newName);
    }

    public MAssoclink createAssoclink(MAssociation child, MAssociation parent) {
        return new MAssoclink(child, parent);
    }

    @Override
    public MMultiModel createMultiModel(String name) {
        return createMLM(name);
    }

    @Override
    public MAssociationEnd createAssociationEnd(MClass cls,
                                                String rolename,
                                                MMultiplicity mult,
                                                int kind,
                                                boolean isOrdered,
                                                List<VarDecl> qualifiers) {
        return new MInternalAssociationEnd(cls, rolename, mult, kind, isOrdered, qualifiers);
    }
}
