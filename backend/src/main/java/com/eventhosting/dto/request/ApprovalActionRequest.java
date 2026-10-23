package com.eventhosting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ApprovalActionRequest {

    @NotBlank(message = "Approval status is required")
    @Pattern(regexp = "^(Approved|Rejected)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Status must be either 'Approved' or 'Rejected'")
    private String status;

    private String rejectionReason;

    public ApprovalActionRequest() {
    }

    public ApprovalActionRequest(String status, String rejectionReason) {
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
