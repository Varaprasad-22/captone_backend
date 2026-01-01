package com.auth_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.auth_service.config.RabbitConfig;
import com.auth_service.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publish(NotificationEvent event, String routingKey) {
		rabbitTemplate.convertAndSend(RabbitConfig.Exchange, routingKey, event);
	}
}
