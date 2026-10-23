package com.eventhosting.repository;

import com.eventhosting.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    Optional<Approval> findByHostingEvent_EventId(Long eventId);

    List<Approval> findByOrganizer_OrganizerId(Long organizerId);
}
