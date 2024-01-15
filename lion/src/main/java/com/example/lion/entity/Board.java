package com.example.lion.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;
    private String title;
    @Column(length = 10000)
    private String content;
    private String password;

    private Integer category;

    // 게시글이 지워지면 댓글들은 모두 지워진다.
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>();


}
