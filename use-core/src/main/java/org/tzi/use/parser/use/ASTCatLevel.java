package org.tzi.use.parser.use;

import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * One "model ID [&lt; PARENT_ID]" level from CatMLM source. Fuses what
 * MLMUse splits across a {@code model} block and a separate
 * {@code mediator} block; {@link USECompilerCatUSE} splits it back apart
 * when desugaring into plain {@link ASTModel}/{@link ASTMediator} objects.
 *
 * @author Claude
 */
public class ASTCatLevel {

    private final Token fName;
    private final Token fParentName; // null if this is the top level

    private final List<ASTClass> fClassifiers = new ArrayList<>();
    private final List<ASTAssociation> fAssociations = new ArrayList<>();
    private final List<ASTCatClabject> fClabjects = new ArrayList<>();
    private final List<ASTConstraintDefinition> fConstraints = new ArrayList<>();

    public ASTCatLevel(Token name, Token parentName) {
        fName = name;
        fParentName = parentName;
    }

    public void addClassifier(ASTClass c) { fClassifiers.add(c); }
    public void addAssociation(ASTAssociation a) { fAssociations.add(a); }
    public void addClabject(ASTCatClabject c) { fClabjects.add(c); }
    public void addConstraint(ASTConstraintDefinition c) { fConstraints.add(c); }

    public Token name() { return fName; }
    public Token parentName() { return fParentName; }
    public List<ASTClass> classifiers() { return fClassifiers; }
    public List<ASTAssociation> associations() { return fAssociations; }
    public List<ASTCatClabject> clabjects() { return fClabjects; }
    public List<ASTConstraintDefinition> constraints() { return fConstraints; }
}
