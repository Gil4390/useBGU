package org.tzi.use.api;

import org.tzi.use.uml.mm.*;
import org.tzi.use.util.StringUtil;

import java.util.List;

public class UseMLMApi extends UseMultiModelApi{

    private MMultiLevelModel mMultiLevelModel;
    MultiLevelModelFactory mFactory = new MultiLevelModelFactory();

    /**
     * Creates a new UseMLMApi instance with an empty multi-level-model named "unnamed".
     * The new multi-level-model instance can be retrieved by {@link #getMultiLevelModel()}.
     */
    public UseMLMApi() {
        mMultiLevelModel = mFactory.createMLM("unnamed");
    }

    public UseMLMApi(MMultiModel multiModel) {
        mMultiLevelModel = mFactory.createMLM(multiModel);
    }

    /**
     * Creates a new UseMLMApi instance with an empty multi-level-model named <code>name</code>.
     * The new multi-level-model instance can be retrieved by {@link #getMultiLevelModel()}.
     * @param name The name of the new model.
     */
    public UseMLMApi(String name) {
        mMultiLevelModel = mFactory.createMLM(name);
    }

    /**
     * Creates a new UseMLMApi instance with
     * the provided <code>multiLevelModel</code> as the multi-level-model instance.
     * This is useful if you want to modify an existing multi-level-model instance.
     * @param mlm The multi-level-model to modify through this API instance.
     */
    public UseMLMApi(MMultiLevelModel mlm) {
        mMultiLevelModel = mlm;
    }

    /**
     * Returns the multi-level-model modified through this API instance.
     * @return the multi-level-model handled by this API instance.
     */
    public MMultiLevelModel getMultiLevelModel() {
        return mMultiLevelModel;
    }



    @Override
    public MClass getClassSafe(String name) throws UseApiException {
        if (!name.contains("@")){
            //inter-class
            MClass cls = mMultiLevelModel.getClass(name);
            if (cls == null) {
                throw new UseApiException("Unknown class " + StringUtil.inQuotes(name));
            }
            return cls;
        }

        //regular class
        String modelName = name.split("@")[0];
        String className = name.split("@")[1];

        MModel model = mMultiLevelModel.getModel(modelName);
        if (model == null){
            throw new UseApiException("Unknown model " + StringUtil.inQuotes(modelName));
        }

        MClass cls = model.getClass(name);
        if (cls == null) {
            throw new UseApiException("Unknown class " + StringUtil.inQuotes(name) +
                    " in model " + StringUtil.inQuotes(modelName));
        }
        return cls;
    }





    /**
     * This method is used to create a new Mediator object and add it to the multi-level model.
     *
     * @param mediatorName The name of the new mediator to be created.
     * @param relatedModel The name of the related model based on which the current model and its parent model are retrieved.
     * @return The newly created Mediator object.
     * @throws Exception If the current model does not exist.
     */
    public MMediator createMediator(String mediatorName, String relatedModel) throws Exception {
        MModel currentModel = mMultiLevelModel.getModel(relatedModel);
        if (currentModel == null) {
            throw new Exception("Model " + relatedModel + " is invalid");
        }
        MModel parentModel = mMultiLevelModel.getParentModel(relatedModel);

        MMediator mediator = mFactory.createMediator(mediatorName);
        this.mMultiLevelModel.addMediator(mediator);
        mediator.setCurrentModel(currentModel);
        mediator.setParentModel(parentModel);
        return mediator;
    }
    public void removeMediator(String name){
        this.mMultiLevelModel.removeMediator(name);
    }

    public MMediator getMediator(String name){
        return this.mMultiLevelModel.getMediator(name);
    }

    public MClabject createClabject(String mediatorName, String childName, String parentName){
        MMediator mediator = this.getMediator(mediatorName);
        if (mediator == null) {
            throw new NullPointerException("Mediator " + mediatorName + " is invalid");
        }

        MClass child = mediator.getCurrentModel().getClass(childName);
        if (child == null) {
            throw new NullPointerException("Child " + childName + " is invalid");
        }

        MClass parent = mediator.getParentModel().getClass(parentName);
        if (parent == null) {
            throw new NullPointerException("Parent " + parentName + " is invalid");
        }


        MClabject clabject = mFactory.createClabject(child, parent);
        mediator.addClabject(clabject);
        return clabject;
    }

    public MAttributeRenaming createAttributeRenaming(String mediatorName, String clabjectName, String oldAttrName, String newAttrName){
        MMediator mediator = this.getMediator(mediatorName);
        MClabject clabject = mediator.getClabject(clabjectName);
        //TODO should we search in subclasses?

        MAttributeRenaming attributeRenaming = clabject.addAttributeRenamingApi(oldAttrName, newAttrName);
        return attributeRenaming;
    }

    public void removeAttribute(String mediatorName, String clabjectName, String oldAttrName){
        MMediator mediator = this.getMediator(mediatorName);
        MClabject clabject = mediator.getClabject(clabjectName);

        MAttribute attr = clabject.child().attribute(oldAttrName, false);

        clabject.addRemovedAttribute(attr);
    }

    public MAssoclink createAssoclink(String mediatorName, String childName, String parentName){
        MMediator mediator = this.getMediator(mediatorName);
        if (mediator == null) {
            throw new NullPointerException("Mediator " + mediatorName + " is invalid");
        }

        MAssociation child  = mediator.getCurrentModel().getAssociation(childName);
        if (child == null) {
            throw new NullPointerException("Child " + childName + " is invalid");
        }

        MAssociation parent  = mediator.getParentModel().getAssociation(parentName);
        if (parent == null) {
            throw new NullPointerException("Parent " + parentName + " is invalid");
        }

        MAssoclink assoclink = mFactory.createAssoclink(child, parent);
        mediator.addAssocLink(assoclink);
        return assoclink;
    }

//    public MRoleRenaming createRoleRenaming(String mediatorName, String assoclinkName, String oldRoleName, String newRoleName){
//        MMediator mediator = this.getMediator(mediatorName);
//        MAssoclink assoclink = mediator.getAssoclink(assoclinkName);
//
//
//        List<MAssociationEnd> ends = ((MAssociation)assoclink.child()).associationEnds();
//
//        MAssociationEnd end = ends.get(0);
//        if (end.name().equals(oldRoleName)) {
//            end = ends.get(1);
//        }
//
//        MRoleRenaming roleRenaming = mFactory.createRoleRenaming(end, newRoleName);
//        assoclink.addRoleRenaming(roleRenaming);
//        return roleRenaming;
//    }



}
