package org.tzi.use.uml.mm;

import org.tzi.use.uml.ocl.type.Type;

import java.util.Objects;

public class MInternalAttribute extends MAttribute{
    private MAttribute originalAttribute;

    public MInternalAttribute(String name, Type type) {
        super(name, type);
    }

    public void setOriginalAttribute(MAttribute originalAttribute) {
        this.originalAttribute = originalAttribute;
    }

    public MAttribute getOriginalAttribute() {
        return originalAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MInternalAttribute that = (MInternalAttribute) o;
        return Objects.equals(originalAttribute, that.originalAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), originalAttribute);
    }
}
