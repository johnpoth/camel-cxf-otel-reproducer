# Camel CXF OpenTelemetry reproducer

## Running


- Run Jaeger UI, in reproducer folder : `docker compose up -d`
- Start the `reproducer`, in reproducer folder : mvn clean spring-boot:run
- Send request in root folder: `curl -i localhost:8899/services/otel-main-service -X POST --header 'Content-Type: application/xml;charset=UTF-8' -d @./reproducer/src/main/resources/requests/sample-request.xml`
- View Traces in Jaeger http://localhost:16686/ also in logs (search for `OtlpJsonLoggingSpanExporter`)
