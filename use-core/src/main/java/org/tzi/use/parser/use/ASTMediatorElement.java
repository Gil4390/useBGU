package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

public class ASTMediatorElement extends ASTAnnotatable{

    private final Token fChildName;
    private final Token fParentName;

    public ASTMediatorElement(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
    }
}
