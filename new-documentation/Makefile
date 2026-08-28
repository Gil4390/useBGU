MANUAL  := mlm-use-extensions-manual
CHAPTER := mlm-use-extensions-manual-chapter

EMACS   := emacs
LATEXMK := latexmk

.PHONY: all manual chapter clean distclean
.PRECIOUS: $(MANUAL).tex $(CHAPTER).tex

all: manual

manual: $(MANUAL).pdf

chapter: $(CHAPTER).pdf

%.tex: %.org
	$(EMACS) --batch -Q \
		--eval "(require 'ox-latex)" \
		--eval "(setq org-latex-listings t)" \
		--eval "(setq org-latex-listings-langs '((use \"MLMUSE\") (mlmuse \"MLMUSE\") (catmlm \"CatMLM\") (ebnf \"EBNF\") (sh \"ShellSession\") (shell \"ShellSession\")))" \
		--eval "(find-file \"$<\")" \
		--eval "(org-latex-export-to-latex)"

%.pdf: %.tex
	$(LATEXMK) -pdf -interaction=nonstopmode -halt-on-error $<

clean:
	$(LATEXMK) -c $(MANUAL).tex 2>/dev/null || true
	$(LATEXMK) -c $(CHAPTER).tex 2>/dev/null || true
	rm -f $(MANUAL).bbl $(CHAPTER).bbl $(MANUAL).ind $(MANUAL).ilg $(CHAPTER).ind $(CHAPTER).ilg

distclean: clean
	rm -f $(MANUAL).tex $(MANUAL).pdf $(CHAPTER).tex $(CHAPTER).pdf
