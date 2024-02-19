package org.tzi.use.parser;

import org.antlr.runtime.Token;
import org.tzi.use.uml.mm.MLevel;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.mm.MultiLevelModelFactory;
import org.tzi.use.uml.ocl.value.VarBindings;

import java.io.PrintWriter;

public class MLMContext extends Context {


    private MultiLevelModelFactory factory;
    private MLMContext parent;
    private MLevel parentLevel;
    private MLevel currentLevel;


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

    public void setParentLevel(MLevel parentLevel) {
        this.parentLevel = parentLevel;
    }

    public MLevel parentLevel() {
        return this.parentLevel;
    }

    public void setCurrentLevel(MLevel level) {
        this.currentLevel = level;
    }

    public MLevel level() {
        return currentLevel;
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
