# Lecture 4 콘텐츠 맵 — Selection and Order Statistics

## 분석·재구성 원칙

- 원본 `PPTX/lecture note 4.pptx` 14장을 모두 검토했다.
- 원본의 Quickselect 배열과 Median of Medians 핵심은 유지하되, 첫 partition에서 멈춘 예제를 정답까지 완성한다.
- 강의 pseudocode는 1-based subarray-relative rank를 사용한다. 실제 C/Java API는 0-based rank이며 주석과 슬라이드에서 변환 관계를 명시한다.
- Lomuto Quickselect 본문 예제는 distinct keys로 한정하고, duplicates는 별도 3-way partition으로 처리한다.
- 모든 배열·group·recurrence 그림은 TikZ, 모든 수식과 pseudocode는 편집 가능한 LaTeX로 작성한다.

## 원본 슬라이드 대응

| 원본 | 핵심 내용 | 유지 | 통합·수정 | 새 위치 |
|---:|---|---|---|---|
| 1 | Selection 제목 | 주제 | Lecture 1–3 디자인으로 재작성 | A1 |
| 2 | i번째 작은 수, 두 알고리즘 | selection 목표와 평균/최악 선형 알고리즘 | order statistic, rank/index, median 정의와 motivation 추가 | A2–B4 |
| 3 | 재귀 `select` pseudocode | `q`, `k=q-p+1`, 세 분기 | Quickselect/RandomizedSelect 명칭 분리, precondition과 invalid rank 명시 | D1–D5 |
| 4 | 2번째 작은 원소, 첫 partition | 원본 10원소 배열과 pivot 15 | 첫 partition 이후도 계속 추적해 답 8 도출 | E1–E2 |
| 5 | 7번째 작은 원소, 오른쪽 rank 3 | `i-k` 갱신 | 최종 답 31까지 완전 추적 | E3–E5 |
| 6–8 | pivot rank 확률과 평균 분석 | uniform rank 아이디어 | `max` recurrence의 의미를 설명하고 randomized 가정을 명시; 직관+수식 2단계로 교정 | G3, H1–H4 |
| 9 | 최악 `T(n-1)+Theta(n)` | worst `Theta(n^2)` | 정렬 입력+last pivot 예시 추가 | G4 |
| 10 | worst-case linear selection 동기 | 균형 pivot과 선형 overhead | “정확한 median의 순환성” 질문과 BFPRT 명칭 추가 | I1–I4 |
| 11 | `linearSelect` 6단계 | group 5, medians, pivot, 한쪽 재귀 | base case·불완전 group·값 기반 pivot·3-way partition을 명확화 | J1–J5 |
| 12 | 흑백 기호 기반 보장 그림 | median-of-medians 주변 보장 | 학생이 직접 셀 수 있는 25개 TikZ group 시각화로 교체 | J2–K2 |
| 13 | `3n/10-3`, `7n/10+2` | 약 `3n/10` 제거 논리 | 예외 group과 `O(1)` 상수항의 출처 설명 | K1–K4, O1 |
| 14 | BFPRT recurrence와 `O(n)` | 두 재귀항과 선형 overhead | 각 항 label, fraction 합 0.9, substitution, lower bound를 추가해 `Theta(n)`으로 교정 | L1–L4 |

## 원본 오류·불명확성 교정

- 원본의 “평균 선형” 알고리즘을 pivot 정책 없는 `select`와 혼동하지 않고, uniform random pivot인 RandomizedSelect에만 expected `Theta(n)`을 부여한다.
- `k`는 전체 배열 rank가 아니라 현재 `A[p..r]` 안의 pivot rank임을 명시한다.
- 오른쪽 재귀 rank가 `i-k`가 되는 이유를 제거된 왼쪽 `k`개로 설명한다.
- 중복 key에서 단일 pivot index를 값의 유일 rank처럼 말하지 않고 3-way equal interval을 사용한다.
- Median of Medians 결과를 `O(n)`에 그치지 않고 입력 검사의 `Omega(n)`과 합쳐 `Theta(n)`으로 정리한다.
- `3n/10-3`, `7n/10+2`의 상수를 마법처럼 제시하지 않고 incomplete/pivot group 예외로 설명한다.

## 새 프레임 순서와 교육적 목적

전문가 교정판은 **67개 conceptual frame**을 다섯 개의 보이는 Part로
묶는다. 표지·학습 목표·동기·roadmap 다섯 프레임 뒤에 Part A가
시작하며, Appendix는 별도 Part가 아니라 선택 자료로 표시한다.

| 구간 | 수 | 실제 흐름 | 교육적 목적·시각화 |
|---|---:|---|---|
| Opening | 5 | 표지 → 학습 목표 → 동기 → 활용 → roadmap | title이 항상 첫 페이지이며 세 pivot policy의 관계를 먼저 보여 준다. |
| Part A. Selection 문제와 Order Statistics | 6 | order statistic → median convention → contract → rank/index → sort baseline | multiset instance, inclusive range, 1-based relative rank를 정확히 구분한다. |
| Part B. Quickselect | 20 | one-sided idea → Lomuto contract → fixed pseudocode → fixed trace → randomized trace → correctness → fixed analysis | Fixed-pivot trace는 매 call의 `A[r]`, Randomized trace는 `A[s]`를 `A[r]`로 옮긴 뒤 partition한다. |
| Part C. Randomized Selection의 성능 | 4 | pseudocode → good pivot → expected upper bound → expected/worst | randomness가 입력 가정이 아니라 내부 pivot 선택임을 명시한다. |
| Part D. Deterministic Linear Selection | 18 | MoM 동기 → group 5 → lower-median convention → 3-way pseudocode → `3n/10-O(1)` → BFPRT recurrence | Median of Medians technique과 DeterministicSelect algorithm을 구분하고 worst-case `Theta(n)`을 증명한다. |
| Part E. 전략 비교, 요약과 Quiz | 14 | 시간/보장 → 공간/사용 맥락 → checklist → Introselect/API → concept map → 세 Quiz → 답 → Appendix | strategy 선택과 1-based/0-based 변환을 회수하고 선택 심화를 분리한다. |

## 애니메이션·코드 계획

- Quickselect: 공통 partition, fixed-pivot 2번째 값 trace 5상태,
  swap-to-last를 모두 보이는 RandomizedSelect 7번째 값 trace 7상태.
- Median of Medians: 25개 원소의 group→sort→median→M→보장 영역 5상태.
- TikZ: active/discarded range, pivot final position, 3-way interval, recurrence term label.
- C: `quickselect.c`, `deterministic_select.c` — 모든 rank를 sorted-copy
  oracle과 비교하며 duplicates, sorted/reverse, all-equal, non-multiple-of-five,
  `INT_MIN`/`INT_MAX`, invalid arguments를 검사한다.
- Java: `Quickselect.java` — injected RNG, 3-way partition, 32개 fixed seed,
  동일 경계값과 exception message를 검사한다.
- 결과물: presentation 88쪽, handout 67쪽.
