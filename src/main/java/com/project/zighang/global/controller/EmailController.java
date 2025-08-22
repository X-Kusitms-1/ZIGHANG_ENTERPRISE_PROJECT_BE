package com.project.zighang.global.controller;

import com.project.zighang.global.service.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/email")
public class EmailController {
    private final EmailSender emailSender;

    @PostMapping()
    public ResponseEntity<?> sendEmail() {
        emailSender.sendNewsLetters("kjeng7897@gmail.com");
        return ResponseEntity.ok("이메일 전송 성공");
    }
}
