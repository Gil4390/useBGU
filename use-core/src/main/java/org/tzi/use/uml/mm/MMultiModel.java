package org.tzi.use.uml.mm;

import org.tzi.use.api.UseModelApi;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.uml.mm.commonbehavior.communications.MSignal;
import org.tzi.use.uml.mm.commonbehavior.communications.MSignalImpl;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.TypeFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MMultiModel {
    private Map<String, MModel> fModels; // <modelName, MModel>
    private String fName;
    private String fFilename; // name of .use file
    private HashMap<String, MInterAssociation> fInterAssociations;

    private List<MClassInvariant> fInterConstraints;

    private Map<String, String> fInterConstraintsString;

    public MMultiModel(String name) {
        fName = name;
        fModels = new TreeMap<>();
        fFilename = "";
        fInterAssociations = new HashMap<>();
        fInterConstraints = new ArrayList<>();
    }
    public String name() {
        return fName;
    }

    public void setFilename(String name) {
        fFilename = name;
    }
    /**
     * Returns the filename of the specification from which this model was read.
     * May be empty if model is not constructed from a file.
     */
    public String filename() {
        return fFilename;
    }

    /**
     * Adds a model. The model must have a unique name within the multiModel.
     *
     * @exception Exception
     *                multiModel already contains a model with the same name.
     */
    public void addModel(MModel model) throws Exception { //TODO: add custom exception
        if (fModels.containsKey(model.name()))
            throw new Exception("MultiModel already contains a model `"
                    + model.name() + "'.");
        // TODO: find out if we need to check more exceptions
        fModels.put(model.name(), model);
        // TODO: add to GenGraph
        //model.setMLModel(this); TODO: add setMLModel method
    }

    /**
     * Removes a model with the given name. The model must exist within the multiModel.
     *
     * @exception Exception
     *                multiModel does not contain a model with the given name.
     */
    public void removeModel(String modelName) throws Exception { //TODO: add custom exception
        if (!fModels.containsKey(modelName))
            throw new Exception("MultiModel does not contain a model `" + modelName + "'.");

        // TODO: find out if we need to check more exceptions
        fModels.remove(modelName);
    }

    public void addInterAssociation(MInterAssociation association) {
        fInterAssociations.put(association.name(), association);
    }
    //TODO: implement in a way that it adds MNavigableElement to each model
    // replace it with gen() in ASTInterAssociation
    public void addInterConstraint(MClassInvariant constraint) {
        fInterConstraints.add(constraint);
    }


    public Collection<MInterAssociation> interAssociations() {
        return fInterAssociations.values();
    }

    public MInterAssociation getInterAssociations(String assocName) {
        return fInterAssociations.get(assocName);
    }

    public List<MClassInvariant> interConstraints() {
        return fInterConstraints;
    }

    /**
     * Returns the specified model by name.
     *
     * @return <code>null</code> if model <code>name</code> does not exist.
     */
    public MModel getModel(String name) {
        return fModels.get(name);
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
        return fModels.values()
                .stream()
                .mapToInt(model -> model.classes().size())
                .sum();
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
        long count = fInterAssociations.values().stream()
                .filter(assoc -> assoc.associationEnds().stream()
                        .anyMatch(assocEnd -> assocEnd.name().equals(clsName)))
                .count();

        return (int) count;
    }

    public MModel toMModel() throws Exception {
        String delimiter= "_";
        MModel result_model = new MModel(this.fName);

        for (MModel model : fModels.values()) {
            // user-defined types
            for (EnumType enumType : model.enumTypes()){
                EnumType newEnumType = enumType.makeCopy(result_model, model.name() + delimiter);
                newEnumType.model = result_model;
            }
            // regular classes
            for (MClass mClass : model.classes()){
                if(mClass instanceof MClassImpl){
                    MClassImpl newClass = ((MClassImpl) mClass).initCopy(model.name() + delimiter);
                    result_model.addClass(newClass);
                    ((MClassImpl) mClass).makeCopy(newClass, model.name() + delimiter, result_model);
                    //MClass newClass = ((MClassImpl) mClass).makeCopy(model.name() + delimiter, result_model);
                }
            }
            // association classes
            for (MClass mClass : model.classes()){
                if(mClass instanceof MAssociationClassImpl){
                    MAssociationClassImpl newClass = ((MAssociationClassImpl) mClass).initCopy(model.name() + delimiter);
                    result_model.addClass(newClass);
                    ((MAssociationClassImpl) mClass).makeCopy(newClass, model.name()+delimiter, result_model);
                    result_model.addAssociation(newClass);
                }
            }
            // associations
            for (MAssociation mAssociation : model.associations()){
                if(!(mAssociation instanceof MAssociationClass)
                        && !(mAssociation instanceof MInterAssociation)) {
                    MAssociation newAssoc = mAssociation.makeCopy(model.name()+delimiter, result_model);
                    result_model.addAssociation(newAssoc);
                }

            }
            // constraints
            for (MClassInvariant mClassInv : model.classInvariants()){
                if(!fInterConstraints.contains(mClassInv)) {
                    MClassInvariant newClassInv = mClassInv.makeCopy(model.name() + delimiter, result_model.classesMap());
                    result_model.addClassInvariant(newClassInv);
                }
            }

            // signals
            for (MSignal mSignal : model.getSignals()){
                MSignal newSignal = ((MSignalImpl)mSignal).makeCopy(model.name() + delimiter);
                result_model.addSignal(newSignal);
            }

            Iterator<MGeneralization> iterator = model.generalizationGraph().edgeIterator();
            while (iterator.hasNext()){
                MGeneralization gen = iterator.next();
                MClassifier parent = gen.parent();
                MClassifier child = gen.child();
                String parentName = parent.name();
                String childName = child.name();

                MClass convertedParentCls = result_model.getClass(model.name() + delimiter + parentName);
                MClass convertedChildCls = result_model.getClass(model.name() + delimiter + childName);
                MGeneralization convertedGen = new MGeneralization(convertedChildCls, convertedParentCls);
                result_model.addGeneralization(convertedGen);
            }

        }

        //inter-associations
        for(MInterAssociation interAssoc : fInterAssociations.values()) {
            MAssociation association = interAssoc.makeCopy("", result_model);
            result_model.addAssociation(association);
        }

        //inter-constraints
        for(MClassInvariant inv : fInterConstraints) {
            UseModelApi api = new UseModelApi(result_model);

            String str = inv.bodyExpression().toString();


            api.createInvariant(inv.cls().model().name() + delimiter + inv.name(),
                    inv.cls().model().name() + delimiter + inv.cls().name(),
                    inv.bodyExpression().toString(),
                    inv.isExistential());
//            MClassInvariant newClassInv = inv.makeCopy(inv.cls().model().name() + delimiter, result_model.classesMap());
//            result_model.addClassInvariant(newClassInv);

        }
        return result_model;
    }

}