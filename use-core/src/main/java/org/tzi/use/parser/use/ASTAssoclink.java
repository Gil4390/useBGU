package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.Objects;

public class ASTAssoclink extends ASTAnnotatable{
    private final Token fChildName;
    private final Token fParentName;
    private Pair<Token> fRoleRenamingEnd1;
    private Pair<Token> fRoleRenamingEnd2;
    public ASTAssoclink(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
    }

    public void addRoleRenaming1(Token oldName, Token newName){
        fRoleRenamingEnd1 = new Pair<>();
        fRoleRenamingEnd1.first = oldName;
        fRoleRenamingEnd1.second = newName;
    }

    public void addRoleRenaming2(Token oldName, Token newName){
        fRoleRenamingEnd2 = new Pair<>();
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

        MAssociationEnd mEndP1 = mlmContext.getParentModel().getAssociation(fParentName.getText()).associationEnds().get(0);
        MAssociationEnd mEndC1 = mlmContext.getCurrentModel().getAssociation(fChildName.getText()).associationEnds().get(0);
        MAssociationEnd mEndP2 = mlmContext.getParentModel().getAssociation(fParentName.getText()).associationEnds().get(1);
        MAssociationEnd mEndC2 = mlmContext.getCurrentModel().getAssociation(fChildName.getText()).associationEnds().get(1);
        boolean isEnd1 = false;
        if(fRoleRenamingEnd1 == null) { // both roles isnt specified
            mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP1, mEndC1, mEndC1.nameAsRolename()));
            mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP2, mEndC2, mEndC2.nameAsRolename()));
        }
        else if(fRoleRenamingEnd1.first != null) {
            if(Objects.equals(mEndP1.nameAsRolename(), fRoleRenamingEnd1.first.getText())) {
                mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP1, mEndC1, fRoleRenamingEnd1.second.getText()));
                isEnd1 = true;
            }
            else if (Objects.equals(mEndP2.nameAsRolename(), fRoleRenamingEnd1.first.getText())) {
                mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP2, mEndC2, fRoleRenamingEnd1.second.getText()));
                isEnd1 = false;
            }
            else{
                //n-arry associations not supported
                throw new Exception("Role: " + fRoleRenamingEnd1.first.getText() + " doesn't exist in the association: " + fChildName.getText());
            }


            if(fRoleRenamingEnd2 == null) {
                if (isEnd1)
                    mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP2, mEndC2,mEndC2.nameAsRolename()));
                else
                    mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP1, mEndC1,mEndC1.nameAsRolename()));
            }
            else if(fRoleRenamingEnd2.first != null) {
                if(Objects.equals(mEndP2.nameAsRolename(), fRoleRenamingEnd2.first.getText())) {
                    mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP2, mEndC2,fRoleRenamingEnd2.second.getText()));
                }
                else if (!isEnd1 && Objects.equals(mEndP1.nameAsRolename(), fRoleRenamingEnd2.first.getText())) {
                    mAssoclink.addRoleRenaming(mlmContext.modelFactory().createRoleRenaming(mEndP1, mEndC1,fRoleRenamingEnd2.second.getText()));
                }
                else{
                    throw new Exception("Role: " + fRoleRenamingEnd2.first.getText() + " doesn't exist in the association: " + fChildName.getText());
                }
            }

        }
        return mAssoclink;
    }

}
