package com.project.zighang.global.client.email.service;

import com.project.zighang.global.client.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendNewsLetters(String destinationEmail) {
        var msg = new org.springframework.mail.SimpleMailMessage();
        msg.setFrom(senderEmail);                 // spring.mail.username과 같게
        msg.setTo(destinationEmail);
        msg.setSubject("[TEST] SimpleMailMessage");
        msg.setText("SMTP auth OK + Plain text body");
        javaMailSender.send(msg);
    }
}
