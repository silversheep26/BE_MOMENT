package com.back.moment.feed.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
import com.back.moment.feed.dto.FeedRequestDto;
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
    public ResponseEntity<Void> uploadImages(FeedRequestDto feedRequestDto, MultipartFile image, Users users) throws IOException {
        if(users.getRole() != RoleEnum.NONE) {
            String imageUrl = s3Uploader.upload(image);
            Photo photo = new Photo(users, imageUrl);
            if(feedRequestDto.getContent() != null)
                photo.updateContents(feedRequestDto.getContent());
            photoRepository.save(photo);

            if(feedRequestDto.getPhotoHashTag() != null) {
                feedRequestDto.setPhotoHashTag(feedRequestDto.getPhotoHashTag());

                for (String photoHashTag : feedRequestDto.getPhotoHashTag()) {
                    String photoHashTagString = photoHashTag.substring(1);
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
    public ResponseEntity<FeedListResponseDto> getAllFeeds(Pageable pageable) {
        List<Photo> getAllPhoto = photoRepository.getAllPhotoWithTag();
        Page<PhotoFeedResponseDto> photoPage;
        if(getAllPhoto.size() > pageable.getOffset()) {
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), getAllPhoto.size());
            List<PhotoFeedResponseDto> getAllPhotoDto = getAllPhoto.subList(startIndex, endIndex)
                    .stream()
                    .map(PhotoFeedResponseDto::new)
                    .toList();
            photoPage = new PageImpl<>(getAllPhotoDto, pageable, getAllPhoto.size());
        } else{
            photoPage = new PageImpl<>(getAllPhoto.stream()
                    .map(PhotoFeedResponseDto::new)
                    .toList(), pageable, getAllPhoto.size());
        }
        int currentPage = photoPage.getNumber();
        int totalPages;
        if(photoPage.isEmpty())
            totalPages = photoPage.getTotalPages();
        else totalPages = photoPage.getTotalPages() - 1;

        List<PhotoFeedResponseDto> photoList =new ArrayList<>(photoPage.getContent());
        List<PhotoFeedResponseDto> responsePhotoList = new ArrayList<>();
        // Sort the photoList by loveCnt in descending order

        photoList.sort(Comparator.comparingInt(PhotoFeedResponseDto::getLoveCnt).reversed());

        // Get the top three photos
        List<PhotoFeedResponseDto> topThreePhotos = photoList.stream()
                .limit(3)
                .toList();

        // Get the remaining photos
        List<PhotoFeedResponseDto> remainingPhotos = photoList.stream()
                .skip(3)
                .collect(Collectors.toList());

        // Shuffle the remaining photos
        Collections.shuffle(remainingPhotos);

        // Add the top three photos and remaining photos to the response list
        responsePhotoList.addAll(topThreePhotos);
        responsePhotoList.addAll(remainingPhotos);

        boolean hasMorePages = photoPage.hasNext();

        return new ResponseEntity<>(new FeedListResponseDto(responsePhotoList, hasMorePages, currentPage, totalPages), HttpStatus.OK);
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


}
