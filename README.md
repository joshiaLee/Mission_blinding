# Mission Blinding - 익명 게시판 기능 개발

## 필수 기능 소개

**Mission Blinding**은 게시판 기능을 구현한 프로젝트로, 글의 종류에 따라 분류하기 위해 게시글(Board)에서 `category`라는 속성을 사용합니다.
Spring Data JPA를 사용하여 SQL을 조작하여 게시글을 조회할 때 `category`를 조건으로 활용하며, 전체 게시글을 조회할 때는 `category`를 사용하지 않고 전체를 조회합니다.
또한 수정및 삭제기능을 구현하기위해 `password`라는 속성을 사용하여 수정및 삭제를 하기전에 이 비밀번호를 입력으로 받고 비밀번호가 일치하면 동작을 수행하고 일치하지 않으면 수행되지 않습니다.
하나의 게시글(Board)에는 여러개의 댓글(Comment)이 달릴수 있기 때문에 Comment라는 Entity를 만들고 일대다 단방향 매핑해주었습니다.
Comment의 속성은 내용(`content`)와 비밀번호(`password`)로 구성되고 Foreign Key로 Board_id를 갖습니다.

## 주요 기능

1. **게시글 조회**
   - 전체 게시글 조회: `category`를 사용하지 않고 모든 게시글을 조회합니다.
   - 특정 카테고리의 게시글 조회: 특정 `category`를 조건으로 지정하여 해당 카테고리의 게시글을 조회합니다.

2. **게시글 작성**
   - 제목(`title`)과 내용(`content`)을 입력하여 새로운 게시글을 작성할 수 있습니다.
   - 카테고리(`category`)를 선택하여 게시글을 해당 카테고리로 등록할 수 있습니다.

3. **게시글 수정**
   - 비밀번호가 일치한다면 기존에 작성된 게시글을 수정할 수 있습니다.
   - 수정 시에는 이전에 선택한 카테고리,제목,내용이 유지되며 임의적으로 바꿀수 있습니다.

4. **게시글 삭제**
   - 비밀번호가 일치한다면 게시글을 삭제할 수 있습니다.


## 게시글 기능

글쓰기 버튼을 클릭하면 카테고리, 제목, 내용, password을 작성하는 html로 이동
게시판에서 제목들은 링크이며 링크를 통해 게시글 단일조회 화면으로 이동가능하고
이 화면에서 댓글 목록, 댓글 추가, 댓글 삭제를 할수 있다.
게시글 수정과 게시글 삭제의 UI는 이 페이지에 있으며 이때 비밀번호가 같아야 진행이 됩니다.

댓글 기능

