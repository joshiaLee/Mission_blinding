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
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Hashtag> hashtags = new ArrayList<>();

}
