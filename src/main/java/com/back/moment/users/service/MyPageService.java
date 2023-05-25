package com.back.moment.users.service;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.entity.Board;
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
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    // 마이페이지 조회
    @Transactional(readOnly = true)
    public ResponseEntity<MyPageResponseDto> getMyPage(Long hostId, Users users){
        existUser(users.getEmail());
        Users host = usersRepository.findById(hostId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_MATCH_USERS)
        );

        List<BoardListResponseDto> boardList = new ArrayList<>();

        for(Board board : host.getBoardList()){
            boardList.add(new BoardListResponseDto(board));
        }
        
        List<OnlyPhotoResponseDto> photoList = photoRepository.getAllOnlyPhotoByHostId(host.getId());
        photoList = photoList.stream()
                .sorted(Comparator.comparingInt(OnlyPhotoResponseDto::getLoveCnt).reversed())
                .toList();

        return new ResponseEntity<>(new MyPageResponseDto(host, boardList, photoList), HttpStatus.OK);
    }
    
    // 마이페이지 수정
    @Transactional
    public ResponseEntity<Void> updateMyPage(Long hostId, UpdateRequestDto updateRequestDto, Users users, MultipartFile profileImg) throws IOException {
        if(!Objects.equals(hostId, users.getId())){
            throw new ApiException(ExceptionEnum.NOT_MATCH_USERS);
        }

        Optional<Users> findNickName = usersRepository.findByNickName(updateRequestDto.getNickName());
        if(findNickName.isPresent()){
            throw new ApiException(ExceptionEnum.DUPLICATED_NICKNAME);
        }
        String changeNickName = null;
        String password = null;
        String profileUrl = null;
        RoleEnum role = null;
        if(updateRequestDto.getNickName() != null) {
            changeNickName = updateRequestDto.getNickName();
        }
        if(updateRequestDto.getPassword() != null) {
            password = passwordEncoder.encode(updateRequestDto.getPassword());
        }
        if(updateRequestDto.getRole() != null){
            if(users.getRole() == null)
                role = updateRequestDto.getRole();
        }
        if(!profileImg.isEmpty()) {
            profileUrl = s3Uploader.upload(profileImg);
        }
        users.updateUsers(changeNickName, profileUrl, password, role);
        usersRepository.save(users);

        return ResponseEntity.ok(null);
    }
    
    // 마이페이지 사진 삭제 
    @Transactional
    public ResponseEntity<String> deletePhoto(Long photoId, Users users){
        existUser(users.getEmail());
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

    public void existUser(String email) {
        usersRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_MATCH_USERS)
        );
    }
    
}
