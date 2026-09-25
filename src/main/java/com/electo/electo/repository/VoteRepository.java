package com.electo.electo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electo.electo.entity.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByElectionId(Long electionId);

    List<Vote> findByPositionId(Long positionId);

    List<Vote> findByCandidateId(Long candidateId);

    boolean existsByVoterIdAndElectionIdAndPositionId(
            Long voterId,
            Long electionId,
            Long positionId);

    long countByElectionId(Long electionId);

    long countByElectionIdAndPositionId(
            Long electionId,
            Long positionId);

    long countByElectionIdAndPositionIdAndCandidateId(
            Long electionId,
            Long positionId,
            Long candidateId);
}