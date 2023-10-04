package org.tzi.use.parser;

import org.antlr.runtime.Token;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.mm.MultiModelFactory;
import org.tzi.use.uml.ocl.value.VarBindings;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MultiContext extends Context{

    private MultiContext parent;
    private MultiModelFactory fMultiModelFactory;

    /**
     *
     * @param filename
     * @param err
     * @param globalBindings
     * @param factory
     */
    public MultiContext(String filename, PrintWriter err, VarBindings globalBindings, MultiModelFactory factory) {
        super(filename, err, globalBindings, factory);
        fMultiModelFactory = factory;
        parent = null;
    }

    public void setParentContext(MultiContext parent) {
        this.parent = parent;
    }

    /**
     * reports all errors to the parent Context.
     * @param t
     * @param msg
     */

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

    public MultiModelFactory modelFactory() {
        return this.fMultiModelFactory;
    }

    public void setMultiModel(MMultiModel multiModel){
        super.setModel(multiModel);
    }
}
