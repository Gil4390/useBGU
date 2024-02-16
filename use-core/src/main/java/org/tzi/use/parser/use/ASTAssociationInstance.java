package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.util.Pair;

public class ASTAssociationInstance extends ASTMediatorElement{
    private final Pair<Token> fRoleRenamingEnd1;
    private final Pair<Token> fRoleRenamingEnd2;
    public ASTAssociationInstance(Token fChildName, Token fParentName) {
        super(fChildName, fParentName);
        fRoleRenamingEnd1 = new Pair<>();
        fRoleRenamingEnd2 = new Pair<>();
    }

    public void addRoleRenaming1(Token oldName, Token newName){
        fRoleRenamingEnd1.first = oldName;
        fRoleRenamingEnd1.second = newName;
    }

    public void addRoleRenaming2(Token oldName, Token newName){
        fRoleRenamingEnd2.first = oldName;
        fRoleRenamingEnd2.second = newName;
    }
}
