package org.example.repositories;

import org.example.entities.Job;
import org.example.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findTop10ByOrderByPostedAtDesc();
    List<Job> findAllByExpireAtBeforeAndStatus(LocalDateTime dateTime, JobStatus status);
    List<Job> findTop10ByStatusOrderByBidCountDesc(JobStatus status);
}
