package com.electo.electo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private Long electionId;

    private Long positionId;

    private String photo;

    @Column(columnDefinition = "TEXT")
    private String biography;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Candidate() {
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // GET NAME
    // =========================================================

    public String getName() {
        return name;
    }


    // =========================================================
    // SET NAME
    // =========================================================

    public void setName(String name) {
        this.name = name;
    }


    // =========================================================
    // GET EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }


    // =========================================================
    // SET EMAIL
    // =========================================================

    public void setEmail(String email) {
        this.email = email;
    }


    // =========================================================
    // GET ELECTION ID
    // =========================================================

    public Long getElectionId() {
        return electionId;
    }


    // =========================================================
    // SET ELECTION ID
    // =========================================================

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }


    // =========================================================
    // GET POSITION ID
    // =========================================================

    public Long getPositionId() {
        return positionId;
    }


    // =========================================================
    // SET POSITION ID
    // =========================================================

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }


    // =========================================================
    // GET PHOTO
    // =========================================================

    public String getPhoto() {
        return photo;
    }


    // =========================================================
    // SET PHOTO
    // =========================================================

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    // =========================================================
    // GET BIOGRAPHY
    // =========================================================

    public String getBiography() {
        return biography;
    }


    // =========================================================
    // SET BIOGRAPHY
    // =========================================================

    public void setBiography(String biography) {
        this.biography = biography;
    }

}