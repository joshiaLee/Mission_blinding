package com.example.lion.repository;

import com.example.lion.entity.Board;
import com.example.lion.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByTitleContaining(String searchKeyword);

    List<Board> findByCategory(Integer category);

    List<Board> findByTitleContainingAndCategoryEquals(String searchKeyword, Integer category);

    List<Board> findByContentContainingAndCategoryEquals(String searchKeyword, Integer category);

    List<Board> findByContentContaining(String searchKeyword);

    List<Board> findByHashtagsEquals(String hashtags);
}
