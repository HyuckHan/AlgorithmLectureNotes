# Lecture 1 콘텐츠 맵

## 재구성 원칙

- 원본: `PPTX/lecture note 1.pptx` (49 slides)
- 목표: 원본의 정의·예제·분석 논리를 보존하되, 번역 중복과 문법 나열을 줄이고 **문제 → 알고리즘 → 실행 → 비용**의 학습 흐름으로 재배치한다.
- 표기: 배열은 `A[1..n]`, 찾을 값은 `x`, 실패 반환값은 `NOT_FOUND`, 입력 크기는 `n`으로 통일한다.
- 복잡도는 기본적으로 RAM 모델의 기본 연산 수로 설명하고, 상한 `O`, 하한 `Ω`, tight bound `Θ`를 구분한다.
- 원본 24의 Greedy 예제는 독립 절에서 제외하되, “Why Algorithms Matter”의 설계 선택 사례로 축약해 보존한다.

## 원본 슬라이드별 매핑

| 원본 | 핵심 내용 | 처리 | 새 슬라이드 |
|---:|---|---|---:|
| 1 | Introduction to Algorithms | 유지·현대화 | 1 |
| 2 | 알고리즘 정의, 프로그램은 구현 | 번역 중복 통합 | 5–6 |
| 3 | 프로그래밍 언어, pseudocode 사용 | 언어 목록 축약 | 6, 13 |
| 4 | 자연어 maximum 절차 | 유지·시각화 | 17 |
| 5 | 실행(run), 손으로 추적, human computer | 유지·축약 | 7 |
| 6 | `{7,12,3,15,8}` maximum 추적 | 유지·TikZ 재작성 | 19 |
| 7 | 입력, 출력, 명확성, 정확성, 유한성 | 유지 | 9 |
| 8 | 효과성, 일반성 | 유지·7과 통합 | 9–10 |
| 9 | pseudocode 문법 목록 | 축약·참조표로 통합 | 14 |
| 10 | procedure, arguments, types | 통합 | 14–15 |
| 11 | assignment | 유지 | 14 |
| 12 | informal statement의 한계 | 유지·예시 개선 | 13 |
| 13 | begin/end block | 현대적 들여쓰기로 대체 | 14 |
| 14 | comment | 유지 | 14–15 |
| 15 | if/else | 유지 | 14–15 |
| 16 | while | 유지 | 14–15 |
| 17 | for와 while의 관계 | 개념만 축약 | 15 |
| 18 | maximum pseudocode | 유지·표기 통일 | 18 |
| 19 | 알고리즘 발명에는 창의성과 연습 필요 | 유지 | 8 |
| 20 | 정렬 리스트 탐색 문제 | 일반 탐색 문제로 확장 | 22 |
| 21 | Linear Search와 코드 | 유지·overlay 추가 | 23–25 |
| 22 | Binary Search 아이디어와 추적 | 유지·overlay 추가 | 27–28 |
| 23 | 정렬 전제와 코드 | 유지·표기 통일 | 26, 29 |
| 24 | Greedy coin change, 최적성 증명/반례 | 축약·도입 사례로 이동 | 3 |
| 25 | Growth of Functions 절 표지 | 새 흐름에 통합 | 34 |
| 26 | 성장률의 의미 | 유지 | 35 |
| 27 | `30n+8` 대 `n²+1` 선택 | 유지·교차점 강조 | 32, 35 |
| 28 | 성장 그래프 | PGFPlots로 재작성 | 35 |
| 29 | order `n`, order `n²` | `O` 오용을 바로잡아 Θ로 설명 | 36 |
| 30 | Big-O 수학적 정의 | 유지·직관과 분리 | 38–39 |
| 31 | `c,k` 비유일성, 증명 책임 | 유지 | 40 |
| 32 | Big-O 증명 두 예 | `30n+8` 중심으로 통합 | 40 |
| 33 | Big-O 그래프 | PGFPlots로 재작성 | 39 |
| 34 | 다항식, 합의 Big-O 예 | 유지·축약 | 41 |
| 35 | `n!`, `log(n!)` 상한 | 유지·축약 | 41 |
| 36 | Big-Ω 정의 | 유지 | 42 |
| 37 | Big-Θ 정의 | 유지·동치 정의 정돈 | 42 |
| 38 | O/Ω/Θ 시각화 | 유지·TikZ 재작성 | 43 |
| 39 | Algorithm Complexity 절 표지 | 새 흐름에 통합 | 31 |
| 40 | 시간/공간 복잡도 | 유지 | 31 |
| 41 | 입력 크기, worst case | 유지·best/average 추가 | 33 |
| 42 | maximum 분석 문제와 비용 가정 | 유지 | 44 |
| 43 | maximum line cost | 통합 | 44 |
| 44 | maximum worst-case 합 | 완성된 식으로 보강 | 44 |
| 45 | linear search best/worst/average | 유지·비교 수 명시 | 45 |
| 46 | binary search 반복 횟수 질문 | 유지 | 46 |
| 47 | `n=2^k` 분석 | 유지·일반 `n`으로 보강 | 46 |
| 48 | 성장률 이름 | `n log n` 명칭을 linearithmic으로 정정 | 36 |
| 49 | 1 ns 연산 시간 표 | 값과 단위 정돈·현대적 표 | 37 |

## 새로 추가한 내용

- 학습목표와 “입력-알고리즘-출력” mental model
- 명세(specification)와 구현(implementation)의 분리, 동일 알고리즘의 여러 구현
- correctness의 precondition/postcondition 관점과 loop invariant 맛보기
- 선형/이진 탐색의 단계별 TikZ overlay 및 정렬 전제 반례
- 비용 모델, best/average/worst case의 역할과 성능 측정 대비 분석
- `O`는 “대략 같다”가 아니라 상한이라는 오개념 교정
- `Θ(n log n)`의 올바른 명칭(linearithmic), 로그 밑이 점근 표기에 미치는 영향
- checkpoint, retrieval quiz, exit ticket

## 새 Beamer 순서와 교육적 목적

| # | 절 / 슬라이드 | 교육적 목적 |
|---:|---|---|
| 1 | Title | 강의 주제와 핵심 질문 제시 |
| 2 | Learning goals | 학습 후 할 수 있어야 할 행동 명료화 |
| 3 | Why Algorithms Matter — 같은 문제, 다른 선택 | 알고리즘 선택·정당화의 필요성 동기화 |
| 4 | Why Algorithms Matter — 규모가 답을 바꾼다 | 입력 규모와 성장률 연결 |
| 5 | Algorithm and Program — 정의 | algorithm/program 구분 |
| 6 | 명세–알고리즘–구현 | 추상화 층위 구분 |
| 7 | 실행과 trace | 손 추적을 디버깅·분석 도구로 이해 |
| 8 | 설계는 창의적 활동 | 예제·연습의 역할 강조 |
| 9 | Properties — 7가지 조건 | 좋은 알고리즘의 평가 기준 습득 |
| 10 | correctness와 finiteness | “멈춤”과 “맞음”의 독립성 이해 |
| 11 | Properties checkpoint | 짧은 판단 문제로 회상 연습 |
| 12 | Section takeaway | 앞 절 핵심 압축 |
| 13 | Pseudocode — 왜 쓰는가 | 자연어와 실제 코드 사이 역할 이해 |
| 14 | 공통 문법 | 이 강의의 표기 규약 습득 |
| 15 | 제어 흐름 읽기 | condition/loop의 실행 의미 확인 |
| 16 | Pseudocode checkpoint | 모호한 절차를 구체화 |
| 17 | Maximum — 문제와 자연어 전략 | running maximum 아이디어 도출 |
| 18 | Maximum pseudocode | 전략을 정확한 절차로 변환 |
| 19 | Maximum trace | invariant가 유지되는 과정 관찰 |
| 20 | 왜 맞는가? | loop invariant를 통한 correctness 입문 |
| 21 | Maximum takeaway | 코드·정확성·비용 질문 연결 |
| 22 | Search problem | 탐색의 입출력과 실패 의미 정의 |
| 23 | Linear Search pseudocode | 순차 탐색 절차 이해 |
| 24 | Linear Search overlay | 비교 과정을 단계별로 시각화 |
| 25 | Linear Search properties | 정렬 불필요·early exit 이해 |
| 26 | Binary Search prerequisite | 정렬이 필요한 이유 확인 |
| 27 | Binary Search pseudocode | 탐색 구간 축소 규칙 이해 |
| 28 | Binary Search overlay | 절반 제거 과정을 단계별로 추적 |
| 29 | Binary Search invariant | 구간 불변식과 종료 조건 이해 |
| 30 | Search checkpoint | 두 탐색법 선택 기준 점검 |
| 31 | Why Complexity Matters — 비용 | 시간/공간 및 기본 연산 모델 정의 |
| 32 | 실측만으로 충분한가? | 분석과 benchmark의 역할 구분 |
| 33 | 입력에 따른 비용 | best/average/worst case 구분 |
| 34 | Complexity takeaway | 분석 질문 네 가지로 정리 |
| 35 | Orders of Growth graph | 상수와 낮은 차수항의 장기 영향 시각화 |
| 36 | 성장률 계층 | 자주 쓰는 함수의 상대적 증가 이해 |
| 37 | 1 ns thought experiment | 점근적 차이의 실제 규모 체감 |
| 38 | Big-O definition | 상한의 수학적 정의 학습 |
| 39 | Big-O intuition | 정의의 기하학적 의미 이해 |
| 40 | Big-O proof | 증명 witness `c,n₀` 구성 연습 |
| 41 | Upper-bound examples | 표준 부등식 패턴 습득 |
| 42 | Ω and Θ definitions | 하한과 tight bound 구분 |
| 43 | O/Ω/Θ together | 세 표기의 관계를 한 그림으로 통합 |
| 44 | Maximum complexity | 정확한 비교 수에서 Θ(n) 도출 |
| 45 | Linear Search complexity | best/worst/average case 분석 |
| 46 | Binary Search complexity | 반복 횟수 `⌈log₂ n⌉` 도출 |
| 47 | Algorithm comparison | 전제·비용·용도를 종합 비교 |
| 48 | Complexity checkpoint | 표기와 분석의 흔한 오류 진단 |
| 49 | Summary | 전체 mental model 회수 |
| 50 | Quiz | retrieval practice 및 형성평가 |
| 51 | Quiz answers / exit ticket | 즉시 피드백과 다음 학습 연결 |

