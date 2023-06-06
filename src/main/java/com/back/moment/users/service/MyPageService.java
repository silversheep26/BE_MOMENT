package com.back.moment.users.service;

import com.back.moment.boards.dto.MyPageBoardListResponseDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.dto.MyPageResponseDto;
import com.back.moment.users.dto.UpdateRequestDto;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UsersRepository usersRepository;
    private final PhotoRepository photoRepository;
    private final BoardRepository boardRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    // 마이페이지 조회
    @Transactional(readOnly = true)
    public ResponseEntity<MyPageResponseDto> getMyPage(Long hostId, Users users){
        Users host = usersRepository.findById(hostId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_MATCH_USERS)
        );

        List<Board> boardList = boardRepository.getBoardListByHostIdWithFetch(hostId);
        List<MyPageBoardListResponseDto> myPageBoardListResponseDtoList = boardList.stream()
                .map(MyPageBoardListResponseDto::new)
                .toList();

        List<OnlyPhotoResponseDto> photoList = photoRepository.getAllOnlyPhotoByHostId(host.getId());

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(users, myPageBoardListResponseDtoList, photoList);

        if (users.getKakaoId() != null){
            myPageResponseDto.setCheckKakaoId(true);
        }

        return new ResponseEntity<>(myPageResponseDto, HttpStatus.OK);
    }
    
    // 마이페이지 수정
    @Transactional
    public ResponseEntity<Void> updateMyPage(Long hostId, UpdateRequestDto updateRequestDto, Users users, MultipartFile profileImg) throws IOException {
        if(!Objects.equals(hostId, users.getId())){
            throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);
        }

        if(!updateRequestDto.getNickName().isEmpty()) {
            String changeNickName = updateRequestDto.getNickName();

            Optional<Users> findNickName = usersRepository.findByNickName(changeNickName);
            if (findNickName.isPresent()) {
                throw new ApiException(ExceptionEnum.DUPLICATED_NICKNAME);
            }
            users.setNickName(changeNickName);
        }
        if(!updateRequestDto.getPassword().isEmpty()) {
            String password = passwordEncoder.encode(updateRequestDto.getPassword());

            users.setPassword(password);
        }
        if(updateRequestDto.getRole() != null && !updateRequestDto.getRole().isEmpty()){
            RoleEnum role = RoleEnum.valueOf(updateRequestDto.getRole());
            users.setRole(role);
        }
        if(profileImg != null) {
            String profileUrl = s3Uploader.upload(profileImg);
            users.setProfileImg(profileUrl);
        }

        usersRepository.save(users);

        return ResponseEntity.ok(null);
    }
    
    // 마이페이지 사진 삭제 
    @Transactional
    public ResponseEntity<String> deletePhoto(Long photoId, Users users){
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        if(!Objects.equals(users.getId(), photo.getUsers().getId())){
            return new ResponseEntity<>("작성자만 삭제 가능", HttpStatus.BAD_REQUEST);
        }
        s3Uploader.delete(photo.getImagUrl());
        photoRepository.delete(photo);

        return ResponseEntity.ok(null);
    }
}
