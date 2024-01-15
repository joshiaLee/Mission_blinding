package com.example.lion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    private String password;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    // 연관관계 편의 메서드
    public void changeComment(Board curBoard, Comment comment) {
        this.content = comment.getContent();
        this.password = comment.getPassword();
        this.board = curBoard;
        board.getComments().add(this);

    }

}
