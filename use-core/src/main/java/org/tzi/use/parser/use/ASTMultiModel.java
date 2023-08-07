package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.AST;
import org.tzi.use.parser.Context;
import org.tzi.use.uml.mm.MMultiModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASTMultiModel extends AST {

    private final Token fName;
    private final List<ASTModel> fModels;

    private final List<ASTAssociation> fInterAssoc;
    // assoc class?
    private final List<ASTConstraintDefinition> fInterConstraints;

    public ASTMultiModel(Token name) {
        fName = name;
        fModels = new ArrayList<>();
        fInterAssoc = new ArrayList<>();
        fInterConstraints = new ArrayList<>();
    }

    public void addModel(ASTModel model) {
        fModels.add(model);
    }

    public MMultiModel gen(Context ctx) {
        MMultiModel mMultiModel = ctx.modelFactory().createMultiModel(fName.getText());
        mMultiModel.setFilename(ctx.filename());
        ctx.setMultiModel(mMultiModel);

        boolean firstIteration = true;

        Iterator<ASTModel> mIt = fModels.iterator();
        while(mIt.hasNext()) {
            ASTModel model = mIt.next();
            try{
                if(firstIteration) {
                    firstIteration = false;
                    mMultiModel.addModel(model.gen(ctx));
                } else{
                    Context curContext = new Context(ctx.filename(), ctx.getOut(), null, ctx.modelFactory());
                    curContext.setMultiModel(mMultiModel);
                    mMultiModel.addModel(model.gen(curContext));
                    ctx.setErrorCount(ctx.errorCount() + curContext.errorCount());
                }
            }catch(Exception e) { //TODO: add custom excepetions
                ctx.reportError(fName,e);
                mIt.remove();
            }
        }


        return mMultiModel;
    }

    public String toString() {
        return "(" + fName + ")";
    }



}