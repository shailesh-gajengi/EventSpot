package com.eventhosting.repository;

import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostingEventRepository extends JpaRepository<HostingEvent, Long> {

    List<HostingEvent> findByOrganizer_OrganizerIdOrderBySubmissionDateDesc(Long organizerId);

    List<HostingEvent> findByApprovalStatusOrderBySubmissionDateDesc(ApprovalStatus approvalStatus);

    List<HostingEvent> findAllByOrderBySubmissionDateDesc();

    long countByApprovalStatus(ApprovalStatus approvalStatus);
}
