package org.tzi.use.tools.catuselatex;

import org.tzi.use.uml.mm.MMPrintVisitor;
import org.tzi.use.uml.mm.MMultiLevelModel;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Renders a compiled {@link MMultiLevelModel} back into valid, re-parseable
 * MLM-USE textual syntax via the stock {@link MMPrintVisitor}.
 */
public class MlmUseTextRenderer {

    public static String render(MMultiLevelModel mlm) {
        return render(mlm, mlm.name());
    }

    /**
     * @param displayName name to print after "MLM " in the header. Pass
     *  this explicitly rather than trusting {@link MMultiLevelModel#name()}:
     *  it is built from an intermediate AST node that keeps its default
     *  placeholder ("MLM") instead of the name actually declared in the
     *  source ("MLM ABCD" -&gt; should print "ABCD", but {@code mlm.name()}
     *  returns "MLM"). Unrelated to, and not fixed alongside, the
     *  visitMLM flattening bug that used to live here.
     */
    public static String render(MMultiLevelModel mlm, String displayName) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        mlm.processWithVisitor(new MMPrintVisitor(out));
        out.flush();
        return sw.toString().replaceFirst("^MLM \\S+", "MLM " + displayName);
    }
}
