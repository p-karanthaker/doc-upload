package me.karanthaker.api.controller;

import lombok.extern.log4j.Log4j2;
import me.karanthaker.api.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
public class UploadController {

    @Autowired
    private JobService service;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        return service.create(file);
    }
}
