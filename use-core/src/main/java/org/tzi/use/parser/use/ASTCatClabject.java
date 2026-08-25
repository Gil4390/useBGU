package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * "clabject NAME [&lt; LOCAL_PARENT ;] : POWERCLASS (, POWERCLASS)* end"
 * -- the CatMLM clabject shorthand. Desugars, in
 * {@link USECompilerCatUSE}, into one plain {@link ASTClabject} per
 * listed powerclass (the same expansion MLMUse itself requires when one
 * class instantiates more than one powerclass), plus, if present, a
 * same-level superclass edge on the generated {@link ASTClass}.
 *
 * @author Claude
 */
public class ASTCatClabject {

    private final Token fName;
    private final Token fLocalParent; // optional same-level "E < D"; null if absent
    private final List<Token> fPowerclasses = new ArrayList<>();

    public ASTCatClabject(Token name, Token localParent, Token firstPowerclass) {
        fName = name;
        fLocalParent = localParent;
        fPowerclasses.add(firstPowerclass);
    }

    public void addPowerclass(Token powerclass) {
        fPowerclasses.add(powerclass);
    }

    public Token name() { return fName; }
    public Token localParent() { return fLocalParent; }
    public List<Token> powerclasses() { return fPowerclasses; }
}
