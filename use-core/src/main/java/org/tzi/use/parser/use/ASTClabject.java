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

        Set<String> renamedAttributes = new HashSet<>();
//        for(MAttribute attribute : parent.allAttributes()) {
//            renamedAttributes.add(attribute.name());
//        }
//        //TODO: fix implementation (doesnt look good)
//        //TODO: still adds the removed attribute, need to check why
//        for(MAttribute attribute : parent.attributes()) {
//            boolean toInherit = true;
//            for(Pair<Token> pair : fAttributeRenaming) {
//                if(pair.first.getText().equals(attribute.name())) {
//                    if (renamedAttributes.contains(pair.second.getText())) {
//                        throw new Exception("Attribute: " + pair.second.getText() + " is already in use");
//                    }
//                    MAttributeRenaming attributeRenaming = mlmContext.modelFactory().createAttributeRenaming(attribute, pair.second.getText());
//                    mClabject.addAttributeRenaming(attributeRenaming);
//                    renamedAttributes.add(pair.second.getText());
//                    toInherit = false;
//                    break;
//                } else if(mClabject.getRemovedAttribute(attribute.name()) != null) {
//                    toInherit = false;
//                    break;
//                }
//            }
//            if(toInherit) {
//                MAttributeRenaming attributeRenaming = mlmContext.modelFactory().createAttributeRenaming(attribute, attribute.name());
//                mClabject.addAttributeRenaming(attributeRenaming);
//            }
//        }

/*
        Set<String> takenAttributes = new HashSet<>();
        for(MAttribute attribute : parent.allAttributes()) {
            takenAttributes.add(attribute.name());
        }

        //check if there is overlap between the attributes of the parent and the child
        for(MAttribute childAttribute : child.allAttributes()) {
            if(!takenAttributes.contains(childAttribute.name())){
                takenAttributes.add(childAttribute.name());
                continue;
            }

            //conflict, check if the attribute is removed or renamed
            boolean conflict = true;
            if (mClabject.getRemovedAttribute(childAttribute.name()) != null) {
                continue;
            }
            MAttributeRenaming attributeRenaming = mClabject.getRenamedAttribute(childAttribute.name());
            if (attributeRenaming != null) {
                String newName = attributeRenaming.newName();
                //check newName is not taken
                if (!takenAttributes.contains(newName)) {
                    takenAttributes.add(newName);
                    conflict = false;
                }
            }
            if (conflict) {
                throw new Exception("Attribute: " + childAttribute.name() + " is inherited from the parent class: " + parent.name() + " and is also present in the child class: " + child.name());
            }
        }
*/
        return mClabject;
    }
}
