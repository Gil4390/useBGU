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
     * The name of the class to be represented as rolename for association end
     * model1@Animal -> animal
     * Animal -> animal
     * @return
     */
    @Override
    public String nameAsRolename() {
        String rolename = name();
        if(name().contains("@")) {
            rolename = rolename.split("@")[1];
        }
        return Character.toLowerCase(rolename.charAt(0)) + rolename.substring(1);
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

            if (edge != null && edge instanceof MClabject) {
                // Remove the roles that were removed or renamed by the clabject
                ((MClabject)edge).getRemovedRoles().forEach(removedEnd -> {
                    parentEnds.removeIf(e -> e.getKey().equals(removedEnd.nameAsRolename()));
                });

                // Rename the roles as defined by the clabject
                ((MClabject)edge).getRenamedRoles().forEach(renamedEnd -> {
                    boolean isEndRemoved = parentEnds.removeIf(e -> e.getValue().equals(renamedEnd.assocEnd()));
                    if(isEndRemoved) {
                        parentEnds.add(new AbstractMap.SimpleEntry<>(renamedEnd.newName(), renamedEnd.assocEnd()));
                    }
                });
            }

            allEnds.addAll(parentEnds);
        }

        // Handle ends that are redefined in assoclinks
        Set<MNavigableElement> keysToRemove = new HashSet<>();

        for (int i = 0; i < allEnds.size(); i++) {
            MNavigableElement end1 = allEnds.get(i).getValue();
            MAssociation assoc1 = end1.association();

            for (int j = i + 1; j < allEnds.size(); j++) {
                MNavigableElement end2 = allEnds.get(j).getValue();
                MAssociation assoc2 = end2.association();

                this.fMultiModel.fGenGraph.edgesBetween(assoc1, assoc2).forEach(e -> {
                    if (e instanceof MAssoclink && associations().contains(e.fChild)) {
                        keysToRemove.addAll(e.fParent.navigableEnds().values());
                    }
                });
            }
        }

        // Remove the redefined ends
        allEnds.removeIf(e -> keysToRemove.contains(e.getValue()));

        // Combine the remaining ends into the result map
        Map<String, MNavigableElement> res = new TreeMap<>(navigableElements());
        allEnds.forEach(e -> res.put(e.getKey(), e.getValue()));

        return res;
    }

    //return the clabject edge that connects this class with the class from the upper level
    // will return null if the clabject doesn't exist
    public MClabject getClabjectEdge(){
        Set<MClass> parents = parents();
        if (parents.isEmpty()) return null;
        //need to find the parent that's not in the current level
        Iterator<MClass> iterator = parents.iterator();
        MClassifier parent = iterator.next();
        if (parent.model().equals(this.model())){
            if (!iterator.hasNext()){
                return null;
            }
            parent = iterator.next();
        }
        MGeneralization edge = this.model.generalizationGraph().edgesBetween(this,parent).iterator().next();

        return (MClabject) edge;
    }
    @Override
    public MAttribute attribute(String name, boolean searchInherited) {
        if (fMultiModel == null) return super.attribute(name, searchInherited);

        MAttribute res = super.attribute(name, searchInherited);
        if (res != null){
            //check if the attribute is removed or renamed by the clabject

            MClabject clabject = getClabjectEdge();
            if (clabject == null){
                return res;
            }
            if (clabject.getRemovedAttribute(name) == null && clabject.getRenamedAttribute(name) == null){
                return res;
            }

        }
        MClabject clabject = getClabjectEdge();
        if (clabject != null && getClabjectEdge().getAttributes().containsKey(name)){
            return getClabjectEdge().getAttributes().get(name);
        }
        return null;
    }

}