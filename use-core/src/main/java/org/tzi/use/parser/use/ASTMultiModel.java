package org.tzi.use.parser.use;

import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.ocl.ASTEnumTypeDefinition;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.type.EnumType;

import java.util.*;

/**
 * Node of the abstract syntax tree constructed by the parser.
 *
 * @author Gil Khais
 * @author Amiel Saad
 */


public class ASTMultiModel extends ASTModel {

    private final List<ASTModel> fModels;

    public ASTMultiModel(Token name) {
        super(name);
        fModels = new ArrayList<>();
    }

    public ASTMultiModel() {
        super(new ClassicToken(1,"MLM"));
        fModels = new ArrayList<>();
    }

    public void addModel(ASTModel model) {
        fModels.add(model);
    }

    /**
     * Generates all models related to the multi-model and inter-elements.
     * @param multiCtx
     * @return
     */
    public MMultiModel gen(MultiContext multiCtx) {
        MMultiModel mMultiModel = multiCtx.modelFactory().createMultiModel(fName.getText());
        mMultiModel.setFilename(multiCtx.filename());
        multiCtx.setMultiModel(mMultiModel);

        Iterator<ASTModel> mIt = fModels.iterator();
        while(mIt.hasNext()) {
            ASTModel model = mIt.next();
            MultiContext ctx = new MultiContext(multiCtx.filename(), multiCtx.getOut(), null, multiCtx.modelFactory());
            ctx.setParentContext(multiCtx);
            //multiCtx.setContext(model.toString(), ctx);

            multiCtx.modelFactory().setModelName(model.fName.getText() + "@");
            mMultiModel.setCurrentModel(model.fName.getText());
            try{
                mMultiModel.addModel(model.gen(ctx));
                if (multiCtx.errorCount() > 0){
                    return null;
                }
            }
            catch(Exception e) {
                multiCtx.reportError(fName,e);
                mIt.remove();
            }
        }

        multiCtx.modelFactory().setModelName("");
        mMultiModel.setCurrentModel("");

        for (ASTEnumTypeDefinition e : fEnumTypeDefs) {
            EnumType enm;
            try {
                enm = e.gen(multiCtx);
                mMultiModel.addEnumType(enm);
            } catch (SemanticException ex) {
                multiCtx.reportError(ex);
            } catch (MInvalidModelException ex) {
                multiCtx.reportError(fName, ex);
            }
        }

        for(ASTClass c : fClasses) {
            try {
                MClass cls = c.genEmptyClass(multiCtx);
                mMultiModel.addClass(cls);
            } catch (SemanticException e) {
                multiCtx.reportError(e);
            } catch (MInvalidModelException e) {
                throw new RuntimeException(e);
            }
        }

        for ( ASTAssociationClass ac : fAssociationClasses) {
            try {
                // The association class can just be added as a class so far,
                // because to keep the order of generating a model.
                // The association class will be added as an association in step 3b.
                MAssociationClass assocCls = ac.genEmptyAssocClass( multiCtx );
                mMultiModel.addClass( assocCls );
            } catch ( SemanticException ex ) {
                multiCtx.reportError( ex );
            } catch ( MInvalidModelException ex ) {
                multiCtx.reportError( fName, ex );
            }
        }

        for (ASTClass c : fClasses) {
            c.genAttributesOperationSignaturesAndGenSpec(multiCtx);
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            ac.genAttributesOperationSignaturesAndGenSpec( multiCtx );
        }

        for(ASTAssociation assoc : fAssociations) {
            try {
                assoc.gen(multiCtx, mMultiModel);
            } catch (SemanticException e) {
                multiCtx.reportError(e);
            }
        }

        for (ASTClass c : fClasses) {
            c.genStateMachinesAndStates(multiCtx);
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            ac.genStateMachinesAndStates(multiCtx);
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            try {
                MAssociationClass assocClass = ac.genAssociation( multiCtx );
                mMultiModel.addAssociation( assocClass );
            } catch ( SemanticException ex ) {
                multiCtx.reportError( ex );
            } catch ( MInvalidModelException ex ) {
                multiCtx.reportError( fName, ex );
            }
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            try {
                ac.genAssociationFinal( multiCtx );
            } catch ( MInvalidModelException ex ) {
                multiCtx.reportError( fName, ex );
            }
        }

        for (ASTAssociationClass a : fAssociationClasses) {
            try {
                a.genEndConstraints(multiCtx);
            } catch (SemanticException ex) {
                multiCtx.reportError(ex);
            }
        }

        for (ASTClass c : fClasses) {
            c.genOperationBodiesAndDerivedAttributes(multiCtx);
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            ac.genOperationBodiesAndDerivedAttributes(multiCtx);
        }

        for (ASTClass c : fClasses) {
            c.genConstraints(multiCtx);
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            ac.genConstraints(multiCtx);
        }

        for(ASTConstraintDefinition inv : fConstraints) {
            inv.gen(multiCtx);
        }

        for (ASTPrePost ppc : fPrePosts) {
            try {
                ppc.gen(multiCtx);
            } catch (SemanticException ex) {
                multiCtx.reportError(ex);
            }
        }

        for (ASTClass c : fClasses) {
            c.genStateMachineTransitions(multiCtx);
        }

        for (ASTAssociationClass ac : fAssociationClasses) {
            ac.genStateMachineTransitions(multiCtx);
        }

        return mMultiModel;
    }

    public void addInterClass(ASTClass cls) {
        fClasses.add(cls);
    }

    public void addInterAssociation(ASTAssociation assoc) {
        fAssociations.add(assoc);
    }

    public void addConstraint(ASTConstraintDefinition cons) {
        fConstraints.add(cons);
    }

    public void addPrePost(ASTPrePost ppc) {
        fPrePosts.add(ppc);
    }

    public String toString() {
        return "(" + fName + ")";
    }

}