package com.electo.electo.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
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

import com.electo.electo.entity.Voter;
import com.electo.electo.repository.VoterRepository;

@RestController
@RequestMapping("/api/voters")
@CrossOrigin(origins = "*")
public class VoterController {

    private final VoterRepository voterRepository;

    public VoterController(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    // Get all voters
    @GetMapping
    public List<Voter> getAllVoters() {
        return voterRepository.findAll();
    }

    // Get voter by email
    @GetMapping("/by-email")
    public Voter getVoterByEmail(@RequestParam String email) {

        return voterRepository.findByEmailIgnoreCase(email)
                .orElse(null);
    }

    // Add a new voter
    @PostMapping
    public ResponseEntity<?> addVoter(@RequestBody Voter voter) {

        if (voter.getEmail() == null ||
                voter.getEmail().trim().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Email is required.");
        }

        if (voterRepository
                .findByEmailIgnoreCase(voter.getEmail())
                .isPresent()) {

            return ResponseEntity.badRequest()
                    .body("A voter with this email already exists.");
        }

        voter.setHasVoted(false);

        if (voter.getRole() == null ||
                voter.getRole().trim().isEmpty()) {

            voter.setRole("VOTER");
        }

        Voter savedVoter = voterRepository.save(voter);

        return ResponseEntity.ok(savedVoter);
    }

    // Update an existing voter
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoter(
            @PathVariable Long id,
            @RequestBody Voter updatedVoter) {

        return voterRepository.findById(id)
                .map(voter -> {

                    voter.setEmail(updatedVoter.getEmail());
                    voter.setName(updatedVoter.getName());
                    voter.setCourse(updatedVoter.getCourse());
                    voter.setYear(updatedVoter.getYear());
                    voter.setShift(updatedVoter.getShift());
                    voter.setRole(updatedVoter.getRole());

                    Voter savedVoter =
                            voterRepository.save(voter);

                    return ResponseEntity.ok(savedVoter);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // Delete a voter
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoter(
            @PathVariable Long id) {

        if (!voterRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        voterRepository.deleteById(id);

        return ResponseEntity.ok(
                "Voter deleted successfully.");
    }

    // Export voters to Excel
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportVoters() {

        List<Voter> voters = voterRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Voters");

            // Header row
            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Course");
            header.createCell(4).setCellValue("Year");
            header.createCell(5).setCellValue("Shift");
            header.createCell(6).setCellValue("Role");
            header.createCell(7).setCellValue("Voting Status");

            // Data rows
            int rowNumber = 1;

            for (Voter voter : voters) {

                Row row = sheet.createRow(rowNumber++);

                row.createCell(0).setCellValue(
                        voter.getId() != null
                                ? voter.getId()
                                : 0
                );

                row.createCell(1).setCellValue(
                        voter.getName() != null
                                ? voter.getName()
                                : ""
                );

                row.createCell(2).setCellValue(
                        voter.getEmail() != null
                                ? voter.getEmail()
                                : ""
                );

                row.createCell(3).setCellValue(
                        voter.getCourse() != null
                                ? voter.getCourse()
                                : ""
                );

                row.createCell(4).setCellValue(
                        voter.getYear() != null
                                ? voter.getYear()
                                : ""
                );

                row.createCell(5).setCellValue(
                        voter.getShift() != null
                                ? voter.getShift()
                                : ""
                );

                row.createCell(6).setCellValue(
                        voter.getRole() != null
                                ? voter.getRole()
                                : ""
                );

                row.createCell(7).setCellValue(
                        voter.isHasVoted()
                                ? "Voted"
                                : "Not Voted"
                );
            }

            // Automatically adjust column widths
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert workbook to byte array
            java.io.ByteArrayOutputStream outputStream =
                    new java.io.ByteArrayOutputStream();

            workbook.write(outputStream);

            byte[] excelFile =
                    outputStream.toByteArray();

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Electo_Voters.xlsx"
                    )
                    .contentType(
                            MediaType.parseMediaType(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                            )
                    )
                    .body(excelFile);

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
}