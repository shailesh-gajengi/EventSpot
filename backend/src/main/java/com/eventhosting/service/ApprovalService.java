package com.eventhosting.service;

import com.eventhosting.dto.request.ApprovalActionRequest;
import com.eventhosting.dto.response.ApprovalResponse;
import com.eventhosting.dto.response.DashboardStatsResponse;

public interface ApprovalService {

    ApprovalResponse processApproval(Long eventId, ApprovalActionRequest request);

    ApprovalResponse getApprovalByEventId(Long eventId);

    DashboardStatsResponse getDashboardStats();
}
