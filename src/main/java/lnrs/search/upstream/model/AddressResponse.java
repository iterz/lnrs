package lnrs.search.upstream.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddressResponse {

    private String premises;
    private String locality;
    private String addressLine1;
    private String country;
    private String postalCode;
}
