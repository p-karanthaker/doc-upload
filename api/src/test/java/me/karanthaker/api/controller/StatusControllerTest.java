package me.karanthaker.api.controller;

import me.karanthaker.db.entity.Job;
import me.karanthaker.db.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class StatusControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private StatusController controller;

    @MockBean
    private JobRepository repository;

    private static Stream<String> approveAndReject() {
        return Stream.of("approve", "reject");
    }

    @Test
    public void testStatus() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/status"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPending() throws Exception {
        List<Job> jobs = List.of(Job.builder().id(1).status(Job.Status.PENDING_APPROVAL).build());
        when(repository.findByStatus(Job.Status.PENDING_APPROVAL)).thenReturn(jobs);
        mvc.perform(MockMvcRequestBuilders.get("/status/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is(Job.Status.PENDING_APPROVAL.toString())));
    }

    @Test
    public void testJobIdNotFound() throws Exception {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders.get("/status/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetJobId() throws Exception {
        when(repository.findById(anyInt())).thenReturn(Optional.of(Job.builder().status(Job.Status.QUEUED).build()));
        mvc.perform(MockMvcRequestBuilders.get("/status/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPendingEmpty() throws Exception {
        when(repository.findByStatus(Job.Status.PENDING_APPROVAL)).thenReturn(Collections.emptyList());
        mvc.perform(MockMvcRequestBuilders.get("/status/pending"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @MethodSource("approveAndReject")
    public void testApproveSuccess(String decision) throws Exception {
        Job job = Job.builder().status(Job.Status.PENDING_APPROVAL).build();
        when(repository.findById(anyInt())).thenReturn(Optional.of(job));
        when(repository.save(job)).thenReturn(Job.builder().status(Job.Status.APPROVED).build());

        String status = Job.Status.REJECTED.toString();
        if (Objects.equals(decision, "approve")) {
            status = Job.Status.APPROVED.toString();
        }
        mvc.perform(MockMvcRequestBuilders.patch("/status/1/" + decision))
                .andExpect(status().isOk())
                .andExpect(content().string(status));

        verify(repository, times(1)).save(job);
    }

    @ParameterizedTest
    @MethodSource("approveAndReject")
    public void testApproveNotAllowed(String decision) throws Exception {
        Job job = Job.builder().status(Job.Status.SCANNING).build();
        when(repository.findById(anyInt())).thenReturn(Optional.of(job));
        mvc.perform(MockMvcRequestBuilders.patch("/status/1/" + decision))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("this job is not pending approval"));

        verify(repository, times(0)).save(job);
    }

    @ParameterizedTest
    @MethodSource("approveAndReject")
    public void testApproveNotFound(String decision) throws Exception {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        mvc.perform(MockMvcRequestBuilders.patch("/status/1/" + decision))
                .andExpect(status().isNotFound())
                .andExpect(content().string("not found"));

        verify(repository, times(0)).save(any(Job.class));
    }

}
