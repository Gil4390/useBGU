package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ASTClabjectInstance extends ASTMediatorElement{

    private final List<Pair<Token>> fAttributeRenaming;
    public ASTClabjectInstance(Token fChildName, Token fParentName) {
        super(fChildName, fParentName);
        fAttributeRenaming = new ArrayList<>();
    }

    public void addAttributeRenaming(Token oldName, Token newName){
        Pair<Token> p = new Pair<>();
        p.first = oldName;
        p.second = newName;
        fAttributeRenaming.add(p);
    }

    public MClabjectInstance gen(MLMContext mlmContext) throws Exception {
        MClass child = mlmContext.getCurrentModel().getClass(this.fChildName.getText());
        if(child == null) {
            throw new Exception("Class: " + this.fChildName.getText() + ", in the Model: "+mlmContext.getCurrentModel().name()+", doesn't exist.");
        }
        MClass parent = mlmContext.getParentModel().getClass(this.fParentName.getText());
        if(parent == null) {
            throw new Exception("Class: " + this.fParentName.getText() + ", in the Model: "+mlmContext.getParentModel().name()+", doesn't exist.");
        }

        MClabjectInstance mClabjectInstance = mlmContext.modelFactory().createClabjectInstance(child,parent);

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
                    mClabjectInstance.addRemovedAttribute(attribute);
                    break;
                } else if(pair.first.getText().equals(attribute.name())) {
                    MAttributeRenaming attributeRenaming = new MAttributeRenaming(attribute, pair.second.getText());
                    mClabjectInstance.addAttributeRenaming(attributeRenaming);
                    break;
                } else {
                    MAttributeRenaming attributeRenaming = new MAttributeRenaming(attribute, attribute.name());
                    mClabjectInstance.addAttributeRenaming(attributeRenaming);
                    break;
                }

            }
        }


        return mClabjectInstance;
    }
}
