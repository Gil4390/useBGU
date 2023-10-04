package org.tzi.use.parser.use;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.tzi.use.parser.MultiContext;
import org.tzi.use.parser.ParseErrorHandler;
import org.tzi.use.uml.mm.MMultiModel;
import org.tzi.use.uml.mm.MultiModelFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Compiler for USE Multi specifications.
 *
 * @author Gil Khais
 * @author Amiel Saad
 */

public class USECompilerMulti {

    private USECompilerMulti() {}

    /**
     * Compiles a multi-model specification.
     *
     * @param  in the source to be compiled
     * @param  inName name of the source stream
     * @param  err output stream for error messages
     * @param factory factory for object creation
     * @return MMultiModel null if there were any errors
     */

    public static MMultiModel compileMultiSpecification(InputStream in,
                                                        String inName,
                                                        PrintWriter err,
                                                        MultiModelFactory factory) {
        MMultiModel multi_model = null;
        ParseErrorHandler errHandler = new ParseErrorHandler(inName, err);

        ANTLRInputStream aInput;
        try {
            aInput = new ANTLRInputStream(in);
            aInput.name = inName;
        } catch (IOException e1) {
            err.println(e1.getMessage());
            return multi_model;
        }

        USELexer lexer = new USELexer(aInput);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        USEParser parser = new USEParser(tokenStream);

        lexer.init(errHandler);
        parser.init(errHandler);

        try {
            // Parse the specification
            ASTMultiModel astMultiModel = parser.multi_model();
            if (errHandler.errorCount() == 0 ) {

                // Generate code
                MultiContext ctx = new MultiContext(inName, err, null, factory);
                multi_model = astMultiModel.gen(ctx);
                if (ctx.errorCount() > 0 )
                    multi_model = null;
            }
        } catch (RecognitionException e) {
            err.println(parser.getSourceName() +":" +
                    e.line + ":" +
                    e.charPositionInLine + ": " +
                    e.getMessage());
        }

        err.flush();
        return multi_model;
    }

}
