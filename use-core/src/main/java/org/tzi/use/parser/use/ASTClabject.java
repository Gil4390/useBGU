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
    public ASTClabject(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
        fAttributeRenaming = new ArrayList<>();
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

        MClabject mClabject = mlmContext.modelFactory().createClabjectInstance(child,parent);

        for(Pair<Token> attributePair : fAttributeRenaming) {
            String oldAttribute = attributePair.first.getText();
            MAttribute oldMAttribute = parent.attribute(oldAttribute,true);
            if(oldMAttribute == null) {
                throw new Exception("Parent class: "+ parent.name()+ ", doesn't contain an attribute with the name: "+oldAttribute);
            }
        }
        //TODO: fix implementation (doesnt look good)
        for(MAttribute attribute : parent.attributes()) {
            for(Pair<Token> pair : fAttributeRenaming) {
                if(pair.first.getText().equals(attribute.name()) && pair.second == null) {
                    mClabject.addRemovedAttribute(attribute);
                    break;
                } else if(pair.first.getText().equals(attribute.name())) {
                    MAttributeRenaming attributeRenaming = new MAttributeRenaming(attribute, pair.second.getText());
                    mClabject.addAttributeRenaming(attributeRenaming);
                    break;
                } else {
                    MAttributeRenaming attributeRenaming = new MAttributeRenaming(attribute, attribute.name());
                    mClabject.addAttributeRenaming(attributeRenaming);
                    break;
                }

            }
        }


        return mClabject;
    }
}
