package com.assignmentService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.assignmentService.dto.UserInfoResponse;

@FeignClient(name = "auth-service", url = "http://localhost:9092")
public interface AuthClient {

	@GetMapping("/auth/getEmail/{userId}")
	UserInfoResponse getByUserId(@PathVariable String userId);
}