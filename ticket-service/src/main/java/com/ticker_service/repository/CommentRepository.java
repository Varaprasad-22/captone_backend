package com.ticker_service.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.multipart.MultipartFile;

import com.ticker_service.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String>{

	List<Comment> findAllByTicketId(String ticketId);

}
