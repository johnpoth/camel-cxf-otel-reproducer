package my.example.otel.reproducer;

import jakarta.ws.rs.core.MediaType;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.springframework.stereotype.Component;

@Component
public class MainRoutes extends RouteBuilder {

    public static final String LOG_MESSAGE = "${routeId}: ${body}";
    private final CreateSayHiPayloadProcessor createSayHiPayloadProcessor;
    private final CreatePizzaPayloadProcessor createPizzaPayloadProcessor;

    public MainRoutes(
            CreateSayHiPayloadProcessor createSayHiPayloadProcessor,
            CreatePizzaPayloadProcessor createPizzaPayloadProcessor
    ) {
        this.createSayHiPayloadProcessor = createSayHiPayloadProcessor;
        this.createPizzaPayloadProcessor = createPizzaPayloadProcessor;
    }

    @Override
    public void configure() {

        from("cxf:bean:otelMainSoapService")
                .routeId("otel-main-route")
                .setProperty("originalBody", body())
                .setBody(e -> null)

                .to("direct:say-hi-soap-invoker")

                // to simulate a parallel splitter
                .setBody(e -> FAKE_XML_BODY)
                .setHeader(Exchange.HTTP_METHOD, () -> "GET")
                .split(super.body().tokenizeXML("order", "orders"))//.streaming().parallelProcessing()
                    .setBody(xpath("Parameter[@Name]", String.class))
                    .log(LOG_MESSAGE)
                    .to("direct:say-hi-rest-invoker")
                .end()

                .to("direct:pizza-soap-invoker")
        ;

        from("direct:pizza-soap-invoker")
                .routeId("pizza-soap-invoker-route")
                .process(createPizzaPayloadProcessor)
                .log(LOG_MESSAGE)
                .to("cxf:bean:pizzaSoap");

        from("direct:say-hi-soap-invoker")
                .routeId("say-hi-soap-invoker-route")
                .process(createSayHiPayloadProcessor)
                .to("cxf:bean:sayHiSoap")
                .log(LOG_MESSAGE);

        from("direct:say-hi-rest-invoker")
                .routeId("say-hi-rest-invoker-route")
                .removeHeader(CxfConstants.CAMEL_CXF_MESSAGE)
                .setHeader(CxfConstants.OPERATION_NAME, () -> "sayHi")
                .setHeader(CxfConstants.HTTP_METHOD, () -> "GET")
                .setHeader(CxfConstants.CONTENT_TYPE, () -> MediaType.TEXT_PLAIN)
                .to("cxfrs:bean:sayHiRest")
                .log(LOG_MESSAGE);
    }

    //language=xml
    private static final String FAKE_XML_BODY = """
            <orders>
                <order>
                    <Parameter Name="CustomerID" Value="701423"/>
                </order>
                <order>
                    <Parameter Name="CustomerID" Value="7011337"/>
                </order>
                <order>
                    <Parameter Name="CustomerID" Value="701789"/>
                </order>
            </orders>
            """;
}
