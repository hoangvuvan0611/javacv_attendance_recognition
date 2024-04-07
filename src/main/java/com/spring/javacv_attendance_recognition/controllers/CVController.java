package com.spring.javacv_attendance_recognition.controllers;

import com.spring.javacv_attendance_recognition.sevices.CVService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cv")
public class CVController {

    private final CVService cvService;

    public CVController(CVService cvService) {
        this.cvService = cvService;
    }

    @GetMapping("/train")
    public String trainModel(){
        return cvService.trainModel();
    }

    @GetMapping("/recognize")
    public String recognize(){
        return cvService.recognize();
    }
}
