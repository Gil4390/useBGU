package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MAssoclink;
import org.tzi.use.uml.mm.MClabject;
import org.tzi.use.uml.mm.MMediator;

import java.util.ArrayList;
import java.util.List;

public class ASTMediator extends ASTAnnotatable{

    private final Token fName;
    private final List<ASTClabject> fClabjectInstances;
    private final List<ASTAssoclink> fAssociationInstances;

    public ASTMediator(Token fName) {
        this.fName = fName;
        fClabjectInstances = new ArrayList<>();
        fAssociationInstances = new ArrayList<>();
    }

    public void addClabject(ASTClabject astClabject){
        this.fClabjectInstances.add(astClabject);
    }

    public void addAssoclink(ASTAssoclink astAssocLink){
        this.fAssociationInstances.add(astAssocLink);
    }

    public String getName(){
        return fName.getText();
    }

    public MMediator gen(MLMContext mlmContext) throws Exception {
        MMediator mMediator = mlmContext.modelFactory().createMediator(fName.getText());
        for (ASTClabject astClabject : fClabjectInstances) {
            MClabject clabjectInstance = astClabject.gen(mlmContext);
            mMediator.addClabjectInstance(clabjectInstance);
        }
        for (ASTAssoclink astAssoclink : fAssociationInstances){
            MAssoclink assoclink = astAssoclink.gen(mlmContext);
            mMediator.addAssocLinkInstance(assoclink);
        }
        return mMediator;
    }
}
