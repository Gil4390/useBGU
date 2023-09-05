package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.mm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ASTInterAssociation extends ASTAssociation{

    protected List<ASTInterAssociationEnd> fInterAssociationEnds;


    public ASTInterAssociation(Token kind, Token name) {
        super(kind, name);
        fInterAssociationEnds = new ArrayList<>();
    }

    public void addInterEnd(ASTInterAssociationEnd iae) {
        fInterAssociationEnds.add(iae);
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
            List<MModel> modelsToAddAssociations = new ArrayList<>();
            for (ASTInterAssociationEnd ae : fInterAssociationEnds) {
                if (!ae.getQualifiers().isEmpty() && this.fInterAssociationEnds.size() > 2) {
                    throw new SemanticException(fName,
                            "Error in " + MAggregationKind.name(assoc.aggregationKind()) + " `" +
                                    assoc.name() + "': Only binary associations can be qualified.");
                }
                // kind of association determines kind of first
                // association end
                Context ctx = contextMap.get(ae.modelName());
                MAssociationEnd aend = ae.gen(ctx, kind);
                assoc.addAssociationEnd(aend);

                // further ends are plain ends
                kind = MAggregationKind.NONE;

                if (aend.isUnion())
                    assoc.setUnion(true);
                modelsToAddAssociations.add(ctx.model());
            }
            //modelsToAddAssociations.get(0).addAssociation(assoc);
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
