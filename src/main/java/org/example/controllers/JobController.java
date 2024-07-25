package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dtos.JobDTO;
import org.example.services.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/jobs")
@PreAuthorize("isAuthenticated()")
public class JobController {
    private final JobService jobService;

    /**
     Retrieves a list of the most recently posted jobs
     @return a list of JobDTOs representing recent jobs
     **/
    @GetMapping("/recent")
    public ResponseEntity<List<JobDTO>> getRecentJobs() {
        return ResponseEntity.ok().body(jobService.getRecentJobs());
    }

    /**
     Retrieves a list of active jobs, ordered by the number of bids
     @return a list of JobDTOs representing active jobs
     **/
    @GetMapping("/active")
    public ResponseEntity<List<JobDTO>> getActiveJobs() {
        return ResponseEntity.ok().body(jobService.getActiveJobs());
    }

    /**
     Posts a new job on the platform
     @param jobDTO the job data transfer object containing job details
     @return a message indicating the success of the job posting
     **/
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('ROLE_POSTER')")
    public ResponseEntity<String> postJob(@RequestBody JobDTO jobDTO) {
        return ResponseEntity.ok().body(jobService.postJob(jobDTO));
    }

    /**
     Retrieves the details of a specific job
     @param jobId the ID of the job to retrieve
     @return a JobDTO containing the job details
     **/
    @GetMapping("/{jobId}")
    public JobDTO getJob(@PathVariable("jobId") Long jobId) {
        return jobService.getJob(jobId);
    }
}
