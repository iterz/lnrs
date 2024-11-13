package lnrs.search.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.upstream.client")
@Validated
@Getter
@RequiredArgsConstructor
public class UpstreamClientProperties {
    
    @NotBlank
    private final String apiKey;

}
