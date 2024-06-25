package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ASTAssoclink extends ASTAnnotatable{
    private final Token fChildName;
    private final Token fParentName;
    private Pair<Token> fRoleBindingEnd1;
    private Pair<Token> fRoleBindingEnd2;
    public ASTAssoclink(Token fChildName, Token fParentName) {
        this.fChildName = fChildName;
        this.fParentName = fParentName;
    }

    public void addRoleBinding1(Token oldName, Token newName){
        fRoleBindingEnd1 = new Pair<>();
        fRoleBindingEnd1.first = oldName;
        fRoleBindingEnd1.second = newName;
    }

    public void addRoleBinding2(Token oldName, Token newName){
        fRoleBindingEnd2 = new Pair<>();
        fRoleBindingEnd2.first = oldName;
        fRoleBindingEnd2.second = newName;
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

        String childRoleNameEnd1 = fRoleBindingEnd1.first.getText();
        String parentRoleNameEnd1 = fRoleBindingEnd1.second.getText();

        String childRoleNameEnd2 = fRoleBindingEnd2.first.getText();
        String parentRoleNameEnd2 = fRoleBindingEnd2.second.getText();


        List<MAssociationEnd> allEnds = mlmContext.getParentModel().getAssociation(fParentName.getText()).associationEnds();
        allEnds.addAll(mlmContext.getCurrentModel().getAssociation(fChildName.getText()).associationEnds());

        MAssociationEnd mEndC1 = allEnds.stream().filter(e -> e.nameAsRolename().equals(childRoleNameEnd1)).findFirst().orElseThrow();
        MAssociationEnd mEndP1 = allEnds.stream().filter(e -> e.nameAsRolename().equals(parentRoleNameEnd1)).findFirst().orElseThrow();
        MAssociationEnd mEndC2 = allEnds.stream().filter(e -> e.nameAsRolename().equals(childRoleNameEnd2)).findFirst().orElseThrow();
        MAssociationEnd mEndP2 = allEnds.stream().filter(e -> e.nameAsRolename().equals(parentRoleNameEnd2)).findFirst().orElseThrow();

        mAssoclink.addRoleBinding(mlmContext.modelFactory().createRoleBinding(mEndC1, mEndP1));
        mAssoclink.addRoleBinding(mlmContext.modelFactory().createRoleBinding(mEndC2, mEndP2));

        return mAssoclink;
    }

}
