package my.example.otel.reproducer;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBElement;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.common.CxfPayload;
import org.apache.camel.component.cxf.converter.CxfPayloadConverter;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.camel.pizza.types.OrderPizzaType;
import org.apache.camel.pizza.types.ToppingsListType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;

import static org.apache.camel.component.cxf.common.message.CxfConstants.OPERATION_NAME;
import static org.apache.camel.component.cxf.common.message.CxfConstants.OPERATION_NAMESPACE;

@Component
class CreatePizzaPayloadProcessor implements Processor {

    private static final QName ORDER_REQUEST_QNAME = new QName("http://camel.apache.org/pizza/types","OrderRequest");

    @Override
    public void process(Exchange exchange) throws Exception {
        Document doc = createDocument(createBody(), exchange);
        CxfPayload<?> cxfPayload = CxfPayloadConverter.documentToCxfPayload(doc, exchange);
        exchange.getMessage().setBody(cxfPayload);
        exchange.getMessage().setHeader(OPERATION_NAME, "OrderPizza");
        exchange.getMessage().setHeader(OPERATION_NAMESPACE, "http://camel.apache.org/pizza");
    }

    private static OrderPizzaType createBody() {
        OrderPizzaType body = new OrderPizzaType();
        body.setToppings(new ToppingsListType());
        body.getToppings().getTopping().add("cheese");
        return body;
    }

    private static Document createDocument(OrderPizzaType body, Exchange exchange) throws IOException, ParserConfigurationException, SAXException {
         JAXBElement<OrderPizzaType> response = new JAXBElement<>(ORDER_REQUEST_QNAME, OrderPizzaType.class, body);
        StringWriter sw = new StringWriter();
        JAXB.marshal(response, sw);
        String docString =  sw.toString();
        return new XmlConverter().toDOMDocument(docString, exchange);
    }

}
