package com.assignmentService.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.assignmentService.model.Assignment;
import com.assignmentService.model.Sla;
import com.assignmentService.model.SlaStatus;
import com.assignmentService.repositories.AssignmentRepository;
import com.assignmentService.repositories.SlaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class SlaSchedular {

	@Autowired
    private final SlaRepository slaRepo;
    private AssignmentRepository assignmentRepo;
//	runs every 60 secs timer
	//see this one aims so if not responded within response time it shows of escalation
	//if not resolved with in time it shows breached
    @Scheduled(fixedRate = 60000)
    public void checkBreaches() {

        LocalDateTime now = LocalDateTime.now();

        for (Sla sla : slaRepo.findByBreachedFalse()) {

            boolean updated = false;

//            see it checks only for escalations
//            like if not escalated already 
//            no response,response crosssed
            if (!sla.isEscalated()
                    && sla.getRespondedAt() == null
                    && now.isAfter(sla.getResponseDeadline())) {

                sla.setEscalated(true);
                
                updated = true;
                
                updateAssignment(sla.getAssignmentId(),SlaStatus.ESCALATED);
            }


//            breach checks like not resolved and dead line crossed even if escalaton true also
            if (sla.getResolvedAt() == null
                    && now.isAfter(sla.getResolutionDeadline())) {

                sla.setBreached(true);
                updated = true;

                updateAssignment(sla.getAssignmentId(),SlaStatus.BREACHED);
            }

//            prevent unnecesary db writes per minute
            if (updated) {
                slaRepo.save(sla);
            }
        }
    }
    
    private void updateAssignment(String assignId,SlaStatus status) {
    	Assignment assignment=assignmentRepo.findById(assignId).orElseThrow(()->new RuntimeException("Failed to find assignment"));
    	assignment.setStatus(status);
    	assignmentRepo.save(assignment);
    	
    }
}