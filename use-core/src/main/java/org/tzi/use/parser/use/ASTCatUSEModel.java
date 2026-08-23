package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse-tree root for the CatMLM surface syntax. Holds the raw,
 * not-yet-desugared levels; {@link USECompilerCatUSE} turns this into a
 * plain {@link ASTMultiLevelModel} before handing off to the existing
 * MLMUse code generation pipeline.
 *
 * @author Claude
 */
public class ASTCatUSEModel {

    private final Token fName;
    private final List<ASTCatLevel> fLevels;

    public ASTCatUSEModel(Token name) {
        fName = name;
        fLevels = new ArrayList<>();
    }

    public void addLevel(ASTCatLevel level) {
        fLevels.add(level);
    }

    public Token name() {
        return fName;
    }

    public List<ASTCatLevel> levels() {
        return fLevels;
    }
}
