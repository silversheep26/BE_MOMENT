package com.back.moment.mainPage.service;

import com.back.moment.boards.dto.MyPageBoardListResponseDto;
import com.back.moment.boards.repository.BoardRepository;
import com.back.moment.mainPage.dto.AfterLogInResponseDto;
import com.back.moment.mainPage.dto.BeforeLogInResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.users.dto.ForMainResponseDto;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final PhotoRepository photoRepository;
    private final UsersRepository usersRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<BeforeLogInResponseDto> getMainPageSource(){
        List<OnlyPhotoResponseDto> photoList = photoRepository.getAllOnlyPhoto();

        List<OnlyPhotoResponseDto> topSixPhotos = photoList.stream()
                .sorted(Comparator.comparingInt(OnlyPhotoResponseDto::getLoveCnt).reversed())
                .limit(9)
                .toList();

        return new ResponseEntity<>(new BeforeLogInResponseDto(topSixPhotos), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<AfterLogInResponseDto> getHomePageSource(Users users) {
        Pageable pageable = PageRequest.of(0, 4);
        List<MyPageBoardListResponseDto> topFourBoard;

        List<ForMainResponseDto> top4;
        String targetRole = null;

        if (users != null && users.getRole() != null) {
            if (users.getRole() == "MODEL") {
                targetRole = "PHOTOGRAPHER";
            } else if (users.getRole() == "PHOTOGRAPHER") {
                targetRole = "MODEL";
            }
        }


        if (targetRole != null) {
            topFourBoard = boardRepository.selectAllEachRoleBoardList(targetRole)
                    .stream()
                    .limit(4)
                    .toList();

            top4 = usersRepository.findTop4ByRole(targetRole, pageable);
        } else {
            topFourBoard = boardRepository.selectAllBoardList()
                    .stream()
                    .limit(4)
                    .toList();

            top4 = usersRepository.findTop4(pageable);
        }

        return new ResponseEntity<>(new AfterLogInResponseDto(top4, topFourBoard), HttpStatus.OK);
    }

}
