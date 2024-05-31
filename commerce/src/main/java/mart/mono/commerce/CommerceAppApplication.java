package mart.mono.commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CommerceAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommerceAppApplication.class, args);
    }
}
