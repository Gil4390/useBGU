package org.tzi.use.uml.mm;

public class MInterClassImpl extends MClassImpl{

    public MClassImpl originalClass;

    MInterClassImpl(String name, boolean isAbstract) {
        super(name, isAbstract);
    }

    public void setOriginalClass(MClassImpl originalClass){
        this.originalClass = originalClass;
    }

}