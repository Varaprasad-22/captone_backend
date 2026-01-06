package com.tickerservice.model;

public enum TicketStatus {
	OPEN, ASSIGNED, RESOLVED, CLOSED, FAILED, INPROGRESS, ESCALATED, BREACHED,REOPEN;
	 public static boolean contains(TicketStatus status) {
        for (TicketStatus ticketStatus : TicketStatus.values()) {
            if (ticketStatus == status) {
                return true;
            }
        }
        return false;
    }
}
