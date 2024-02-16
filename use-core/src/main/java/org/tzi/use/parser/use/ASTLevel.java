package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

public class ASTLevel extends ASTAnnotatable {

    private final Token fName;
    private final Token fParentName;

    private ASTModel fModel;
    private ASTMediator fMediator;


    public ASTLevel(Token fName, Token fParentName) {
        this.fName = fName;
        this.fParentName = fParentName;
    }

    public void setModel(ASTModel model) {
        this.fModel = model;
    }

    public void setMediator(ASTMediator mediator) {
        this.fMediator = mediator;
    }
}
