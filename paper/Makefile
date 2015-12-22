NAME:=pbicc

pdf:
	-rm -f $(NAME).aux
	latexmk -pdf $(NAME)

final: pdf
	@echo '******** Did you spell-check the paper? ********'
	pdffonts $(NAME).pdf; fi

clean:
	-rm -f *aux *bbl *blg *log *.dvi pbicc.pdf
