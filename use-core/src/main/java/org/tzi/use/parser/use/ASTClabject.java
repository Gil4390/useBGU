package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.*;

public class ASTClabject extends ASTAnnotatable{

    private final Token fChildName;
    private final Token fParentName;
    private final List<Pair<Token>> fAttributeRenaming;
    private final List<Token> fAttributeRemoving;
    private final List<Token> fRoleRemoving;
    private final List<Token> fConstraintRemoving;
    private MClabject fClabject;

    public ASTClabject(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
        fAttributeRenaming = new ArrayList<>();
        fAttributeRemoving = new ArrayList<>();
        fRoleRemoving = new ArrayList<>();
        fConstraintRemoving = new ArrayList<>();
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

    public void addRoleRemoving(Token removedName) {
        fRoleRemoving.add(removedName);
    }

    public void addConstraintRemoving(Token removedName) {
        fConstraintRemoving.add(removedName);
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
            for(MAttribute attribute : parent.allAttributes()) {
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

        for (Token removedConstraint : fConstraintRemoving){
            String constraintName = removedConstraint.getText();
            MClassInvariant constraint = parent.model().getClassInvariant(parent.name() + "::" + parent.model().name() + "@" + constraintName);
            if(constraint == null) {
                throw new Exception("Parent class: "+ parent.name()+ ", doesn't contain a constraint with the name: "+constraintName);
            }
            mClabject.addRemovedConstraint(constraint);
        }

        return mClabject;
    }

    public void genClabjectRoles(MLMContext mlmContext) {
        MClass parent = mlmContext.getParentModel().getClass(this.fParentName.getText());
        MClass child = mlmContext.getCurrentModel().getClass(this.fChildName.getText());

        for(Token removedRoleToken : fRoleRemoving) {
            String removedRole = removedRoleToken.getText();
            MAssociationEnd removedAssocEnd = (MAssociationEnd) parent.navigableEnd(removedRole);

            if(removedAssocEnd == null) {
                throw new NullPointerException("Role: "+removedRole+" is not defined in the parent class: "+parent.name());
            }
            fClabject.addRemovedRole(removedAssocEnd);
        }

        // Check that there is no role conflict
        child.navigableEnds();
    }
}
