# Lecture 10 Content Map — State-Space Tree Search

## 원본 분석과 교정 정책

| 원본 | 핵심 내용 | 유지·통합 | 오류/혼동과 수정 | 새 frame과 교육적 목적 |
|---:|---|---|---|---|
| 1 | 상태공간 트리의 탐색 | 제목 유지 | 영문 제목·범위 누락 | 제목 — 강의 범위 명시 |
| 2 | state-space tree, 세 탐색 기법 | 정의 유지 | node가 “중간 상태”에만 한정됨 | 상태·결정·트리 / Candidate–Feasible–Optimal — 용어 분리 |
| 3 | Backtracking은 깊게 갔다가 복귀 | maze, queens, coloring 유지 | Backtracking을 DFS의 동의어로 표현 | 탐색 순서와 pruning 목적 분리 |
| 4 | `pick` 코드 | `pick(5,...,4)` 의도를 \(5P_4\)로 보존 | `arr` 미선언, 중복 loop index/continue 오류, 불명확한 `lastIndex`, indexing·출력 조건 불명 | 원본 `pick` 진단 / 0-based `ChoosePermutation` — 올바른 생성기 |
| 5 | 순열 상태공간 트리 | 결정 sequence 유지 | 지나치게 큰 명시적 트리, parent/level 불명확 | \(5P_4\) partial tree / permutation–combination 비교 |
| 6 | root-to-leaf candidate, 불필요한 descendants | candidate 개념과 비효율 유지 | candidate와 feasible 혼동 | Candidate–Feasible 구분 / implicit generation |
| 7 | promising, backtrack | feasibility pruning 설명 유지 | Branch-and-Bound 제목 아래 Backtracking 정의 | Promising predicate / sound pruning |
| 8 | DFS, promising, pruning 절차 | Backtracking 절차로 이동 | Branch-and-Bound를 DFS로 정의, incumbent/bound 없음 | Backtracking skeleton / correctness proof |
| 9 | 정렬된 집합과 등차수열 prefix | 입력과 `[1,3,4]` 유지 | `[1,3,4]` 뒤 확장, 문제 정의 불명 | AP 문제 계약 / feasibility trace |
| 10 | `[1,3,5,7]`, length 4 | 결과 유지 | pruning과 incumbent 갱신 구분 없음 | incumbent update animation |
| 11 | 이후 branch pruning | “4보다 긴 해 불가” 유지 | objective bound 식 없음 | `selectedCount+(n-i)` bound / AP와 DP 비교 |

## 최종 강의 순서와 시각화

내부 section 파일은 유지하되, 강의 중 보이는 divider는 다음 6개 Part로 통합한다.

1. **Part A — State-Space Search Fundamentals**: 학습 목표, 상태/결정/해의 종류, implicit tree, DFS/BFS/best-first, 순열과 조합.
2. **Part B — Backtracking**: Apply–Promising–Recurse–Undo, sound pruning, N-Queens, Subset Sum, Graph Coloring, 선택 심화 AP 예제.
3. **Part C — Bounding and Optimization**: Backtracking과 Branch-and-Bound의 목적·증거 분리, incumbent, maximization UB와 minimization LB의 안전 조건.
4. **Part D — 0/1 Knapsack Branch-and-Bound**: fractional-relaxation UB, partial feasible state의 incumbent 갱신, deterministic max-PQ trace, 선택 심화 FIFO/LIFO/least-cost 정책.
5. **Part E — A\***: nonnegative cost와 consistent heuristic에서 permanent CLOSED, non-stale goal extract 종료, \(g\) snapshot 기반 stale-entry 검사, 올바른 candidate-path consistency 식. Inconsistent heuristic/reopening은 Advanced로 구분한다.
6. **Part F — Comparison, Implementation, Summary, and Quiz**: 공통 탐색 pipeline, 비교, 재현 가능한 metric, Java/C, validation, summary/quiz, appendix.

공유 의존성은 `theme/beamerthemealgorithms.sty`와
`common/state_space.tex`만 사용한다. Lecture 10은 다른 lecture
디렉터리의 `common.tex`를 더 이상 입력하지 않는다.

## Animation 계획

- State-space expansion 4단계; DFS/BFS 4단계; permutation 4단계.
- Apply/Undo 4단계; N-Queens 6단계; subset sum 5단계; coloring 4단계.
- AP feasibility/incumbent/bound 5단계.
- Knapsack root/children/PQ/incumbent/prune/final 7단계.
- A* OPEN/CLOSED 6단계; path reconstruction 3단계; Dijkstra 대비 3단계.

모든 tree는 좌표를 고정하고 미래 child를 미리 회색으로 표시하지 않는다. 잘린 subtree는 `P` 삼각형과 이유를 함께 표시한다.
