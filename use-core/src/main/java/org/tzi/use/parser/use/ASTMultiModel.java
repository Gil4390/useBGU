package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.AST;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MMultiModel;

import java.util.*;

public class ASTMultiModel extends AST {

    private final Token fName;
    private final List<ASTModel> fModels;

    private final List<ASTInterAssociation> fInterAssoc;
    // assoc class?
    private final List<ASTInterConstraintDefinition> fInterConstraints;

    private final List<ASTPrePost> fInterPrePosts;

    private HashMap<String, Context> contextMap;


    public ASTMultiModel(Token name) {
        fName = name;
        fModels = new ArrayList<>();
        fInterAssoc = new ArrayList<>();
        fInterConstraints = new ArrayList<>();
        fInterPrePosts = new ArrayList<>();
        contextMap = new HashMap<>();
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
                    contextMap.put(model.name(), ctx);
                    mMultiModel.addModel(model.gen(ctx));
                } else{
                    Context curContext = new Context(ctx.filename(), ctx.getOut(), null, ctx.modelFactory());
                    contextMap.put(model.name(), curContext);
                    curContext.setMultiModel(mMultiModel);
                    mMultiModel.addModel(model.gen(curContext));
                    ctx.setErrorCount(ctx.errorCount() + curContext.errorCount());
                }
            }catch(Exception e) { //TODO: add custom excepetions
                ctx.reportError(fName,e);
                mIt.remove();
            }
        }

        for(ASTInterAssociation assoc : fInterAssoc) {
            try {
                assoc.gen(contextMap, mMultiModel);
            } catch (SemanticException e) {
                throw new RuntimeException(e);
            }
        }

        for(ASTInterConstraintDefinition inv : fInterConstraints) {


            Collection<MClassInvariant> invariants = inv.gen(contextMap);
            for(MClassInvariant invariant : invariants) {
                mMultiModel.addInterConstraint(invariant);
            }
        }


        return mMultiModel;
    }

    public void addInterAssociation(ASTInterAssociation assoc) {
        fInterAssoc.add(assoc);
    }

    public void addConstraint(ASTInterConstraintDefinition cons) {
        fInterConstraints.add(cons);
    }

    public void addPrePost(ASTPrePost ppc) {
        fInterPrePosts.add(ppc);
    }

    public String toString() {
        return "(" + fName + ")";
    }



}