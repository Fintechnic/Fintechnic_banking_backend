package com.fintechnic.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fintechnic.backend.dto.SystemStatsDTO;
import com.fintechnic.backend.service.SystemStatsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class SystemStatsController {
    private final SystemStatsService systemStatsService;

    @GetMapping("/systemstats")
    public ResponseEntity<SystemStatsDTO> getAllStats(){
        return ResponseEntity.ok(systemStatsService.getAllStats());
        
    }

}
