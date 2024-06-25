package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASTClabject extends ASTAnnotatable{

    private final Token fChildName;
    private final Token fParentName;
    private final List<Pair<Token>> fAttributeRenaming;
    private final List<Token> fAttributeRemoving;
    private final List<Pair<Token>> fRoleRenaming;
    private final List<Token> fRoleRemoving;
    private MClabject fClabject;
    public ASTClabject(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
        fAttributeRenaming = new ArrayList<>();
        fAttributeRemoving = new ArrayList<>();
        fRoleRenaming = new ArrayList<>();
        fRoleRemoving = new ArrayList<>();
    }

    public void addAttributeRemoving(Token removedName) {
        fAttributeRemoving.add(removedName);
    }

    public void addAttributeRenaming(Token oldName, Token newName){
        Pair<Token> p = new Pair<>();
        p.first = oldName;
        p.second = newName;
        fAttributeRenaming.add(p);
    }

    public void addRoleRenaming(Token oldName, Token newName) {
        Pair<Token> p = new Pair<>();
        p.first = oldName;
        p.second = newName;
        fRoleRenaming.add(p);
    }

    public void addRoleRemoving(Token removedName) {
        fRoleRemoving.add(removedName);
    }

    public MClabject gen(MLMContext mlmContext) throws Exception {
        MClass child = mlmContext.getCurrentModel().getClass(this.fChildName.getText());
        if(child == null) {
            throw new Exception("Class: " + this.fChildName.getText() + ", in the Model: "+mlmContext.getCurrentModel().name()+", doesn't exist.");
        }
        MClass parent = mlmContext.getParentModel().getClass(this.fParentName.getText());
        if(parent == null) {
            throw new Exception("Class: " + this.fParentName.getText() + ", in the Model: "+mlmContext.getParentModel().name()+", doesn't exist.");
        }

        MClabject mClabject = mlmContext.modelFactory().createClabject(child,parent);
        fClabject = mClabject;
        //check that the attributes renaming exists in the parent class
        for(Pair<Token> attributePair : fAttributeRenaming) {
            String oldAttribute = attributePair.first.getText();
            MAttribute oldMAttribute = parent.attribute(oldAttribute,true);
            if(oldMAttribute == null) {
                throw new Exception("Parent class: "+ parent.name()+ ", doesn't contain an attribute with the name: "+oldAttribute);
            }
        }
        //check that the attributes removing exists in the parent class
        for(Token attribute : fAttributeRemoving) {
            String removedAttribute = attribute.getText();
            MAttribute removedMAttribute = parent.attribute(removedAttribute,true);
            if(removedMAttribute == null) {
                throw new Exception("Parent class: "+ parent.name()+ ", doesn't contain an attribute with the name: "+removedAttribute);
            }
        }

        for(Token removedAttrToken : fAttributeRemoving) {
            for(MAttribute attribute : parent.attributes()) {
                if (removedAttrToken.getText().equals(attribute.name())) {
                    mClabject.addRemovedAttribute(attribute);
                    break;
                }
            }
        }

        for(Pair<Token> pair : fAttributeRenaming) {
            String oldAttribute = pair.first.getText();
            MAttribute oldMAttribute = parent.attribute(oldAttribute,true);
            String newAttribute = pair.second.getText();
            MAttributeRenaming attributeRenaming = mlmContext.modelFactory().createAttributeRenaming(oldMAttribute, newAttribute);
            mClabject.addAttributeRenaming(attributeRenaming);
        }

        return mClabject;
    }

    public void genClabjectRoles(MLMContext mlmContext) {
        MClass parent = mlmContext.getParentModel().getClass(this.fParentName.getText());
        MClass child = mlmContext.getCurrentModel().getClass(this.fChildName.getText());

//        MMediator mediator = ((MMultiLevelModel)mlmContext.model()).getMediator(mlmContext.getCurrentModel().name());

        for(Pair<Token> pair : fRoleRenaming) {
            String oldRoleName = pair.first.getText();
            MAssociationEnd oldAssocEnd = (MAssociationEnd) parent.navigableEnd(oldRoleName);
            if(oldAssocEnd == null) {
                throw new NullPointerException("Role: "+oldRoleName+" is not defined in the parent class: "+parent.name());
            }
            String newRoleName = pair.second.getText();
            MAssociationEnd newAssocEnd = (MAssociationEnd) child.navigableEnd(newRoleName);
            if(newAssocEnd != null) {
                throw new NullPointerException("Role: "+newRoleName+" is already defined in the child class: "+child.name());
            }

            ((MInternalAssociationEnd)oldAssocEnd).setNewName(newRoleName, child);
            MRoleRenaming roleRenaming = mlmContext.modelFactory().createRoleRenaming(oldAssocEnd,newRoleName);
            fClabject.addRoleRenaming(roleRenaming);
        }
    }
}
