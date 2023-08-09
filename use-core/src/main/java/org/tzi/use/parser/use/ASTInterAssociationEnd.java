package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

public class ASTInterAssociationEnd extends ASTAssociationEnd{

    private Token fModelName;

    public ASTInterAssociationEnd(Token modelName, Token name, ASTMultiplicity mult) {
        super(name, mult);
        fModelName = modelName;
    }

    public String modelName() {
        return fModelName.getText();
    }


}
