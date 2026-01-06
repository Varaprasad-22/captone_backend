package com.tickerservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.tickerservice.config.RabbitConfig;
import com.tickerservice.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publish(NotificationEvent event, String routingKey) {
		rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, event);
	}
}
