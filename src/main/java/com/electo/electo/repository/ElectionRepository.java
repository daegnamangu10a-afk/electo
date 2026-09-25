package com.electo.electo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.electo.electo.entity.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {
}