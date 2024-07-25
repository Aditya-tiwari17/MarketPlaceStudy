package org.example.services;

import org.example.entities.Actor;
import org.example.entities.Job;
import org.example.enums.JobStatus;
import org.example.enums.RoleEnum;
import org.example.exceptions.MarketPlaceException;
import org.example.repositories.BidRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {
    @InjectMocks
    BidService bidService;
    @Mock
    BidRepository bidRepository;
    @Mock
    JobRepository jobRepository;
    @Mock
    UserRepository userRepository;

    @Test
    void placeBidSuccess() {
        Job job = Job.builder()
                .expireAt(LocalDateTime.now().plusMinutes(10))
                .lowestBidAmount(3000.0)
                .status(JobStatus.OPEN)
                .bidCount(0.0)
                .build();
        Actor user = Actor.builder().role(RoleEnum.BIDDER).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertEquals("Bid placed successfully!", bidService.placeBid(5L, 2000.0, 5L));
    }

    @Test
    @DisplayName(value = "Throws not found exception on invalid job id")
    void placeBidJobNotFound() {
        when(jobRepository.findById(any())).thenReturn(Optional.empty());
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.placeBid(5L,
                5000.0, 5L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Job not found", exception.getEndUserMessage());
        assertEquals("Job not found", exception.getInternalMessage());

    }

    @Test
    @DisplayName(value = "Throws bad request exception on auction expiry")
    void placeBidAuctionExpiry() {
        Job job = Job.builder().expireAt(LocalDateTime.now().minusMinutes(10)).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.placeBid(5L,
                5000.0, 5L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("The auction has expired.", exception.getEndUserMessage());
        assertEquals("The auction has expired.", exception.getInternalMessage());
    }

    @Test
    @DisplayName(value = "Throws bad request exception on user role other than bidder")
    void placeBidNonBidder() {
        Job job = Job.builder().expireAt(LocalDateTime.now().plusMinutes(10)).status(JobStatus.OPEN).build();
        Actor user = Actor.builder().role(RoleEnum.POSTER).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.placeBid(5L,
                5000.0, 5L));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("User not allowed to place bids", exception.getEndUserMessage());
        assertEquals("User not allowed to place bids", exception.getInternalMessage());

    }

    @Test
    @DisplayName(value = "Successfully provides lowest bid amount for job")
    void getLowestBidAmountSuccess() {
        Job job = Job.builder().lowestBidAmount(5000.0).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        assertEquals(5000.0, bidService.getLowestBidAmount(5L));
    }

    @Test
    @DisplayName(value = "Throws not found exception on invalid job id")
    void getLowestBidAmountFailure() {
        when(jobRepository.findById(any())).thenReturn(Optional.empty());
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.getLowestBidAmount(5L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Job not found", exception.getEndUserMessage());
        assertEquals("Job not found", exception.getInternalMessage());
    }

    @Test
    @DisplayName(value = "Successfully provides count of bids for job")
    void getLowestBidCountSuccess() {
        Job job = Job.builder().bidCount(5.0).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        assertEquals(5.0, bidService.getBidCount(5L));
    }

    @Test
    @DisplayName(value = "Throws not found exception on invalid job id")
    void getLowestBidCountFailure() {
        when(jobRepository.findById(any())).thenReturn(Optional.empty());
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.getBidCount(5L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Job not found", exception.getEndUserMessage());
        assertEquals("Job not found", exception.getInternalMessage());
    }

    @Test
    @DisplayName(value = "Successfully fetches the auction expiration")
    void getAuctionExpirationSuccess() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 26, 5, 25, 30, 0);
        Job job = Job.builder().expireAt(dateTime).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        assertEquals(dateTime, bidService.getAuctionExpiration(5L));
    }

    @Test
    @DisplayName(value = "Throws not found exception on invalid job id")
    void getAuctionExpirationFailure() {
        when(jobRepository.findById(any())).thenReturn(Optional.empty());
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.getAuctionExpiration(5L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Job not found", exception.getEndUserMessage());
        assertEquals("Job not found", exception.getInternalMessage());
    }

    @Test
    @DisplayName(value = "Successfully fetches the time remaning in auction")
    void getTimeRemainingSuccess() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 26, 5, 25, 30, 0);
        Job job = Job.builder().expireAt(dateTime).build();
        when(jobRepository.findById(any())).thenReturn(Optional.of(job));
        assertNotNull(bidService.getTimeRemaining(5L));

    }

    @Test
    @DisplayName(value = "Throws not found exception on invalid job id")
    void getTimeRemainingFailure() {
        when(jobRepository.findById(any())).thenReturn(Optional.empty());
        MarketPlaceException exception = assertThrows(MarketPlaceException.class, () -> bidService.getAuctionExpiration(5L));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Job not found", exception.getEndUserMessage());
        assertEquals("Job not found", exception.getInternalMessage());
    }
}