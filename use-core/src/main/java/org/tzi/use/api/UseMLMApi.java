package org.tzi.use.api;

import org.tzi.use.uml.mm.*;

public class UseMLMApi extends UseMultiModelApi{

    private MMultiLevelModel mMultiLevelModel;
    MultiLevelModelFactory mFactory = new MultiLevelModelFactory();

    /**
     * Creates a new UseMultiModelApi instance with an empty multi-model named "unnamed".
     * The new multi-model instance can be retrieved by {@link #getMultiModel()}.
     */
    public UseMLMApi() {
        mMultiLevelModel = mFactory.createMLM("unnamed");
    }

    /**
     * Creates a new UseMultiModelApi instance with an empty multi-model named <code>name</code>.
     * The new multi-model instance can be retrieved by {@link #getMultiModel()}.
     * @param name The name of the new model.
     */
    public UseMLMApi(String name) {
        mMultiLevelModel = mFactory.createMLM(name);
    }

    /**
     * Creates a new UseMultiModelApi instance with
     * the provided <code>multiModel</code> as the multi-model instance.
     * This is useful if you want to modify an existing multi-model instance.
     * @param mlm The multi-model to modify through this API instance.
     */
    public UseMLMApi(MMultiLevelModel mlm) {
        mMultiLevelModel = mlm;
    }

    /**
     * Returns the multi-model modified through this API instance.
     * @return the multi-model handled by this API instance.
     */
    public MMultiLevelModel getMultiLevelModel() {
        return mMultiLevelModel;
    }

    public MMediator createMediator(String name) throws Exception {
        //TODO set current and parent models
        MMediator mediator = mFactory.createMediator(name);
        this.mMultiLevelModel.addMediator(mediator);
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

        MClass parent = mediator.getParentModel().getClass(childName);
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

        MAttribute attr = clabject.child().attribute(oldAttrName, false);

        MAttributeRenaming attributeRenaming = mFactory.createAttributeRenaming(attr, newAttrName);
        clabject.addAttributeRenaming(attributeRenaming);
        return attributeRenaming;
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

        MAssociation parent  = mediator.getParentModel().getAssociation(childName);
        if (parent == null) {
            throw new NullPointerException("Parent " + parentName + " is invalid");
        }

        MAssoclink assoclink = mFactory.createAssoclink(child, parent);
        mediator.addAssocLink(assoclink);
        return assoclink;
    }

    public MRoleRenaming createRoleRenaming(String parentName, String childName){
        return null;
    }



}
