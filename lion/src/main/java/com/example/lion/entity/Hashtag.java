package com.example.lion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tag;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    // 연관관계 편의 메서드
    public void changeHashtag(Board board, String tag) {
        this.tag = tag;
        this.board = board;
        board.getHashtags().add(this);
    }

}
