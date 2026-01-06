package com.tickerservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tickerservice.model.Attachment;

@Repository
public interface AttachmentRepository extends MongoRepository<Attachment, String>{

	List<Attachment> findByTicketId(String ticketId);

}
