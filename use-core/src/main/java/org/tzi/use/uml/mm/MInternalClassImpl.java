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
            Set<MGeneralization> edges = fMultiModel.generalizationGraph().edgesBetween(this, superclass);
            if (edges.isEmpty()) continue;
            MGeneralization edge = edges.iterator().next();
            List<Map.Entry<String,MNavigableElement>> parentEnds = new ArrayList<>();

            for (Map.Entry<String, ? extends MNavigableElement> entry : superclass.navigableEnds().entrySet()) {
                parentEnds.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
            }

            if (edge instanceof MClabject) {
                // remove the roles that were removed or renamed by the clabject
                for (MNavigableElement removedEnd : ((MClabject) edge).getRemovedRoles()) {
                    Map.Entry<String,MNavigableElement> endToRemove = parentEnds.stream().filter(e -> e.getKey().equals(removedEnd.nameAsRolename())).findFirst().orElse(null);
                    if (endToRemove != null) {
                        parentEnds.remove(endToRemove);
                    }
                }

                for (MRoleRenaming renamedEnd : ((MClabject) edge).getRenamedRoles()) {
                    Map.Entry<String, MNavigableElement> endToRename = parentEnds.stream().filter(e -> e.getValue().equals(renamedEnd.assocEnd())).findFirst().orElse(null);
                    if (endToRename != null) {
                        parentEnds.remove(endToRename);
                        Map.Entry<String, MNavigableElement> endToAdd = new AbstractMap.SimpleEntry<>(renamedEnd.newName(), renamedEnd.assocEnd());
                        parentEnds.add(endToAdd);
                    }
                }
                allEnds.addAll(parentEnds);
//                res.putAll(parentEnds);
            }
            else{
                allEnds.addAll(parentEnds);
//                res.putAll(superclass.navigableEnds());
            }
        }


        //handle ends that are redefined in assoclinks
        Set<MNavigableElement> keysToRemove = new HashSet<>();
        List<Map.Entry<String, MNavigableElement>> entries = new ArrayList<>(allEnds);

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, MNavigableElement> entry1 = entries.get(i);
            MNavigableElement end1 = entry1.getValue();
            MAssociation assoc1 = end1.association();

            for (int j = i + 1; j < entries.size(); j++) {
                Map.Entry<String, MNavigableElement> entry2 = entries.get(j);
                MNavigableElement end2 = entry2.getValue();
                MAssociation assoc2 = end2.association();

                this.fMultiModel.fGenGraph.edgesBetween(assoc1, assoc2).forEach(e -> {
                    if (e instanceof MAssoclink && associations().contains(e.fChild)) {
                        keysToRemove.addAll(e.fParent.navigableEnds().values());
                    }
                });
//                if (!this.fMultiModel.fGenGraph.edgesBetween(assoc1, assoc2).isEmpty()) {
//                    if(!navigableElements().containsKey(end1.nameAsRolename())){
//                        keysToRemove.add(entry1.getValue());
//                    }
//                    if(!navigableElements().containsKey(end2.nameAsRolename())) {
//                        keysToRemove.add(entry2.getValue());
//                    }
//                    break; // No need to continue checking other pairs for entry1
//                }
            }
        }

        for(MNavigableElement key : keysToRemove){
            allEnds.removeIf(e -> e.getValue().equals(key));
        }
        Map<String, MNavigableElement> res = new TreeMap<>(navigableElements());
        allEnds.forEach(e -> res.put(e.getKey(), e.getValue()));
//        keysToRemove.forEach(res::remove);

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