package com.assignmentService.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.assignmentService.model.Sla;
import com.assignmentService.repositories.SlaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class SlaSchedular {

    private final SlaRepository slaRepo;

    @Scheduled(fixedRate = 60000)
    public void checkBreaches() {

        for (Sla sla : slaRepo.findActiveSlas()) {

            if (sla.getRespondedAt() == null &&
                LocalDateTime.now().isAfter(sla.getResponseDeadline())) {
                sla.setEscalated(true);
            }

            if (sla.getResolvedAt() == null &&
                LocalDateTime.now().isAfter(sla.getResolutionDeadline())) {
                sla.setBreached(true);
            }

            slaRepo.save(sla);
        }
    }
}
