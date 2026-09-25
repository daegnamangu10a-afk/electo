package com.electo.electo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electo.electo.entity.Candidate;

public interface CandidateRepository
        extends JpaRepository<Candidate, Long> {

    List<Candidate> findByElectionId(Long electionId);

    List<Candidate> findByPositionId(Long positionId);
}