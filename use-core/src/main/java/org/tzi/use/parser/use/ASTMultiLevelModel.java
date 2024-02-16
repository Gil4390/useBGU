package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class ASTMultiLevelModel extends ASTAnnotatable{

    private final Token fName;
    private final List<ASTLevel> fLevels;
    public ASTMultiLevelModel(Token name) {
        fName = name;
        fLevels = new ArrayList<>();
    }

    public void addLevel(ASTLevel level) {
        fLevels.add(level);
    }
}
