package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.AST;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MMultiModel;

import java.util.*;

public class ASTMultiModel extends AST {

    private final Token fName;
    private final List<ASTModel> fModels;

    private final List<ASTAssociation> fInterAssoc;

    private final List<ASTConstraintDefinition> fInterConstraints;

    private final List<ASTPrePost> fInterPrePosts;


    public ASTMultiModel(Token name) {
        fName = name;
        fModels = new ArrayList<>();
        fInterAssoc = new ArrayList<>();
        fInterConstraints = new ArrayList<>();
        fInterPrePosts = new ArrayList<>();
    }

    public void addModel(ASTModel model) {
        fModels.add(model);
    }

    public MMultiModel gen(MultiContext multiCtx) {
        MMultiModel mMultiModel = multiCtx.modelFactory().createMultiModel(fName.getText());
        mMultiModel.setFilename(multiCtx.filename());
        multiCtx.setModel(mMultiModel);

        Iterator<ASTModel> mIt = fModels.iterator();
        while(mIt.hasNext()) {
            ASTModel model = mIt.next();
            Context ctx = new Context(multiCtx.filename(), multiCtx.getOut(), null, multiCtx.modelFactory());
            multiCtx.setContext(model.toString(), ctx);
            try{
                mMultiModel.addModel(model.gen(ctx));
                if (ctx.errorCount() > 0){
                    return null;
                }
            }
            catch(Exception e) {
                multiCtx.reportError(fName,e);
                mIt.remove();
            }
        }

        for(ASTAssociation assoc : fInterAssoc) {
            try {
                assoc.gen(multiCtx, mMultiModel);
            } catch (SemanticException e) {
                throw new RuntimeException(e);
            }
        }

        for(ASTConstraintDefinition inv : fInterConstraints) {
            inv.gen(multiCtx);
        }

        return mMultiModel;
    }

    public void addInterAssociation(ASTAssociation assoc) {
        fInterAssoc.add(assoc);
    }

    public void addConstraint(ASTConstraintDefinition cons) {
        fInterConstraints.add(cons);
    }

    public void addPrePost(ASTPrePost ppc) {
        fInterPrePosts.add(ppc);
    }

    public String toString() {
        return "(" + fName + ")";
    }

}