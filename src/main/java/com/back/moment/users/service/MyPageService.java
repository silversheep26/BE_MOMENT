package com.back.moment.users.service;

import com.back.moment.boards.dto.BoardListResponseDto;
import com.back.moment.boards.entity.Board;
import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.photos.dto.PhotoMyPageResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.users.dto.MyPageResponseDto;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UsersRepository usersRepository;
    private final PhotoRepository photoRepository;

    // 마이페이지 조회
    @Transactional(readOnly = true)
    public ResponseEntity<MyPageResponseDto> getMyPage(String nickName, Users users){
        existUser(users.getEmail());
        Users host = usersRepository.findByNickName(nickName).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_MATCH_USERS)
        );

        List<BoardListResponseDto> boardList = new ArrayList<>();

        for(Board board : host.getBoardList()){
            boardList.add(new BoardListResponseDto(board));
        }
        
        List<PhotoMyPageResponseDto> photoList = photoRepository.getAllOnlyPhoto();
        photoList = photoList.stream()
                .sorted(Comparator.comparingInt(PhotoMyPageResponseDto::getLoveCnt).reversed())
                .toList();

        return new ResponseEntity<>(new MyPageResponseDto(host, boardList, photoList), HttpStatus.OK);
    }
    
    // 마이페이지 수정
    
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
        photoRepository.delete(photo);

        return ResponseEntity.ok(null);
    }

    public void existUser(String email) {
        usersRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_MATCH_USERS)
        );
    }
    
}
