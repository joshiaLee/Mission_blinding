## Mission Blinding - 익명 게시판 기능 개발

### 필수 기능 소개

**Mission Blinding**은 게시판 기능을 구현한 프로젝트로, 글의 종류에 따라 분류하기 위해 게시글(Board)에서 `category`라는 속성을 사용합니다. Spring Data JPA를 사용하여 SQL을 조작하여 게시글을 조회할 때 `category`를 조건으로 활용하며, 전체 게시글을 조회할 때는 `category`를 사용하지 않고 전체를 조회합니다. 또한 수정 및 삭제 기능을 구현하기 위해 `password`라는 속성을 사용하여 수정 및 삭제를 하기 전에 이 비밀번호를 입력으로 받고, 비밀번호가 일치하면 동작을 수행하고 일치하지 않으면 수행되지 않습니다. 하나의 게시글(Board)에는 여러개의 댓글(Comment)이 달릴 수 있기 때문에 Comment라는 Entity를 만들고 일대다 단방향 매핑해주었습니다. Comment의 속성은 내용(`content`)과 비밀번호(`password`)로 구성되고 Foreign Key로 `board_id`를 갖습니다.

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
기능을 구현하기위해 Comment라는 Entity를 객체를 만들고 속성으로는 `content`와 `password`를 갖고 Foreign Key로는 `board_id`를 갖습니다.
따라서 join을 통해 해당 게시물에 달린 댓글을 모두 가져올수 있고 이것을 thymeleaf의 반복문을 사용해서 모든 댓글을 출력할수 있습니다.

- 댓글의 작성은 게시글 단일 조회 페이지에서 이루어지며
- 댓글을 작성할 때는 작성자가 자신임을 증명할 수 있는 비밀번호를 추가해서 작성합니다.
- 댓글의 목록은 게시글 단일 조회 페이지에서 확인이 가능합니다.
- 댓글의 삭제는 게시글 단일 조회 페이지에서 가능합니다.
- 이 비밀번호가 댓글 작성 당시의 비밀번호와 일치해야 실제로 삭제가 이뤄집니다.

### 컨트롤러 설명

```java
@GetMapping("/board/list")
public String board() // 시작 URL로 전체게시판으로 이동합니다

@GetMapping("/board/list/{category}")
public String boardList() // 해당 카테고리 게시판으로 이동합니다, 검색창을 사용할때도 사용됩니다

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
```

### 도전 과제
해쉬태그 추출및 검색 기능, 해당게시판에서 제목이나 내용으로 게시판 검색기능 추가

### 해쉬 태그 기능
한 게시물(Board)에 해쉬태그가 여러개 있을수 있으므로 Hashtag라는 Entity를 새로 만들고 일대다 단방향 매핑을 해주었습니다. 
게시글을 작성하거나 수정할때 게시글 내용에서 #이 포함된 단어를 추출하기 위해 정규표현식을 이용한 메서드를 작성하였고
BoardService에서 저장하기 전에 해쉬태그들을 추출합니다.
이때 업데이트 될때에는 전에 저장된 해쉬태그들을 다 비우고 새로 추출하는 방식을 사용했습니다.
해당 자바 코드는 아래와 같습니다.
```java
    public void write(Board board) throws Exception{

        List<String> hashWords = extractHashWords(board.getContent());
        board.getHashtags().clear();

        for(String tag : hashWords){
            Hashtag hashtag = new Hashtag(tag);

            hashtagRepository.save(hashtag);
            board.getHashtags().add(hashtag);
        }
        boardRepository.save(board);
    }

    private static List<String> extractHashWords(String input) {
        List<String> hashWords = new ArrayList<>();
        Pattern pattern = Pattern.compile("#([\\p{L}\\p{N}_]+)"); // #으로 시작하고, 그 뒤에 단어 문자(알파벳, 숫자, 언더스코어)가 하나 이상인 패턴

        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String hashWord = matcher.group(1);
            hashWords.add(hashWord);
        }

        return hashWords;
    }

```
추출한 해쉬태그는 게시글의 내용 밑에 달리게되고 각각의 해쉬태그는 링크가 돼서 해당 해쉬태그를 포함한 게시글을 검색하는곳으로 이동합니다.
이때 게시판은 원래 게시판이랑 큰 차이는 없으나 검색창이 사라지고 게시판 이름이 있는곳에 검색된 해쉬태그가 위치하게 됩니다.

### 검색 기능
searchKeyWord가 없으면 카테고리에 따라 게시물을 조회하고
searchKeyword가 있으면 제목검색인지 내용검색인지 구분하고 스프링 데이터 JPA를 사용해서 해당 쿼리가 나가도록합니다
이때 현재 카테고리정보도 포함되게해서 해당게시판에서만 검색을 하도록 구현했습니다.
이에 대한 자바코드는 아래와 같습니다.

```java
@GetMapping("/board/list/{category}")
    public String boardList(Model model,
                            @PathVariable(name = "category") Integer category,
                            @RequestParam(name = "searchKeyword", required = false) String searchKeyword,
                            @RequestParam(name = "toc", required = false) Integer toc){

        List<Board> list = null;

        if(searchKeyword == null){
            if(category == 1) list = boardService.boardList();
            else list = boardService.boardListByCategory(category);
        } else {
            // 전체 게시글에서
            if(category == 1){
                // 제목 검색
                if(toc == 1) list = boardService.boardSearchByTitleContaining(searchKeyword);
                // 내용 검색
                else list = boardService.boardSearchByContentContaining(searchKeyword);


            }
            // 특정 카테고리 게시글에서
            else {
                // 제목과 카테고리 검색
                if(toc == 1) list = boardService.boardSearchByTitleAndCategory(searchKeyword, category);
                // 내용과 카테고리 검색
                else list = boardService.boardSearchByContentAndCategory(searchKeyword, category);
            }
        }

        Collections.reverse(list);

        model.addAttribute("category", category);
        model.addAttribute("list", list); // 리턴값이 "list"로 넘어간다


        return "boardlist";
    }
```

### 프로젝트 진행중 발생한 어려움
1. **git 사용**
   - git을 사용하는데 익숙하지 않아서 오류를 많이 접했는데 사용하다보니 버전관리의 편리함을 느낄수 있었다.
   - pull 먼저하고 push하도록 하기

2. **게시판 삭제시 댓글 관리**
   - 게시판이 삭제되면 그 게시판에 해당하는 댓글들은 DB에 남게되는데 이부분을 처리하기 위해서 고민하다가 @OneToMany(orphanRemoval = true) 옵션설정을 알게되었다.
   - 이름처럼 부모와 생명주기를 동기화할수 있었다.

3. **SQLite 테이블 업데이트 문제**
   - SQLite가 테이블 업데이트가 불가능한 DB인줄 모르고 처음에 설정한 UNIQUE 조건때문에 프로젝트 진행이 막혔었다.
   - 과감하게 ddl-auto: craete로 DB를 초기화하면서 이전 설정을 없앨수 있음을 상기했다.
     
4. **게시판 수정시 해쉬태그 문제**
   - 게시판을 수정할때 이전 해쉬태그들을 어떻게 처리하는지 어려움이 있었다. 이전 해쉬태그를 갖고 수정된것만 삭제하고 다시 넣고하는 과정을 하기엔 번거롭다고 생각했다.
   - 따라서 아예 clear()로 다 비우고 새로 추출해서 저장했다.(이를 위해 @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true) 옵션을 추가했다.)
   - 이전것을 새로운것과 일일이 비교하는 로직보다 그냥 디비를 비우고 id만 바뀐채 업데이트하는것이 효율적인 면에서 더 낫다고 판단했다.

5. **테스트 문제**
   - 웹에서 여러가지 동작들을 시험하기위해 직접 값을 넣고 삭제하는데 번거러움이 있었다.
   - TDD(Test Driven Development)방식으로 프로젝트를 진행할 필요성을 느꼈다.
     
### 실행 및 테스트 방법
1. application.yml에서 ddl-auto: `create`로 설정합니다
2. LionApplication을 실행합니다.
3. 주소창에 시작 URL인 http://localhost:8080/board/list 입력합니다.
 ![image](https://github.com/joshiaLee/Mission_blinding/assets/93809073/60c03ce3-78c3-40da-8666-254d1c7e7b6a)

4. 게시판 UI를 클릭하면 해당 게시판으로 이동하고 글쓰기 UI를 클릭하면 글을 작성하는 페이지로 이동합니다.
5. 게시판 카테고리, 제목, 내용, 비밀번호를 설정할수 있고 나중에 수정및 삭제를 할경우 비밀번호가 일치해야 합니다.
![image](https://github.com/joshiaLee/Mission_blinding/assets/93809073/57f15744-5a46-41e9-ad4e-f9022d77144e)
6. 가운데 있는 검색창 UI를 통해 제목이나 내용을 선택하고 검색하고 싶은 게시판을 조회할수 있습니다.
7. 게시글을 클릭하면 게시글 단일 조회 페이지로 이동합니다.
   ![image](https://github.com/joshiaLee/Mission_blinding/assets/93809073/713dc8a8-5e21-4a1b-a807-75a26ad7b41b)
8. 해당 게시글에 댓글을 달수 있습니다.(비밀번호를 제출하고 나중에 삭제할때는 비밀번호가 일치해야 합니다.)
   ![image](https://github.com/joshiaLee/Mission_blinding/assets/93809073/2db99d69-fc0b-44eb-978d-4150eca2069b)

   



