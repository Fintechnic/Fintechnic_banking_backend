package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.HomeDTO;
import com.fintechnic.backend.service.HomeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

@Slf4j
@RestController
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/api/home")
    public ResponseEntity<HomeDTO> getHomeInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            HomeDTO homeInfo = homeService.getHomeInformation(authHeader);
            return ResponseEntity.ok(homeInfo);
        } catch (AccountNotFoundException e) {
            log.info("User not found");
            return ResponseEntity.badRequest().build();
        }
    }
}
