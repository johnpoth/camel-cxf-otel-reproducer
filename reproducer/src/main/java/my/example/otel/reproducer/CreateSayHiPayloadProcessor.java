package my.example.otel.reproducer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.springframework.stereotype.Component;

@Component
class CreateSayHiPayloadProcessor implements Processor {

    private static final XmlConverter CONVERTER = new XmlConverter();

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getMessage().setBody(CONVERTER.toDOMDocument(TEMPLATE, exchange));
        exchange.getMessage().setHeader("operationName", "sayHi");
        exchange.getMessage().setHeader("operationNamespace", "http://camel.apache.org/cxf/wsrm");
    }

    //language=xml
    private static final String TEMPLATE = """
            <sayHi:sayHi xmlns:sayHi="http://camel.apache.org/cxf/wsrm">
                <arg0>Camel</arg0>
            </sayHi:sayHi>
            """;
}
