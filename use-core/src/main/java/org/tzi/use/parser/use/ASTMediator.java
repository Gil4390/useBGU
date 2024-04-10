package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;

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

            // check child association connects two classes that are defined as clabjects of the parent association classes
            MClass childClass1 = mlmContext.getCurrentModel().getClass(assoclink.child().associationEnds().get(0).cls().name());
            MClass childClass2 = mlmContext.getCurrentModel().getClass(assoclink.child().associationEnds().get(1).cls().name());
            MClass parentClass1 = mlmContext.getParentModel().getClass(assoclink.parent().associationEnds().get(0).cls().name());
            MClass parentClass2 = mlmContext.getParentModel().getClass(assoclink.parent().associationEnds().get(1).cls().name());

            if (childClass1 == null || childClass2 == null || parentClass1 == null || parentClass2 == null) {
                throw new Exception("Child class or parent class is not defined in the model");
            }
            MClabject clabject1 = mMediator.getClabject(childClass1.name());
            MClabject clabject2 = mMediator.getClabject(childClass2.name());
            if(clabject1 == null || clabject2 == null) {
                throw new Exception("Child class: "+childClass1.name()+ " or "+childClass2.name()+ " is not defined as a clabject in the mediator: "+mMediator.name());
            }

            if (!clabject1.parent().equals(parentClass1) || !clabject2.parent().equals(parentClass2)) {
                throw new Exception("Child class: "+childClass1.name()+ " or "+childClass2.name()+ " is not instantiating the appropriate class int the mediator: "+mMediator.name());
            }


            mMediator.addAssocLink(assoclink);
        }
        return mMediator;
    }
}
