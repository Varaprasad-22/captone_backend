package com.ticker_service.repository;

import org.springframework.data.mongodb.repository.cdi.MongoRepositoryBean;
import org.springframework.stereotype.Repository;

import com.ticker_service.model.Ticket;

import org.springframework.data.mongodb.repository.MongoRepository;
@Repository
public interface TicketRepository extends MongoRepository<Ticket, String>{

}
