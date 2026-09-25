package com.electo.electo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electo.electo.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByElectionId(Long electionId);
}