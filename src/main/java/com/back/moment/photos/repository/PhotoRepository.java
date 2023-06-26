package com.back.moment.photos.repository;

import com.back.moment.boards.entity.Board;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.feedSearch.FeedSearch;
import com.back.moment.photos.repository.getAll.GetAllPhoto;
import com.back.moment.photos.repository.getAll.GetAllPhotoByLove;
import com.back.moment.photos.repository.getPhoto.GetPhoto;
import com.back.moment.photos.repository.getPhotoWhoLove.GetPhotoWhoLove;
import com.back.moment.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long>, GetAllPhoto, GetAllPhotoByLove, GetPhoto, GetPhotoWhoLove, FeedSearch {
//    @Query("select new com.back.moment.photos.dto.PhotoFeedResponseDto(p) from Photo p")
//    Page<PhotoFeedResponseDto> getAllPhoto(Pageable pageable);

//    @Query("select distinct p from Photo p left join fetch p.tag_photoList pt left join fetch pt.photoHashTag ptp order by p.createdAt desc")
//    List<Photo> getAllPhotoWithTag();
//
//    @Query("select distinct p from Photo p left join fetch p.tag_photoList pt left join fetch pt.photoHashTag ptp order by p.loveCnt desc")
//    List<Photo> getAllPhotoWithTagByLove();

    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p")
    List<OnlyPhotoResponseDto> getAllOnlyPhoto();

//    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p where p.users.id = :hostId order by p.loveCnt desc ")
//    List<OnlyPhotoResponseDto> getAllOnlyPhotoByHostId(@Param("hostId") Long hostId);

//    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p where p.users.id = :hostId order by p.createdAt desc")
//    List<OnlyPhotoResponseDto> getAllOnlyPhotoByHostId(@Param("hostId") Long hostId);

//    boolean existsByIdAndUsersId(Long photoId, Long userId);
//    @Query("SELECT p.id, CASE WHEN (COUNT(l) > 0) THEN true ELSE false END FROM Photo p LEFT JOIN p.loveList l ON l.users.id = :usersId WHERE p.id IN :photoIdList GROUP BY p.id")
//    List<Object[]> checkLoveList(@Param("photoIdList") List<Long> photoIdList, @Param("usersId") Long usersId);
//
//    @Query("delete from Photo p where p.id = :photoId")
//    void removeFeed(@Param("photoId") Long photoId);
//
//    void deleteAllByUsersId(Long usersId);

    List<Photo> findByUsers(Users users);

    @Query("SELECT DISTINCT p FROM Photo p LEFT JOIN FETCH p.tag_photoList tp left JOIN FETCH tp.photoHashTag tpl WHERE p.id = :photoId")
    Optional<Photo> findExistPhoto(@Param("photoId") Long photoId);

    int countByUsers(Users users);

    List<Photo> findAllByUploadCnt(int uploadCnt);
}