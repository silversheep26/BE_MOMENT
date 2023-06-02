package com.back.moment.feed.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
import com.back.moment.love.entity.Love;
import com.back.moment.love.repository.LoveRepository;
import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.PhotoRepository;
//import com.back.moment.recommend.entity.Recommend;
//import com.back.moment.recommend.repository.RecommendRepository;
import com.back.moment.s3.S3Uploader;
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
//    private final RecommendRepository recommendRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public ResponseEntity<Void> uploadImages(String content, List<MultipartFile> imageList, Users users) throws IOException {

        for(MultipartFile image : imageList){
            Photo photo = new Photo();
            String imageUrl = s3Uploader.upload(image);
            photo = new Photo(users, imageUrl);
            if(content != null)
                photo.updateContents(content);
            photoRepository.save(photo);
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
        Page<PhotoFeedResponseDto> photoPage = photoRepository.getAllPhoto(pageable);

        List<PhotoFeedResponseDto> photoList = photoPage.getContent();
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

        return new ResponseEntity<>(new FeedListResponseDto(responsePhotoList, hasMorePages), HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    public ResponseEntity<FeedDetailResponseDto> getFeed(Long photoId, Users users){
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        boolean checkLove = loveRepository.checkLove(photo.getId(), users.getId());

        FeedDetailResponseDto feedDetailResponseDto = new FeedDetailResponseDto(photo.getUsers().getId(),
                                                                                photo.getImagUrl(),
                                                                                photo.getLoveCnt(),
                                                                                photo.getUsers().getProfileImg(),
                                                                                photo.getUsers().getNickName(),
                                                                                photo.getUsers().getRole(),
                                                                                photo.getContents());
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
