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


    public Comment(String content, String password) {
        this.content = content;
        this.password = password;
    }

    public Comment(){

    }
}
