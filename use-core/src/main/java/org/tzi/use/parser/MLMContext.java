package org.tzi.use.parser;

import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.value.VarBindings;

import java.io.PrintWriter;

public class MLMContext extends MultiContext {

    private MModel parentModel; // the model that is the parent of the current model in th mediator hierarchy
    private MModel currentModel; // the model that is currently being parsed

    public MLMContext(String filename, PrintWriter err, VarBindings globalBindings, ModelFactory factory) {
        super(filename, err, globalBindings, factory);
        setMainContext(null);
    }

    public void setMLModel(MMultiLevelModel mlModel) {
        super.setModel(mlModel);
    }

    public MModel getParentModel() {
        return parentModel;
    }

    public void setParentModel(MModel parentModel) {
        this.parentModel = parentModel;
    }

    public MModel getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(MModel currentModel) {
        this.currentModel = currentModel;
    }

    @Override
    public MultiLevelModelFactory modelFactory() {
        return (MultiLevelModelFactory) super.modelFactory();
    }

}
