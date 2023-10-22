package me.karanthaker.api.service;

import lombok.extern.log4j.Log4j2;
import me.karanthaker.db.entity.Job;
import me.karanthaker.db.repository.JobRepository;
import me.karanthaker.s3.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class JobService {

    @Autowired
    private JobRepository repository;

    @Autowired
    private StorageService storage;

    @Autowired
    private JmsProducer producer;

    @Transactional
    public ResponseEntity<String> create(MultipartFile file) {
        try {
            final UUID uuid = UUID.randomUUID();

            storage.persist(uuid, file);

            Job j = Job.builder()
                    .status(Job.Status.QUEUED)
                    .documentName(file.getOriginalFilename())
                    .documentKey(uuid)
                    .build();
            j = repository.save(j);

            producer.sendMessage(j.getId());
            return new ResponseEntity<>("sent for processing", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Failed to send job for processing {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> read(int id) {
        Optional<Job> j = repository.findById(id);
        return j.map(job -> new ResponseEntity<>(job.getStatus().toString(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("not found", HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<Iterable<Job>> readAll() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Iterable<Job>> readByStatus(Job.Status status) {
        return new ResponseEntity<>(repository.findByStatus(status), HttpStatus.OK);
    }

    public ResponseEntity<String> updateStatus(int id, Job.Status status) {
        Optional<Job> j = repository.findById(id);
        return j.map(job -> {
                    if (j.get().getStatus() != Job.Status.PENDING_APPROVAL) {
                        return new ResponseEntity<>("this job is not pending approval", HttpStatus.BAD_REQUEST);
                    } else {
                        job.setStatus(status);
                    }
                    repository.save(job);
                    return new ResponseEntity<>(job.getStatus().toString(), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>("not found", HttpStatus.NOT_FOUND));
    }

}
