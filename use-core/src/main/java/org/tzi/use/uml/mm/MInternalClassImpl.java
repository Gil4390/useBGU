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



//        List<MAssociation> assocLinksAssociations =((MMultiLevelModel)this.fMultiModel).getMediator(this.model.name()).assocLinks()
//                .stream().map(assocLink -> (MAssociation)assocLink.child()).collect(Collectors.toList()); //{cd1}
//
//
//        List<MAssociation> cAssocLinksAssociations = this.associations().stream()
//                .filter(assocLinksAssociations::contains).collect(Collectors.toList()); //{cd1}
//
//        //super.navigableEnds() // {dd, dd2, bb}
//        List<MAssociationEnd> ends = super.navigableEnds().values().stream().map(end -> (MAssociationEnd)end)
//                .filter(end -> cAssocLinksAssociations.contains(end.association())).collect(Collectors.toList()); //{dd}
//
//        List<String> oldRoleName = new ArrayList<>();
//        List<String> newRoleName = new ArrayList<>();
//
//        Collection<MAssoclink> assoclinks = ((MMultiLevelModel)this.fMultiModel).getMediator(this.model.name()).assocLinks();
//        for (MAssoclink assocLink : assoclinks){
//            oldRoleName.add((assocLink.parent().name()));
//            newRoleName.add((assocLink.child().name()));
//        }
//
//        for (String end : res.keySet()){
//            if (oldRoleName.contains(end)){
//                res.put(newRoleName.get(oldRoleName.indexOf(end)), res.get(end));
//                res.remove(end);
//            }
////            // end = {dd}
////            MAssoclink al = end.assocLink;
////            result.add(al.getOriginalEnd(end)); //{bb}
//        }



        return res;
    }


    @Override
    public boolean isSubClassOf(MClassifier otherClassifier, boolean excludeThis) {
        if (fMultiModel == null) return Iterators.contains(this.generalizationHierachie(!excludeThis).iterator(), otherClassifier);

        /*  in order to account for the entire gen graph
            the method in org/tzi/use/uml/mm/MClassifierImpl.java:415
            should look in the gen graph of the MLM and not the internal model
         */
        MModel originalModel = model;
        setModel(fMultiModel);

        boolean result = super.isSubClassOf(otherClassifier, excludeThis);

        setModel(originalModel);

        return result;
    }
}
