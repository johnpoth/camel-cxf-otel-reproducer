package my.example.otel.reproducer.support;

import https.www_w3schools_com.xml.TempConvertSoap;
import io.opentelemetry.api.OpenTelemetry;
import org.apache.camel.component.cxf.common.DataFormat;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.camel.component.cxf.spring.jaxrs.SpringJAXRSClientFactoryBean;
import org.apache.camel.cxf.wsrm.HelloWorld;
import org.apache.camel.pizza.Pizza;
import org.apache.cxf.tracing.opentelemetry.OpenTelemetryClientFeature;
import org.apache.cxf.tracing.opentelemetry.OpenTelemetryFeature;
import org.apache.cxf.tracing.opentelemetry.jaxrs.OpenTelemetryProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class CxfBeans {

    @Bean
    @ConditionalOnProperty(prefix = "reproducer", name = "enable-cxf-otel-features", havingValue = "true")
    OpenTelemetryFeature openTelemetryFeature() {
        return new OpenTelemetryFeature();
    }

    @Bean
    @ConditionalOnProperty(prefix = "reproducer", name = "enable-cxf-otel-features", havingValue = "true")
    OpenTelemetryClientFeature openTelemetryClientFeature() {
        return new OpenTelemetryClientFeature();
    }

    @Bean
    @ConditionalOnBean(OpenTelemetry.class)
    OpenTelemetryProvider openTelemetryProvider(OpenTelemetry openTelemetry) {
        return new OpenTelemetryProvider(openTelemetry, (String) null);
    }

    @Value("${reproducer.cxf-sync:false}")
    boolean cxfSynchronous;

    @Bean
    CxfEndpoint otelMainSoapService(
            ObjectProvider<OpenTelemetryFeature> openTelemetryFeature
    ) {
        CxfEndpoint endpoint = new CxfEndpoint();

        endpoint.setAddress("/otel-main-service");
        endpoint.setLoggingFeatureEnabled(true);
        endpoint.setSkipFaultLogging(false);
        endpoint.setDataFormat(DataFormat.PAYLOAD);
        endpoint.setServiceClass(TempConvertSoap.class);
        endpoint.setSynchronous(cxfSynchronous);
        openTelemetryFeature.ifAvailable(f -> endpoint.getFeatures().add(f));

        return endpoint;
    }

    @Bean
    CxfEndpoint sayHiSoap(
            ObjectProvider<OpenTelemetryClientFeature> openTelemetryClientFeature,
            @Value("${say-hi-soap-url}") String address
    ) {
        CxfEndpoint endpoint = new CxfEndpoint();

        endpoint.setAddress(address);
        endpoint.setLoggingFeatureEnabled(true);
        endpoint.setLoggingSizeLimit(5_000);
        endpoint.setSkipFaultLogging(false);
        endpoint.setDataFormat(DataFormat.PAYLOAD);
        endpoint.setServiceClass(HelloWorld.class);
        endpoint.setSynchronous(cxfSynchronous);
        openTelemetryClientFeature.ifAvailable(f -> endpoint.getFeatures().add(f));

        return endpoint;
    }

    @Bean
    CxfEndpoint pizzaSoap(
            ObjectProvider<OpenTelemetryClientFeature> openTelemetryClientFeature,
            @Value("${pizza-soap-url}") String address
    ) {
        CxfEndpoint endpoint = new CxfEndpoint();

        endpoint.setAddress(address);
        endpoint.setLoggingFeatureEnabled(true);
        endpoint.setLoggingSizeLimit(5_000);
        endpoint.setSkipFaultLogging(false);
        endpoint.setDataFormat(DataFormat.PAYLOAD);
        endpoint.setServiceClass(Pizza.class);
        endpoint.setSynchronous(cxfSynchronous);
        openTelemetryClientFeature.ifAvailable(f -> endpoint.getFeatures().add(f));

        return endpoint;
    }

    @Bean
    SpringJAXRSClientFactoryBean sayHiRest(
            ObjectProvider<OpenTelemetryProvider> openTelemetryProvider,
            @Value("${say-hi-rest-url}") String address
    ) {
        SpringJAXRSClientFactoryBean endpoint = new SpringJAXRSClientFactoryBean();
        
        endpoint.setAddress(address);
        endpoint.setLoggingFeatureEnabled(true);
        endpoint.setLoggingSizeLimit(5_000);
        endpoint.setSkipFaultLogging(false);
        endpoint.setServiceClass(SayHiRestService.class);
        openTelemetryProvider.ifAvailable(endpoint::setProvider);

        return endpoint;
    }
    
}
