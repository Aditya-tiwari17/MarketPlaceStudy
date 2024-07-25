package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dtos.BidDTO;
import org.example.services.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bids")
@PreAuthorize("isAuthenticated()")
public class BidController {
    private final BidService bidService;

    /**
     Retrieves the lowest bid amount for a specific job.
     @param jobId the ID of the job
     @return the lowest bid amount
     **/
    @GetMapping("/{jobId}/lowest")
    @PreAuthorize("hasAnyRole('ROLE_POSTER', 'ROLE_BIDDER')")
    public ResponseEntity<Double> getLowestBidAmount(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok().body(bidService.getLowestBidAmount(jobId));
    }

    /**
     Retrieves the total count of bids placed for a specific job
     @param jobId the ID of the job
     @return the bid count
     **/
    @GetMapping("/{jobId}/count")
    @PreAuthorize("hasAnyRole('ROLE_POSTER', 'ROLE_BIDDER')")
    public ResponseEntity<Double> getBidCount(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok().body(bidService.getBidCount(jobId));
    }

    /**
     Retrieves the expiration time of the auction for a specific job
     @param jobId the ID of the job
     @return the auction expiration time
     **/
    @GetMapping("/{jobId}/expiration")
    @PreAuthorize("hasAnyRole('ROLE_POSTER', 'ROLE_BIDDER')")
    public ResponseEntity<LocalDateTime> getExpiration(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok().body(bidService.getAuctionExpiration(jobId));
    }

    /**
     Retrieves the remaining time until the auction expires for a specific job
     @param jobId the ID of the job
     @return the remaining time as a Duration object
     **/
    @GetMapping("/{jobId}/time-remaining")
    @PreAuthorize("hasAnyRole('ROLE_POSTER', 'ROLE_BIDDER')")
    public ResponseEntity<Duration> getTimeRemaining(@PathVariable("jobId") Long jobId) {
        return ResponseEntity.ok().body(bidService.getTimeRemaining(jobId));
    }

    /**
     Places a bid on a specific job
     @param bidDTO the bid data transfer object containing job ID, bid amount, and bidder ID
     @return a message indicating the success of the bid placement
     **/
    @PostMapping("/place")
    @PreAuthorize("hasAnyRole('ROLE_BIDDER')")
    public ResponseEntity<String> placeBid(@RequestBody BidDTO bidDTO) {
        return ResponseEntity.ok().body(bidService.placeBid(bidDTO.getJobId(), bidDTO.getAmount(), bidDTO.getBidderId()));
    }
}
