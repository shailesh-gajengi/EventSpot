package com.eventhosting.repository;

import com.eventhosting.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    Optional<Organizer> findByOrganizerEmail(String organizerEmail);

    boolean existsByOrganizerEmail(String organizerEmail);
}
