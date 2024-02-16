package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class ASTMediator extends ASTAnnotatable{

    private final Token fName;
    private final List<ASTMediatorElement> fMediatorElements;

    public ASTMediator(Token fName) {
        this.fName = fName;
        fMediatorElements = new ArrayList<>();
    }

    public void addMediatorElement(ASTMediatorElement astMediatorElement){
        fMediatorElements.add(astMediatorElement);
    }
}
