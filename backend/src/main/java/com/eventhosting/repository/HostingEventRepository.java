package com.eventhosting.repository;

import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostingEventRepository extends JpaRepository<HostingEvent, Long> {

    @Query("SELECT DISTINCT e FROM HostingEvent e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.approval WHERE e.organizer.organizerId = :organizerId ORDER BY e.submissionDate DESC")
    List<HostingEvent> findByOrganizer_OrganizerIdOrderBySubmissionDateDesc(@Param("organizerId") Long organizerId);

    @Query("SELECT DISTINCT e FROM HostingEvent e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.approval WHERE e.approvalStatus = :approvalStatus ORDER BY e.submissionDate DESC")
    List<HostingEvent> findByApprovalStatusOrderBySubmissionDateDesc(@Param("approvalStatus") ApprovalStatus approvalStatus);

    @Query("SELECT DISTINCT e FROM HostingEvent e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.approval ORDER BY e.submissionDate DESC")
    List<HostingEvent> findAllByOrderBySubmissionDateDesc();

    @Query("SELECT DISTINCT e FROM HostingEvent e LEFT JOIN FETCH e.organizer LEFT JOIN FETCH e.approval WHERE e.eventId = :id")
    Optional<HostingEvent> findByIdWithDetails(@Param("id") Long id);

    long countByApprovalStatus(ApprovalStatus approvalStatus);

    @Query("SELECT " +
           "COUNT(e), " +
           "COALESCE(SUM(CASE WHEN e.approvalStatus = com.eventhosting.entity.ApprovalStatus.PENDING THEN 1L ELSE 0L END), 0L), " +
           "COALESCE(SUM(CASE WHEN e.approvalStatus = com.eventhosting.entity.ApprovalStatus.APPROVED THEN 1L ELSE 0L END), 0L), " +
           "COALESCE(SUM(CASE WHEN e.approvalStatus = com.eventhosting.entity.ApprovalStatus.REJECTED THEN 1L ELSE 0L END), 0L) " +
           "FROM HostingEvent e")
    List<Object[]> getAggregatedEventStats();
}

