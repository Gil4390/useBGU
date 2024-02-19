package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.uml.mm.MLevel;
import org.tzi.use.uml.mm.MMultiLevelModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTMultiLevelModel extends ASTAnnotatable{

    private final Token fName;
    private final List<ASTLevel> fLevels;
    public ASTMultiLevelModel(Token name) {
        fName = name;
        fLevels = new ArrayList<>();
    }

    public void addLevel(ASTLevel level) {
        fLevels.add(level);
    }

    public MMultiLevelModel gen(MLMContext mlmContext) {
        MMultiLevelModel mMultiLevelModel = mlmContext.modelFactory().createMLM(fName.getText());
//        mMultiModel.setFilename(multiCtx.filename());
        mlmContext.setMLModel(mMultiLevelModel);

        Iterator<ASTLevel> mlmIt = fLevels.iterator();
        MLevel prevLevel = null;
        while(mlmIt.hasNext()) {
            MLMContext ctx = new MLMContext(mlmContext.filename(), mlmContext.getOut(), null, mlmContext.modelFactory());
            ctx.setMainContext(mlmContext);
            ctx.setParentLevel(prevLevel);
            ASTLevel level = mlmIt.next();
            try{
                MLevel currentLevel = level.gen(ctx);
                mMultiLevelModel.addLevel(currentLevel);
                if (mlmContext.errorCount() > 0){
                    return null;
                }
                prevLevel = currentLevel;
            }
            catch(Exception e) {
                mlmContext.reportError(fName,e);
                mlmIt.remove();
            }
        }
        return mMultiLevelModel;
    }

}
