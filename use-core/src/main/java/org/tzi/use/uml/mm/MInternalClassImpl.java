package org.tzi.use.uml.mm;

import com.google.common.collect.Iterators;
import org.tzi.use.graph.DirectedGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MInternalClassImpl instances represent classes in a model related to multi-model.
 *
 * @author  Gil Khais
 * @author  Amiel Saad
 */

public class MInternalClassImpl extends MClassImpl{

    private MMultiModel fMultiModel;
    MInternalClassImpl(String name, boolean isAbstract) {
        super(name, isAbstract);
    }

    public void setMultiModel(MMultiModel multi) {
        this.fMultiModel = multi;
    }

    /**
     * The name of the class to be represented as role name for association end
     *  - model1@Animal -> animal
     *  - Animal -> animal
     */
    @Override
    public String nameAsRolename() {
        String rolename = name();
        if(name().contains("@")) {
            rolename = rolename.split("@")[1];
        }
        return Character.toLowerCase(rolename.charAt(0)) + rolename.substring(1);
    }
    public MMultiModel getMultiModel() {
        return fMultiModel;
    }
    @Override
    public Set<MClass> parents() {
        if (fMultiModel == null)
            return super.parents();
        return fMultiModel.generalizationGraph().targetNodeSet(MClass.class, this);
    }
    public Set<MClass> allParents() {
        if (fMultiModel == null)
            return super.allParents();
        return Collections.unmodifiableSet(fMultiModel.generalizationGraph().targetNodeClosureSet(MClass.class, this));
    }
    @Override
    public List<MAttribute> allAttributes() {

        if (fMultiModel == null) return super.allAttributes();
        // start with local attributes
        Set<MAttribute> result = new HashSet<>(attributes());

        // add attributes from all super classes
        // call recursively to get all attributes from all super classes
        for (MClass cls : allParents() ) {
            Set<MGeneralization> edges = fMultiModel.generalizationGraph().edgesBetween(this, cls);
            if (edges.isEmpty()) continue;
            MGeneralization edge = edges.iterator().next();
            if (edge instanceof MClabject){
                List<MAttribute> parentAttributes = cls.allAttributes();

                for (MAttribute removedAttribute : ((MClabject) edge).getRemovedAttributes()) {
                    parentAttributes.remove(removedAttribute);
                }

                for (MAttributeRenaming renamedAttribute : ((MClabject) edge).getAttributeRenaming()) {
                    MAttribute oldAttribute = renamedAttribute.attribute();
                    parentAttributes.remove(oldAttribute);
                    for (MAttribute parentAttribute : parentAttributes) {
                        if (parentAttribute instanceof MInternalAttribute) {
                            MInternalAttribute internalAttribute = (MInternalAttribute) parentAttribute;
                            if (internalAttribute.getOriginalAttribute().equals(oldAttribute)) {
                                parentAttributes.remove(parentAttribute);
                                break;
                            }
                        }
                    }
                    MInternalAttribute attr =  new MInternalAttribute(renamedAttribute.newName(), oldAttribute.type());
                    attr.setOriginalAttribute(oldAttribute);
                    parentAttributes.add(attr);
                }
                result.addAll(parentAttributes);
            }
            else{
                //regular generalization
                result.addAll(cls.allAttributes());
            }
        }

        return new ArrayList<>(result);
    }

    @Override
    public Map<String, MNavigableElement> navigableEnds() {
        if (fMultiModel == null)
            return super.navigableEnds();

        List<Map.Entry<String,MNavigableElement>> allEnds = new ArrayList<>(navigableElements().entrySet());
        // recursively add association ends from superclasses
        for (MClass superclass : parents() ) {
            List<Map.Entry<String,MNavigableElement>> parentEnds = new ArrayList<>();
            for (Map.Entry<String, ? extends MNavigableElement> entry : superclass.navigableEnds().entrySet()) {
                parentEnds.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }

            MGeneralization edge = fMultiModel.generalizationGraph()
                    .edgesBetween(this, superclass).stream().findFirst().orElse(null);

            if (edge instanceof MClabject) {
                // Remove the roles that were removed
                ((MClabject)edge).getRemovedRoles().forEach(removedEnd -> {
                    parentEnds.removeIf(e -> e.getKey().equals(removedEnd.nameAsRolename()));
                });
            }

            allEnds.addAll(parentEnds);
        }


        // Check that allEnds doesn't contain duplicates, throw error if there is any duplicates
        Set<String> endSet = new HashSet<>();
        for (Map.Entry<String, MNavigableElement> entry : allEnds) {
            if (!endSet.add(entry.getKey())) {
                throw new RuntimeException("Role: "+entry.getKey()+" is already defined in class "+name());
            }
        }

        // Combine the remaining ends into the result map
        Map<String, MNavigableElement> res = new TreeMap<>(navigableElements());
        allEnds.forEach(e -> res.put(e.getKey(), e.getValue()));

        return res;
    }

    public Map<String, MNavigableElement> navigableElements(){
        return fNavigableElements;
    }

    // returns the clabjects edges that connect this class with classes from the upper level
    public Set<MClabject> clabjectsFromParents(){
        Set<MClabject> res = new HashSet<>();
        Set<MClass> parents = parents();

        //need to find the parent that's not in the current level
        for (MClassifier parent : parents) {
            if (!parent.model().equals(this.model())) {
                MGeneralization edge = this.model.generalizationGraph().edgesBetween(this, parent).iterator().next();
                res.add((MClabject) edge);
            }
        }
        return res;
    }

    // returns the clabjects edges that connect this class with classes from the lower level
    public Set<MClabject> clabjectsFromChildren(){
        Set<MClabject> res = new HashSet<>();
        Set<MClass> children = children();

        //need to find the children that's not in the current level
        for (MClassifier child : children) {
            if (!child.model().equals(this.model())) {
                MGeneralization edge = this.model.generalizationGraph().edgesBetween(child, this).iterator().next();
                res.add((MClabject) edge);
            }
        }
        return res;
    }

    @Override
    public MAttribute attribute(String name, boolean searchInherited) {
        if (fMultiModel == null) return super.attribute(name, searchInherited);

        MAttribute res = super.attribute(name, searchInherited);
        if (res == null){
            // check if the given name is a renamed name of an attribute
            for (MClabject clab : clabjectsFromParents()) {
                if (clab.getAttributes().containsKey(name)){
                    return clab.getAttributes().get(name);
                }
            }
            return null;
        }

        // check if the given name was removed or renamed if so return null
        for (MClabject clab : clabjectsFromParents()){
            if (clab.getRemovedAttribute(name) != null || clab.getRenamedAttribute(name) != null){
                return null;
            }
        }
        return res;
    }

}