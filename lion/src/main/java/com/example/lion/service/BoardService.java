package com.example.lion.service;

import com.example.lion.entity.Board;
import com.example.lion.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BoardService {

    @Autowired private BoardRepository boardRepository;


    public void join(Board board){
        boardRepository.save(board);
    }

    public void write(Board board) throws Exception{
        boardRepository.save(board);
    }


    //특정 게시글 불러오기
    public Board boardView(Long id){
        return boardRepository.findById(id).get();
    }



    //특정 게시글 삭제
    public void boardDelete(Long id){
        boardRepository.deleteById(id);
    }

    //게시글 리스트 처리
    public List<Board> boardList(){
        return boardRepository.findAll();
    }
    public List<Board> boardListByCategory(Integer category){
        return boardRepository.findByCategory(category);
    }
    public List<Board> boardSearchByTitleAndCategory(String searchKeyword, Integer category){

        return boardRepository.findByTitleContainingAndCategoryEquals(searchKeyword, category);
    }

    public List<Board> boardSearchByContentAndCategory(String searchKeyword, Integer category) {

        return boardRepository.findByContentContainingAndCategoryEquals(searchKeyword, category);
    }

    public List<Board> boardSearchByTitleContaining(String searchKeyword) {
        return boardRepository.findByTitleContaining(searchKeyword);
    }

    public List<Board> boardSearchByContentContaining(String searchKeyword) {
        return boardRepository.findByContentContaining(searchKeyword);
    }
}