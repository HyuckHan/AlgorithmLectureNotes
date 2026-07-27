# Lecture 3 콘텐츠 맵 — Sorting

## 재구성 원칙

- 원본 `PPTX/lecture note 3.pptx`의 149개 슬라이드를 모두 검토했다.
- 반복 캡처로 구성된 시연은 하나의 Beamer 프레임과 3–5개의 의미 있는 overlay로 통합한다.
- 1-based indexing을 사용한다. Heap의 자식은 `2i`, `2i+1`, 부모는 `floor(i/2)`이다.
- 비교 정렬의 하한은 `Omega(n log n)`, BUILD-MAX-HEAP은 `Theta(n)`으로 정확히 구분한다.
- Java와 C 예제는 현재 문법과 comparator contract에 맞는 컴파일 가능한 코드로 다시 쓴다.
- 모든 그림은 TikZ, 모든 수식과 의사코드는 편집 가능한 LaTeX로 작성한다.

## 원본 슬라이드 대응

| 원본 | 핵심 내용 | 유지 | 통합·축약·삭제 | 새 강의에서의 위치 |
|---:|---|---|---|---|
| 1 | Sorting 제목 | 강의 주제 | 표지 현대화 | Part A 표지 |
| 2 | 정렬 알고리즘 개관 | 알고리즘 계열 | 기준 없는 나열을 로드맵으로 교체 | A6 |
| 3 | 단순 정렬 도입 | 작은 입력에서의 역할 | 동기 설명과 통합 | A3, C–E |
| 4–6 | Selection Sort | 최댓값 선택, 의사코드, `Theta(n^2)` | 3장을 invariant·animation·분석 5장으로 교육적으로 확장 | C1–C5 |
| 7–8 | Bubble Sort | 인접 교환, 조기 종료 가능성 | 잘못 적힌 `selectionSort` 함수명 삭제·교정 | D1–D5 |
| 9–12 | Insertion Sort | 정렬된 prefix, 삽입 과정, 분석 | 반복 예시를 5-state overlay로 통합 | E1–E6 |
| 13–16 | Divide-and-Conquer와 Merge Sort | divide/conquer/combine, 재귀 구조 | Lecture 2 연결과 큰 그림으로 통합 | F1–F3, G1–G2 |
| 17–26 | Merge 과정 | 두 정렬 배열의 병합 | 10장의 한 칸씩 복사를 4-state pointer animation으로 축약 | G3–G5 |
| 27–29 | Merge 의사코드·점화식 | `T(n)=2T(n/2)+Theta(n)` | midpoint를 overflow-safe 식으로 교정; 풀이를 한 장으로 통합 | G2, G4, G6 |
| 30–32 | Quick Sort 개요 | partition 중심 구조 | Mission과 의사코드로 정리 | H1–H3 |
| 33–37 | Partition 시연 | pivot, 경계, swap | 5장을 4-state Lomuto animation으로 통합 | H4 |
| 38–44 | Quick Sort 분석·pivot | 최악/최선/평균, pivot 전략 | 평균을 expected `Theta(n log n)`으로 명확화; 중복 key 이슈 추가 | H5–H8 |
| 45–51 | Heap 정의·배열 표현 | complete tree, max/min heap, 인덱스 공식 | 중복 도식을 동기화된 array/tree 그림으로 통합 | J1–J5 |
| 52–58 | MAX-HEAPIFY | 전제, 재귀 하강, `Theta(log n)` | 재귀·반복 버전을 분리하고 4-state animation으로 통합 | K1–K6 |
| 59–71 | BUILD-MAX-HEAP 시연 | 아래에서 위로 heapify, 원본 배열 | 13장을 5-state array/tree animation으로 통합 | L1–L3 |
| 72–73 | BUILD-MAX-HEAP 분석 | 느슨한 `O(n log n)`, 정밀 합 | `Theta(n)` 증명을 레벨별 비용 합으로 교정 | L4–L6 |
| 74–90 | Heapsort 시연 | max 추출, heap 축소, sorted suffix | 17장을 5-state animation으로 축약 | M1–M3 |
| 91 | Heapsort 분석 | `Theta(n log n)` | 공간·안정성까지 보강 | M4–M6 |
| 92–98 | Decision tree | 비교 결과의 분기, n=3 | 7장을 완전한 n=3 결정 트리 한 장으로 통합 | I1–I2 |
| 99–103 | 비교 정렬 하한 | `n!` leaves, tree height | `O` 오기를 `Omega`로 교정; Stirling 직관과 적용 범위 추가 | I3–I4 |
| 104–118 | Counting Sort | count 배열과 누적합 | 15장을 4-state animation으로 통합 | N1–N4 |
| 119–125 | Counting 분석 | `Theta(n+k)`, 제약 | stable record output과 음수 offset을 추가 | N5–N6 |
| 126–127 | Radix Sort | 자리별 stable sort | 표준 LSD 예제를 3-state animation으로 확장 | O1–O5 |
| 128–132 | 알고리즘 비교 | 시간·공간 비교 | best/average/worst, stable, in-place 열을 정확히 분리 | P1–P5 |
| 133–136 | Java primitive/String/List 정렬 | 표준 라이브러리 사용 | 오래된 API 설명을 `Arrays.sort`, `List.sort` 중심으로 갱신 | Q1–Q2 |
| 137–142 | Comparable/Comparator | 자연 순서와 별도 순서 | `Comparator class extends` 오류를 interface/functional interface로 교정; chaining 추가 | Q3–Q6 |
| 143–146 | C 함수 포인터 | callback 개념 | 선언 문법을 간결히 정리 | R1–R2 |
| 147–149 | `qsort` | 시그니처와 비교 함수 | overflow 위험한 `a-b`를 relational comparison으로 교정; struct multi-key 추가 | R2–R5 |

## 유지·통합·축약·추가 요약

- **유지:** 모든 정렬 계열, 원본의 대표 배열, heap 인덱스 관계, 결정 트리 하한, Java/C API.
- **통합:** Merge 10장, BUILD-MAX-HEAP 13장, Heapsort 17장, Counting Sort 15장을 각각 핵심 상태 animation으로 묶는다.
- **축약:** 코드와 같은 내용을 반복하는 문장, 배열에서 한 원소만 이동한 중간 캡처, 중복된 복잡도 표를 제거한다.
- **교정:** Bubble 함수명, Quick Sort expected time, 비교 정렬 하한, BUILD-MAX-HEAP tight bound, Java Comparator 설명, C/Java 정수 comparator.
- **추가:** stability와 record, auxiliary space/in-place, adaptive와 online, inversion, 3-way partition 언급, external sorting, comparator contract, 알고리즘 선택 checklist, hand-trace quiz.

## 새 Beamer 순서와 교육적 목적

전문가 교정판은 **109개 개념 프레임**을 다섯 개의 보이는 상위 Part로 묶는다.
기존 A–S 소스 그룹은 파일을 불필요하게 합치지 않고 아래 Part 안에서 순서대로
입력한다. Part divider는 다섯 번만 표시되며 frame 번호에는 포함하지 않는다.

| 보이는 Part | 개념 프레임 | 포함하는 기존 소스 그룹 | 교육적 목적 |
|---|---:|---|---|
| Part A. 정렬 문제와 평가 기준 | 12 | A–B | 정렬 명세, record/comparator 의미, stability·space·adaptivity 등 선택 기준을 세운다. |
| Part B. 기본 Comparison Sort | 16 | C–E | Selection, Bubble, Insertion의 invariant·trace·비용을 같은 기준으로 비교한다. |
| Part C. Divide-and-Conquer와 비교 하한 | 22 | F–I | Merge와 Quick의 재귀 구조를 분석하고 비교 정렬의 `Omega(n log n)` 하한을 연결한다. |
| Part D. Heap과 Heapsort | 23 | J–M | 1-based heap 표현부터 MAX-HEAPIFY, BUILD-MAX-HEAP, Heapsort까지 invariant를 추적한다. |
| Part E. Key 구조를 이용한 정렬과 실무 API | 36 | N–S | Counting/Radix, 전체 비교, Java/C comparator, 요약과 모든 Quiz 답을 회수한다. |

## 시각화·애니메이션 계획

- TikZ 배열: Selection, Bubble, Insertion, Merge, Quick partition, Heap 계열, Counting, Radix.
- TikZ tree: n=3 decision tree, heap의 array/tree 동기화.
- PGFPlots: 대표 성장률 비교.
- overlay는 Selection 5, Bubble 4, Insertion 5, Merge 4, Partition 4, Heapify 4, Build 5, Heapsort 5, Counting 4, Radix 3 상태로 제한한다.
- 교정판 결과는 presentation **148쪽**, handout **109쪽**이다.

## 프레임별 구현 인벤토리

아래 제목은 실제 `.tex`의 frame title과 일치한다. `A1` 같은 번호는
초기 설계에서 사용한 **소스 그룹 식별자**이며, 현재 보이는 다섯 Part의
letter와는 별개다.

| 소스 그룹 | 실제 frame title 순서 | 편집 가능한 구현 요소 |
|---|---|---|
| A | A1 표지; A2 학습 목표; A3 왜 정렬인가?; A4 정렬 문제의 명세; A5 Key, Record, Total Order; A6 정렬 알고리즘 Road Map | 수식, TikZ 분류 tree, takeaway |
| B | B1 정렬 알고리즘을 보는 여섯 기준; B2 Stability: 같은 Key의 순서를 지키는가?; B3 Auxiliary Space와 In-place; B4 Adaptive, Online / Offline; B5 Comparison-based와 External Sorting; B6 정렬의 성질 Checkpoint | TikZ record 배열, 비교 block, checkpoint |
| C | C1 Selection Sort의 Mission과 Invariant; C2 Selection Sort 의사코드; C3 Selection Sort: 한 Pass씩 확정하기; C4 Selection Sort의 비용; C5 Selection Sort Takeaway | algorithmicx, initial + 4-pass의 5-state TikZ array, 정확한 합 |
| D | D1 Bubble Sort의 Mission과 Invariant; D2 조기 종료 Bubble Sort; D3 Bubble Sort: 인접 비교의 흐름; D4 Bubble Sort의 시간과 성질; D5 Bubble Sort Takeaway | algorithmicx, 4-state TikZ array, 경우별 표 |
| E | E1 카드를 정리하듯 삽입한다; E2 Insertion Sort의 Mission과 Invariant; E3 Insertion Sort 의사코드; E4 Insertion Sort: Shift하고 Insert; E5 Inversion과 Adaptive 성능; E6 단순 정렬 Checkpoint | TikZ 카드와 5-state array, inversion 수식, 비교표 |
| F | F1 Divide, Conquer, Combine; F2 Merge Sort와 Quick Sort의 차이; F3 점화식으로 보는 두 알고리즘 | 비교 block, recurrence 수식 |
| G | G1 Merge Sort: 먼저 반으로 나눈다; G2 MERGE-SORT 의사코드; G3 MERGE의 Mission; G4 MERGE 의사코드; G5 MERGE: 두 Pointer만 전진한다; G6 Merge Sort의 시간; G7 Merge Sort Takeaway | TikZ 분할도, algorithmicx, 4-state pointer array, recursion tree |
| H | H1 Quick Sort: Partition이 핵심; H2 QUICKSORT 의사코드; H3 Lomuto PARTITION 의사코드; H4 Partition: 선택된 한쪽씩만 확정한다; H5 균형이 성능을 결정한다; H6 Quick Sort의 경우별 시간; H7 Pivot 선택과 중복 Key; H8 Quick Sort Takeaway | algorithmicx, 4-state partition array, 2-state recurrence 대비 |
| I | I1 Comparison Model; I2 n=3 Decision Tree; I3 왜 Ω(n log n)인가?; I4 하한의 적용 범위 | 완전한 6-leaf TikZ decision tree, lower-bound 수식 |
| J | J1 Heap은 Complete Binary Tree; J2 Max-Heap과 Min-Heap; J3 1-based Heap Index 공식; J4 같은 Heap: Array와 Tree; J5 Heap 기초 Checkpoint | TikZ array/tree 동기화, index 수식 |
| K | K1 MAX-HEAPIFY의 전제; K2 재귀 MAX-HEAPIFY; K3 반복형 MAX-HEAPIFY; K4 MAX-HEAPIFY: 위반을 아래로 이동; K5 정확성과 시간; K6 MAX-HEAPIFY Takeaway | algorithmicx, 4-state array/tree animation |
| L | L1 Leaves는 이미 Heap이다; L2 BUILD-MAX-HEAP 의사코드; L3 BUILD-MAX-HEAP: 아래에서 위로; L4 느슨한 상한은 O(n log n); L5 정밀한 합은 Θ(n); L6 BUILD-MAX-HEAP Takeaway | algorithmicx, 5-state array/tree, level-cost 합 |
| M | M1 Heapsort의 아이디어; M2 HEAPSORT 의사코드; M3 Heapsort: Heap은 줄고 정렬 구간은 자란다; M4 Heapsort의 분석; M5 Merge, Quick, Heap 비교; M6 Heapsort Takeaway | algorithmicx, 5-state active heap/sorted suffix, 비교표 |
| N | N1 Counting Sort가 요구하는 것; N2 Frequency 출력과 Stable Record 출력; N3 Stable COUNTING-SORT 의사코드; N4 Counting Sort: Count → Prefix → Place; N5 Counting Sort의 복잡도; N6 Counting Sort Takeaway | algorithmicx, 4-state A/C/B TikZ arrays |
| O | O1 LSD Radix Sort의 아이디어; O2 Radix Sort: 자리 하나씩 안정적으로; O3 왜 내부 정렬이 Stable이어야 하는가?; O4 Radix Sort의 복잡도; O5 Radix Sort Takeaway | 3-state digit-pass TikZ array, 조건부 복잡도 |
| P | P1 단순 정렬 비교; P2 효율적인 Comparison Sort; P3 Non-comparison Sort; P4 어떤 상황에 무엇을 선택할까?; P5 알고리즘 선택 Checklist | booktabs 비교표, scenario checklist |
| Q | Q1 Java: 표준 라이브러리부터; Q2 Primitive, String, List 정렬; Q3 Comparable: 자연 순서 한 가지; Q4 Comparator: 별도의 정렬 기준; Q5 Comparator Chaining과 Lambda; Q6 Comparator Contract와 Stability | editable listings, 완전한 Java 예제 파일 |
| R | R1 함수 포인터는 Callback이다; R2 qsort의 시그니처; R3 Overflow-safe 정수 Comparator; R4 Struct와 Multi-key Comparator; R5 C Comparator Contract | editable listings, 완전한 C17 예제 파일 |
| S | S1 정렬 개념 Map; S2 선택 Checklist 요약; S3 자주 하는 실수; S4 Hand Trace Quiz 1: 단순 정렬; S5 Hand Trace Quiz 2: Divide와 Heap; S6 복잡도·성질 Quiz; S7 Java/C Code Quiz; S8 Quiz 정답 I: Trace; S9 Quiz 정답 II·다음 강의 | TikZ concept map, trace/property/code retrieval quiz와 전체 답 |
