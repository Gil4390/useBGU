package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.uml.mm.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTMultiLevelModel extends ASTMultiModel{

    //private final Token fName;
    private ASTMultiModel fMultiModel;
    private final List<ASTMediator> fMediators;
    public ASTMultiLevelModel(Token name) {
        super(name);
        fMediators = new ArrayList<>();
    }

    public void addMultiModel(ASTMultiModel multiModel){
        this.fMultiModel = multiModel;
    }

    public void addMediator(ASTMediator mediator){
        this.fMediators.add(mediator);
    }


    public MMultiLevelModel gen(MLMContext mlmContext) {
        MMultiLevelModel mMultiLevelModel = null;
        try{
            MultiContext multiCtx = new MultiContext(mlmContext.filename(), mlmContext.getOut(), null, mlmContext.modelFactory());
            MMultiModel multiModel = fMultiModel.gen(multiCtx);
            mMultiLevelModel = mlmContext.modelFactory().createMLM(multiModel);
            mMultiLevelModel.setFilename(mlmContext.filename());
        }
        catch (Exception e){
            mlmContext.reportError(fName,e);
        }



        Iterator<ASTMediator> medIt = fMediators.iterator();
        MModel prevModel = null;
        while(medIt.hasNext()) {
            ASTMediator mediator = medIt.next();

            MLMContext ctx = new MLMContext(mlmContext.filename(), mlmContext.getOut(), null, mlmContext.modelFactory());
            ctx.setMainContext(mlmContext);
            ctx.setParentModel(prevModel);

            MModel currentModel = mMultiLevelModel.getModel(mediator.getName());
            ctx.setCurrentModel(currentModel);

            try {
                MMediator mMediator = mediator.gen(ctx);
                mMultiLevelModel.addMediator(mMediator);
                if (ctx.errorCount() > 0){
                    return null;
                }

                prevModel = currentModel;
            }
            catch(Exception e) {
                mlmContext.reportError(fName,e);
            }
        }
        return mMultiLevelModel;
    }

}
