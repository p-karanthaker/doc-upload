package me.karanthaker.scanner;

import lombok.extern.log4j.Log4j2;
import me.karanthaker.db.entity.Job;
import me.karanthaker.db.repository.JobRepository;
import me.karanthaker.s3.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;
import java.util.Random;

@Log4j2
@Service
public class ScannerService {

    @Autowired
    private JobRepository repository;

    @Autowired
    private StorageService storage;

    @Value("${scan-time}")
    private long scanTime;

    public void scan(int jobId) {
        Optional<Job> j = repository.findById(jobId);
        j.ifPresentOrElse(
                job -> {
                    job.setStatus(Job.Status.SCANNING);
                    repository.save(job);

                    try (InputStream is = storage.fetch(job.getDocumentKey())) {
                        if (is == null) {
                            throw new Exception("");
                        }
                        log.info("scanning {} on {}", jobId, Thread.currentThread().getName());
                        // Pretend to scan document
                        Thread.sleep((long) (Math.random() * scanTime));

                        // pass at 60% rate
                        if (new Random().nextDouble() < 0.60) {
                            job.setStatus(Job.Status.PENDING_APPROVAL);
                        } else {
                            job.setStatus(Job.Status.FAILED_SCAN);
                        }

                        repository.save(job);
                        log.info("finished scanning {}", jobId);
                    } catch (Exception e) {
                        log.error("failed to read document for scanning {}", e.getMessage());
                    }
                },
                () -> log.error("job doesn't exist"));
    }
}
