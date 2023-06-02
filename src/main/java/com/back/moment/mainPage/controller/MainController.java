package com.back.moment.mainPage.controller;

import com.back.moment.mainPage.dto.AfterLogInResponseDto;
import com.back.moment.mainPage.dto.BeforeLogInResponseDto;
import com.back.moment.mainPage.service.MainService;
import com.back.moment.users.entity.Users;
import com.back.moment.users.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<BeforeLogInResponseDto> getMainPageSource(HttpServletResponse response){
        response.setHeader("Cache-Control", "no-store");
        return mainService.getMainPageSource();
    }

    @GetMapping("home")
    public ResponseEntity<AfterLogInResponseDto> getHomePageSource(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Users users = userDetails != null ? userDetails.getUsers() : null;
        return mainService.getHomePageSource(users);
    }

}
