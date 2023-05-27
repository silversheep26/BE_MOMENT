package com.back.moment.mainPage.service;

import com.back.moment.mainPage.dto.AfterLogInResponseDto;
import com.back.moment.mainPage.dto.BeforeLogInResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.users.dto.ForMainResponseDto;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
    private final PhotoRepository photoRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<BeforeLogInResponseDto> getMainPageSource(){
        List<OnlyPhotoResponseDto> photoList = photoRepository.getAllOnlyPhoto();

        List<OnlyPhotoResponseDto> topSixPhotos = photoList.stream()
                .sorted(Comparator.comparingInt(OnlyPhotoResponseDto::getLoveCnt).reversed())
                .limit(6)
                .toList();

        return new ResponseEntity<>(new BeforeLogInResponseDto(topSixPhotos), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<AfterLogInResponseDto> getHomePageSource(Users users){
        Pageable pageable = PageRequest.of(0, 3);
        if(users.getRole() == RoleEnum.MODEL){
            List<ForMainResponseDto> top3Photographers = usersRepository.findTop3Photographer(users.getRole(), pageable);
            return new ResponseEntity<>(new AfterLogInResponseDto(top3Photographers), HttpStatus.OK);
        }else if(users.getRole() == RoleEnum.PHOTOGRAPHER) {
            List<ForMainResponseDto> top3Models = usersRepository.findTop3Model(users.getRole(), pageable);
            return new ResponseEntity<>(new AfterLogInResponseDto(top3Models), HttpStatus.OK);
        }
        List<ForMainResponseDto> top3 = usersRepository.findTop3(pageable);
        return new ResponseEntity<>(new AfterLogInResponseDto(top3), HttpStatus.OK);
    }
}
