package org.tzi.use.parser.ocl;

import org.antlr.runtime.Token;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.ocl.type.Type;

public class ASTMultiType extends ASTSimpleType{

    private Token fModelName;

    public ASTMultiType(Token modelName, Token className) {
        super(className);
        fModelName = modelName;
    }

    @Override
    public Type gen(Context ctx) throws SemanticException {
        String modelName = fModelName.getText();
        String className = super.toString();

        Type res = ctx.model().getClassifier(modelName + "@" + className);

        if (res == null )
            throw new SemanticException(fModelName,
                    "Expected type name, found `" + modelName + "@" + className + "'.");

        return res;
    }

    public String toString() {
        return fModelName.getText() + "@" + super.toString();
    }
}