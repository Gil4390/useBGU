package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.analysis.coverage.BasicCoverageData;
import org.tzi.use.analysis.coverage.BasicExpressionCoverageCalulator;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.uml.mm.*;

import java.util.*;

public class ASTMultiLevelModel extends ASTMultiModel{

    //private final Token fName;
    private ASTMultiModel fMultiModel;
    private final List<ASTMediator> fMediators;
    public ASTMultiLevelModel(Token name) {
        super(name);
        fMediators = new ArrayList<>();
    }

    public void addMultiModel(ASTMultiModel multiModel){
        this.fMultiModel = multiModel;
    }

    public void addMediator(ASTMediator mediator){
        this.fMediators.add(mediator);
    }


    public MMultiLevelModel gen(MLMContext mlmContext) {
        MMultiLevelModel mMultiLevelModel = null;
        try{
            MultiContext multiCtx = new MultiContext(mlmContext.filename(), mlmContext.getOut(), null, mlmContext.modelFactory());
            MMultiModel multiModel = fMultiModel.gen(multiCtx);
            if (multiModel == null){
                throw new Exception("error parsing multi model");
            }
            mMultiLevelModel = mlmContext.modelFactory().createMLM(multiModel);
            mMultiLevelModel.setFilename(mlmContext.filename());
        }
        catch (Exception e){
            mlmContext.reportError(fName,e);
            return null;
        }



        Iterator<ASTMediator> medIt = fMediators.iterator();
        MModel prevModel = null;
        while(medIt.hasNext()) {
            ASTMediator mediator = medIt.next();

            MLMContext ctx = new MLMContext(mlmContext.filename(), mlmContext.getOut(), null, mlmContext.modelFactory());
            ctx.setMainContext(mlmContext);
            ctx.setParentModel(prevModel);
            ctx.setModel(mMultiLevelModel);

            MModel currentModel = mMultiLevelModel.getModel(mediator.getName());
            ctx.setCurrentModel(currentModel);

            try {
                MMediator mMediator = mediator.gen(ctx);
                mMultiLevelModel.addMediator(mMediator);
                if (ctx.errorCount() > 0){
                    return null;
                }

                prevModel = currentModel;
            }
            catch(Exception e) {
                mlmContext.reportError(fName,e);
            }
        }

        mlmContext.setModel(mMultiLevelModel);
        fMultiModel.genInterConstraints(mlmContext);

        checkInvariantParsingWarning(mMultiLevelModel, mlmContext);

        return mMultiLevelModel;
    }

    public void checkInvariantParsingWarning(MMultiLevelModel mMultiLevelModel, MLMContext mlmContext) {
        Map<MClassInvariant, BasicCoverageData> invCoverage = new HashMap<MClassInvariant, BasicCoverageData>();
        BasicExpressionCoverageCalulator invCalc = new BasicExpressionCoverageCalulator(true);

        for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
            invCoverage.put(inv, invCalc.calcualteCoverage(inv.flaggedExpression()));
        }

        for (MMediator med : mMultiLevelModel.mediators()) {
            for (MClabject clab : med.clabjects()) {
                for (MAttribute attr : clab.getRemovedAttributes()) {
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (invCoverage.get(inv).getCoveredAttributes().contains(attr)) {
                            mlmContext.reportWarning(fName,
                                    "\n\tAttribute " + attr.name()
                                    + "\n\tis removed from clabject " + clab.name()
                                    + "\n\tbut is covered by invariant " + inv.name()
                                    + "\n\tthis may cause the check state to be invalid");
                        }
                    }
                }

                for (MAttributeRenaming attributeRenaming : clab.getAttributeRenaming()) {
                    MAttribute attr = attributeRenaming.attribute();
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (invCoverage.get(inv).getCoveredAttributes().contains(attr)) {
                            mlmContext.reportWarning(fName,
                                    "\n\tAttribute " + attr.name()
                                    + "\n\tis renamed in clabject " + clab.name()
                                    + "\n\tbut is covered by invariant " + inv.name()
                                    + "\n\tthis may cause the check state to be invalid");
                        }
                    }
                }

                for (MAssociationEnd end : clab.getOnlyClabjectRemovedRoles()) {
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (invCoverage.get(inv).getCoveredAssociations().contains(end.association())) {
                            mlmContext.reportWarning(fName,
                                    "\n\tRole " + end.name()
                                    + "\n\tis removed from clabject " + clab.name()
                                    + "\n\tbut is covered by invariant " + inv.name()
                                    + "\n\tthis may cause the check state to be invalid");
                        }
                    }
                }

                for (MAssociationEnd end : clab.getOnlyAssoclinkRemovedRoles()) {
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (invCoverage.get(inv).getCoveredAssociations().contains(end.association())) {
                            mlmContext.reportWarning(fName,
                                "\n\tRole " + end.name()
                                    + "\n\tis removed from an assoclink"
                                    + "\n\tbut is covered by invariant " + inv.name()
                                    + "\n\tthis may cause the check state to be invalid");
                        }
                    }
                }
            }
        }
    }
}
