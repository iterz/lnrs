package lnrs.search.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;

public class UpstreamClientConfig {
 
    public static final String HEADER_API_KEY = "x-api-key";

    @Bean
    RequestInterceptor headerInterceptor(UpstreamClientProperties props) {
        return request -> request.header(HEADER_API_KEY, props.getApiKey());
    }

    @Bean
    Logger v3Logger() {
        return new Slf4jLogger("trace.request.upstream");
    }

}
