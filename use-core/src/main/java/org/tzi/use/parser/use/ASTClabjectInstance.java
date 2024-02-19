package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MClabjectInstance;
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

    public MClabjectInstance gen(MLMContext mlmContext) {
        MClassifier child = mlmContext.level().model().getClass(this.fChildName.getText());
        //TODO: throw error if not exists
        MClassifier parent = mlmContext.parentLevel().model().getClass(this.fParentName.getText());
        //TODO: throw error if not exists
        MClabjectInstance mClabjectInstance = mlmContext.modelFactory().createClabjectInstance(child,parent);
        //TODO: iterate attribute renaming
        return mClabjectInstance;
    }
}
