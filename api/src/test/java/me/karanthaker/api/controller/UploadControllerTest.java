package me.karanthaker.api.controller;

import me.karanthaker.api.service.JmsProducer;
import me.karanthaker.db.entity.Job;
import me.karanthaker.db.repository.JobRepository;
import me.karanthaker.s3.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UploadControllerTest {

    private final MockMultipartFile mockFile
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
    );
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UploadController controller;
    @MockBean
    private JobRepository repository;
    @MockBean
    private StorageService storage;
    @MockBean
    private JmsProducer producer;

    @Test
    public void testHappyPath() throws Exception {
        when(repository.save(any(Job.class))).thenReturn(Job.builder().id(1).build());
        mvc.perform(MockMvcRequestBuilders.multipart("/upload").file(mockFile))
                .andExpect(status().isAccepted())
                .andExpect(content().string("sent for processing"));
    }

    @Test
    public void testFailure() throws Exception {
        doThrow(new IOException("")).when(storage).persist(any(UUID.class), any(MultipartFile.class));
        mvc.perform(MockMvcRequestBuilders.multipart("/upload").file(mockFile))
                .andExpect(status().isInternalServerError());
    }

}
