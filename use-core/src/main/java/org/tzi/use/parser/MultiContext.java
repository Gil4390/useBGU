package org.tzi.use.parser;

import org.antlr.runtime.Token;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.mm.MultiModelFactory;
import org.tzi.use.uml.ocl.value.VarBindings;

import java.io.PrintWriter;

public class MultiContext extends Context{

    private MultiContext mainContext; // used to track weather this context is of a multi-model or internal model

    /**
     *
     * @param filename
     * @param err
     * @param globalBindings
     * @param factory
     */
    public MultiContext(String filename, PrintWriter err, VarBindings globalBindings, ModelFactory factory) {
        super(filename, err, globalBindings, factory);
        mainContext = null;
    }

    public void setParentContext(MultiContext parent) {
        this.mainContext = parent;
    }

    @Override
    public MultiModelFactory modelFactory() {
        return (MultiModelFactory) super.modelFactory();
    }

    /**
     * reports all errors to the parent Context.
     * @param t
     * @param msg
     */

    @Override
    public void reportError(Token t, String msg) {
        if(mainContext != null) {
            mainContext.reportError(t, msg);
        } else {
            super.reportError(t, msg);
        }
    }

    @Override
    public void reportError(Token t, Exception ex) {
        if(mainContext != null) {
            mainContext.reportError(t, ex);
        } else {
            super.reportError(t, ex);
        }
    }

    @Override
    public void reportError(SemanticException ex) {
        if(mainContext != null) {
            mainContext.reportError(ex);
        } else {
            super.reportError(ex);
        }
    }

    public void setMultiModel(MMultiModel multiModel){
        super.setModel(multiModel);
    }
}
