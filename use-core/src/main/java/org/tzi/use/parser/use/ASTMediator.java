package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MClabjectInstance;
import org.tzi.use.uml.mm.MRestrictionClass;
import org.tzi.use.uml.mm.MMediator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTMediator extends ASTAnnotatable{

    private final Token fName;
    private final List<ASTMediatorElement> fMediatorElements;

    public ASTMediator(Token fName) {
        this.fName = fName;
        fMediatorElements = new ArrayList<>();
    }

    public void addMediatorElement(ASTMediatorElement astMediatorElement){
        fMediatorElements.add(astMediatorElement);
    }

    public String getName(){
        return fName.getText();
    }

    public MMediator gen(MLMContext mlmContext) throws Exception {
        MMediator mMediator = mlmContext.modelFactory().createMediator(fName.getText());
        Iterator<ASTMediatorElement> meIt = fMediatorElements.iterator();
        while(meIt.hasNext()) {
            ASTMediatorElement currentElement = meIt.next();
            if(currentElement instanceof ASTClabjectInstance) {
                MClabjectInstance clabjectInstance = ((ASTClabjectInstance) currentElement).gen(mlmContext);
                mMediator.addClabjectInstance(clabjectInstance);
            }

        }
        return mMediator;
    }
}
