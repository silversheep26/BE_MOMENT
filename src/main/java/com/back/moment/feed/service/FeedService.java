package com.back.moment.feed.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
//import com.back.moment.feed.dto.FeedRequestDto;
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
            if(contents != null)
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
    public ResponseEntity<String> lovePhoto(Long photoId, Users users) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        Love existLove = loveRepository.findExistLove(photoId, users.getId());

        String message;

        if(existLove != null){
            loveRepository.delete(existLove);
            message = "좋아요 취소";
            photo.getUsers().setTotalLoveCnt(photo.getUsers().getTotalLoveCnt() - 1);
        }else {
            Love love = new Love(users, photo);
            message = "좋아요 등록";
            loveRepository.save(love);
            photo.getUsers().setTotalLoveCnt(photo.getUsers().getTotalLoveCnt() + 1);
        }
        int loveCnt = loveRepository.findCntByPhotoId(photoId);
        photo.setLoveCnt(loveCnt);
        photoRepository.save(photo);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

//    @Transactional
//    public ResponseEntity<String> recommendUser(String nickName, Users users){
//        Users recommendedUser = usersRepository.findByNickName(nickName).orElseThrow(
//                () -> new ApiException(ExceptionEnum.NOT_FOUND_USER)
//        );
//
//        Recommend recommend = new Recommend(users, recommendedUser);
//        Recommend existRecommend = recommendRepository.existRecommend(users.getNickName(), nickName);
//
//        String message;
//
//        if(existRecommend != null){
//            recommendRepository.delete(existRecommend);
//            message = "추천 취소";
//        } else{
//            recommendRepository.save(recommend);
//            message = "추천 등록";
//        }
//        int recommendCnt = recommendRepository.countByRecommendedId(recommendedUser.getId());
//        recommendedUser.setRecommendCnt(recommendCnt);
//        usersRepository.save(recommendedUser);
//
//        return new ResponseEntity<>(message, HttpStatus.OK);
//    }

    @Transactional(readOnly = true)
    public ResponseEntity<FeedListResponseDto> getAllFeeds(Pageable pageable, Users users) {
        List<Photo> getAllPhoto = photoRepository.getAllPhotoWithTag();

        int currentPage = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        // Calculate the start and end index for the current page
        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, getAllPhoto.size());

        List<Photo> currentPagePhotos = getAllPhoto.subList(startIndex, endIndex);

        List<PhotoFeedResponseDto> responsePhotoList = new ArrayList<>(currentPagePhotos.size());

        if (users != null) {
            List<Long> photoIdList = getAllPhoto.stream().map(Photo::getId).collect(Collectors.toList());
            List<Object[]> photoLoveList = photoRepository.checkLoveList(photoIdList, users.getId());
            Map<Long, Boolean> photoLoveMap = new HashMap<>();

            for (Object[] result : photoLoveList) {
                Long photoId = (Long) result[0];
                Boolean isLoved = (Boolean) result[1];
                photoLoveMap.put(photoId, isLoved);
            }

            for (Photo photo : currentPagePhotos) {
                boolean isLoved = photoLoveMap.getOrDefault(photo.getId(), false);
                responsePhotoList.add(new PhotoFeedResponseDto(photo, isLoved));
            }
        } else {
            for (Photo photo : currentPagePhotos) {
                responsePhotoList.add(new PhotoFeedResponseDto(photo, false));
            }
        }

        // Sort the responsePhotoList by loveCnt in descending order
        responsePhotoList.sort(Comparator.comparingInt(PhotoFeedResponseDto::getLoveCnt).reversed());

        // Get the top three photos
        List<PhotoFeedResponseDto> topThreePhotos = responsePhotoList.stream()
                .limit(3)
                .toList();

        // Get the remaining photos
        List<PhotoFeedResponseDto> remainingPhotos = responsePhotoList.stream()
                .skip(3)
                .collect(Collectors.toList());

        // Shuffle the remaining photos
        Collections.shuffle(remainingPhotos);

        // Add the top three photos and remaining photos to the response list
        List<PhotoFeedResponseDto> finalPhotoList = new ArrayList<>();
        finalPhotoList.addAll(topThreePhotos);
        finalPhotoList.addAll(remainingPhotos);

        boolean hasMorePages = endIndex < getAllPhoto.size();

        int totalPages = (int) Math.ceil((double) getAllPhoto.size() / pageSize) - 1;

        return new ResponseEntity<>(new FeedListResponseDto(finalPhotoList, hasMorePages, currentPage, totalPages), HttpStatus.OK);
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
