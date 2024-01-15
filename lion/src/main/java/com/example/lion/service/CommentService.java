package com.example.lion.service;


import com.example.lion.entity.Comment;
import com.example.lion.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void join(Comment comment){
        commentRepository.save(comment);
    }

    public Comment findById(Long id){
        return commentRepository.findById(id).get();
    }

    public void delete(Comment comment){
        commentRepository.delete(comment);
    }

}
