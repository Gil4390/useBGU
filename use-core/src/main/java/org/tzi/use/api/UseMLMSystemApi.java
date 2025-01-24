package org.tzi.use.api;

import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MLMSystem;
import org.tzi.use.uml.sys.MLMSystemState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.NullPrintWriter;

import java.io.PrintWriter;
import java.util.Collections;

public class UseMLMSystemApi extends UseSystemApiUndoable {
    public UseMLMSystemApi(Session session) {
        super(session);
    }

    public UseMLMSystemApi(MSystem system) {
        super(system);
    }

    public UseMLMSystemApi(MModel model) {
        this(new MLMSystem(model));
    }

    public UseMLMSystemApi(UseModelApi api) {
        super(api);
    }

    public MLMSystemState.Definedness checkWellDefinedness(PrintWriter error){
        MLMSystemState.Definedness result;
        // Check structure
        result = ((MLMSystemState)system.state()).checkWellDefinedStructure(NullPrintWriter.getInstance());
        // Check Invariants
        MLMSystemState.Definedness check = ((MLMSystemState)system.state()).checkWellDefinedness(error, false, false, true, Collections.<String>emptyList());
        if (check == MLMSystemState.Definedness.NotWellDefined){
            result = MLMSystemState.Definedness.NotWellDefined;
        }
        return result;
    }

    public MLMSystemState.Definedness checkWellDefinedness() {
        return checkWellDefinedness(NullPrintWriter.getInstance());
    }
}
