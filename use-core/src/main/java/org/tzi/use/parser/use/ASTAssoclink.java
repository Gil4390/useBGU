package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.Objects;

public class ASTAssoclink extends ASTAnnotatable{
    private final Token fChildName;
    private final Token fParentName;
    private final Pair<Token> fRoleRenamingEnd1;
    private final Pair<Token> fRoleRenamingEnd2;
    public ASTAssoclink(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
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

    public MAssoclink gen(MLMContext mlmContext) throws Exception {
        MAssociation child = mlmContext.getCurrentModel().getAssociation(this.fChildName.getText());
        if(child == null) {
            throw new Exception("Association: " + this.fChildName.getText() + ", in the Model: " + mlmContext.getCurrentModel().name() + ", doesn't exist.");
        }
        MAssociation parent = mlmContext.getParentModel().getAssociation(this.fParentName.getText());
        if(parent == null) {
            throw new Exception("Association: " + this.fParentName.getText() + ", in the level: " + mlmContext.getParentModel().name() + ", doesn't exist.");
        }
        MAssoclink mAssoclink = mlmContext.modelFactory().createAssoclink(child,parent);

        MAssociationEnd mEnd1 = mlmContext.getParentModel().getAssociation(fParentName.getText()).associationEnds().get(0);
        if(fRoleRenamingEnd1 == null) { // both roles isnt specified
            mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEnd1, mEnd1.nameAsRolename()));
        }
        else if(fRoleRenamingEnd1.first != null) {
            if(Objects.equals(mEnd1.nameAsRolename(), fRoleRenamingEnd1.first.getText())) {
                mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEnd1, fRoleRenamingEnd1.second.getText()));
            }
        }


        MAssociationEnd mEnd2 = mlmContext.getParentModel().getAssociation(fParentName.getText()).associationEnds().get(1);
        if(fRoleRenamingEnd2 == null) {
            mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEnd2,mEnd2.nameAsRolename()));
        }
        else if(fRoleRenamingEnd2.first != null) {
            if(Objects.equals(mEnd2.nameAsRolename(), fRoleRenamingEnd2.first.getText())) {
                mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEnd2,fRoleRenamingEnd2.second.getText()));
            }
        }

        return mAssoclink;
    }

}
