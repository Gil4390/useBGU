package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ASTClabject extends ASTAnnotatable{

    private final Token fChildName;
    private final Token fParentName;
    private final List<Pair<Token>> fAttributeRenaming;
    private final List<Token> fAttributeRemoving;
    public ASTClabject(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
        fAttributeRenaming = new ArrayList<>();
        fAttributeRemoving = new ArrayList<>();
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

        for(Pair<Token> attributePair : fAttributeRenaming) {
            String oldAttribute = attributePair.first.getText();
            MAttribute oldMAttribute = parent.attribute(oldAttribute,true);
            if(oldMAttribute == null) {
                throw new Exception("Parent class: "+ parent.name()+ ", doesn't contain an attribute with the name: "+oldAttribute);
            }
        }

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
        //TODO: fix implementation (doesnt look good)
        //TODO: still adds the removed attribute, need to check why
        for(MAttribute attribute : parent.attributes()) {
            boolean toInherit = true;
            for(Pair<Token> pair : fAttributeRenaming) {
                if(pair.first.getText().equals(attribute.name())) {
                    MAttributeRenaming attributeRenaming = mlmContext.modelFactory().createAttributeRenaming(attribute, pair.second.getText());
                    mClabject.addAttributeRenaming(attributeRenaming);
                    toInherit = false;
                    break;
                } else if(mClabject.getRemovedAttribute(attribute.name()) != null) {
                    toInherit = false;
                    break;
                }
            }
            if(toInherit) {
                MAttributeRenaming attributeRenaming = mlmContext.modelFactory().createAttributeRenaming(attribute, attribute.name());
                mClabject.addAttributeRenaming(attributeRenaming);
            }
        }


        return mClabject;
    }
}
