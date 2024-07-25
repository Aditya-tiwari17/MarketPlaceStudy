package org.example.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.entities.Actor;
import org.example.entities.Bid;
import org.example.entities.Job;
import org.example.enums.JobStatus;
import org.example.enums.RoleEnum;
import org.example.exceptions.MarketPlaceException;
import org.example.repositories.BidRepository;
import org.example.repositories.JobRepository;
import org.example.repositories.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
@Log4j2
@AllArgsConstructor
public class BidService {
    private static final String JOB_NOT_FOUND = "Job not found";
    private static final String USER_NOT_FOUND = "User not found";

    private final BidRepository bidRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    /**
     Places a bid on a job.
     @param jobId the ID of the job to place a bid on
     @param bidAmount the amount of the bid
     @param bidderId the ID of the bidder
     @return a success message if the bid is placed successfully
     **/
    @Transactional
    public String placeBid(Long jobId, Double bidAmount, Long bidderId) {
        log.info("Placing bid for job ID: {} by bidder ID: {} with amount: {}", jobId, bidderId, bidAmount);

        // Retrieve the job and validate its status
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, JOB_NOT_FOUND, JOB_NOT_FOUND));

        if (job.getExpireAt().isBefore(LocalDateTime.now()) || job.getStatus() == JobStatus.CLOSED) {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "The auction has expired.", "The auction has expired.");
        }

        // Retrieve the bidder and validate the role
        Actor bidder = userRepository.findById(bidderId)
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, USER_NOT_FOUND, USER_NOT_FOUND));

        if (!RoleEnum.BIDDER.equals(bidder.getRole())) {
            throw new MarketPlaceException(HttpStatus.BAD_REQUEST, "User not allowed to place bids", "User not allowed to place bids");
        }

        // Create and save the new bid
        Bid newBid = Bid.builder()
                .amount(bidAmount)
                .bidTime(LocalDateTime.now())
                .job(job)
                .bidder(bidder)
                .build();

        bidRepository.save(newBid);
        log.info("Bid saved successfully for job ID: {} by bidder ID: {}", jobId, bidderId);

        // Update job's lowest bid amount and bid count
        job.setLowestBidAmount(Math.min(job.getLowestBidAmount(), bidAmount));
        job.setBidCount(job.getBidCount() + 1);
        jobRepository.save(job);
        log.info("Job ID: {} updated with new lowest bid amount and bid count.", jobId);

        return "Bid placed successfully!";
    }

    /**
     Gets the lowest bid amount for a specific job.
     @param jobId the ID of the job
     @return the lowest bid amount
     **/
    public double getLowestBidAmount(Long jobId) {
        log.info("Retrieving lowest bid amount for job ID: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, JOB_NOT_FOUND, JOB_NOT_FOUND));

        return job.getLowestBidAmount();
    }

    /**
     Gets the total bid count for a specific job.
     @param jobId the ID of the job
     @return the number of bids placed on the job
     **/
    public Double getBidCount(Long jobId) {
        log.info("Retrieving bid count for job ID: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, JOB_NOT_FOUND, JOB_NOT_FOUND));

        return job.getBidCount();
    }

    /**
     Gets the expiration time of the auction for a specific job.
     @param jobId the ID of the job
     @return the expiration time of the auction
     **/
    public LocalDateTime getAuctionExpiration(Long jobId) {
        log.info("Retrieving auction expiration for job ID: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, JOB_NOT_FOUND, JOB_NOT_FOUND));

        return job.getExpireAt();
    }

    /**
     Gets the remaining time before the auction for a specific job expires.
     @param jobId the ID of the job
     @return the remaining time before auction expiration
     **/
    public Duration getTimeRemaining(Long jobId) {
        log.info("Calculating time remaining for auction on job ID: {}", jobId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new MarketPlaceException(HttpStatus.NOT_FOUND, JOB_NOT_FOUND, JOB_NOT_FOUND));

        return Duration.between(LocalDateTime.now(), job.getExpireAt());
    }
}
