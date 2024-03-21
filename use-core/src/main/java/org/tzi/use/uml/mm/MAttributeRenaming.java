package org.tzi.use.uml.mm;

public class MAttributeRenaming {

    private MAttribute fAttribute;
    private String newName;

    public MAttributeRenaming(MAttribute attribute, String newAttributeName) {
        this.fAttribute = attribute;
        this.newName = newAttributeName;
    }

    public MAttribute attribute() {
        return fAttribute;
    }

}
