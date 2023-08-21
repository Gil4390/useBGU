package org.tzi.use.parser.ocl;

import org.antlr.runtime.Token;
import org.tzi.use.parser.Context;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeFactory;

public class ASTMultiType extends ASTType{

    private Token fModelName;
    private Token fClassName;

    public ASTMultiType(Token modelName, Token className) {
        fModelName = modelName;
        fClassName = className;
    }

    @Override
    public Type gen(Context ctx) throws SemanticException {
        String modelName = fModelName.getText();
        String className = fClassName.getText();

        // check for object type
        Type res = ctx.multiModel().getModel(modelName).getClassifier(className);
        if (res == null )
            throw new SemanticException(fModelName,
                    "Expected type name, found `" + modelName + "_" + className + "'.");

        return res;
    }

    public String toString() {
        return fModelName.getText() + "_" + fClassName.getText();
    }
}
