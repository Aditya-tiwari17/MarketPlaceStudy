package org.example.services;

import org.example.dtos.JobDTO;
import org.example.entities.Actor;
import org.example.entities.Job;
import org.example.enums.RoleEnum;
import org.example.exceptions.MarketPlaceException;
import org.example.repositories.JobRepository;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @InjectMocks
    private JobService jobService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should successfully post a job")
    void postJobSuccess() {
        JobDTO jobDTO = JobDTO.builder()
                .description("Sample Job")
                .requirements("Sample Requirements")
                .posterId(1L)
                .expireAt(LocalDateTime.now().plusDays(1))
                .build();

        Actor poster = Actor.builder()
                .id(1L)
                .role(RoleEnum.POSTER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(poster));

        String response = jobService.postJob(jobDTO);

        assertEquals("Job posted successfully!", response);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should throw exception if poster not found")
    void postJobPosterNotFound() {
        JobDTO jobDTO = JobDTO.builder()
                .posterId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> jobService.postJob(jobDTO));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Poster not found", exception.getEndUserMessage());
    }

    @Test
    @DisplayName("Should throw exception if user is not a poster")
    void postJobInvalidRole() {
        JobDTO jobDTO = JobDTO.builder()
                .posterId(1L)
                .build();

        Actor actor = Actor.builder()
                .id(1L)
                .role(RoleEnum.BIDDER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(actor));

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> jobService.postJob(jobDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User is not a poster", exception.getEndUserMessage());
    }

    @Test
    @DisplayName("Should successfully fetch a job by ID")
    void getJobSuccess() {
        Job job = Job.builder()
                .id(1L)
                .description("Sample Job")
                .requirements("Sample Requirements")
                .expireAt(LocalDateTime.now().plusDays(1))
                .poster(Actor.builder().id(1L).build())
                .build();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        JobDTO jobDTO = jobService.getJob(1L);

        assertNotNull(jobDTO);
        assertEquals("Sample Job", jobDTO.getDescription());
        assertEquals("Sample Requirements", jobDTO.getRequirements());
    }

    @Test
    @DisplayName("Should throw exception if job not found")
    void getJobNotFound() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> jobService.getJob(1L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Job not found", exception.getEndUserMessage());
    }
}
