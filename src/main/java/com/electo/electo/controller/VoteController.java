package com.electo.electo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electo.electo.entity.Candidate;
import com.electo.electo.entity.Election;
import com.electo.electo.entity.Position;
import com.electo.electo.entity.Vote;
import com.electo.electo.repository.CandidateRepository;
import com.electo.electo.repository.ElectionRepository;
import com.electo.electo.repository.PositionRepository;
import com.electo.electo.repository.VoteRepository;
import com.electo.electo.repository.VoterRepository;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
public class VoteController {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final PositionRepository positionRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;

    public VoteController(
            VoteRepository voteRepository,
            ElectionRepository electionRepository,
            PositionRepository positionRepository,
            CandidateRepository candidateRepository,
            VoterRepository voterRepository) {

        this.voteRepository = voteRepository;
        this.electionRepository = electionRepository;
        this.positionRepository = positionRepository;
        this.candidateRepository = candidateRepository;
        this.voterRepository = voterRepository;
    }

    @PostMapping
    public ResponseEntity<?> castVote(
            @RequestBody Vote vote) {

        if (vote.getVoterId() == null) {
            return ResponseEntity.badRequest()
                    .body("Voter ID is required.");
        }

        if (vote.getElectionId() == null) {
            return ResponseEntity.badRequest()
                    .body("Election ID is required.");
        }

        if (vote.getPositionId() == null) {
            return ResponseEntity.badRequest()
                    .body("Position ID is required.");
        }

        if (vote.getCandidateId() == null) {
            return ResponseEntity.badRequest()
                    .body("Candidate ID is required.");
        }

        if (!voterRepository.existsById(vote.getVoterId())) {
            return ResponseEntity.badRequest()
                    .body("Voter not found.");
        }

        Election election = electionRepository
                .findById(vote.getElectionId())
                .orElse(null);

        if (election == null) {
            return ResponseEntity.badRequest()
                    .body("Election not found.");
        }

        if (!"ACTIVE".equalsIgnoreCase(election.getStatus())) {
            return ResponseEntity.badRequest()
                    .body("Voting is not currently active for this election.");
        }

        Position position = positionRepository
                .findById(vote.getPositionId())
                .orElse(null);

        if (position == null) {
            return ResponseEntity.badRequest()
                    .body("Position not found.");
        }

        if (!position.getElectionId()
                .equals(vote.getElectionId())) {

            return ResponseEntity.badRequest()
                    .body("This position does not belong to the selected election.");
        }

        Candidate candidate = candidateRepository
                .findById(vote.getCandidateId())
                .orElse(null);

        if (candidate == null) {
            return ResponseEntity.badRequest()
                    .body("Candidate not found.");
        }

        if (!candidate.getElectionId()
                .equals(vote.getElectionId())) {

            return ResponseEntity.badRequest()
                    .body("This candidate does not belong to the selected election.");
        }

        if (!candidate.getPositionId()
                .equals(vote.getPositionId())) {

            return ResponseEntity.badRequest()
                    .body("This candidate does not belong to the selected position.");
        }

        boolean alreadyVoted =
                voteRepository
                        .existsByVoterIdAndElectionIdAndPositionId(
                                vote.getVoterId(),
                                vote.getElectionId(),
                                vote.getPositionId()
                        );

        if (alreadyVoted) {
            return ResponseEntity.badRequest()
                    .body("You have already voted for this position.");
        }

        vote.setVotedAt(LocalDateTime.now());

        Vote savedVote = voteRepository.save(vote);

        return ResponseEntity.ok(savedVote);
    }

    @GetMapping
    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }
}