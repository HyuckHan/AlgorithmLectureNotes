# Lecture 8 Content Map — Graph Algorithms

## 재구성 원칙

- 원본 123장은 graph 표현, BFS/DFS, 두 위상정렬, Prim/Kruskal, Dijkstra, Bellman–Ford를 포함한다. 새 강의는 이를 18개 Part로 재구성하고 모든 도형·표·수식을 편집 가능한 LaTeX/TikZ로 다시 만든다.
- 원본의 반복 애니메이션은 queue, recursion stack, indegree, heap, DSU, distance table의 의미 있는 상태만 남긴다. 예제의 adjacency/edge tie order를 명시해 손 추적 결과를 재현 가능하게 한다.
- 복잡도는 representation과 자료구조 조건을 함께 말한다. MST와 shortest-path tree, Prim key와 Dijkstra dist, zero-weight edge와 no-edge를 끝까지 분리한다.

## 원본 슬라이드 대응

| 원본 | 원본 핵심 내용 | 유지·통합·축약 | 오류·불명확성 교정 | 새 frame과 교육적 목적 | 필요한 시각화 |
|---:|---|---|---|---|---|
| 1–6 | graph 정의와 종류, 친분 graph | modern network 예제로 통합 | directed/undirected edge 표기, weight 의미 명시 | Graph 정의; 종류; 용어; 경로·cycle; degree | directed/undirected TikZ graph |
| 7–16 | adjacency matrix/list와 예제 | 10장을 7개 frame으로 통합 | zero edge와 no-edge 분리, matrix orientation 명시 | Matrix; weighted matrix; List; 변환; 비교 | matrix/list overlay |
| 17–20 | traversal, DFS/BFS pseudocode | 순서를 BFS 뒤 DFS로 재배치 | BFS s/v 혼용, representation 없는 복잡도 교정 | traversal 목적; 정확한 pseudocode | queue/stack legend |
| 21–29 | BFS 8단계 | 7개 의미 상태로 통합 | dequeue 전 vertex는 Output에 넣지 않고 마지막 7 처리 상태를 분리 | BFS trace/layers/order/path/forest | 고정 graph, queue, dist/parent |
| 30–38 | DFS 9단계 | discovery/backtrack 중심 6단계 | finish 뒤 pop된 stack 상태를 표시하고 exact iterative simulation은 frame stack으로 구분 | DFS trace/forest/iterative/complexity | recursion stack, d/f labels |
| 39–49 | topo 정의와 두 알고리즘 | Kahn과 DFS topo로 명명 | queue/stack \(\Theta(V+E)\)와 min-heap \(O(E+V\log V)\)를 분리 | topo 정의; Kahn; DFS topo; 비교 | indegree table, zero heap |
| 42–48 | Kahn 라면 trace | 5단계로 통합 | processed count 검증 추가 | Kahn animation/cycle | graph+indegree+output |
| 50–68 | DFS topo 반복 trace | finish-order reverse 5단계 | 3-color back-edge cycle 추가 | DFS topo animation | stack/finish list |
| 69–76 | spanning tree, 응용, greedy | 응용 축약, coin 반례 교체 | disconnected→MSF, greedy proof 필요 | spanning/MST; greedy; coin 반례 | tree comparison, coins |
| 77–86 | Prim과 A–G trace | verified A–G graph 6단계 | parent 기록, key 의미, 조건별 복잡도 | Prim pseudocode/trace/analysis | graph, key/parent, PQ |
| 87–96 | Kruskal과 A–G trace | accept/reject 7단계 | `T∪{(u,v)}`, disconnected 처리, DSU 추가 | Kruskal/DSU/trace/analysis | edge list, component colors |
| 97 | MST proof | exchange argument로 확장 | `<`를 `≤`로 교정해 tie 포함 | Cut property; exchange proof | cut line, cycle exchange |
| 98–100 | shortest path 종류와 relaxation | 유지·확장 | reachable negative cycle만 영향, INF guard | SP 정의; 종류; Relax | candidate update overlay |
| 101–113 | Dijkstra와 12단계 trace | 6단계로 통합 | finalized+stale-entry convention으로 negative counterexample와 일치 | Dijkstra/trace/proof/failure | PQ, dist/parent |
| 114–122 | Bellman–Ford와 List trace | 표준 full-pass 5단계로 교체 | SPFA 혼동 제거, cycle pass와 guard 추가 | BF/trace/V−1/cycle | edge-order pass table |
| 123 | Bellman–Ford DP | 유지·교정 | `D_{k-1}(v)` 항 추가, synchronous DP와 in-place 구분 | DP recurrence | layered DP table |
| — | DAG shortest path | 신규 | Lecture 5 DP와 topo order 연결 | DAG SP | topo relaxation |
| — | 구현·검증 | 신규 | — | Java/C API, invariants, test report | code/API tables |
| — | 종합 선택 | 신규 | — | algorithm chooser; MST vs SPT; quiz | comparison tables |

## 새 Beamer 순서와 목적

1. **Part A. Graph란 무엇인가?** — 정의, 종류, 경로·cycle·degree 용어를 통일한다.
2. **Part B. Graph Representation** — matrix/list의 의미와 비용을 representation별로 비교한다.
3. **Part C. BFS** — visited-on-enqueue, layer, shortest edge-count path를 추적한다.
4. **Part D. DFS** — recursion stack, discovery/finish, forest를 분리한다.
5. **Part E. DFS 활용과 Cycle Detection** — GRAY back edge와 undirected parent 예외를 이해한다.
6. **Part F. Topological Sort** — Kahn/DFS 방식과 각 cycle 검출을 수행한다.
7. **Part G. Minimum Spanning Tree** — spanning tree, MST, MSF, SPT 차이를 정의한다.
8. **Part H. Greedy와 Cut Property** — safe edge를 exchange argument로 정당화한다.
9. **Part I. Prim Algorithm** — vertex key/parent와 priority queue를 추적한다.
10. **Part J. Kruskal과 Disjoint Set** — sorted edge와 DSU accept/reject를 추적한다.
11. **Part K. MST Correctness와 비교** — Prim/Kruskal, uniqueness, MST/SPT 차이를 종합한다.
12. **Part L. Shortest Paths와 Relaxation** — distance, INF guard, predecessor invariant를 정의한다.
13. **Part M. Unweighted·DAG Shortest Paths** — BFS와 topo relaxation의 적용 조건을 구분한다.
14. **Part N. Dijkstra Algorithm** — nonnegative 전제와 finalized state를 이해한다.
15. **Part O. Bellman–Ford Algorithm** — full pass, early exit, reachable negative cycle을 검출한다.
16. **Part P. 알고리즘 선택과 종합 비교** — graph 조건에 맞는 알고리즘을 선택한다.
17. **Part Q. Summary와 Quiz** — representation, traversal, DAG, MST, SP를 종합 평가한다.
18. **Part R. Appendix** — edge classification, DP recurrence, 구현 invariant를 확장한다.

## 검증 계획

- 고정 예제의 matrix/list, BFS order/dist/parent, DFS d/f time, topo validity를 자동 검증한다.
- A–G weighted graph에서 Prim/Kruskal의 선택 edge와 total weight가 일치하는지 검사한다.
- Dijkstra의 dist/parent, Bellman–Ford의 pass 결과와 reachable negative-cycle 검출을 검사한다.
- Java/C 구현은 bounds, duplicate policy, zero weight, ownership, overflow guard, path reconstruction을 포함해 strict compiler option과 deterministic small-oracle test로 확인한다.
