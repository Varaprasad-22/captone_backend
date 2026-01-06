package com.tickerservice.repository;


import org.springframework.stereotype.Repository;

import com.tickerservice.dto.TicketResponse;
import com.tickerservice.model.Ticket;
import com.tickerservice.model.TicketStatus;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

	List<TicketResponse> findByStatus(TicketStatus open);

	List<TicketResponse> findByCreatedByUserId(String userId);

	List<TicketResponse> findByAssignedAgentId(String agentId);

	List<TicketResponse> findByAssignedAgentIdAndStatusIn(String agentId, List<TicketStatus> statuses);

	// for the dashboards sake

	long countByCreatedByUserId(String userId);

	long countByCreatedByUserIdAndStatus(String userId, TicketStatus status);

	long countByCreatedByUserIdAndAssignedAgentIdIsNotNull(String userId);

	long countByCreatedByUserIdAndStatusIn(String userId, List<TicketStatus> statuses);

}
