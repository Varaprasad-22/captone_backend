package com.ticker_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ticker_service.model.Attachment;

@Repository
public interface AttachmentRepository extends MongoRepository<Attachment, String>{

}
