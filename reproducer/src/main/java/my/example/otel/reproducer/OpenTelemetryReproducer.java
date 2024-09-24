package my.example.otel.reproducer;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@CamelOpenTelemetry
@SpringBootApplication
public class OpenTelemetryReproducer {

    public static void main(String[] args) {
        SpringApplication.run(OpenTelemetryReproducer.class, args);
    }
}
