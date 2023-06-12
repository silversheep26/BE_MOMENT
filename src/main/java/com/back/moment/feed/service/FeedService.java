package com.back.moment.feed.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
//import com.back.moment.feed.dto.FeedRequestDto;
import com.back.moment.feed.dto.LoveCheckResponseDto;
import com.back.moment.love.entity.Love;
import com.back.moment.love.repository.LoveRepository;
import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.entity.PhotoHashTag;
import com.back.moment.photos.entity.Tag_Photo;
import com.back.moment.photos.repository.PhotoHashTagRepository;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.photos.repository.Tag_PhotoRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final S3Uploader s3Uploader;
    private final PhotoRepository photoRepository;
    private final LoveRepository loveRepository;
    private final Tag_PhotoRepository tag_photoRepository;
    private final PhotoHashTagRepository photoHashTagRepository;
    //    private final RecommendRepository recommendRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public ResponseEntity<Void> uploadImages(String contents, List<String> photoHashTag, MultipartFile image, Users users) throws IOException {
        List<String> hashTags;
        if(users.getRole() != RoleEnum.NONE) {
            String imageUrl = s3Uploader.upload(image);
            Photo photo = new Photo(users, imageUrl);
            photo.updateContents(contents);
            photoRepository.save(photo);

            if(photoHashTag != null) {
                hashTags = photoHashTag.stream()
                        .filter(tag -> tag.startsWith("#"))
                        .collect(Collectors.toList());

                for (String hashTag : hashTags) {
                    String photoHashTagString = hashTag.substring(1);
                    PhotoHashTag existTag = photoHashTagRepository.findByHashTag(photoHashTagString);
                    if (existTag != null) {
                        Tag_Photo tag_photo = new Tag_Photo(existTag, photo);
                        tag_photoRepository.save(tag_photo);
                    } else {
                        PhotoHashTag photoHashTagTable = new PhotoHashTag(photoHashTagString);
                        photoHashTagRepository.save(photoHashTagTable);
                        Tag_Photo tag_photo = new Tag_Photo(photoHashTagTable, photo);
                        tag_photoRepository.save(tag_photo);
                    }
                }
            }
        } else{
            throw new ApiException(ExceptionEnum.NOT_FOUND_ROLE);
        }
        return ResponseEntity.ok(null);
    }

    @Transactional
    public ResponseEntity<LoveCheckResponseDto> lovePhoto(Long photoId, Users users) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        Love existLove = loveRepository.findExistLove(photoId, users.getId());

        String message;
        LoveCheckResponseDto loveCheckResponseDto;

        if(existLove != null){
            loveRepository.delete(existLove);
            message = "좋아요 취소";
            photo.getUsers().setTotalLoveCnt(photo.getUsers().getTotalLoveCnt() - 1);
            loveCheckResponseDto = new LoveCheckResponseDto(false);
        }else {
            Love love = new Love(users, photo);
            message = "좋아요 등록";
            loveRepository.save(love);
            photo.getUsers().setTotalLoveCnt(photo.getUsers().getTotalLoveCnt() + 1);
            loveCheckResponseDto = new LoveCheckResponseDto(true);
        }
        int loveCnt = loveRepository.findCntByPhotoId(photoId);
        photo.setLoveCnt(loveCnt);
        loveCheckResponseDto.setTotalLoveCnt(loveCnt);
        photoRepository.save(photo);

        return new ResponseEntity<>(loveCheckResponseDto, HttpStatus.OK);
    }



    @Transactional(readOnly = true)
    public ResponseEntity<FeedListResponseDto> getAllFeeds(Pageable pageable, Users users) {
        List<Photo> allPhoto = photoRepository.getAllPhotoWithTag();
        List<Photo> allPhotoByLove = photoRepository.getAllPhotoWithTagByLove();

        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        // Calculate the start and end index for the current page
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allPhoto.size());

        List<Photo> currentPagePhotos1 = allPhoto.subList(startIndex, endIndex);
        List<Photo> currentPagePhotos2 = allPhotoByLove.subList(startIndex, endIndex);

        boolean hasMorePages = endIndex < allPhoto.size();

        int totalPages = (int) Math.ceil((double) allPhoto.size() / pageSize) - 1;

        Page<PhotoFeedResponseDto> page1 = createResponsePhotoPage(pageable, currentPagePhotos1, users);
        Page<PhotoFeedResponseDto> page2 = createResponsePhotoPage(pageable, currentPagePhotos2, users);

        FeedListResponseDto responseDto = new FeedListResponseDto(page1, page2, hasMorePages, currentPage, totalPages);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private Page<PhotoFeedResponseDto> createResponsePhotoPage(Pageable pageable, List<Photo> photos, Users users) {
        List<Long> photoIdList = photos.stream().map(Photo::getId).collect(Collectors.toList());
        List<Object[]> photoLoveList = photoRepository.checkLoveList(photoIdList, users != null ? users.getId() : null);
        Map<Long, Boolean> photoLoveMap = new HashMap<>();

        for (Object[] result : photoLoveList) {
            Long photoId = (Long) result[0];
            Boolean isLoved = (Boolean) result[1];
            photoLoveMap.put(photoId, isLoved);
        }

        List<PhotoFeedResponseDto> responsePhotoList = new ArrayList<>(photos.size());

        for (Photo photo : photos) {
            boolean isLoved = photoLoveMap.getOrDefault(photo.getId(), false);
            responsePhotoList.add(new PhotoFeedResponseDto(photo, isLoved));
        }

        return new PageImpl<>(responsePhotoList, pageable, responsePhotoList.size());
    }





    @Transactional(readOnly = true)
    public ResponseEntity<FeedDetailResponseDto> getFeed(Long photoId, Users users){
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        FeedDetailResponseDto feedDetailResponseDto = new FeedDetailResponseDto(photo);
        feedDetailResponseDto.setCheckLove(loveRepository.checkLove(photo.getId(), users.getId()));

//        if(recommendRepository.existsByRecommendedIdAndRecommenderId(photo.getUsers().getId(), users.getId())) feedDetailResponseDto.setCheckRecommend(true);

        return new ResponseEntity<>(feedDetailResponseDto, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> writeContents(Long photoId, String content, Users users){
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );
        if(!Objects.equals(photo.getUsers().getId(), users.getId()))
            throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);

        photo.updateContents(content);
        photoRepository.save(photo);

        return ResponseEntity.ok(null);
    }

//    private boolean isPhotoLoved(Photo photo, Users currentUser) {
//        return currentUser.getLoveList().stream()
//                .anyMatch(love -> love.getPhoto().equals(photo));
//    }
}
