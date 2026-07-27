# Lecture 5 콘텐츠 맵 — Dynamic Programming

## 재구성 원칙

- 원본 46개 슬라이드의 Fibonacci, 행렬 최소 경로 합, LCS, Maximum Sum Interval을 모두 유지한다.
- `F(0)=0, F(1)=1` convention과 1-based 수학 state, 0-based C/Java 배열을 일관되게 구분한다.
- recurrence, memoization, bottom-up tabulation을 서로 다른 개념 층으로 설명한다.
- 원본의 잘못된 base case, unreachable branch, `min`/`max`, 반환 인덱스, 비교 연산자 오류를 교정한다.
- 길이·합 계산에서 끝내지 않고 Matrix Path, LCS, Maximum Subarray의 실제 해를 복원한다.
- 기존 section 파일은 유지하되 visible Part를 12개에서 5개로 통합한다.
- 모든 예제는 `State → Transition → Base Case → Evaluation Order → Answer` template로 설명한다.
- 복잡도는 가능한 경우 `computed states × work per state`로 유도한다.
- 총 85개 conceptual frame이며 section divider와 overlay page는 별도로 센다.

## 원본 슬라이드 대응

| 원본 | 핵심 내용 | 유지·통합 | 오류 수정·추가 | 새 파트 |
|---:|---|---|---|---|
| 1–2 | 제목, motivation | 제목과 질문 중심 도입 | recurrence와 평가 전략 구분, road map | A |
| 3 | Fibonacci recursion tree | 중복 호출과 재귀 코드 | `F(0),F(1)` 통일, partial call tree 표시, 복잡도 | B |
| 4–6 | memoization, bottom-up, 비교 | cache와 반복 계산 | 오탈자 교정, sentinel, rolling state, 정확한 비교표 | C |
| 7 | Basic Example | DP 전환점 | 공통 5단계 design template와 engineering checks | B |
| 8–14 | 행렬과 recurrence 직관 | 4×4 입력, right/down, 최종 40 | state/base case/경계 명시 | C |
| 15–17 | recursive/memo code | top-down 구조와 중복 call | `i==1 || j==1` 오류와 unreachable branch 제거 | E |
| 18–20 | recurrence, bottom-up table/code | 완성 DP 표 | 첫 row/column 누적합, path reconstruction, 1-row DP | E |
| 21–26 | optimal substructure, D&C 비교 | 응용 전에 핵심 정의 배치 | overlap 분리, recursion tree vs DAG, greedy 조건 | B |
| 27–33 | LCS 정의·recurrence | 문자열과 두 case | substring/subsequence 검증, 복수 LCS | G |
| 34–38 | recursion, call tree, table code | naive 중복과 bottom-up | equality/type, `max`, `dp[m][n]` 교정 | G |
| 37–38 | ABCBDAB/BDCABA table | 예제 유지 | 전체 표 검증, `BCBA` backtracking, row compression | H |
| 39–46 | Maximum Sum Interval | nonempty 최대 연속 구간, recurrence | O(n²) 경계, Kadane 구간 복원, all-negative | I |

## 최종 visible Part와 교육적 목적

| 파트 | conceptual frames | 교육적 목적 | 주요 시각화 |
|---|---:|---|---|
| A. Why Dynamic Programming? | 13 | motivation과 Fibonacci로 중복 state를 발견 | roadmap, partial call tree |
| B. Memoization, Tabulation, and DP Workflow | 17 | 구현 방식, 공통 template, DP 성립 조건을 응용 전에 확립 | cache/table progression, workflow |
| C. Representative DP Problems | 25 | Matrix Path와 LCS에 동일 template 적용 | dependency arrows, DP tables |
| D. Advanced DP and Reconstruction | 14 | LCS 복원·공간 tradeoff와 Kadane의 state/answer 분리 | backtracking path, running trace |
| E. Comparison, Summary, and Quiz | 13 | 네 예제의 설계를 비교하고 오개념 진단 | design comparison, concept map, answer frames |
| Appendix | 2 + transition | Hirschberg와 D&C Maximum Subarray preview | compact comparison |

## 정확성 교정

- Fibonacci: `F(0)=0`, `F(1)=1`; memoized `Θ(n)`, rolling auxiliary `Θ(1)`.
- Matrix Path: 첫 row/column은 누적합이며 완성 표의 우하단은 40이다.
- Matrix Path 복원: `(1,1)→(2,1)→(2,2)→(2,3)→(3,3)→(3,4)→(4,4)`, 합 40.
- LCS: mismatch transition은 `max`, 답은 `dp[m][n]`; 예제 길이는 4이고 tie-up convention으로 `BCBA`.
- Maximum Subarray: nonempty convention; 교과서 예제 답은 `[4,-1,2,1]`, 합 6; all-negative 답은 -2.
- LCS 수학 표기는 1-based prefix, C/Java 문자열 index는 0-based로 명시적으로 연결한다.
- `bestEndingAt[i]`는 현재 index에서 끝나는 state이고 `bestOverall`이 최종 answer임을 분리한다.

## 애니메이션·코드 계획

- Overlay: memo cache hit, bottom-up progression, matrix row progression, LCS table progression. Handout은 최종 state만 표시한다.
- TikZ: recursion tree, dependency DAG, DP matrix, reconstruction arrow, active interval.
- 코드: C와 Java 각각 Fibonacci, Matrix Path, LCS, Maximum Subarray. 모든 파일에 assertions와 invalid/boundary/representative test를 둔다.
