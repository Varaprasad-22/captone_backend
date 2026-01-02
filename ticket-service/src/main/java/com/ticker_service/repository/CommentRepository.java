package com.ticker_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ticker_service.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String>{

}
