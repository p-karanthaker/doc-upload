package me.karanthaker.api.controller;

import lombok.extern.log4j.Log4j2;
import me.karanthaker.api.service.JobService;
import me.karanthaker.db.entity.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/status")
public class StatusController {

    @Autowired
    private JobService service;

    @GetMapping
    public ResponseEntity<Iterable<Job>> getAll() {
        return service.readAll();
    }

    @GetMapping("/pending")
    public ResponseEntity<Iterable<Job>> getPendingApproval() {
        return service.readByStatus(Job.Status.PENDING_APPROVAL);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<String> getStatus(@PathVariable int jobId) {
        return service.read(jobId);
    }

    @PatchMapping("/{jobId}/approve")
    public ResponseEntity<String> approve(@PathVariable int jobId) {
        return service.updateStatus(jobId, Job.Status.APPROVED);
    }

    @PatchMapping("/{jobId}/reject")
    public ResponseEntity<String> reject(@PathVariable int jobId) {
        return service.updateStatus(jobId, Job.Status.REJECTED);
    }

}
