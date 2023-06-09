package com.back.moment.photos.repository;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
//    @Query("select new com.back.moment.photos.dto.PhotoFeedResponseDto(p) from Photo p")
//    Page<PhotoFeedResponseDto> getAllPhoto(Pageable pageable);

    @Query("select distinct p from Photo p left join fetch p.tag_photoList pt left join fetch pt.photoHashTag ptp")
    List<Photo> getAllPhotoWithTag();

    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p")
    List<OnlyPhotoResponseDto> getAllOnlyPhoto();

    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p where p.users.id = :hostId order by p.loveCnt desc ")
    List<OnlyPhotoResponseDto> getAllOnlyPhotoByHostId(@Param("hostId") Long hostId);

//    boolean existsByIdAndUsersId(Long photoId, Long userId);
    @Query("SELECT p.id, CASE WHEN (COUNT(l) > 0) THEN true ELSE false END FROM Photo p LEFT JOIN p.loveList l ON l.users.id = :usersId WHERE p.id IN :photoIdList GROUP BY p.id")
    List<Object[]> checkLoveList(@Param("photoIdList") List<Long> photoIdList, @Param("usersId") Long usersId);

    @Query("delete from Photo p where p.id = :photoId")
    void removeFeed(@Param("photoId") Long photoId);

    void deleteAllByUsersId(Long usersId);

    List<Photo> findByUsers(Users users);
}