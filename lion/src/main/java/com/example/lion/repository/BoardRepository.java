package com.example.lion.repository;

import com.example.lion.entity.Board;
import com.example.lion.entity.Hashtag;
import jdk.jfr.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByTitleContaining(String searchKeyword);

    List<Board> findByCategory(Integer category);

    List<Board> findByTitleContainingAndCategoryEquals(String searchKeyword, Integer category);

    List<Board> findByContentContainingAndCategoryEquals(String searchKeyword, Integer category);

    List<Board> findByContentContaining(String searchKeyword);

    List<Board> findByHashtagsTag(String tag);

    // 해쉬태그 검색중 이전글 하나
    Optional<Board> findFirstByIdLessThanAndHashtagsTagOrderByIdDesc(Long id, String tag);

    // 해쉬태그 검색중 다음글 하나
    Optional<Board> findFirstByIdGreaterThanAndHashtagsTag(Long id, String tag);

    // 전체글중 이전글 하나
    Optional<Board> findFirstByIdLessThanOrderByIdDesc(Long id);

    // 특정 게시판글중 이전글 하나
    Optional<Board> findFirstByIdLessThanAndCategoryOrderByIdDesc(Long id, Integer category);

    // 전체글중 다음글 하나
    Optional<Board> findFirstByIdGreaterThan(Long id);

    // 특정 게시판글중 다음글 하나
    Optional<Board> findFirstByIdGreaterThanAndCategory(Long id, Integer category);
}
