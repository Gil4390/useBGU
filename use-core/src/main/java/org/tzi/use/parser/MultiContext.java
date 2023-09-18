package org.tzi.use.parser;

import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.mm.MultiModelFactory;
import org.tzi.use.uml.ocl.value.VarBindings;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class MultiContext extends Context{

    private MultiModelFactory fMultiModelFactory;
    private Map<String, Context> contextMap;
    public MultiContext(String filename, PrintWriter err, VarBindings globalBindings, MultiModelFactory factory) {
        super(filename, err, globalBindings, factory);
        contextMap = new HashMap<>();
        fMultiModelFactory = factory;
    }


    public void setContext(String modelName, Context ctx){
        contextMap.put(modelName, ctx);
    }

    public Context getContext(String modelName) {
        return contextMap.get("(" + modelName + ")");
    }



    public MultiModelFactory modelFactory() {
        return this.fMultiModelFactory;
    }

}
