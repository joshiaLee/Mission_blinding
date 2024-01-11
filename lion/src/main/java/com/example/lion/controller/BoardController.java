package com.example.lion.controller;

import com.example.lion.entity.Board;
import com.example.lion.entity.Comment;
import com.example.lion.repository.CommentRepository;
import com.example.lion.service.BoardService;
import com.example.lion.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
public class BoardController {

    @Autowired BoardService boardService;
    @Autowired CommentService commentService;


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
                              Model model){


        Board currentBoard = boardService.boardView(id);


        if (currentBoard.getPassword().equals(password)) {
            model.addAttribute("board", boardService.boardView(id));
            return "boardmodify";
        } else {
            model.addAttribute("message", "비밀번호가 다릅니다.");
            model.addAttribute("searchUrl", "/board/view?id=" + id + "&category=" + category);
            return "message";
        }



    }

    // 글 업데이트
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Long id,
                              @ModelAttribute Board board,
                              @RequestParam(name = "category") Integer category,
                              Model model) throws Exception{

        Board boardTemp = boardService.boardView(id);

        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardTemp.setCategory(board.getCategory());

        boardService.write(boardTemp);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/view?id=" + id + "&category=" + category);
        return "message";
    }



    @GetMapping("/board/view") // localhost:8080/board/view?id=1?category=1?
    public String boardView(Model model,
                            @RequestParam(name = "id") Long id,
                            @RequestParam(name = "category") Integer category){
        Board board = boardService.boardView(id);

        model.addAttribute("board", board);
        model.addAttribute("category", category);
        return "boardview";
    }

    //댓글 달기
    @PostMapping("/board/view") // localhost:8080/board/view?id=1?category=1?
    public String boardViewComment(@ModelAttribute Comment comment,
                                   @RequestParam(name = "id") Long id,
                                   @RequestParam(name = "category") Integer category,
                                   Model model){

        Board curBoard = boardService.boardView(id);
        Comment newComment = new Comment(comment.getContent(), comment.getPassword());
        curBoard.getComments().add(newComment);

        commentService.join(newComment);
        boardService.join(curBoard);


        model.addAttribute("board", curBoard);
        model.addAttribute("category", category);
        return "boardview";
    }


    // 댓글 삭제
    @GetMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable(name = "id") Long id,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "board_id") Long board_id,
                                @RequestParam(name = "category") Integer category,
                                Model model){


        Comment comment = commentService.findById(id);

        if(!comment.getPassword().equals(password)){
            model.addAttribute("message", "비밀번호가 다릅니다.");
            model.addAttribute("searchUrl", "/board/view?id=" + board_id + "&category=" + category);
            return "message";
        }

        commentService.delete(comment);

        model.addAttribute("message", "댓글이 삭제되었습니다.");
        model.addAttribute("searchUrl", "/board/view?id=" + board_id + "&category=" + category);

        return "message";

    }

    // 게시글 삭제
    @PostMapping("board/delete/{id}")
    public String boardDelete(@PathVariable(name = "id") Long id,
                              @RequestParam(name = "password") String password,
                              @RequestParam(name = "category") Integer category,
                              Model model){


        Board currentBoard = boardService.boardView(id);


        if(currentBoard.getPassword().equals(password)) {
            boardService.boardDelete(id);

            model.addAttribute("message", "게시글이 삭제되었습니다.");
            model.addAttribute("searchUrl", "/board/list/" + category);
            return "message";
        }

        else{
            model.addAttribute("message", "비밀번호가 다릅니다.");
            model.addAttribute("searchUrl", "/board/view?id=" + id + "&category=" + category);
            return "message";
        }
    }

    @GetMapping("/hashtag") // localhost:8080/hashtag?tag=dream
    public String boardHashtag(@RequestParam(name = "tag") String tag,
                               Model model){
        List<Board> list = boardService.boardSearchByTag(tag);
        Collections.reverse(list);

        model.addAttribute("list", list);
        model.addAttribute("tag", tag);
        return "boardlisthash";
    }
}
