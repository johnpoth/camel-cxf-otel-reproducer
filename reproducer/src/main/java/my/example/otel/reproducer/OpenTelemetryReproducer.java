package my.example.otel.reproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenTelemetryReproducer {

    public static void main(String[] args) {
        SpringApplication.run(OpenTelemetryReproducer.class, args);
    }
}
