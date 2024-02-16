package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
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
}
