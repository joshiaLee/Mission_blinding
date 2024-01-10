package com.example.lion.repository;

import com.example.lion.entity.Board;
import com.example.lion.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {


}
