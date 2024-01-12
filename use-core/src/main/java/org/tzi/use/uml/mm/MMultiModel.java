package org.tzi.use.uml.mm;

import org.eclipse.jdt.annotation.Nullable;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.mm.commonbehavior.communications.MSignal;
import org.tzi.use.uml.mm.commonbehavior.communications.MSignalImpl;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.util.collections.CollectionUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A MultiModel is a top-level package containing models.
 * <p>
 * holds inter-classes, inter-associations, inter-invariants, etc.
 */
public class MMultiModel extends MModel{

    private String currentModel;
    private Map<String, MModel> fModels; // <modelName, MModel>

    public MMultiModel(String name) {
        super(name);
        fModels = new TreeMap<>();
        currentModel = "";
    }


    /**
     * Adds a model. The model must have a unique name within the multiModel.
     *
     * @exception Exception
     *                multiModel already contains a model with the same name.
     */
    public void addModel(MModel model) throws Exception {
        if (fModels.containsKey(model.name()))
            throw new Exception("MultiModel already contains a model `"
                    + model.name() + "'.");
        fModels.put(model.name(), model);
    }

    /**
     * Removes a model with the given name. The model must exist within the multiModel.
     *
     * @exception Exception
     *                multiModel does not contain a model with the given name.
     */
    public void removeModel(String modelName) throws Exception {
        if (!fModels.containsKey(modelName))
            throw new Exception("MultiModel does not contain a model `" + modelName + "'.");

        fModels.remove(modelName);
    }


    public MAssociation getInterAssociations(String assocName) {
        return this.fAssociations.get(assocName);
    }

    public Collection<MClassInvariant> interConstraints() {
        return this.fClassInvariants.values();
    }

    /**
     * Returns the specified model by name.
     *
     * @return <code>null</code> if model <code>name</code> does not exist.
     */
    public MModel getModel(String name) {
        return fModels.get(name);
    }

    public void setCurrentModel(String currentModel) {
        this.currentModel = currentModel;
    }

    public String getCurrentModel() {
        return currentModel;
    }

    /**
     * Returns a collection containing all models in this multiModel.
     *
     * @return collection of MModel objects.
     */
    public Collection<MModel> models() {
        return fModels.values();
    }


    public int size(){return fModels.size();}

    /**
     * Calculates the total number of classes across all MModel objects.
     *
     * @return The total number of classes.
     */
    public int numOfClasses() {
        return classes().size();
    }

    /**
     * Calculates the total number of associations across all MModel objects.
     *
     * @return The total number of associations.
     */
    public int numOfAssociations() {
        return associations().size();
    }

    /**
     * Calculates the maximum number of classes among all MModel objects.
     *
     * @return The maximum number of classes, or -1 if no models are found.
     */
    public int maxNumOfClasses() {
        return fModels.values()
                .stream()
                .mapToInt(model -> model.classes().size())
                .max()
                .orElse(-1);
    }

    /**
     * Checks if there are duplicate class names across all MModel objects.
     *
     * @return true if duplicate class names are found, false otherwise.
     */
    public boolean containsDuplicateClassNames() {
        Set<String> allNames = new HashSet<>();
        for (MModel model : fModels.values()) {
            List<String> classNames = model.classes().stream()
                    .map(cls -> cls.name())
                    .collect(Collectors.toList());
            for (String cname : classNames) {
                if (!allNames.add(cname)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Calculates the number of intermediate associations that involve a specified class name.
     * @param clsName The name of the class for which intermediate associations are to be counted.
     * @return The count of intermediate associations that involve the specified class name.
     */
    public int numOfInterAssociations(String clsName) {
        long count = this.fAssociations.values().stream()
                .filter(assoc -> assoc.associationEnds().stream()
                        .anyMatch(assocEnd -> assocEnd.name().equals(clsName)))
                .count();

        return (int) count;
    }

    @Override
    public EnumType enumType(String name) {
        if(name.contains("@")) {
            //regular enum
            String modelName = name.split("@")[0];
            String enumName = name.split("@")[1];
            return fModels.get(modelName).enumType(enumName);
        } else {
            //inter-enum
            return super.enumType(name);
        }
    }

    /**
     * Returns the specified class by the specified model, if there is
     * no model name, it's an inter-class
     */
    @Override
    public MClass getClass(String name) {
        if (!currentModel.isEmpty()){
            return fModels.get(currentModel).getClass(currentModel + "@" + name);
        }

        if(name.contains("@")) {
            //regular class
            String modelName = name.split("@")[0];
            String clsName = name.split("@")[1];
            return fModels.get(modelName).getClass(name);
        } else {
            //inter-class
            return super.getClass(name);
        }
    }

    public MClass getClass(String modelName, String clsName){
        MModel model = this.getModel(modelName);
        return model.getClass(clsName);
    }

    /**
     * Returns the related association by model name
     * @param modelName related model name
     * @param assocName association name
     * @return
     */
    public MAssociation getAssociation(String modelName, String assocName){
        MModel model = this.getModel(modelName);
        return model.getAssociation(assocName);
    }

    /**
     * Returns the related invariant by model name
     * @param modelName related model name
     * @param invName invariant name
     * @return
     */
    public MClassInvariant getClassInvariant(String modelName, String invName){
        MModel model = this.getModel(modelName);
        return model.getClassInvariant(invName);
    }

    /**
     * Returns all classes in each model and all inter-classes
     * @return
     */
    @Override
    public Collection<MClass> classes() {
        Collection<MClass> classes = new ArrayList<>();
        //regular classes
        for (MModel model : fModels.values()){
            classes.addAll(model.classes());
        }
        //inter-classes
        classes.addAll(this.fClasses.values());
        return classes;
    }

    public Collection<MClass> interClasses() {
        return this.fClasses.values();
    }

    /**
     * Returns all associations in each model and all inter-associations
     * @return
     */
    @Override
    public Collection<MAssociation> associations() {
        Collection<MAssociation> associations = new ArrayList<>();
        //regular associations
        for (MModel model : fModels.values()){
            associations.addAll(model.associations());
        }
        //inter-associations
        associations.addAll(this.fAssociations.values());
        return associations;
    }

    public Collection<MAssociation> interAssociations() {
        return this.fAssociations.values();
    }

    /**
     * Returns all class invariants in each model and all inter-invariants
     * @return
     */
    @Override
    public Collection<MClassInvariant> classInvariants() {
        Collection<MClassInvariant> invariants = new ArrayList<>();
        for(MModel model : fModels.values()) {
            invariants.addAll(model.classInvariants());
        }
        //inter-constraints
        invariants.addAll(this.fClassInvariants.values());
        return invariants;
    }

    /**
     * Return class invariant by a given name and a model name, if no model
     * name specified, returns the inter-invariant.
     * @param name class invariant name
     * @return
     */

    @Override
    public MClassInvariant getClassInvariant(String name) {
        String invName = name.split("::")[1];

        if(invName.contains("@")) {
            //regular inv
            String modelName = invName.split("@")[0];
            String clsName = invName.split("@")[1];
            return fModels.get(modelName).getClassInvariant(name);
        }
        //inter inv
        return super.getClassInvariant(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof MMultiModel))
            return false;

        MMultiModel mm = (MMultiModel) obj;
        if (this.size() != mm.size())
            return false;
        if (this.numOfClasses() != mm.numOfClasses())
            return false;

        for (MModel model1 : this.models()){
            if (mm.getModel(model1.name()) == null)
                return false;

            for (MClass cls1 : model1.classes()){
                if (mm.getClass(model1.name(), cls1.name()) == null)
                    return false;
            }

            for (MAssociation assoc1 : model1.associations()){
                if (mm.getAssociation(model1.name(), assoc1.name()) == null)
                    return false;
            }

            for (MClassInvariant inv1 : model1.fClassInvariants.values()){
                if (mm.getClassInvariant(model1.name(), inv1.qualifiedName()) == null)
                    return false;
            }
        }

        return true;
    }

    public MModel toMModel() throws Exception {
        String delimiter= "@";
        MModel result_model = new MModel(name());

        for (MModel model : fModels.values()) {
            // regular classes
            for (MClass mClass : model.classes()) {
                if (mClass instanceof MClassImpl) {
                    ((MClassImpl)mClass).setName(model.name() + delimiter + mClass.name());

                    result_model.addClass(mClass);
                }
            }

            // generalizations
            Iterator<MGeneralization> iterator = model.generalizationGraph().edgeIterator();
            while (iterator.hasNext()){
                MGeneralization gen = iterator.next();
                result_model.addGeneralization(gen);
            }

            // associations
            for (MAssociation mAssociation : model.associations()){
                if(!(mAssociation instanceof MAssociationClass)) {
                    ((MAssociationImpl)mAssociation).setName(model.name() + delimiter + mAssociation.name());
                    result_model.addAssociation(mAssociation);
                }

            }
            // constraints
            for (MClassInvariant mClassInv : model.classInvariants()){
                mClassInv.setName(model.name() + delimiter + mClassInv.name());
                result_model.addClassInvariant(mClassInv);
            }
        }

        return result_model;
    }

}