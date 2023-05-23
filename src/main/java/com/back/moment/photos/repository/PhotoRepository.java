package com.back.moment.photos.repository;

import com.back.moment.photos.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
