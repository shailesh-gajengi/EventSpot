package com.eventhosting.dto.response;

public class DashboardStatsResponse {

    private long totalEvents;
    private long pendingEvents;
    private long approvedEvents;
    private long rejectedEvents;
    private long totalOrganizers;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalEvents, long pendingEvents, long approvedEvents, long rejectedEvents, long totalOrganizers) {
        this.totalEvents = totalEvents;
        this.pendingEvents = pendingEvents;
        this.approvedEvents = approvedEvents;
        this.rejectedEvents = rejectedEvents;
        this.totalOrganizers = totalOrganizers;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getPendingEvents() {
        return pendingEvents;
    }

    public void setPendingEvents(long pendingEvents) {
        this.pendingEvents = pendingEvents;
    }

    public long getApprovedEvents() {
        return approvedEvents;
    }

    public void setApprovedEvents(long approvedEvents) {
        this.approvedEvents = approvedEvents;
    }

    public long getRejectedEvents() {
        return rejectedEvents;
    }

    public void setRejectedEvents(long rejectedEvents) {
        this.rejectedEvents = rejectedEvents;
    }

    public long getTotalOrganizers() {
        return totalOrganizers;
    }

    public void setTotalOrganizers(long totalOrganizers) {
        this.totalOrganizers = totalOrganizers;
    }
}
