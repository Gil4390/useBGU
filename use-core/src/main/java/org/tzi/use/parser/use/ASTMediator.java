package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.*;
import org.tzi.use.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        for (ASTClabject astClabject : fClabjects) {
            MClabject clabjectInstance = astClabject.gen(mlmContext);

            mMediator.addClabject(clabjectInstance);
            mlmContext.model().addGeneralization(clabjectInstance);
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
            MClabject clabject1 = mMediator.getClabject("GEN_" + childClass1.name() + "_" + parentClass1.name());
            MClabject clabject2 = mMediator.getClabject("GEN_" + childClass2.name() + "_" + parentClass2.name());
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
/*
    public void checkClabject(MLMContext mlmContext, MClabject clabject) throws Exception {


        Set<String> renamedAttributes = new HashSet<>();
        for(MAttribute attribute : parent.allAttributes()) {
            renamedAttributes.add(attribute.name());
        }



        Set<String> takenAttributes = new HashSet<>();
        for(MAttribute attribute : parent.allAttributes()) {
            takenAttributes.add(attribute.name());
        }

        //check if there is overlap between the attributes of the parent and the child
        for(MAttribute childAttribute : child.allAttributes()) {
            if(!takenAttributes.contains(childAttribute.name())){
                takenAttributes.add(childAttribute.name());
                continue;
            }

            //conflict, check if the attribute is removed or renamed
            boolean conflict = true;
            if (mClabject.getRemovedAttribute(childAttribute.name()) != null) {
                continue;
            }
            MAttributeRenaming attributeRenaming = mClabject.getRenamedAttribute(childAttribute.name());
            if (attributeRenaming != null) {
                String newName = attributeRenaming.newName();
                //check newName is not taken
                if (!takenAttributes.contains(newName)) {
                    takenAttributes.add(newName);
                    conflict = false;
                }
            }
            if (conflict) {
                throw new Exception("Attribute: " + childAttribute.name() + " is inherited from the parent class: " + parent.name() + " and is also present in the child class: " + child.name());
            }
        }

        return mClabject;
    }

 */
}
