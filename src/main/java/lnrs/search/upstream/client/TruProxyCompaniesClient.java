package lnrs.search.upstream.client;

import lnrs.search.config.UpstreamClientConfig;
import lnrs.search.upstream.model.CompaniesResponse;
import lnrs.search.upstream.model.OfficersResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "companiesClient", url = "${app.upstream.client.companies-endpoint}", configuration = UpstreamClientConfig.class)
public interface TruProxyCompaniesClient {

    @GetMapping("/Search")
    CompaniesResponse getCompanies(@RequestParam(value = "Query", required = true) String query);
   
    @GetMapping("/Officers")
    OfficersResponse getOfficers(@RequestParam(value = "CompanyNumber", required = true) String companyNo);

}
