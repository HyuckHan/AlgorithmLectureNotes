# Lecture 2 콘텐츠 맵 — Recursion

원본 `lecture note 2.pptx` 100장을 분석하였다. Lecture 1의 16:9 Metropolis 테마, 색상, footer와 LuaLaTeX 빌드를 재사용한다.

## 원본 → 새 강의 대응

| 원본 | 핵심 내용 | 처리·수정 | 새 슬라이드 / 목적 | 구현 |
|---:|---|---|---|---|
| 1–5 | 재귀 정의, sum, base/recursive case, mission | 번역 중복 통합; progress measure 추가 | 1–7 / 재귀 설계의 세 요소 이해 | call flow |
| 6–7 | 귀납법, correctness/efficiency | 직관 우선으로 재작성 | 15–19 / 코드와 증명 대응 | 대응 도식 |
| 8–13 | factorial 정의·코드·분석·증명 | `O(n)`을 `Θ(n)`으로 강화; base cost 명시 | 17–18, 22 / 정확성과 비용 분리 | code, table |
| 14 | power | 잘못된 dash 수정, 입력 감소 명시 | 23 / 선형 재귀 분석 | code |
| 15–18 | Fibonacci와 점화식 | 깨진 증명 제거; `Θ(φ^n)`, `O(2^n)` 구분 | 24–25, 31 / 분기 재귀·중복 인식 | overlay tree |
| 19–24 | 점화식 풀이 세 방법과 세 예제 | 수식 정돈; 강화된 귀납가정의 이유 설명 | 30–37 / 방법 선택 능력 | expansion overlay |
| 25,29 | mergesort | midpoint `q=(p+r)/2`로 수정 | 43–44 / Master Theorem 적용 | tree |
| 26–28 | Master Theorem | 표준 case 1,2,3 순서로 교정; 결과는 Θ | 38–44 / 경쟁 관점 이해 | three-case overlay |
| 30–31 | Recursive Thinking 도입 | 유지·확장 | 45 / 수학 함수 밖의 재귀 인식 | concept map |
| 32–36 | 문자열 길이/출력/역출력/이진수 | `'/0'`, 스마트 따옴표 수정; 호출 전후 구분 | 26–29 / 실행 순서 이해 | reverse overlay |
| 37–38 | recursion vs iteration, 설계 | tail recursion·explicit stack·depth 추가 | 77–80 / 선택 기준 형성 | comparison table |
| 39–40 | iterative/recursive binary search | overflow-safe midpoint; 구간 mission 명시 | 46–50 / implicit→explicit parameter | range overlay |
| 41–46 | Hanoi | `2n-1`을 `2^n-1`로 수정, `%d`→`%c` | 51–57 / 구조·점화식 연결 | TikZ pegs, overlay |
| 47–56 | 8×8 maze, decision/path 코드 | 6×6 교육 예제로 통합; row/col 일관화; backtracking 추가 | 58–65 / DFS와 choose–explore–unchoose | grid overlay |
| 57–71 | blob과 9단계 trace | 8-neighbor로 코드·설명 통일; 한 grid overlay로 통합 | 66–70 / flood fill·marking | grid overlay |
| 72–86 | power set·state-space tree | 중복 `{a,c}`/잘못된 순서 수정; 빈 집합·출력비용 추가 | 71–76 / include/exclude 모델 | TikZ tree overlay |
| 87–100 | permutation과 swap/undo | 권장 흐름의 state-space 확장으로 2장 축약 보존 | 75–76 / mutable state 복구 이해 | choose–undo code |

## 새로 추가한 교육 요소

- mission–base case–recursive case–progress measure 체크리스트
- call stack/activation record와 호출 단계·반환 단계 분리
- 코드 줄과 귀납 증명의 base/step 대응
- recurrence tree, substitution, guess-and-prove, Master Theorem 선택표
- recursion depth, stack overflow, tail-call optimization의 구현 의존성
- 미로와 blob을 DFS로 연결하고 backtracking을 choose–explore–unchoose로 정리
- power set 출력 크기 lower bound와 `Θ(n2^n)` 출력 비용
- Fibonacci 중복 부분문제와 memoization preview

## 새 순서와 교육적 목적

새 강의는 85 numbered frames로 구성한다. 각 Part는 다음 목적을 가진다.

| Part | 새 슬라이드 | 교육적 목적 | 핵심 시각화 |
|---|---:|---|---|
| A Why Recursion? | 1–7 | 재귀를 작은 동일 문제의 명세로 이해 | mission/checklist |
| B Execution | 8–14 | sum(4)의 push와 return을 stack으로 추적 | stack overlay |
| C Correctness | 15–19 | 귀납법과 재귀 코드 대응 | proof map |
| D Basic Functions | 20–29 | 대표 선형·분기 재귀의 패턴 비교 | string/Fibonacci overlays |
| E Recurrences | 30–37 | 점화식 구성과 세 풀이법 비교 | expansion/tree |
| F Master Theorem | 38–44 | 재귀 작업과 결합 작업의 경쟁 이해 | three cases, mergesort |
| G Recursive Thinking | 45–50 | explicit parameter와 구간 재귀 설계 | binary-search overlay |
| H Hanoi | 51–57 | 동일 문제 두 번+한 이동 구조와 지수 비용 연결 | peg animation/tree |
| I Maze | 58–65 | visited와 되돌리기를 통한 backtracking 이해 | 6×6 maze overlay |
| J Blob | 66–70 | 8-neighbor flood fill과 중복 방문 방지 | grid overlay |
| K Power Set | 71–76 | include/exclude state-space와 출력 하한 이해 | tree overlay, permutation |
| L Recursion vs Iteration | 77–80 | 구현 선택의 trade-off 판단 | comparison table |
| M Summary/Assessment | 81–85 | 회상·코드 추적·점화식·설계 평가 | concept map, quiz |

