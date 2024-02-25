package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.uml.mm.MRestrictionAssociation;
import org.tzi.use.util.Pair;

import java.util.Objects;

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

    public MRestrictionAssociation gen(MLMContext mlmContext) throws Exception {
        MClassifier child = mlmContext.level().model().getAssociation(this.fChildName.getText());
        if(child == null) {
            throw new Exception("Association: "+fChildName.getText() + ", in the level: "+mlmContext.level().name()+", doesn't exist.");
        }
        MClassifier parent = mlmContext.parentLevel().model().getAssociation(this.fParentName.getText());
        if(parent == null) {
            throw new Exception("Association: "+fChildName.getText() + ", in the level: "+mlmContext.parentLevel().name()+", doesn't exist.");
        }
        MRestrictionAssociation mRestrictionAssociation = mlmContext.modelFactory().createAssociationInstance(child,parent);

        MAssociationEnd mEnd1 = mlmContext.level().model().getAssociation(fParentName.getText()).associationEnds().get(0);
        if(fRoleRenamingEnd1.first != null) {
            if(Objects.equals(mEnd1.nameAsRolename(), fRoleRenamingEnd1.first.getText())) {
                mRestrictionAssociation.addRoleMapping1(fRoleRenamingEnd1.first.getText(),fRoleRenamingEnd1.second.getText());
            }
        } else {
            mRestrictionAssociation.addRoleMapping1(mEnd1.nameAsRolename(),mEnd1.nameAsRolename());
        }

        MAssociationEnd mEnd2 = mlmContext.level().model().getAssociation(fParentName.getText()).associationEnds().get(1);
        if(fRoleRenamingEnd2.first != null) {
            if(Objects.equals(mEnd2.nameAsRolename(), fRoleRenamingEnd2.first.getText())) {
                mRestrictionAssociation.addRoleMapping2(fRoleRenamingEnd2.first.getText(),fRoleRenamingEnd2.second.getText());
            }
        } else {
            mRestrictionAssociation.addRoleMapping2(mEnd2.nameAsRolename(),mEnd2.nameAsRolename());
        }
        return mRestrictionAssociation;
    }

}
