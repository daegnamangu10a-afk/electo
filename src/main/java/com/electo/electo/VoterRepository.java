package com.electo.electo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoterRepository extends JpaRepository<Voter, Long> {

    Optional<Voter> findByEmailIgnoreCase(String email);
}