package com.example.lion.service;

import com.example.lion.entity.Board;
import com.example.lion.entity.Hashtag;
import com.example.lion.repository.BoardRepository;
import com.example.lion.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final HashtagRepository hashtagRepository;

    public void join(Board board){
        boardRepository.save(board);
    }

    public void write(Board board) throws Exception{

        List<String> hashWords = extractHashWords(board.getContent());
        // @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
        board.getHashtags().clear();
        // cascade = CascadeType.ALL 옵션으로 인해 해쉬태그 리스트가 초기화 되고 save 하는 순간 delete 쿼리 나감
        boardRepository.save(board);

        for(String tag : hashWords){
            Hashtag hashtag = new Hashtag();
            hashtag.changeHashtag(board, tag);

            hashtagRepository.save(hashtag);
        }
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

    public List<Board> boardSearchByTag(String tag){
        return boardRepository.findByHashtagsTag(tag);
    }

    public Optional<Board> boardPrevAll(Long id) {
        return boardRepository.findFirstByIdLessThanOrderByIdDesc(id);
    }

    public Optional<Board> boardPrevByCategory(Long id, Integer category){
        return boardRepository.findFirstByIdLessThanAndCategoryOrderByIdDesc(id, category);
    }

    public Optional<Board> boardNextAll(Long id){
        return boardRepository.findFirstByIdGreaterThan(id);
    }

    public Optional<Board> boardNextByCategory(Long id, Integer category){
        return boardRepository.findFirstByIdGreaterThanAndCategory(id, category);
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

}