package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.parser.AST;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.ocl.ASTType;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.ocl.type.Type;

import java.util.*;

public class ASTInterConstraintDefinition extends AST {
    private List<Token> fVarNames;   // optional

    private Token fModelName;
    private ASTType fType;
    private ArrayList<ASTInvariantClause> fInvariantClauses;
    public ASTInterConstraintDefinition() {
        fVarNames = new ArrayList<Token>();
        fInvariantClauses = new ArrayList<ASTInvariantClause>();
    }

    public void addInvariantClause(ASTInvariantClause inv) {
        fInvariantClauses.add(inv);
    }

    public void addVarName(Token tok) {
        fVarNames.add(tok);
    }

    public void setType(ASTType t) {
        fType = t;
    }

    public void setModelName(Token modelName) {
        fModelName = modelName;
    }

/*    public void gen(Context ctx) {
        gen(ctx, true);
    }*/

    public Collection<MClassInvariant> gen(HashMap<String, Context> contextMap) {
        Collection<MClassInvariant> invs = new LinkedList<MClassInvariant>();
        Context ctx = contextMap.get(fModelName.getText());
        try {
            Type t = fType.gen(ctx);
            if (! t.isTypeOfClass() )
                throw new SemanticException(fType.getStartToken(),
                        "Expected an object type, found `" +
                                t + "'");
            MClass cls = (MClass)t;
            ctx.setCurrentClass(cls);

            MClassInvariant inv;
            for (ASTInvariantClause astInv : fInvariantClauses) {
                inv = astInv.gen(ctx, fVarNames, cls, true);
                if(inv != null){
                    invs.add(inv);
                }
            }

        } catch (SemanticException ex) {
            ctx.reportError(ex);
        } finally {
            ctx.setCurrentClass(null);
        }
        return invs;
    }
}
