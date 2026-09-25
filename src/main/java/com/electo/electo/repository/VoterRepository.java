package com.electo.electo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electo.electo.entity.Voter;

public interface VoterRepository extends JpaRepository<Voter, Long> {

    Optional<Voter> findByEmailIgnoreCase(String email);
}