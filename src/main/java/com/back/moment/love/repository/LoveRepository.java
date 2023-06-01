package com.back.moment.love.repository;

import com.back.moment.love.entity.Love;
import com.back.moment.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoveRepository extends JpaRepository<Love, Long> {
    @Query("select l from Love l where l.photo.id = :photoId and l.users.id = :userId")
    Love findExistLove(@Param("photoId") Long photoId, @Param("userId") Long userId);

    @Query("select count(l) from Love l where l.photo.id = :photoId")
    int findCntByPhotoId(@Param("photoId") Long photoId);

    boolean existsByPhotoIdAndUsersId(Long photoId, Long usersId);
}
