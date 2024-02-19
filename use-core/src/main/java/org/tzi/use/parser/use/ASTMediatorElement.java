package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

public class ASTMediatorElement extends ASTAnnotatable{

    protected final Token fChildName;
    protected final Token fParentName;

    public ASTMediatorElement(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
    }
}
