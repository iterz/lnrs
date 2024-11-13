package lnrs.search.service;

import java.math.BigDecimal;
import java.util.List;

import lnrs.search.gateway.CompanyGateway;
import lnrs.search.generated.api.model.CompaniesResource;
import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.generated.api.model.CompanyResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    public static final String ACTIVE_STATUS = "active";
    
    private final CompanyGateway gateway;

    public CompaniesResource searchCompanies(CompanyRequest request, Boolean active) {
        List<CompanyResource> companies = gateway.getCompanies(request)
                                                 .stream()
                                                 .filter(c -> !active || ACTIVE_STATUS.equals(c.getCompanyStatus()))
                                                 .toList();
        companies.forEach(c -> {
            c.officers(gateway.getActiveOfficers(c.getCompanyNumber()));
        });
        return new CompaniesResource().items(companies).totalResults(BigDecimal.valueOf(companies.size()));
    }

}
