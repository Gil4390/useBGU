package org.tzi.use.parser.use;

import org.antlr.runtime.Token;
import org.tzi.use.analysis.coverage.*;
import org.tzi.use.parser.MLMContext;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.uml.mm.*;

import java.util.*;
import java.util.stream.Collectors;

public class ASTMultiLevelModel extends ASTMultiModel{

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
                throw new Exception("error parsing multi level model");
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
            if (currentModel == null){
                mlmContext.reportError(fName,"Model " + mediator.getName() + " not found");
                return null;
            }
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
        Map<MModelElement, CoverageData> completeData = CoverageAnalyzer
                .calculateInvariantCoverage(mMultiLevelModel, true);

        for (MMediator med : mMultiLevelModel.mediators()) {
            for (MClabject clab : med.clabjects()) {
                for (MAttribute attr : clab.getRemovedAttributes()) {
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if(clab.child().isSubClassifierOf(inv.cls()) && completeData.get(inv).getAttributeCoverage().containsKey(attr)) {
                            if (clab.getRemovedConstraints().contains(inv)) {
                                continue;
                            }

                            // if the attribute is removed from the clabject, and is accessed by an invariant (local), but the class be navigate to it, then it shouldnt throw an error.
                            boolean isOtherEndRemoved = false;
                            for(MAssociation assoc : completeData.get(inv).getAssociationCoverage().keySet()) {
                                MAssociationEnd end = assoc.associationEnds().stream().filter(e -> !clab.child().isSubClassifierOf(e.cls())).findAny().get();
                                if(clab.getRemovedRoles().contains(end)) {
                                    isOtherEndRemoved = true;
                                }
                            }

                            if(isOtherEndRemoved) continue;

                            // if the attribute is removed from the clabject, but the attribute is inherited from other source (superclass etc.), then it shouldn't throw an error.
                            boolean isAttributeExists = false;
                            for(MAttribute currentAttr : clab.child().allAttributes()) {
                                if(currentAttr.name().equals(attr.name())) {
                                    isAttributeExists = true;
                                    break;
                                }
                            }

                            if(isAttributeExists) continue;

                            mlmContext.reportError(fName,
                                    "Attribute " + attr.name()
                                    + "\n\tthat is accessed by invariant " + inv.name()
                                    + "\n\ton " + clab.name() + " is missing");
                        }
                    }
                }

                for (MAttributeRenaming attributeRenaming : clab.getAttributeRenaming()) {
                    MAttribute attr = attributeRenaming.attribute();
                    for(MAttribute currentAttr : clab.child().allAttributes()) {
                        if(currentAttr.name().equals(attr.name()))
                            mlmContext.reportWarning(fName,
                                    "Attribute " + attr.name()
                                    + "\n\tis renamed to  " + attributeRenaming.newName() + ", "
                                    + "\n\tand the base attribute " + attr.name() + " is also inherited.");
                    }
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (completeData.get(inv).getAttributeCoverage().keySet().contains(attr)) {
                            // 1. if a user renames an attribute, and remove the invariant (local), then it shouldn't throw an error
                            // 2. if a user renames an attribute, and an inter-constraint related to it, then it shouldn't throw an error
                            if(clab.getRemovedConstraints().contains(inv) || mMultiLevelModel.interInvariants().contains(inv) )
                                continue;
                            mlmContext.reportError(fName,
                                    "Attribute " + attr.name()
                                    + "\n\tis renamed in clabject " + clab.name()
                                    + "\n\tbut is accessed by invariant " + inv.name());
                        }
                    }
                }

                for (MAssociationEnd end : clab.getOnlyClabjectRemovedRoles()) {
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (completeData.get(inv).getPropertyCoverage().keySet().contains(end)) {
                            if (clab.getRemovedConstraints().contains(inv)) {
                                continue;
                            }
                            //1. if the role is removed from a clabject that its power-type class isn't the 'context' class, it shouldn't throw an error.
                            if(!inv.cls().equals(clab.parent())) {
                                mlmContext.reportWarning(fName,
                                        "Role " + end.name()
                                                + "\n\tthat is accessed by a invariant " + inv.name()
                                                + "\n\ton clabject "+ clab.name() +" is missing");
                                continue;
                            }

                            //2. if the role is removed from the clabject, and is accessed by an invariant (local), it should throw an error.
                            mlmContext.reportError(fName,
                                    "Role " + end.name()
                                    + "\n\tis removed by clabject " + clab.name()
                                    + "\n\tbut is accessed by invariant " + inv.name());
                        }
                    }
                }

                for(MAssociationEnd end : clab.getOnlyAssoclinkRemovedRoles()) {
                    for (MClassInvariant inv : mMultiLevelModel.classInvariants()) {
                        if (completeData.get(inv).getPropertyCoverage().keySet().contains(end)) {
                            if (clab.getRemovedConstraints().contains(inv)) {
                                continue;
                            }

                            // if the role is removed from the clabject, and is accessed by an invariant (local), but the class cant be navigated to it, then it shouldn't throw an error.
                            if(clab.getRemovedRoles().contains(end)) {
                                continue;
                            }

                            MAssoclink assoclink = med.assoclinkOfClabject(clab.name());
                            mlmContext.reportError(fName,
                                    "Role " + end.name()
                                            + "\n\tremoved by assoclink: " + assoclink
                                            + "\n\tand by clabject: " + clab
                                            + "\n\tbut accessed by invariant " + inv.name());
                        }
                    }
                }

            }
        }
    }
}
