# Lecture 7 Content Map — Hash Tables

## 재구성 원칙

- 원본 35장은 정의와 정적 표 중심이다. 새 강의는 모든 내용을 편집 가능한 LaTeX/TikZ로 재작성하고 계산·불변식·API 계약·보안을 보강한다.
- 원본의 `평균 O(1)`은 controlled load와 suitable hashing 아래 expected bound로 한정한다. resize 비용은 amortized, collision 집중은 worst case로 분리한다.
- `key → hash code → bucket index → entry`를 분리하고, collision과 duplicate, EMPTY와 DELETED를 끝까지 구분한다.
- Lecture 6의 dynamic-set 비교를 출발점으로 삼되 BST CPU cost와 B-Tree page I/O를 섞지 않는다.

## 원본 슬라이드 대응

| 원본 | 핵심 내용 | 유지·통합·축약 | 오류·낡은 표현 교정 | 새 frame / 교육적 목적 | 시각화·코드 |
|---:|---|---|---|---|---|
| 1 | Hash Table 제목 | 유지 | 한글 중심 제목과 부제 추가 | 해시 테이블; Part A | 편집 가능한 title |
| 2–3 | 자료구조 비용, 활용 | 통합 | 무조건 평균 O(1), B-Tree 상수 인자 표현 제거 | Workload; 구조 비교; Hash Table의 약속과 한계 | 비교표 |
| 4, 7–8 | 주소 계산, key space | 통합 | 값이 저장 자리를 “결정”한다는 표현 완화 | Hashing pipeline; 추상 모델 | U/K/m diagram |
| 5, 19 | m=13 정수 예제, collision | 유지·연결 | collision과 duplicate 구분 | 원본 정수 예제; INSERT 29 | TikZ table overlay |
| 6 | 구조 소개 | 축약 | 고정 크기만을 본질로 보지 않음 | Direct addressing에서 hashing으로 | dense/sparse 비교 |
| 9 | insert/delete/search | 개념 유지 | `null` 삭제를 open addressing에 일반화하지 않음 | Map semantics; operation contract | key-value entry |
| 10–13 | 좋은 함수, division, multiplication | 유지·확장 | prime 절대 규칙, 깨진 multiplication 식 교정 | 좋은 함수; Division; Multiplication trace | 수식 overlay |
| 14–17 | 문자열 hash, Java hashCode | 유지·통합 | Java hash code와 table index 분리 | ASCII sum; prefix loss; Horner; equals/hashCode | Horner trace, Java code |
| 18, 20 | collision resolution | 유지 | open addressing “추가 공간 없음” 교정 | Collision overview; chaining vs OA | 두 구조 비교 |
| 21–22 | chaining 예제와 분석 | 예제 단순화 | `M≈N/10` 일반 규칙 제거 | Chaining operations/animation/analysis | bucket-chain TikZ, pseudocode |
| 23 | open addressing | 유지·확장 | slot state와 termination 조건 추가 | Probe model; EMPTY/OCCUPIED/DELETED | state legend |
| 24–25 | linear probing, primary clustering | 유지·확장 | 자기 강화와 wrap-around 설명 | Linear trace/final table/clustering/cost | stable array overlay, PGFPlots |
| 26–27 | quadratic, secondary clustering | 유지·교정 | 식의 계수 순서 통일, coverage 조건과 정의 교정 | Quadratic trace; coverage; clustering 비교 | offset table |
| 28 | double hashing | 유지·검증 | `gcd(h2,m)=1` 조건 추가 | Double-hash trace; full-cycle condition | probe table |
| 29 | 삭제 표시 | 유지·확장 | EMPTY와 DELETED를 문자·패턴으로 구분 | 잘못된 삭제; tombstone 수정; reuse | 6-state overlay |
| 30 | load factor | 유지·분리 | chaining과 OA의 α 의미 구분 | Load Factor; 두 scheme 비교 | load gauges |
| 31–35 | resize/rehash/amortization | 통합·확장 | 0.5 universal threshold, 단순 copy 오해 제거 | Resize vs Rehash; migration; geometric series | old/new tables, cost bars |
| — | 분석 모델 | 신규 | average/expected/amortized/worst 혼용 방지 | Simple uniform hashing; 네 비용 개념 | terminology matrix |
| — | Java contract | 신규 | mutable key 위험 추가 | equals/hashCode; mutable-key failure | custom key code |
| — | 구현·검증 | 신규 | — | API contract; validators; test strategy | Java/C source frames |
| — | 보안 | 신규 | adversarial input 누락 보완 | Hash Flooding | attack/mitigation diagram |
| — | 선택과 평가 | 신규 | — | Tree vs Hash; Summary; Quiz/answers | comparison table |

## 새 Beamer 순서와 목적

1. **Part A — 왜 Hash Table인가?**: exact-match workload와 ordered workload를 분리한다.
2. **Part B — Hashing의 기본 모델**: universe, stored keys, hash code, index, entry를 구분한다.
3. **Part C — Hash Function 설계**: 결정성·속도·분산과 분석 가정을 이해한다.
4. **Part D — Integer와 String Hashing**: division, multiplication, polynomial hash를 계산한다.
5. **Part E — Collision은 왜 생기는가?**: pigeonhole principle과 duplicate 차이를 설명한다.
6. **Part F — Separate Chaining**: map semantics 연산과 `Theta(1+alpha)` expected cost를 추적한다.
7. **Part G — Open Addressing**: probe contract와 세 slot state를 정의한다.
8. **Part H — Linear Probing**: 원본 10-key trace, wrap-around, primary clustering을 분석한다.
9. **Part I — Quadratic Probing**: offset, coverage, secondary clustering을 검증한다.
10. **Part J — Double Hashing**: step과 capacity의 서로소 조건을 확인한다.
11. **Part K — 삭제**: EMPTY 삭제의 false negative와 tombstone 해결을 보여준다.
12. **Part L — Load Factor와 Expected Cost**: scheme별 α와 expected/worst를 구분한다.
13. **Part M — Resize, Rehashing, Amortized Analysis**: 재배치와 기하급수 비용 합을 설명한다.
14. **Part N — 구현과 API Contract**: Java/C 구현, null·update·equality·invariant 정책을 명시한다.
15. **Part O — Search Tree와 비교**: equality, order, I/O, adversarial workload에 따라 선택한다.
16. **Part P — Summary와 Quiz**: 계산과 설계 판단을 종합 평가한다.
17. **Part Q — Appendix**: probe 이론식, incremental rehash, 보안·테스트 확장을 제공한다.

## 구현 산출물

- TikZ: hash-table array, bucket chain, probe arrows, clusters, tombstone, old/new rehash table.
- PGFPlots: 표시된 고전적 linear-probing 식을 직접 사용한 expected probe 곡선. arbitrary open addressing의 general uniform-hashing bound와 구분한다.
- Pseudocode: chaining SEARCH/PUT/DELETE, common open-address SEARCH/PUT/DELETE.
- Java: `ChainedHashMap`, `OpenAddressHashMap`, `HashTableDemo`.
- 검증: `MutableKeyExample` compile test와 모든 표시 probe/삭제/rehash 상태를 확인하는 `HashTableExamplesTest`.
- C: integer-key chained/open-address hash table과 자체 테스트·validator.
