package com.back.moment.photos.repository.getPhoto;

import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.Users;
import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.util.List;

public interface GetPhoto {
    List<Photo> findPhotosByCreatedAtAndUsers(Integer uploadCnt, Users users);
}
