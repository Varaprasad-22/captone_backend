package com.notificationService.Listener;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.notificationService.config.RabbitConfig;
import com.notificationService.dto.NotificationEvent;
import com.notificationService.model.Notification;
import com.notificationService.repository.NotificationRepository;
import com.notificationService.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationListener {

	private final EmailService emailService;
	private final NotificationRepository notificationRepo;

	@RabbitListener(queues = RabbitConfig.Queue)
	public void consume(NotificationEvent event) {
		
		//this is for saving it to db 
		Notification notify=new Notification();
		notify.setEventType(event.getType());
		notify.setCreatedAt(LocalDateTime.now());
		notify.setMessage(event.getMessage());
		notify.setRecipientEmail(event.getEmail());
		notify.setSubject(event.getSubject());
		
		//now we can say like sometimes it failed by rabbitmq things so we see in  the status and error message try catch block
		try {
			emailService.send(event.getEmail(), event.getSubject(), event.getMessage());
			notify.setStatus("SENT");
		}catch(Exception e) {
			notify.setErrorMessage(e.getMessage());
			notify.setStatus("FAILED");
		}
		
		notificationRepo.save(notify);
	}
}
