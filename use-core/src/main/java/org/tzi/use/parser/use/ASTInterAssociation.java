package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.mm.*;

import java.util.HashMap;

public class ASTInterAssociation extends ASTAssociation{

    //private String modelName;

    public ASTInterAssociation(Token kind, Token name) {
        super(kind, name);
    }

    public MAssociation gen(HashMap<String, Context> contextMap, MMultiModel multiModel) throws SemanticException
    {
        checkDerive();

        MInterAssociation assoc = ((Context)contextMap.values().toArray()[0]).modelFactory().createInterAssociation(fName.getText());
        this.genAnnotations(assoc);

        // sets the line position of the USE-Model in this association
        assoc.setPositionInModel( fName.getLine() );
        String kindname = fKind.getText();
        int kind = MAggregationKind.NONE;

        if (kindname.equals("aggregation") )
            kind = MAggregationKind.AGGREGATION;
        else if (kindname.equals("composition") )
            kind = MAggregationKind.COMPOSITION;

        try {
            for (ASTAssociationEnd ae : fAssociationEnds) {
                if (!ae.getQualifiers().isEmpty() && this.fAssociationEnds.size() > 2) {
                    throw new SemanticException(fName,
                            "Error in " + MAggregationKind.name(assoc.aggregationKind()) + " `" +
                                    assoc.name() + "': Only binary associations can be qualified.");
                }
                // kind of association determines kind of first
                // association end
                String modelName = ae.getClassName().split("_")[0];
                Context ctx = contextMap.get(modelName);
                MAssociationEnd aend = ae.gen(ctx, kind);
                assoc.addAssociationEnd(aend);

                // further ends are plain ends
                kind = MAggregationKind.NONE;

                if (aend.isUnion())
                    assoc.setUnion(true);
            }
            multiModel.addInterAssociation(assoc);
        } catch (MInvalidModelException ex) {
            throw new SemanticException(fName,
                    "In " + MAggregationKind.name(assoc.aggregationKind()) + " `" +
                            assoc.name() + "': " +
                            ex.getMessage());
        }
        return assoc;
    }
}
