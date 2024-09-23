package my.example.otel.reproducer.support;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
class ExternalServicesMockingRoutes extends RouteBuilder {

    @Override
    public void configure() {

        restConfiguration().inlineRoutes(false);

        rest("/mock/services")
                .post("/sayHiSoap").routeId("rest-POST-say-hi").to("direct:mock-POST-say-hi")
                .post("/pizzaSoap").routeId("rest-POST-pizza").to("direct:mock-POST-pizza")
                .get("/sayHiRest").routeId("rest-GET-say-hi").param().name("name").type(RestParamType.header).endParam().to("direct:mock-GET-say-hi")
        ;

        from("direct:mock-POST-pizza")
                .routeId("mock-POST-pizza")
                .log("from ${routeId}. body received: ${body}")
                .to("language:file:classpath:responses/pizza-response.xml")
                .log("postPizza response: ${body}")
        ;

        from("direct:mock-POST-say-hi")
                .routeId("mock-POST-say-hi")
                .log("from ${routeId}. body received: ${body}")
                .to("language:file:classpath:responses/sayHi-response.xml")
                .log("${routeId} response: ${body}")
        ;

        from("direct:mock-GET-say-hi")
                .routeId("mock-GET-say-hi")
                .log("from ${routeId}. body received: ${body}; name header: ${headers.name}")
                .setBody(e -> "Hello there, %s".formatted(e.getMessage().getHeader("name", String.class)))
                .log("${routeId} response: ${body}")
        ;
    }
}
