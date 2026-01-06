package com.tickerservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tickerservice.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String>{

	List<Comment> findAllByTicketId(String ticketId);

}
