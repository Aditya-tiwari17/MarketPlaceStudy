package org.example.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dtos.JobDTO;
import org.example.entities.Actor;
import org.example.entities.Bid;
import org.example.entities.Job;
import org.example.enums.JobStatus;
import org.example.enums.RoleEnum;
import org.example.exceptions.MarketPlaceException;
import org.example.repositories.BidRepository;
import org.example.repositories.JobRepository;
import org.example.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Log4j2
@AllArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    /**
     Scheduled method to close expired jobs every 5 minutes.
     **/
    @Scheduled(fixedRate = 300000)
    public void closeExpiredJobs() {
        log.info("Checking for expired jobs to close.");
        List<Job> expiredJobs = jobRepository.findAllByExpireAtBeforeAndStatus(LocalDateTime.now(), JobStatus.OPEN);
        for (Job job : expiredJobs) {
            closeJob(job);
        }
    }

    /**
     Closes a job and assigns the winner if applicable.
     @param job the job to be closed
     **/
    @Transactional
    private void closeJob(Job job) {
        log.info("Closing job ID: {}", job.getId());
        job.setStatus(JobStatus.CLOSED);

        Bid lowestBid = bidRepository.findFirstByJobOrderByAmountAsc(job);
        if (lowestBid != null) {
            Actor winner = lowestBid.getBidder();
            job.setWinner(winner);
            bidRepository.save(lowestBid);

            log.info("Job ID: {} closed. Winner: User ID: {}", job.getId(), winner.getId());
            // TODO: Implement notification sending to winner and other bidders
            // sendNotificationToWinner(winner);
            // sendNotificationToOtherBidders(job);
        }
        jobRepository.save(job);
    }

    /**
     Retrieves the 10 most recently posted jobs.
     @return a list of JobDTOs
     **/
    public List<JobDTO> getRecentJobs() {
        log.info("Fetching 10 most recent jobs.");
        return jobRepository.findTop10ByOrderByPostedAtDesc().stream()
                .map(job -> JobDTO.builder()
                        .jobId(job.getId())
                        .description(job.getDescription())
                        .requirements(job.getRequirements())
                        .posterId(job.getPoster().getId())
                        .expireAt(job.getExpireAt())
                        .build())
                .toList();
    }

    /**
     Retrieves the top 10 active jobs based on bid count.
     @return a list of JobDTOs
     **/
    public List<JobDTO> getActiveJobs() {
        log.info("Fetching top 10 active jobs.");
        return jobRepository.findTop10ByStatusOrderByBidCountDesc(JobStatus.OPEN).stream()
                .map(job -> JobDTO.builder()
                        .jobId(job.getId())
                        .description(job.getDescription())
                        .requirements(job.getRequirements())
                        .posterId(job.getPoster().getId())
                        .expireAt(job.getExpireAt())
                        .build())
                .toList();
    }

    /**
     Posts a new job.
     @param jobDTO the details of the job to be posted
     @return a success message
     **/
    public String postJob(JobDTO jobDTO) {
        log.info("Posting a new job with description: {}", jobDTO.getDescription());
        Actor poster = userRepository.findById(jobDTO.getPosterId())
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, "Poster not found", "Poster not found"));
        if (!RoleEnum.POSTER.equals(poster.getRole())) {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "User is not a poster", "User is not a poster");
        }
        Job job = Job.builder()
                .description(jobDTO.getDescription())
                .requirements(jobDTO.getRequirements())
                .poster(poster)
                .postedAt(LocalDateTime.now())
                .expireAt(jobDTO.getExpireAt())
                .status(JobStatus.OPEN)
                .lowestBidAmount(Double.MAX_VALUE)
                .bidCount(0.0)
                .build();
        jobRepository.save(job);
        log.info("Job posted successfully with ID: {}", job.getId());
        return "Job posted successfully!";
    }

    /**
     Retrieves details of a specific job.
     @param jobId the ID of the job
     @return the details of the job as a JobDTO
     **/
    public JobDTO getJob(Long jobId) {
        log.info("Fetching details for job ID: {}", jobId);
        return jobRepository.findById(jobId)
                .map(j -> JobDTO.builder()
                        .jobId(j.getId())
                        .description(j.getDescription())
                        .requirements(j.getRequirements())
                        .posterId(j.getPoster().getId())
                        .expireAt(j.getExpireAt())
                        .build())
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, "Job not found", "Job not found"));
    }
}
