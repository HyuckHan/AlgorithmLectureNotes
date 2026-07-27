.PHONY: all lecture01 lecture01-handout lecture02 lecture02-handout lecture03 lecture03-handout lecture04 lecture04-handout lecture05 lecture05-handout lecture06 lecture06-handout lecture07 lecture07-handout lecture08 lecture09 lecture09-handout lecture10 lecture10-handout combined combined10 combined09 combined08 combined06 combined05 combined04 combined03 clean
all: lecture01 lecture01-handout lecture02 lecture02-handout lecture03 lecture03-handout lecture04 lecture04-handout lecture05 lecture05-handout lecture06 lecture06-handout lecture07 lecture07-handout lecture08 lecture09 lecture09-handout lecture10 lecture10-handout combined
lecture01:
	latexmk -lualatex lecture01/lecture01.tex
lecture01-handout:
	latexmk -lualatex lecture01/lecture01_handout.tex
lecture02:
	latexmk -xelatex lecture02/lecture02.tex
lecture02-handout:
	latexmk -xelatex lecture02/lecture02_handout.tex
lecture03:
	latexmk -lualatex lecture03/lecture03.tex
lecture03-handout:
	latexmk -lualatex lecture03/lecture03_handout.tex
lecture04:
	latexmk -lualatex lecture04/lecture04.tex
lecture04-handout:
	latexmk -lualatex lecture04/lecture04_handout.tex
lecture05:
	latexmk -lualatex lecture05/lecture05.tex
lecture05-handout:
	latexmk -lualatex lecture05/lecture05_handout.tex
lecture06:
	latexmk -lualatex lecture06/lecture06.tex
lecture06-handout:
	latexmk -lualatex lecture06/lecture06_handout.tex
lecture07:
	latexmk -lualatex lecture07/lecture07.tex

lecture07-handout:
	latexmk -lualatex lecture07/lecture07_handout.tex
lecture08:
	latexmk -lualatex lecture08/lecture08.tex
lecture09:
	latexmk -lualatex lecture09/lecture09.tex
lecture09-handout:
	latexmk -lualatex lecture09/lecture09_handout.tex
lecture10:
	latexmk -lualatex lecture10/lecture10.tex
lecture10-handout:
	latexmk -lualatex lecture10/lecture10_handout.tex
combined03: lecture01 lecture02 lecture03
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lectures01-03.pdf
combined04: lecture01 lecture02 lecture03 lecture04
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lecture04.pdf build/lectures01-04.pdf
combined05: lecture01 lecture02 lecture03 lecture04 lecture05
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lecture04.pdf build/lecture05.pdf build/lectures01-05.pdf
combined06: lecture01 lecture02 lecture03 lecture04 lecture05 lecture06
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lecture04.pdf build/lecture05.pdf build/lecture06.pdf build/lectures01-06.pdf
combined08: lecture01 lecture02 lecture03 lecture04 lecture05 lecture06 lecture07 lecture08
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lecture04.pdf build/lecture05.pdf build/lecture06.pdf build/lecture07.pdf build/lecture08.pdf build/lectures01-08.pdf
combined09: lecture01 lecture02 lecture03 lecture04 lecture05 lecture06 lecture07 lecture08 lecture09
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lecture04.pdf build/lecture05.pdf build/lecture06.pdf build/lecture07.pdf build/lecture08.pdf build/lecture09.pdf build/lectures01-09.pdf
combined10: lecture01 lecture02 lecture03 lecture04 lecture05 lecture06 lecture07 lecture08 lecture09 lecture10
	pdfunite build/lecture01.pdf build/lecture02.pdf build/lecture03.pdf build/lecture04.pdf build/lecture05.pdf build/lecture06.pdf build/lecture07.pdf build/lecture08.pdf build/lecture09.pdf build/lecture10.pdf build/lectures01-10.pdf
combined: combined10
clean:
	latexmk -C lecture01/lecture01.tex
	latexmk -C lecture01/lecture01_handout.tex
	latexmk -C lecture02/lecture02.tex
	latexmk -C lecture02/lecture02_handout.tex
	latexmk -C lecture03/lecture03.tex
	latexmk -C lecture03/lecture03_handout.tex
	latexmk -C lecture04/lecture04.tex
	latexmk -C lecture04/lecture04_handout.tex
	latexmk -C lecture05/lecture05.tex
	latexmk -C lecture05/lecture05_handout.tex
	latexmk -C lecture06/lecture06.tex
	latexmk -C lecture06/lecture06_handout.tex
	latexmk -C lecture07/lecture07.tex
	latexmk -C lecture07/lecture07_handout.tex
	latexmk -C lecture08/lecture08.tex
	latexmk -C lecture09/lecture09.tex
	latexmk -C lecture09/lecture09_handout.tex
	latexmk -C lecture10/lecture10.tex
	latexmk -C lecture10/lecture10_handout.tex
