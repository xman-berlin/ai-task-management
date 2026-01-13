package at.geise.test.springboot4test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Profile("test")
public class TestAiConfig {

    @Bean
    @Primary
    public WebClient aiWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultHeader("Authorization", "Bearer test-key")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

