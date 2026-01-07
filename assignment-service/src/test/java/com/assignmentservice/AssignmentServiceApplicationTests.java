package com.assignmentservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
@Disabled("Context load test disabled due to config-server dependency")
@SpringBootTest(
		  properties = {
		    "spring.cloud.config.enabled=false",
		    "spring.config.import=optional:classpath:/"
		  }
		)
class AssignmentServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
