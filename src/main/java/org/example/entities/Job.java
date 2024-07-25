package org.example.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.JobStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "job")
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String requirements;

    @ManyToOne
    @JoinColumn(name = "poster_id")
    private Actor poster;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Actor winner;

    private LocalDateTime postedAt;

    private LocalDateTime expireAt;

    private Double lowestBidAmount;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids;

    @Column(name = "bid_count", nullable = false)
    private Double bidCount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;
}
