package com.assignmentService.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	public static final String Exchange = "notification.exchange";

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(Exchange);
	}
}
