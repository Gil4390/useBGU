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
                    parentAttributes.add(new MAttribute(renamedAttribute.newName(), oldAttribute.type()));
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

    public Map<String, MNavigableElement> navigableEnds() {
        if (fMultiModel == null)
            return super.navigableEnds();

        Map<String, MNavigableElement> res = new TreeMap<String, MNavigableElement>();
        res.putAll(super.navigableEnds());

        // recursively add association ends in superclasses
        for (MClass superclass : allParents() ) {
            res.putAll(superclass.navigableEnds());
        }
        Map<String, MNavigableElement> parentEnds = new TreeMap<String, MNavigableElement>();
        if (this.parents() != null && !this.parents().isEmpty()){
            MClass parentClass = this.parents().iterator().next();
            if (parentClass != null) {
                parentEnds.putAll(parentClass.navigableEnds());
            }
        }

        //res.entrySet().removeIf(entry -> parentEnds.containsKey(entry.getKey()));

        Set<String> keysToRemove = new HashSet<>();
        Map<String, MNavigableElement> newEntries = new HashMap<>();

        Iterator<Map.Entry<String, MNavigableElement>> iterator = res.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MNavigableElement> entry = iterator.next();
            MNavigableElement endElement = entry.getValue();
            if (endElement instanceof MInternalAssociationEnd) {
                MInternalAssociationEnd internalEnd = (MInternalAssociationEnd) endElement;
                if (internalEnd.isRenamed(this)) {
                    keysToRemove.add(entry.getKey());
                    newEntries.put(internalEnd.newName, endElement);
                }
            }
        }

        keysToRemove.forEach(res::remove);
        res.putAll(newEntries);


        Set<String> keysToRemove2 = new HashSet<>();
        List<Map.Entry<String, MNavigableElement>> entries = new ArrayList<>(res.entrySet());

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, MNavigableElement> entry1 = entries.get(i);
            MNavigableElement end1 = entry1.getValue();
            MAssociation assoc1 = end1.association();

            for (int j = i + 1; j < entries.size(); j++) {
                Map.Entry<String, MNavigableElement> entry2 = entries.get(j);
                MNavigableElement end2 = entry2.getValue();
                MAssociation assoc2 = end2.association();

                if (!this.fMultiModel.fGenGraph.edgesBetween(assoc1, assoc2).isEmpty()) {
                    if (parentEnds.containsValue(end1)) {
                        keysToRemove2.add(entry1.getKey());
                    } else {
                        keysToRemove2.add(entry2.getKey());
                    }
                    break; // No need to continue checking other pairs for entry1
                }
            }
        }


        keysToRemove2.forEach(res::remove);

        return res;
    }

    @Override
    public MAttribute attribute(String name, boolean searchInherited) {
        if (fMultiModel == null) return super.attribute(name, searchInherited);

        MAttribute res = super.attribute(name, searchInherited);
        if (res != null) return res;

        Set<MClass> allParents = allParents();
        Set<MClass> parents = parents();
        MClassifier parent = this.parents().iterator().next();
        MGeneralization edge = this.model.generalizationGraph().edgesBetween(this,parent).iterator().next();

        if (edge instanceof MClabject){
            if (((MClabject) edge).getAttributes().containsKey(name)){
                return ((MClabject) edge).getAttributes().get(name);
            }
        }
        return null;
    }
}