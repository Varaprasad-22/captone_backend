package com.ticker_service.repository;

import org.springframework.data.mongodb.repository.cdi.MongoRepositoryBean;

import org.springframework.stereotype.Repository;

import com.ticker_service.dto.TicketResponse;
import com.ticker_service.model.Ticket;
import com.ticker_service.model.TicketStatus;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

	List<TicketResponse> findByStatus(TicketStatus open);

	List<TicketResponse> findByCreatedByUserId(String userId);

	List<TicketResponse> findByAssignedAgentId(String agentId);
	
	//for the dashboards sake

	 long countByCreatedByUserId(String userId);

	    long countByCreatedByUserIdAndStatus(String userId, TicketStatus status);

	    long countByCreatedByUserIdAndAssignedAgentIdIsNotNull(String userId);

	    long countByCreatedByUserIdAndStatusIn(
	            String userId,
	            List<TicketStatus> statuses
	    );

}
