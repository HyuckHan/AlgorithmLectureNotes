# Lecture 6 Content Map — Search Trees

## 재구성 원칙

- 원본 104장은 화면 복제가 아니라 개념 흐름으로 재구성한다.
- root depth 0, leaf height 0, empty tree height -1의 edge-height convention을 사용한다.
- 본문 BST는 distinct key만 허용한다. Java 구현도 같은 정책을 사용한다.
- AVL balance factor는 `BF(x)=height(left)-height(right)`로 통일한다.
- RB Tree는 shared black `NIL` sentinel과 `z/p/g/u` 용어를 사용한다.
- B-Tree는 minimum degree `t`와 split-before-descend/top-down delete를 사용한다.
- 모든 그림은 TikZ로 새로 작성하며 원본·외부 slide 이미지는 삽입하지 않는다.

## 원본 slide 대응

| 원본 | 핵심 내용 | 유지·통합·수정 | 새 frame / 교육적 목적 |
|---|---|---|---|
| 1–3 | Search Tree, 계층 구조 motivation | title과 motivation으로 통합; file system·syntax tree·index 추가 | 검색 트리; 왜 Tree인가? |
| 4–9 | root, edge, parent, child, sibling, leaf, ancestor, subtree | 반복 그림을 고정 tree overlay로 통합 | Tree 용어 I–III; 동일 그림에서 관계 구분 |
| 10–11 | level, height | root level 1 표현을 root depth 0으로 수정; convention 경고 추가 | Depth · Level · Height |
| 12 | edge 수, unique path | “같은 node 재방문”을 unique simple path로 교정 | Tree의 세 가지 동치 관점 |
| 13 | binary tree와 left/right | 유지; 한 child의 방향이 다른 두 tree를 나란히 표시 | Binary Tree의 순서 있는 child |
| 14–15 | expression tree, Huffman | expression tree는 traversal로 확장; Huffman은 application preview로 축약 | Binary Tree 응용 |
| 16 | full/complete와 높이 | full/perfect 혼동 교정; perfect node 공식 convention 명시 | Full · Perfect · Complete; 크기와 높이 |
| 17–18 | linked representation | parent pointer 장단점과 heap array 표현 비교 추가 | Linked vs Array Representation |
| 19–23 | 네 traversal, recursive code | 하나의 고정 tree와 일관된 pseudocode로 통합 | DFS traversal mission; preorder/inorder/postorder animation |
| 24 | expression traversal | prefix/infix/postfix를 정확히 검증; inorder 괄호 필요성 강조 | Expression Tree |
| 25–26 | level-order와 queue | root 중복 처리 가능성이 있는 순서를 표준 queue 알고리즘으로 교정 | Level-Order pseudocode와 queue animation |
| 27–33 | Java BinaryTree | 중복 `getLeftSubtree`, 누락된 right method 수정; sharing 대신 deep-copy subtree API | BinaryTree contract, Node, traversal API, code checkpoint |
| 34–37 | dynamic set과 search tree | array/list/hash/B-Tree preview를 trade-off 표로 통합 | Dynamic Set ADT; 구조 선택 preview |
| 38–39 | BST property | `<=`, `>=`의 모호한 duplicate 정책을 distinct-key invariant로 교정 | BST Property; inorder 정렬 직관 |
| 40–42 | recursive/iterative SEARCH | key 13 경로 유지; discarded subtree와 Θ(h) 설명 | BST SEARCH pseudocode와 animation |
| 43–44 | minimum/maximum | empty 정책과 Θ(h) 추가 | MINIMUM · MAXIMUM |
| 45–48 | successor/predecessor | 표준 right-subtree/ancestor 두 case로 단순화; 예제 검증 | SUCCESSOR 두 case; PREDECESSOR |
| 49–51 | INSERT와 x/y cursor | 기존 key 14 재삽입 오류를 제거하고 absent key 12의 실제 경로 `15→6→7→13→9→NIL`로 교정; duplicate checkpoint 추가 | BST INSERT pseudocode와 full-tree path animation |
| 52–56 | DELETE 세 case와 key copy code | `Transplant` 기반 표준 알고리즘으로 교체; root/parent/satellite data 처리 명확화 | DELETE case 1/2/3, Transplant, correctness |
| 57 | degenerate BST | 정렬 입력 1..7 animation과 balanced guarantee 질문 추가 | 왜 Balanced Tree인가? |
| 58–60 | AVL 정의와 height difference | 부호를 `left-right`로 변경하고 BF label 명시 | AVL invariant와 BF |
| 61–71 | AVL insertion case animation | CW/CCW 표현을 LL/RR/LR/RL과 left/right rotation으로 교정·통합 | Rotation invariant; 네 AVL case; insertion workflow |
| 72–74 | RB invariant와 NIL | 다섯 invariant 번호화; ordinary leaf와 sentinel 구분 | RB invariant; NIL과 black-height |
| 75–81 | RB height proof와 외부 slide | 외부 저작권 이미지를 제거하고 자체 TikZ·LaTeX proof로 재작성 | RB height theorem proof |
| 82–88 | RB insertion cases | `x,p,p2,s`를 `z,p,g,u`로 표준화; recolor/inner/outer case로 통합 | RB insert overview, cases, sequence 41·38·31·12·19·8 |
| 89–94 | B-Tree motivation, multiway node | disk 고정 수치 제거; page/I/O 관점과 `t` 정의 추가 | 왜 B-Tree인가?; minimum degree; range invariant |
| 95–100 | overflow 후 insertion 예제 | top-down split-before-descend로 교체; 작은 `t=2` sequence 사용 | SplitChild, promotion, insertion animation |
| 101–104 | underflow 후 deletion | top-down preventive delete case map으로 교체; borrow/merge/root shrink 명시 | B-Tree DELETE, borrow, merge |

## 새 Beamer 순서와 교육적 목적

| Part | 핵심 frame 흐름 | 목적 / 시각 자료 |
|---|---|---|
| A | title → 목표 → motivation → rooted tree → 용어 → depth/height → 성질 | 고정 terminology tree overlay, unique-path diagram |
| B | binary definition → full/perfect/complete → representation | left/right counterexample, 세 형태 TikZ |
| C | traversal mission → 세 DFS → level-order → expression tree → complexity | 방문 순서 overlays, recursion stack/queue |
| D | Java representation와 API | editable code snippets, ownership contract |
| E | Dynamic Set와 자료구조 preview | ordered-operation 관점의 비교표 |
| F | BST property → inorder → search/min/max/successor | search path, discarded subtree, successor overlay |
| G | insert → Transplant → delete cases → correctness | x/y cursor, 세 deletion animation |
| H | degenerate construction → height dependence | sorted insertion overlay |
| I | AVL invariant → rotations → LL/RR/LR/RL → insert/delete | 동일 layout의 rotation before/after |
| J | RB invariants → black-height → height proof → insertion | R/B 문자 병기, recolor와 rotation animation |
| K | B-Tree motivation → `t` definition → search → split/insert/delete | page nodes, promotion/borrow/merge overlays |
| L | 종합 비교와 선택 | height, update, layout, use-case 비교 |
| M | summary와 quiz | convention별 문제와 정답 |
| Appendix | AVL Fibonacci bound, RB delete, B+Tree preview, code validation | 본문 범위를 넘는 연결 고리 |

## 검증 계획

- 고정 traversal tree의 preorder/inorder/postorder/level-order와 expression 표기를 프로그램으로 확인한다.
- BST successor, deletion, parent pointer 및 strictly sorted inorder를 테스트한다.
- AVL의 stored height, BF, BST invariant를 삽입·삭제·정렬·난수 입력에서 검증한다.
- RB의 root/NIL black, red-red 금지, equal black-height를 매 삽입·삭제 후 검증한다.
- `t=2` B-Tree의 key bounds, child count, range, leaf depth를 split/borrow/merge 후 검증한다.
- standalone와 Lecture 1–6 합본을 LuaLaTeX로 빌드하고 전 페이지를 이미지로 검토한다.
