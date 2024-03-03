package org.tzi.use.parser;

import org.antlr.runtime.Token;
import org.tzi.use.uml.mm.*;
import org.tzi.use.uml.ocl.value.VarBindings;

import java.io.PrintWriter;

public class MLMContext extends MultiContext {


    private MultiLevelModelFactory factory;
    private MLMContext parent;
    private MModel parentModel;
    private MModel currentModel;


    public MLMContext(String filename, PrintWriter err, VarBindings globalBindings, MultiLevelModelFactory factory) {
        super(filename, err, globalBindings, factory);
        this.factory = factory;
        parent = null;
    }

    public void setMainContext(MLMContext parent) {
        this.parent = parent;
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

    public MultiLevelModelFactory modelFactory() {
        return this.factory;
    }

    @Override
    public void reportError(Token t, String msg) {
        if(parent != null) {
            parent.reportError(t, msg);
        } else {
            super.reportError(t, msg);
        }
    }

    @Override
    public void reportError(Token t, Exception ex) {
        if(parent != null) {
            parent.reportError(t, ex);
        } else {
            super.reportError(t, ex);
        }
    }

    @Override
    public void reportError(SemanticException ex) {
        if(parent != null) {
            parent.reportError(ex);
        } else {
            super.reportError(ex);
        }
    }

}
