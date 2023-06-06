package com.back.moment.photos.repository;

import com.back.moment.photos.entity.PhotoHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoHashTagRepository extends JpaRepository<PhotoHashTag, Long> {
    PhotoHashTag findByHashTag(String hashTag);
}
