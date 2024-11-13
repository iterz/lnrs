package lnrs.search.upstream.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CompaniesResponse {

    private List<CompanyResponse> items = new ArrayList<>();

}
