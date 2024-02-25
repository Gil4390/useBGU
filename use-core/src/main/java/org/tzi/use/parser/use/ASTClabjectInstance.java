package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MRestrictionClass;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.util.Pair;

import java.util.ArrayList;
import java.util.List;

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

    public MRestrictionClass gen(MLMContext mlmContext) throws Exception {
        MClassifier child = mlmContext.level().model().getClass(this.fChildName.getText());
        if(child == null) {
            throw new Exception("Class: "+fChildName.getText() + ", in the level: "+mlmContext.level().name()+", doesn't exist.");
        }
        MClassifier parent = mlmContext.parentLevel().model().getClass(this.fParentName.getText());
        if(parent == null) {
            throw new Exception("Class: "+fChildName.getText() + ", in the level: "+mlmContext.parentLevel().name()+", doesn't exist.");
        }

        MRestrictionClass mRestrictionClass = mlmContext.modelFactory().createClabjectInstance(child,parent);

        for(Pair<Token> attributePair : fAttributeRenaming) {
            String oldAttribute = attributePair.first.getText();
            MAttribute oldMAttribute = parent.attribute(oldAttribute,true);
            if(oldMAttribute == null) {
                throw new Exception("Parent class: "+ parent.name()+ ", doesn't contain an attribute with the name: "+oldAttribute);
            }
            mRestrictionClass.addAttributeRenaming(attributePair.second.getText(),oldMAttribute);

        }
        return mRestrictionClass;
    }
}
