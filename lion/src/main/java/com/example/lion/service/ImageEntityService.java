package com.example.lion.service;

import com.example.lion.entity.ImageEntity;
import com.example.lion.repository.ImageEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageEntityService {
    private final ImageEntityRepository imageEntityRepository;

    public void ImageJoin(ImageEntity imageEntity){
        imageEntityRepository.save(imageEntity);
    }

    public void ImageDeleteById(Long id){
        imageEntityRepository.deleteById(id);
    }
}
