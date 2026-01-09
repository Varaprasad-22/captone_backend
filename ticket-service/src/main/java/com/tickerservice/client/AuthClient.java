package com.tickerservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tickerservice.dto.UserInfoResponse;


@FeignClient(name = "auth-service", url = "${auth.service.url}")
public interface AuthClient {

	@GetMapping("/auth/getEmail/{userId}")
	UserInfoResponse getByUserId(@PathVariable String userId);
}