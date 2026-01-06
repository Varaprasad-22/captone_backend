package com.notificationservice.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

//	see these are used for the rabbitmq to see or how it look like for us
	public static final String Exchange = "notification.exchange";
	public static final String Queue = "notification.email.queue";

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(Exchange);
	}

	@Bean
	public Queue queue() {
		return new Queue(Queue, true);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("#");
	}
	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
	    Jackson2JsonMessageConverter converter =
	            new Jackson2JsonMessageConverter();

	    converter.setTypePrecedence(
	            org.springframework.amqp.support.converter.Jackson2JavaTypeMapper.TypePrecedence.INFERRED
	    );

	    return converter;
	}

}
