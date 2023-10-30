package com.example.tangerine.api;

import com.example.tangerine.api.testcontainers.TestcontainersInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersInitializer.class)
class TangerineApiApplicationTests {
  @Test
  void contextLoads() {
  }
}
