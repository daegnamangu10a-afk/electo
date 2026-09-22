package com.electo.electo;

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

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
public class PositionController {

    private final PositionRepository positionRepository;

    public PositionController(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @GetMapping("/election/{electionId}")
    public List<Position> getPositionsByElection(
            @PathVariable Long electionId) {

        return positionRepository.findByElectionId(electionId);
    }

    @PostMapping
    public ResponseEntity<?> createPosition(
            @RequestBody Position position) {

        if (position.getName() == null ||
                position.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Position name is required.");
        }

        if (position.getElectionId() == null) {

            return ResponseEntity.badRequest()
                    .body("Election ID is required.");
        }

        position.setName(position.getName().trim());

        Position savedPosition =
                positionRepository.save(position);

        return ResponseEntity.ok(savedPosition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePosition(
            @PathVariable Long id,
            @RequestBody Position updatedPosition) {

        return positionRepository.findById(id)
                .map(position -> {

                    if (updatedPosition.getName() == null ||
                            updatedPosition.getName().trim().isEmpty()) {

                        return ResponseEntity.badRequest()
                                .body("Position name is required.");
                    }

                    position.setName(
                            updatedPosition.getName().trim());

                    position.setElectionId(
                            updatedPosition.getElectionId());

                    Position savedPosition =
                            positionRepository.save(position);

                    return ResponseEntity.ok(savedPosition);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePosition(
            @PathVariable Long id) {

        if (!positionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        positionRepository.deleteById(id);

        return ResponseEntity.ok(
                "Position deleted successfully.");
    }
}