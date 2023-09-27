package org.tzi.use.uml.mm;

public class MultiModelFactory extends ModelFactory{
    public MMultiModel createMultiModel(String name) {
        return new MMultiModel(name);
    }

}
