package org.tzi.use.uml.mm;

import java.util.*;

/**
 * MInternalClassImpl instances represent classes in a model related to multi-model.
 *
 * @author  Gil Khais
 * @author  Amiel Saad
 */

public class MInternalClassImpl extends MClassImpl{
    MInternalClassImpl(String name, boolean isAbstract) {
        super(name, isAbstract);
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
    public List<MAttribute> allAttributes() {

        // start with local attributes
        Set<MAttribute> result = new HashSet<>(attributes());

        // add attributes from all super classes
        // call recursively to get all attributes from all super classes
        for (MClass cls : allParents() ) {
            Set<MGeneralization> edges = model.generalizationGraph().edgesBetween(cls, this);
            if (edges.isEmpty()) continue;
            if (edges.size() != 1){
                //throw new Exception("expected exactly one edge between " + cls.name() + " and " + this.name());
            }
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
}
