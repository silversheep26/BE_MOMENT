package com.back.moment.mainPage.controller;

import com.back.moment.mainPage.dto.AfterLogInResponseDto;
import com.back.moment.mainPage.dto.BeforeLogInResponseDto;
import com.back.moment.mainPage.service.MainService;
import com.back.moment.users.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    @GetMapping("main")
    public ResponseEntity<BeforeLogInResponseDto> getMainPageSource(){
        return mainService.getMainPageSource();
    }

    @GetMapping("home")
    public ResponseEntity<AfterLogInResponseDto> getHomePageSource(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mainService.getHomePageSource(userDetails.getUsers());
    }

}
