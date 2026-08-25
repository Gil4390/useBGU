package org.tzi.use.tools.catuselatex;

import org.tzi.use.parser.use.USECompilerCatUSE;
import org.tzi.use.uml.mm.MMultiLevelModel;
import org.tzi.use.uml.mm.MultiLevelModelFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Study/teaching tool: compiles a CatUSE specification and writes a
 * self-contained LaTeX report showing, side by side, the CatUSE source,
 * its desugared plain MLM-USE form (see {@link MlmUseTextRenderer} -- the
 * translation this whole front-end exists to perform), and a PlantUML
 * class diagram of the resulting multi-level model.
 *
 * <p>Usage: {@code CatUSELatexReport <input.use> [outputDir]}
 *
 * <p>The generated .tex references its sibling .puml file via
 * {@code \write18}, calling {@code plantuml} (to SVG) and then
 * {@code inkscape} (SVG to PDF) to render the diagram as a vector PDF at
 * LaTeX-compile time -- both must be on {@code PATH}. Run {@code pdflatex}
 * with {@code -shell-escape} in the output directory.
 */
public class CatUSELatexReport {

    private static final Pattern MLM_NAME = Pattern.compile("^\\s*MLM\\s+(\\S+)", Pattern.MULTILINE);

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("usage: CatUSELatexReport <input.use> [outputDir]");
            System.exit(2);
        }

        Path inputFile = Paths.get(args[0]);
        Path outputDir = args.length >= 2 ? Paths.get(args[1]) : inputFile.toAbsolutePath().getParent();
        Files.createDirectories(outputDir);
        Path absOutputDir = outputDir.toAbsolutePath();

        String baseName = stripExtension(inputFile.getFileName().toString());
        String catUseSource = Files.readString(inputFile, StandardCharsets.UTF_8);

        StringWriter errBuf = new StringWriter();
        PrintWriter err = new PrintWriter(errBuf);
        MMultiLevelModel mlm = USECompilerCatUSE.compileCatUSESpecification(
                new ByteArrayInputStream(catUseSource.getBytes(StandardCharsets.UTF_8)),
                inputFile.toString(), err, new MultiLevelModelFactory());
        err.flush();

        if (mlm == null) {
            System.err.println("CatUSE compilation failed:\n" + errBuf);
            System.exit(1);
        }

        String displayName = extractMlmName(catUseSource, baseName);
        String mlmUseText = MlmUseTextRenderer.render(mlm, displayName);
        String flatUseText = FlatUseTextRenderer.render(mlm, displayName);
        String plantUml = PlantUmlDiagramGenerator.generate(mlm);

        String pumlFileName = baseName + "_diagram.puml";
        String svgFileName = baseName + "_diagram.svg";
        String pdfFileName = baseName + "_diagram.pdf";

        Path pumlPath = outputDir.resolve(pumlFileName);
        Files.writeString(pumlPath, plantUml, StandardCharsets.UTF_8);

        String tex = buildLatex(baseName, displayName, catUseSource, mlmUseText, flatUseText, plantUml,
                absOutputDir.resolve(pumlFileName).toString(),
                absOutputDir.resolve(svgFileName).toString(),
                absOutputDir.resolve(pdfFileName).toString(),
                pdfFileName);
        Path texPath = outputDir.resolve(baseName + ".tex");
        Files.writeString(texPath, tex, StandardCharsets.UTF_8);

        System.out.println("wrote " + texPath);
        System.out.println("wrote " + pumlPath);
        System.out.println();
        System.out.println("to build: (cd " + outputDir + " && pdflatex -shell-escape " + baseName + ".tex)");
    }

    private static String extractMlmName(String source, String fallback) {
        Matcher m = MLM_NAME.matcher(source);
        return m.find() ? m.group(1) : fallback;
    }

    private static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? filename : filename.substring(0, dot);
    }

    private static String buildLatex(String baseName, String displayName, String catUseSource, String mlmUseText,
                                      String flatUseText, String plantUml, String absPumlPath, String absSvgPath,
                                      String absPdfPath, String pdfFileNameForInclude) {
        StringBuilder tex = new StringBuilder();
        // Requires plantuml and inkscape on PATH (rendering the class diagram at build time via \write18).
        tex.append("% pdflatex -shell-escape ").append(baseName).append(".tex\n");
        tex.append("\\documentclass[11pt]{article}\n");
        tex.append("\\usepackage[margin=1in]{geometry}\n");
        tex.append("\\usepackage{listings}\n");
        tex.append("\\usepackage{xcolor}\n");
        tex.append("\\usepackage{graphicx}\n");
        tex.append("\\usepackage{parskip}\n\n");

        tex.append("\\lstdefinelanguage{MlmUse}{\n");
        tex.append("  morekeywords={MLM,model,mediator,clabject,assoclink,class,category,").append("\n");
        tex.append("    abstract,attributes,operations,associations,association,between,role,end,\n");
        tex.append("    constraints,context,inv,catAtt,catConstr,catAssociation,").append("\n");
        tex.append("    attributes,roles,inter-classes,inter-associations,inter-constraints,NONE},\n");
        tex.append("  sensitive=true,\n");
        tex.append("  morecomment=[l]{--},\n");
        tex.append("  morestring=[b]',\n");
        tex.append("}\n\n");

        tex.append("\\lstset{\n");
        tex.append("  basicstyle=\\ttfamily\\small,\n");
        tex.append("  keywordstyle=\\color{blue!60!black}\\bfseries,\n");
        tex.append("  commentstyle=\\color{gray},\n");
        tex.append("  stringstyle=\\color{green!40!black},\n");
        tex.append("  breaklines=true,\n");
        tex.append("  columns=fullflexible,\n");
        tex.append("  frame=single,\n");
        tex.append("  tabsize=2,\n");
        tex.append("  showstringspaces=false,\n");
        tex.append("  numbers=left,\n");
        tex.append("  numberstyle=\\tiny\\bfseries,\n");
        tex.append("  numbersep=8pt,\n");
        tex.append("  language=MlmUse\n");
        tex.append("}\n\n");

        tex.append("\\title{CatUSE Report: ").append(escapeLatexText(displayName)).append("}\n");
        tex.append("\\author{}\n\\date{}\n\n");
        tex.append("\\begin{document}\n");
        tex.append("\\maketitle\n\n");

        tex.append("\\section{CatUSE source}\n");
        tex.append("\\begin{lstlisting}\n");
        tex.append(catUseSource);
        if (!catUseSource.endsWith("\n")) tex.append("\n");
        tex.append("\\end{lstlisting}\n\n");

        tex.append("\\section{Desugared MLM-USE}\n");
        tex.append("\\begin{lstlisting}\n");
        tex.append(mlmUseText);
        if (!mlmUseText.endsWith("\n")) tex.append("\n");
        tex.append("\\end{lstlisting}\n\n");

        tex.append("\\section{Flattened USE}\n");
        tex.append("Plain, single-level USE -- what the underlying OCL evaluator actually sees. "
                + "Every class's attributes are copied down fully resolved (cancellations already "
                + "applied), with no inheritance between them: plain USE cannot remove an inherited "
                + "feature, which is exactly why the clabject mechanism exists in the first place.\n\n");
        tex.append("\\begin{lstlisting}\n");
        tex.append(flatUseText);
        if (!flatUseText.endsWith("\n")) tex.append("\n");
        tex.append("\\end{lstlisting}\n\n");

        tex.append("\\section{Class diagram}\n");
        // Two separate \write18 calls, not one chained with "&&": inside a
        // \write18 argument, "&" still has its normal catcode (alignment
        // tab) since this isn't a verbatim environment, so a literal "&&"
        // here would raise "Misplaced alignment tab character &". Absolute
        // paths throughout: at least one common inkscape packaging (snap)
        // resolves a relative path against its own sandboxed idea of the
        // current directory rather than the shell's actual cwd.
        tex.append("\\immediate\\write18{plantuml -tsvg \"").append(absPumlPath).append("\"}\n");
        tex.append("\\immediate\\write18{inkscape \"").append(absSvgPath)
                .append("\" --export-type=pdf --export-filename=\"").append(absPdfPath).append("\"}\n");
        tex.append("\\begin{figure}[h]\n\\centering\n");
        tex.append("\\includegraphics[width=\\linewidth,height=0.85\\textheight,keepaspectratio]{")
                .append(pdfFileNameForInclude).append("}\n");
        tex.append("\\end{figure}\n\n");

        tex.append("\\subsection{PlantUML source}\n");
        tex.append("\\begin{lstlisting}[language={}]\n");
        tex.append(plantUml);
        if (!plantUml.endsWith("\n")) tex.append("\n");
        tex.append("\\end{lstlisting}\n\n");

        tex.append("\\end{document}\n");
        return tex.toString();
    }

    private static String escapeLatexText(String s) {
        return s.replace("\\", "\\textbackslash{}")
                .replace("&", "\\&").replace("%", "\\%").replace("$", "\\$")
                .replace("#", "\\#").replace("_", "\\_").replace("{", "\\{")
                .replace("}", "\\}").replace("~", "\\textasciitilde{}")
                .replace("^", "\\textasciicircum{}");
    }
}
