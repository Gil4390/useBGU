package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MAssoclink;
import org.tzi.use.uml.mm.MClabject;
import org.tzi.use.uml.mm.MMediator;
import org.tzi.use.uml.mm.MModel;

import java.util.ArrayList;
import java.util.List;

public class ASTMediator extends ASTAnnotatable{

    private final Token fName;
    private final Token fParentModelName;
    private final List<ASTClabject> fClabjects;
    private final List<ASTAssoclink> fAssoclinks;

    public ASTMediator(Token fName, Token fParentModelName) {
        this.fName = fName;
        this.fParentModelName = fParentModelName;
        fClabjects = new ArrayList<>();
        fAssoclinks = new ArrayList<>();
    }

    public void addClabject(ASTClabject astClabject){
        this.fClabjects.add(astClabject);
    }

    public void addAssoclink(ASTAssoclink astAssocLink){
        this.fAssoclinks.add(astAssocLink);
    }

    public String getName(){
        return fName.getText();
    }

    public MMediator gen(MLMContext mlmContext) throws Exception {
        MMediator mMediator = mlmContext.modelFactory().createMediator(fName.getText());
        MModel parentModel = mlmContext.getParentModel();
        mMediator.setCurrentModel(mlmContext.getCurrentModel());

        //TODO ask amiel if this is correct
        if(fParentModelName.getText().equals("NONE")) {
            return mMediator;
        }
        else if (!parentModel.name().equals(fParentModelName.getText())){
            throw new Exception("parent model name incorrect");
        }
        mMediator.setParentModel(parentModel);
        //
        for (ASTClabject astClabject : fClabjects) {
            MClabject clabjectInstance = astClabject.gen(mlmContext);
            mMediator.addClabject(clabjectInstance);
        }
        for (ASTAssoclink astAssoclink : fAssoclinks){
            MAssoclink assoclink = astAssoclink.gen(mlmContext);
            mMediator.addAssocLink(assoclink);
        }
        return mMediator;
    }
}
