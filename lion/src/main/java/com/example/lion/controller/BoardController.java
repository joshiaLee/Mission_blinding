package com.example.lion.controller;

import com.example.lion.entity.Board;
import com.example.lion.entity.Comment;
import com.example.lion.entity.ImageEntity;
import com.example.lion.repository.ImageEntityRepository;
import com.example.lion.service.BoardService;
import com.example.lion.service.CommentService;
import com.example.lion.service.ImageEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final ImageEntityService imageEntityService;

    @GetMapping("/board/list")
    public String board(){
        return "redirect:/board/list/1";
    }

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

    // 글 작성 html
    @GetMapping("/board/write") // localhost:8080/board/write
    public String boardWriteForm(){

        return "boardwrite";
    }

    // 새로운 글 작성
    @PostMapping("/board/writePro")
    public String boardWritePro(@ModelAttribute Board board,
                                Model model) throws Exception{
        boardService.write(board);
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    // 글 수정
    @PostMapping("/board/modify/{id}")
    public String boardModify(@PathVariable(name = "id") Long id,
                              @RequestParam(name = "password") String password,
                              @RequestParam(name = "category") Integer category,
                              @RequestParam(name = "tag", required = false) String tag,
                              Model model){


        Board currentBoard = boardService.boardView(id);


        if (currentBoard.getPassword().equals(password)) {
            model.addAttribute("board", boardService.boardView(id));
            model.addAttribute("tag", tag);
            return "boardmodify";

        } else {
            model.addAttribute("message", "비밀번호가 다릅니다.");

            String url = "/board/view?id=" + id + "&category=" + category;
            if(tag != null) url = url + "&tag=" + tag;

            model.addAttribute("searchUrl", url);

            return "message";
        }



    }

    // 글 업데이트
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Long id,
                              @ModelAttribute Board board,
                              @RequestParam(name = "category") Integer category,
                              @RequestParam(name = "tag", required = false) String tag,
                              Model model) throws Exception{

        Board changedBoard = changeBoard(id, board);
        String url = "/board/view?id=" + id + "&category=" + category;
        if(tag != null) url = url + "&tag=" + tag;

        boardService.write(changedBoard);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", url);
        return "message";
    }



    // 단일 조회 페이지

    @GetMapping("/board/view") // localhost:8080/board/view?id=1&category=1&tag=dream
    public String boardView(Model model,
                            @RequestParam(name = "id") Long id,
                            @RequestParam(name = "category") Integer category,
                            @RequestParam(name = "tag", required = false) String tag){
        Board board = boardService.boardView(id);

        model.addAttribute("board", board);
        model.addAttribute("category", category);
        model.addAttribute("tag", tag);

        return "boardview";
    }

    // 단일 조회 페이지(댓글 달기)
    @PostMapping("/board/view") // localhost:8080/board/view?id=1&category=1
    public String boardViewComment(@ModelAttribute Comment comment,
                                   @RequestParam(name = "id") Long id,
                                   @RequestParam(name = "category") Integer category,
                                   @RequestParam(name = "tag", required = false) String tag,
                                   Model model){

        Board curBoard = boardService.boardView(id);
        Comment newComment = new Comment();
        newComment.changeComment(curBoard, comment);
        commentService.join(newComment);
        // test


        String url = "/board/view?id=" + id + "&category=" + category;
        if(tag != null) url = url + "&tag=" + tag;

        model.addAttribute("searchUrl", url);

        return "noMessage";
    }


    // 댓글 삭제
    @PostMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable(name = "id") Long id,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "board_id") Long boardId,
                                @RequestParam(name = "category") Integer category,
                                @RequestParam(name = "tag", required = false) String tag,
                                Model model){


        Comment comment = commentService.findById(id);
        String url = "/board/view?id=" + boardId + "&category=" + category;

        if(tag != null) url = url + "&tag=" + tag;


        if(!comment.getPassword().equals(password)){
            model.addAttribute("message", "비밀번호가 다릅니다.");
            model.addAttribute("searchUrl", url);

            return "message";
        }

        commentService.delete(comment);

        model.addAttribute("message", "댓글이 삭제되었습니다.");
        model.addAttribute("searchUrl", url);

        return "message";

    }

    // 게시글 삭제
    @PostMapping("board/delete/{id}")
    public String boardDelete(@PathVariable(name = "id") Long id,
                              @RequestParam(name = "password") String password,
                              @RequestParam(name = "category") Integer category,
                              @RequestParam(name = "tag", required = false) String tag,
                              Model model){


        Board currentBoard = boardService.boardView(id);


        if(currentBoard.getPassword().equals(password)) {
            boardService.boardDelete(id);

            model.addAttribute("message", "게시글이 삭제되었습니다.");

            if(tag == null) model.addAttribute("searchUrl", "/board/list/" + category);
            else model.addAttribute("searchUrl", "/hashtag?tag=" + tag);
            return "message";
        }

        else{
            model.addAttribute("message", "비밀번호가 다릅니다.");
            String url = "/board/view?id=" + id + "&category=" + category;
            if(tag != null) url = url + "&tag=" + tag;

            model.addAttribute("searchUrl", url);
            return "message";
        }
    }

    // 해쉬태그 검색
    @GetMapping("/hashtag") // localhost:8080/hashtag?tag=dream
    public String boardHashtag(@RequestParam(name = "tag") String tag,
                               Model model){
        List<Board> list = boardService.boardSearchByTag(tag);
        Collections.reverse(list);

        model.addAttribute("list", list);
        model.addAttribute("tag", tag);
        return "boardlisthash";
    }

    // 이전글
    @GetMapping("/board/prev") // localhost:8080/board/prev?id=9&category=2
    public String boardPrev(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "category") Integer category,
                            @RequestParam(name = "tag", required = false) String tag,
                            Model model){
        Optional<Board> board;
        String url;

        if(tag != null) board = boardService.boardPrevHash(id, tag);
        else if (category == 1) board = boardService.boardPrevAll(id);
        else board = boardService.boardPrevByCategory(id, category);

        if(board.isPresent()){
            url = "/board/view?id=" + board.get().getId() + "&category=" + category;
            if(tag != null) url = url + "&tag=" + tag;

            model.addAttribute("searchUrl", url);
            return "noMessage";
        }
        
        else{
            url = "/board/view?id=" + id + "&category=" + category;
            if(tag != null) url = url + "&tag=" + tag;

            model.addAttribute("message", "맨 처음 글 입니다.");
            model.addAttribute("searchUrl", url);

            return "message";
        }

    }

    // 다음글
    @GetMapping("/board/next") // localhost:8080/board/next?id=9&category=2
    public String boardNext(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "category") Integer category,
                            @RequestParam(name = "tag", required = false) String tag,
                            Model model){
        Optional<Board> board;
        String url;

        if(tag != null) board = boardService.boardNextHash(id, tag);
        else if (category == 1) board = boardService.boardNextAll(id);
        else board = boardService.boardNextByCategory(id, category);


        if(board.isPresent()){
            url = "/board/view?id=" + board.get().getId() + "&category=" + category;
            if(tag != null) url = url + "&tag=" + tag;

            model.addAttribute("searchUrl", url);
            return "noMessage";
        }

        else{
            url = "/board/view?id=" + id + "&category=" + category;
            if(tag != null) url = url + "&tag=" + tag;

            model.addAttribute("message", "맨 마지막 글 입니다.");
            model.addAttribute("searchUrl", url);

            return "message";
        }
    }

    // 게시물 사진 업로드
    @PostMapping("/upload")
    public String boardUpload(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "password") String password,
                              @RequestParam(name = "category") Integer category,
                              @RequestParam(name = "tag", required = false) String tag,
                              MultipartFile file,
                              Model model) throws IOException {

        Board curBoard = boardService.boardView(id);
        String url = "/board/view?id=" + id + "&category=" + category;
        if(tag != null) url = url + "&tag=" + tag;

        if(curBoard.getPassword().equals(password)){
            if(!file.isEmpty()) {

                // 현재 클래스에서 이미지 파일까지 절대경로 추출
                String projectPath = extractPath();

                // 보안상 UUID 사용
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + file.getOriginalFilename();

                // 파일 객체 생성(절대경로, 파일이름)
                File saveFile = new File(projectPath, fileName);

                // 절대경로에 파일 저장
                file.transferTo(saveFile);

                ImageEntity imageEntity = new ImageEntity();
                imageEntity.changeImageEntity(curBoard ,fileName, projectPath + fileName);

                // ImageEntity 저장
                imageEntityService.ImageJoin(imageEntity);

            }

            model.addAttribute("searchUrl", url);
            return "noMessage";
        }

        else{
            model.addAttribute("message", "비밀번호가 다릅니다.");
            model.addAttribute("searchUrl", url);
            return "message";
        }
    }



    // 이미지 보기(이거 없으면 액박뜸)
    @GetMapping("/images/{filename}")
    public @ResponseBody Resource downloadImage(@PathVariable(name = "filename") String filename) throws MalformedURLException {

        String projectPath = extractPath();
        return new UrlResource("file:" + projectPath + filename);
    }

    // 이미지 삭제
    @PostMapping("/image/delete/{imageId}")
    public String deleteImage(@PathVariable(name = "imageId") Long imageId,
                              @RequestParam(name = "imagePassword") String imagePassword,
                              @RequestParam(name = "board_id") Long boardId,
                              @RequestParam(name = "category") Integer category,
                              @RequestParam(name = "tag", required = false) String tag,
                              Model model){
        Board curBoard = boardService.boardView(boardId);
        String url = "/board/view?id=" + boardId + "&category=" + category;
        if(tag != null) url = url + "&tag=" + tag;

        if(curBoard.getPassword().equals(imagePassword)){
            imageEntityService.ImageDeleteById(imageId);

            model.addAttribute("searchUrl", url);
            return "noMessage";
        }

        else{
            model.addAttribute("message", "비밀번호가 다릅니다.");
            model.addAttribute("searchUrl", url);
            return "message";
        }

    }


    // 게시글 값 수정 메서드
    private Board changeBoard(Long id, Board board) {
        Board boardTemp = boardService.boardView(id);

        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardTemp.setCategory(board.getCategory());

        return boardTemp;
    }



    private static String extractPath() {

        Class<?> clazz = BoardController.class;
        // 정규식 패턴 설정

                // 클래스의 위치를 나타내는 URL을 가져옴
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        String fullPath = location.getPath();

        String pattern = "(/home.+?)/build";
        Pattern regex = Pattern.compile(pattern);

        // 매칭 수행
        Matcher matcher = regex.matcher(fullPath);
        if (matcher.find()) {
            // 매칭된 부분 반환
            return matcher.group(1) + "/src/main/java/com/example/images/";
        } else {
            // 매칭되지 않은 경우 예외처리 또는 기본값 반환 가능
            return "";
        }
    }
}
