package com.electo.electo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electo.electo.entity.Election;
import com.electo.electo.repository.ElectionRepository;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = "*")
public class ElectionController {

    private final ElectionRepository electionRepository;

    public ElectionController(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    // Get all elections
    @GetMapping
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    // Get one election
    @GetMapping("/{id}")
    public ResponseEntity<?> getElection(@PathVariable Long id) {

        return electionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create election
    @PostMapping
    public ResponseEntity<?> createElection(
            @RequestBody Election election) {

        if (election.getName() == null ||
                election.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Election name is required.");
        }

        if (election.getStartTime() == null ||
                election.getEndTime() == null) {

            return ResponseEntity.badRequest()
                    .body("Start time and end time are required.");
        }

        if (!election.getEndTime().isAfter(election.getStartTime())) {

            return ResponseEntity.badRequest()
                    .body("End time must be after start time.");
        }

        election.setName(election.getName().trim());

        Election savedElection =
                electionRepository.save(election);

        return ResponseEntity.ok(savedElection);
    }

    // Update election
    @PutMapping("/{id}")
    public ResponseEntity<?> updateElection(
            @PathVariable Long id,
            @RequestBody Election updatedElection) {

        return electionRepository.findById(id)
                .map(election -> {

                    if (updatedElection.getName() == null ||
                            updatedElection.getName().trim().isEmpty()) {

                        return ResponseEntity.badRequest()
                                .body("Election name is required.");
                    }

                    if (updatedElection.getStartTime() == null ||
                            updatedElection.getEndTime() == null) {

                        return ResponseEntity.badRequest()
                                .body("Start time and end time are required.");
                    }

                    if (!updatedElection.getEndTime()
                            .isAfter(updatedElection.getStartTime())) {

                        return ResponseEntity.badRequest()
                                .body("End time must be after start time.");
                    }

                    election.setName(
                            updatedElection.getName().trim());

                    election.setDescription(
                            updatedElection.getDescription());

                    election.setStartTime(
                            updatedElection.getStartTime());

                    election.setEndTime(
                            updatedElection.getEndTime());

                    Election savedElection =
                            electionRepository.save(election);

                    return ResponseEntity.ok(savedElection);

                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    // Delete election
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteElection(
            @PathVariable Long id) {

        if (!electionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        electionRepository.deleteById(id);

        return ResponseEntity.ok(
                "Election deleted successfully.");
    }
}