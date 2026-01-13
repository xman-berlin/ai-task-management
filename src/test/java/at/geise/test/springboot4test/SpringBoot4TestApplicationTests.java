package at.geise.test.springboot4test;

import at.geise.test.springboot4test.config.TestAiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAiConfig.class)
class SpringBoot4TestApplicationTests {

    @Test
    void contextLoads() {
    }

}
