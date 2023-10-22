package me.karanthaker.scanner;

import me.karanthaker.db.entity.Job;
import me.karanthaker.db.repository.JobRepository;
import me.karanthaker.s3.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
class ScannerServiceTests {

    @Autowired
    private ScannerService service;

    @MockBean
    private JobRepository repository;

    @MockBean
    private JmsConsumer consumer;

    @MockBean
    private StorageService storage;

    @Test
    void testJobNotFound() {
        service.scan(1);
        verify(repository, times(1)).findById(1);
        verifyNoMoreInteractions(repository, storage);
    }

    @Test
    void testFailedFetch() {
        when(repository.findById(1)).thenReturn(Optional.of(Job.builder().build()));

        service.scan(1);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(any());
        verify(storage, times(1)).fetch(null);
        verifyNoMoreInteractions(repository, storage);
    }

    @Test
    void testEmptyObject() {
        when(repository.findById(1)).thenReturn(Optional.of(Job.builder().build()));
        when(storage.fetch(any())).thenReturn(null);

        service.scan(1);
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(any());
        verify(storage, times(1)).fetch(null);
        verifyNoMoreInteractions(repository, storage);
    }

    @Test
    void testSuccessful() {
        Job job = Job.builder().build();
        when(repository.findById(1)).thenReturn(Optional.of(job));
        when(storage.fetch(any())).thenReturn(new ByteArrayInputStream(new byte[]{}));

        service.scan(1);
        verify(repository, times(1)).findById(1);
        verify(repository, times(2)).save(job);
        verify(storage, times(1)).fetch(null);
        verifyNoMoreInteractions(repository, storage);
    }

}

