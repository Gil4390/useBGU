package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.ocl.ASTVariableDeclaration;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.ocl.expr.VarDecl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Node of the abstract syntax tree constructed by the parser.
 *
 * @author Gil Khais
 * @author Amiel Saad
 */

public class ASTInterAssociationEnd extends ASTAssociationEnd{

    private Token fModelName;

    public ASTInterAssociationEnd(Token modelName, Token name, ASTMultiplicity mult) {
        super(name, mult);
        fModelName = modelName;
    }

    /**
     * The name of the related model
     * @return
     */
    public String modelName() {
        return fModelName.getText();
    }

    /**
     * Same functionality as parent class, only distinguish between an
     * inter-class and regular-class.
     * @param ctx
     * @param kind
     * @return
     * @throws SemanticException
     */
    @Override
    public MAssociationEnd gen(Context ctx, int kind) throws SemanticException {
        if(fModelName == null) {
            //inter class
            return super.gen(ctx,kind);
        }
        // lookup class at association end in current model
        MClass cls = ctx.model().getClass( modelName() + "@" +  getClassName());


        if (cls == null )
            // this also renders the rest of the association useless
            throw new SemanticException(fName, "Class `" + modelName() + "@" +  getClassName() +
                    "' does not exist in this multi models.");

        MMultiplicity mult = fMultiplicity.gen(ctx);
        if (fOrdered && ! mult.isCollection() ) {
            ctx.reportWarning(fName, "Specifying `ordered' for " +
                    "an association end targeting single objects has no effect.");
            fOrdered = false;
        }

        List<VarDecl> generatedQualifiers;
        if (qualifiers.size() == 0) {
            generatedQualifiers = Collections.emptyList();
        } else {
            generatedQualifiers = new ArrayList<VarDecl>(qualifiers.size());

            for (ASTVariableDeclaration var : qualifiers ) {
                generatedQualifiers.add(var.gen(ctx));
            }
        }

        mAend = ctx.modelFactory().createAssociationEnd(cls, getRolename(ctx),
                mult, kind, fOrdered, generatedQualifiers);

        mAend.setUnion(this.isUnion);
        mAend.setDerived(this.derivedExpression != null);

        this.genAnnotations(mAend);

        return mAend;
    }
}
