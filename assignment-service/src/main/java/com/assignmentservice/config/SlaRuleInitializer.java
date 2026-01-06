package com.assignmentservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.assignmentservice.model.Priority;
import com.assignmentservice.model.SlaRule;
import com.assignmentservice.repositories.SlaRuleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlaRuleInitializer implements CommandLineRunner {

    private final SlaRuleRepository repo;

    @Override
    public void run(String... args) {

        create(Priority.HIGH, 30, 8);
        create(Priority.MEDIUM, 60, 16);
        create(Priority.LOW, 120, 24);
    }

    private void create(Priority priority, int resp, int res) {

        repo.findByPriorityAndActiveTrue(priority)
            .orElseGet(() -> {
                SlaRule rule = new SlaRule();
                rule.setPriority(priority);
                rule.setResponseMinutes(resp);
                rule.setResolutionHours(res);
                rule.setActive(true);
                return repo.save(rule);
            });
    }
}


