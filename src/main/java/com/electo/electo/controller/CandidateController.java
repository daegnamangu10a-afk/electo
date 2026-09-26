package com.electo.electo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.electo.electo.entity.Candidate;
import com.electo.electo.repository.CandidateRepository;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "*")
public class CandidateController {

    private static final long MAX_PHOTO_SIZE = 150L * 1024; // 150 KB

    private final CandidateRepository candidateRepository;

    public CandidateController(
        CandidateRepository candidateRepository) {     
        this.candidateRepository = candidateRepository;
    }

    // =========================
    // GET CANDIDATES BY ELECTION
    // =========================

    @GetMapping("/election/{electionId}")
    public List<Candidate> getCandidatesByElection(
            @PathVariable Long electionId) {

        return candidateRepository.findByElectionId(electionId);
    }

    // =========================
    // GET CANDIDATES BY POSITION
    // =========================

    @GetMapping("/position/{positionId}")
    public List<Candidate> getCandidatesByPosition(
            @PathVariable Long positionId) {

        return candidateRepository.findByPositionId(positionId);
    }

    // =========================
    // UPLOAD CANDIDATE PHOTO
    // =========================

    @PutMapping(
            value = "/{id}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        Candidate candidate = candidateRepository.findById(id).orElse(null);

        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }

        if (file == null || file.isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Please select an image.");
        }

        if (file.getSize() > MAX_PHOTO_SIZE) {
            return ResponseEntity.badRequest()
                    .body("Image must be 150 KB or smaller.");
        }

        String contentType = file.getContentType();

        if (!"image/jpeg".equals(contentType) &&
                !"image/png".equals(contentType)) {
            return ResponseEntity.badRequest()
                    .body("Only JPEG and PNG images are allowed.");
        }

        try {
            byte[] bytes = file.getBytes();

            // Check that the uploaded bytes can be decoded as an image.
            if (ImageIO.read(new ByteArrayInputStream(bytes)) == null) {
                return ResponseEntity.badRequest()
                        .body("The uploaded file is not a valid image.");
            }

            candidate.setData(bytes);
            candidate.setPhotoContentType(contentType);
            candidate.setPhoto("/api/candidates/" + id + "/photo");

            Candidate savedCandidate = candidateRepository.save(candidate);
            return ResponseEntity.ok(savedCandidate);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Could not read the uploaded image.");
        }
    }

    // =========================
    // GET PHOTO
    // =========================

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {

        Candidate candidate = candidateRepository.findById(id).orElse(null);

        if (candidate == null ||
                candidate.getData() == null ||
                candidate.getData().length == 0 ||
                candidate.getPhotoContentType() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        candidate.getPhotoContentType()))
                .body(candidate.getData());
    }
    
    // =========================
    // CREATE CANDIDATE
    // =========================

    @PostMapping
    public ResponseEntity<?> createCandidate(
            @RequestBody Candidate candidate) {

        if (candidate.getName() == null ||
                candidate.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Candidate name is required.");
        }

        if (candidate.getElectionId() == null) {

            return ResponseEntity.badRequest()
                    .body("Election ID is required.");
        }

        if (candidate.getPositionId() == null) {

            return ResponseEntity.badRequest()
                    .body("Position ID is required.");
        }

        candidate.setName(
                candidate.getName().trim());

        // Photos can only be set through PUT /{id}/photo.
        candidate.setPhoto(null);
        candidate.setData(null);
        candidate.setPhotoContentType(null);

        Candidate savedCandidate =
                candidateRepository.save(candidate);

        return ResponseEntity.ok(savedCandidate);
    }

    // =========================
    // UPDATE CANDIDATE
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(
            @PathVariable Long id,
            @RequestBody Candidate updatedCandidate) {

        Candidate candidate = candidateRepository.findById(id).orElse(null);

        if (candidate == null) {
            return ResponseEntity.notFound().build();
        }

        if (updatedCandidate.getName() == null ||
                updatedCandidate.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Candidate name is required.");
        }

        if (updatedCandidate.getElectionId() == null ||
                updatedCandidate.getPositionId() == null) {
            return ResponseEntity.badRequest()
                    .body("Election ID and position ID are required.");
        }

        candidate.setName(updatedCandidate.getName().trim());
        candidate.setEmail(updatedCandidate.getEmail());
        candidate.setElectionId(updatedCandidate.getElectionId());
        candidate.setPositionId(updatedCandidate.getPositionId());
        candidate.setBiography(updatedCandidate.getBiography());

        // Do not change photo, data, or photoContentType here.
        Candidate savedCandidate = candidateRepository.save(candidate);
        return ResponseEntity.ok(savedCandidate);
    }

    // =========================
    // DELETE CANDIDATE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(
            @PathVariable Long id) {

        if (!candidateRepository.existsById(id)) {

            return ResponseEntity.notFound().build();
        }

        candidateRepository.deleteById(id);

        return ResponseEntity.ok(
                "Candidate deleted successfully.");
    }
}