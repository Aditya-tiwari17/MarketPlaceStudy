package org.example.repositories;

import org.example.entities.Bid;
import org.example.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Bid findFirstByJobOrderByAmountAsc(Job job);
}
