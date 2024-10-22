package com.aws.project;

import com.aws.project.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(AppConfig.class)
class ProjectApplicationTests {

	@Test
	void contextLoads() {
	}

}
