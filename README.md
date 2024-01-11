## Mission Blinding - 익명 게시판 기능 개발

### 필수 기능 소개

**Mission Blinding**은 게시판 기능을 구현한 프로젝트로, 글의 종류에 따라 분류하기 위해 게시글(Board)에서 `category`라는 속성을 사용합니다. Spring Data JPA를 사용하여 SQL을 조작하여 게시글을 조회할 때 `category`를 조건으로 활용하며, 전체 게시글을 조회할 때는 `category`를 사용하지 않고 전체를 조회합니다. 또한 수정 및 삭제 기능을 구현하기 위해 `password`라는 속성을 사용하여 수정 및 삭제를 하기 전에 이 비밀번호를 입력으로 받고, 비밀번호가 일치하면 동작을 수행하고 일치하지 않으면 수행되지 않습니다. 하나의 게시글(Board)에는 여러개의 댓글(Comment)이 달릴 수 있기 때문에 Comment라는 Entity를 만들고 일대다 단방향 매핑해주었습니다. Comment의 속성은 내용(`content`)과 비밀번호(`password`)로 구성되고 Foreign Key로 `Board_id`를 갖습니다.

### 게시글 주요 기능

1. **게시글 조회**
   - **전체 게시글 조회:** `category`를 사용하지 않고 모든 게시글을 조회합니다.
   - **특정 카테고리의 게시글 조회:** 특정 `category`를 조건으로 지정하여 해당 카테고리의 게시글을 조회합니다. 이때 게시판의 구성은 거의 같고 화면 중앙에 어떤 게시판인지 표시되는 내용만 바뀝니다.

2. **게시글 작성**
   - 제목(`title`)과 내용(`content`)을 입력하여 새로운 게시글을 작성할 수 있습니다.
   - 카테고리(`category`)를 선택하여 게시글을 해당 카테고리로 등록할 수 있습니다.

3. **게시글 수정**
   - 비밀번호가 일치한다면 기존에 작성된 게시글을 수정할 수 있습니다.
   - 수정 시에는 이전에 선택한 카테고리, 제목, 내용이 유지되며 임의로 바꿀 수 있습니다.

4. **게시글 삭제**
   - 비밀번호가 일치한다면 게시글을 삭제할 수 있습니다.

### 댓글 기능

- 댓글의 작성은 게시글 단일 조회 페이지에서 이루어지며
- 댓글을 작성할 때는 작성자가 자신임을 증명할 수 있는 비밀번호를 추가해서 작성합니다.
- 댓글의 목록은 게시글 단일 조회 페이지에서 확인이 가능합니다.
- 댓글의 삭제는 게시글 단일 조회 페이지에서 가능합니다.
  - 댓글 삭제를 하기 위한 UI가 존재해야 합니다.
  - 댓글 삭제를 위해 비밀번호를 제출할 수 있어야 합니다.
    - 이 비밀번호가 댓글 작성 당시의 비밀번호와 일치해야 실제로 삭제가 이뤄집니다.

### 컨트롤러 설명

```java
@GetMapping("/board/list")
public String board() // 시작 URL로 전체게시판으로 이동합니다

@GetMapping("/board/list/{category}")
public String boardList() // 해당 카테고리 게시판으로 이동합니다

@GetMapping("/board/write") 
public String boardWriteForm() // 게시글 작성 페이지로 이동합니다

@PostMapping("/board/writePro")
public String boardWritePro() // 작성한 게시글을 저장합니다

@PostMapping("/board/modify/{id}")
public String boardModify() // 비밀번호가 같다면 게시글 수정 페이지로 이동합니다

@PostMapping("/board/update/{id}")
public String boardUpdate() // 수정한 게시글을 업데이트 합니다

@GetMapping("/board/view") 
public String boardView() // 게시판에서 클릭한 게시글에 대한 단일 조회 페이지로 이동합니다

@PostMapping("/board/view") 
public String boardViewComment() // 게시글에 대한 댓글을 추가합니다

@PostMapping("/comment/delete/{id}")
public String deleteComment() // 비밀번호가 일치하면 게시글에 달린 댓글을 삭제합니다

@PostMapping("board/delete/{id}")
public String boardDelete() // 비밀번호가 일치하면 해당 게시글을 삭제합니다

@GetMapping("/hashtag") 
public String boardHashtag() // 해당 해시태그를 포함하는 게시글들을 조회합니다

