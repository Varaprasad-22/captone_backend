package com.ticker_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ticker_service.model.Attachment;

public interface AttachmentRepository extends MongoRepository<Attachment, String>{

}
