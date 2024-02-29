package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.uml.mm.MLevel;
import org.tzi.use.uml.mm.MMediator;
import org.tzi.use.uml.mm.MModel;

public class ASTLevel extends ASTAnnotatable {

    private final Token fName;
    private final Token fParentName;

    private ASTModel fModel;
    private ASTMediator fMediator;


    public ASTLevel(Token fName, Token fParentName) {
        this.fName = fName;
        this.fParentName = fParentName;
    }

    public void setModel(ASTModel model) {
        this.fModel = model;
    }

    public void setMediator(ASTMediator mediator) {
        this.fMediator = mediator;
    }

//    public MLevel gen(MLMContext mlmContext) {
//        MLevel mLevel = mlmContext.modelFactory().createLevel(fName.getText(),fParentName.getText());
//        mlmContext.setCurrentLevel(mLevel);
//        try{
//            MModel mModel = fModel.gen(mlmContext);
//            mLevel.setModel(mModel);
//        } catch(Exception e) {
//            mlmContext.reportError(fName,e);
//        }
//
//        try{
//            MMediator mMediator = fMediator.gen(mlmContext);
//            mLevel.setMediator(mMediator);
//        } catch(Exception e) {
//            mlmContext.reportError(fName,e);
//        }
//        return mLevel;
//    }

}
