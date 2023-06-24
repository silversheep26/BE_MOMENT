package com.back.moment.photos.repository.getAll;

import com.back.moment.photos.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GetAllPhotoByLove {
    List<Photo> getAllPhotoWithTagByLove();
}
