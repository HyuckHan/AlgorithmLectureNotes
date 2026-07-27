# Algorithm Lecture Notes

학부 컴퓨터공학과 학생을 위한 알고리즘 강의노트입니다. 각 강의는
LaTeX Beamer로 작성되어 있으며 발표용 PDF와 handout을 생성할 수
있습니다.

## 필요 도구

- TeX Live와 `latexmk`
- LuaLaTeX 및 XeLaTeX
- Noto Sans, Noto Sans CJK KR, Noto Sans Mono 글꼴
- Metropolis Beamer theme
- 결합 PDF 생성 시 Poppler의 `pdfunite`

## 빌드

저장소 루트에서 실행합니다.

```bash
# 전체 강의와 지원되는 handout, 통합 PDF 빌드
make all

# 개별 강의
make lecture10

# 개별 handout
make lecture10-handout

# Lecture 01–10 발표본을 하나의 PDF로 결합
make combined10
```

`latexmk`가 필요한 횟수만큼 LaTeX를 반복 실행하여 참조와 전체 frame
수를 안정화합니다. 생성된 PDF와 보조 파일은 `build/`에 저장됩니다.

예:

```text
build/lecture10.pdf
build/lecture10_handout.pdf
build/lectures01-10.pdf
```

사용 가능한 개별 target과 handout 범위는 [Makefile](Makefile)을
참고하십시오.

## 정리

```bash
make clean
```

## 주요 구조

```text
lecture01/ ... lecture10/   강의별 TeX 소스
common/                     공용 강의 매크로
theme/                      공용 Beamer theme
code/                       Java/C 예제와 테스트
docs/                       콘텐츠 맵과 검증 문서
build/                      생성된 PDF와 빌드 파일
```
