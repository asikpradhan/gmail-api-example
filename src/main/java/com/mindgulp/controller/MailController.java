package com.mindgulp.controller;

import com.mindgulp.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @GetMapping("/mail")
    public void send() {
        mailService.send("reciever@gmail.com", "Sending email using Gmail API", "Hello GMAIL world!");
    }
}
