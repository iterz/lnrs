package lnrs.search.upstream.model;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OfficerResponse {
    
    private String name;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appointedOn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate resignedOn;
    private String officerRole;
    private AddressResponse address;

}
