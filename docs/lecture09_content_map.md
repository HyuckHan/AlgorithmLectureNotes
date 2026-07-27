# Lecture 9 Content Map — String Matching

## 분석·재구성 원칙

- 원본 45장은 Naive, Rabin–Karp, border/KMP, Boyer–Moore–Horspool의 핵심 흐름을 유지한다.
- 구현·수식·trace는 모두 0-based indexing으로 재작성한다. `T[0..n-1]`, `P[0..m-1]`, valid shift `0 <= s <= n-m`을 공통 convention으로 쓴다.
- 원본의 반복적인 KMP/Border 애니메이션은 의미 있는 fallback과 preprocessing 상태만 overlay로 통합한다.
- hash equality는 candidate일 뿐이며 exact verification 뒤에만 occurrence로 확정한다.
- standard LPS와 `failure[0]=-1` sentinel convention을 분리한다. 본문과 구현은 standard LPS만 사용한다.
- 모든 문자·표·수식·alignment는 편집 가능한 LaTeX/TikZ이며 원본 PPT/PDF 페이지를 삽입하지 않는다.

## 원본 슬라이드별 대응

| 원본 | 원본 핵심 | 유지 | 통합·축약 | 오류·모호성 교정 | 새 frame | 교육적 목적·시각화 |
|---:|---|---|---|---|---|---|
| 1 | String Matching 제목 | 제목 | — | 한·영 제목과 부제 명시 | 문자열 매칭 | 첫 physical page |
| 2 | text/pattern과 포함 여부 | 문제 설정 | API/출력 유형 추가 | 1-based를 0-based로 교정 | Text·Pattern·Alphabet; Exact Occurrence | index와 shift 정의 |
| 3 | Naive pseudocode, O(mn) | 모든 alignment 검사 | best/worst 분석 분리 | `n-m+1`, empty/m>n 정책 | Naive Pseudocode; Complexity | 정확한 반복 범위 |
| 4 | Naive 동작 trace | alignment 이동 | 12단계를 4 overlay로 축약 | match/mismatch 표식 추가 | Naive Alignment Trace | 고정 character row |
| 5 | 반복 prefix의 비효율 | 중복 비교 | 작은 `A...AB` 예제로 단순화 | comparison 수 설명 | Naive 최악 예 | repeated-prefix overlay |
| 6 | numeric encoding | alphabet/base 아이디어 | Horner와 분리 | `cad=28` 오류를 **53**으로 수정 | Numeric Encoding | place-value overlay |
| 7 | nested value와 rolling 식 | rolling update | 산술을 3단계로 통합 | 0-based leading/trailing index | Horner; Rolling Update | leaving/entering 화살표 |
| 8 | `eeaab` raw-value 예 | pattern/window value | 긴 산술을 초기/roll/candidate로 분리 | 모든 값 script 검증 | 고정 RK 예제; Hash Trace | hash table |
| 9 | basic RK, O(n) | rolling scan | 완전 RK와 통합 | hash-only match를 불완전 exact matcher로 명시 | Hash Equality만으로 부족 | H/V/C badge |
| 10 | modulus 필요 | bounded hash 동기 | negative modulo 추가 | modulus는 stored value를 제한하지만 intermediate overflow는 구현이 별도로 방지 | 왜 Modulus; Negative Remainder | 수식·경고 |
| 11 | q=113 예 | modular trace | 12개 hash를 한 표로 통합 | 값 `17,87,...,63,...` 검증 | 검증된 Hash Trace | 안정된 표 |
| 12 | RK pseudocode, 평균 O(n) | modular RK | exact verification 유지 | `Θ(n+m+(z+c)m)`; verification 총비용 가정 아래 expected `Θ(n+m)`, worst `Θ(nm)` | RK Pseudocode; Complexity | candidate/verification |
| 13 | Naive 재시작 | KMP motivation | 14와 통합 | preprocessing 정보 재사용 강조 | 같은 Text Character; KMP가 아는 것 | 비교표 |
| 14 | 실패 상태 준비 | KMP 핵심 | 13과 통합 | text index no-rewind와 matched suffix=pattern prefix 정렬 명시 | Mismatch Fallback | 고정 text 위 pattern 이동 overlay |
| 15 | BAABABAA border | prefix/suffix | 정의 slide와 예제 분리 | proper prefix/empty border convention | Prefix·Suffix; BAABABAA Border | bracket/수식 |
| 16 | border 적용 도입 | matched prefix relation | 17–18과 통합 | 실제 equality를 index와 함께 설명 | KMP가 이미 아는 것 | matched-prefix relation |
| 17 | brute-force 재시작 | 대비 | 16–18로 통합 | 잘못된 비교 label 제거 | 두 재사용 전략 | Naive/KMP 대비 |
| 18 | max border로 이동 | 핵심 fallback | 대표 4 overlay로 축약 | `j=lps[j-1]`, same `i` | KMP Mismatch Recovery | old/new j |
| 19 | border table 전체 | BAABABAA 값 | standard table로 재작성 | length-0 `-1`은 별도 convention | Standard LPS Table | 0-based table |
| 20 | KMP trace 1 | text scan | 20–29를 5 overlay로 통합 | 실제 문자·index 자동 검증 | KMP Trace Setup/Recovery | i/j state |
| 21 | KMP trace 2 | 같은 흐름 | 20–29 통합 | invalid equality 문구 제거 | KMP Recovery | fallback |
| 22 | KMP trace 3 | 같은 흐름 | 20–29 통합 | index/value 구분 | KMP Recovery | fallback |
| 23 | KMP trace 4 | 같은 흐름 | 20–29 통합 | index/value 구분 | KMP Recovery | fallback |
| 24 | KMP trace 5 | 같은 흐름 | 20–29 통합 | actual character comparison | KMP Recovery | fixed rows |
| 25 | KMP trace 6 | 같은 흐름 | 20–29 통합 | `j=0` mismatch 규칙 | KMP Recovery | state label |
| 26 | KMP trace continuation | 검색 진행 | 20–29 통합 | no-match/termination 정책 | Part L Checkpoint | trace answer |
| 27 | second KMP mismatch | multi-fallback | 대표 mismatch에 통합 | fallback chain 검증 | KMP Mismatch Recovery | 5 overlays |
| 28 | fallback continuation | multi-fallback | 대표 mismatch에 통합 | same text char 재비교 허용 | Text Index no-rewind | statement |
| 29 | search continuation | progress | 대표 mismatch에 통합 | excessive “once” claim 제거 | KMP Complexity | potential intuition |
| 30 | sentinel KMP C code | search implementation | appendix convention으로 이동 | empty/no-match/overlap 추가 | KMP Search Pseudocode | standard LPS |
| 31 | border table initial | preprocessing | 31–39를 6 overlay로 통합 | unsafe `table[1]` 제거 | BuildLPS; LPS Trace | no future cells |
| 32 | first border entry | preprocessing | 31–39 통합 | standard `lps[0]=0` | LPS Trace | assigned prefix |
| 33 | next entry | preprocessing | 31–39 통합 | prefix length/index 구분 | LPS Trace | state |
| 34 | next entry | preprocessing | 31–39 통합 | mismatch rule 명시 | LPS Trace | state |
| 35 | first match | preprocessing | 31–39 통합 | `len` invariant 추가 | LPS Trace | match marker |
| 36 | next match | preprocessing | 31–39 통합 | verified LPS value | LPS Trace | match marker |
| 37 | mismatch/fallback | preprocessing | 핵심 fallback state 유지 | `i` 유지 이유 설명 | Mismatch에서 i 유지 | fallback |
| 38 | match after fallback | preprocessing | 31–39 통합 | standard value 2 | LPS Trace | assigned table |
| 39 | final entry | preprocessing | 31–39 통합 | final value 3 검증 | LPS Trace | final array |
| 40 | sentinel preprocess C | O(m) | appendix comparison으로 축약 | `pattern_len<2` 안전, standard LPS 구현 | BuildLPS; Sentinel Appendix | pseudocode |
| 41 | Boyer–Moore motivation | right-to-left idea | family 소개로 확장 | full BM vs Horspool 분리 | BM Family; 오른쪽부터 비교 | comparison direction |
| 42 | absent `b`, 5 jump | 핵심 예제 | 2 overlay | 실제 shift table lookup | Pattern에 없는 Character | skipped region |
| 43 | `i`, 3 jump | 핵심 예제 | 계산식을 간결화 | `shift[I]=3` 검증 | Pattern 안쪽 Character | alignment shift |
| 44 | TIGER/RATIONAL jump table | preprocessing | repeated A overwrite overlay | RATIONAL 최종 `R7,A1,T5,I4,O3,N2,L8` 검증 | TIGER Shift; RATIONAL | lookup table |
| 45 | BMH pseudocode, O(mn) | search structure | API/overlap/space 추가 | match/mismatch 뒤 공통 shift를 명확히 하고 all-match trace로 교정 | Horspool Pseudocode; Trace; Complexity | RTL comparison |

## 새로 추가한 교육적 내용

- empty pattern, `m > n`, first/all-match API 정책
- alignment 수와 character-comparison 수의 분리
- candidate/valid/spurious hit 용어와 exact verification
- expected `Θ(n+m)` 대 worst `Θ(nm)`의 가정
- negative remainder 및 overflow-safe modular arithmetic
- standard LPS와 sentinel failure table의 명시적 분리
- KMP overlapping occurrence와 match 후 fallback
- full Boyer–Moore와 Horspool의 구조적 차이
- dense alphabet table과 map preprocessing 비용
- Java UTF-16 code unit, C byte, Unicode normalization
- invariant validator, strict compile, sanitizer, 6,000-case differential test
- presentation overlay PDF와 final-state handout PDF
- BuildLPS 연속 fallback chain `AAAAABAAAAAC`

## 새 Beamer 순서와 목적

1. **Part A. Exact String Matching 문제** — notation, occurrence, API 정책을 고정한다.
2. **Part B. Naive Matching** — alignment trace와 exact best/worst cost를 계산한다.
3. **Part C. 중복 비교는 왜 생기는가?** — Rabin–Karp/KMP의 서로 다른 재사용 전략을 연결한다.
4. **Part D. Rabin–Karp의 기본 아이디어** — fingerprint와 exact match를 분리한다.
5. **Part E. Rolling Hash** — Horner와 O(1) window update를 유도한다.
6. **Part F. Modular Hash와 Collision** — modulus, normalization, collision을 설명한다.
7. **Part G. Rabin–Karp 분석과 활용** — verified trace와 expected/worst 분석을 수행한다.
8. **Part H. Prefix, Suffix와 Border** — proper prefix와 LPS convention을 정의한다.
9. **Part I. KMP의 핵심 아이디어** — same text index에서 pattern fallback을 이해한다.
10. **Part J. LPS/Failure Table** — standard LPS와 legacy sentinel을 구분한다.
11. **Part K. KMP Preprocessing** — BuildLPS invariant와 fallback을 추적한다.
12. **Part L. KMP Search** — mismatch, recovery, overlap occurrence를 추적한다.
13. **Part M. KMP Correctness와 Complexity** — border argument와 `Θ(n+m)`을 설명한다.
14. **Part N. Boyer–Moore의 아이디어** — right-to-left family를 소개한다.
15. **Part O. Bad-Character Rule** — absent/present character shift를 계산한다.
16. **Part P. Boyer–Moore–Horspool** — table, trace, overlap, worst/practical cost를 검증한다.
17. **Part Q. 알고리즘 비교와 선택** — workload와 Unicode semantics에 맞게 선택한다.
18. **Part R. 구현·테스트** — Java/C API와 invariants를 연결한다.
19. **Part S. Summary와 Quiz** — 네 알고리즘과 convention을 종합 평가한다.
20. **Part T. Appendix** — sentinel, bit complexity, Unicode, multi-pattern을 확장한다.

## 검증 기준

- `value("cad", base 5)=53`
- original example: pattern raw `3001`, pattern hash `63`, window hashes
  `[17,87,65,33,91,42,63,21,39,86,94,58]`, occurrence `[6]`
- `LPS("BAABABAA")=[0,0,0,1,2,1,2,3]`
- TIGER: `T=4,I=3,G=2,E=1,R=5,other=5`
- RATIONAL: `R=7,A=1,T=5,I=4,O=3,N=2,L=8,other=8`
- Naive/Rabin–Karp/KMP/Horspool agreement on fixed and randomized cases
