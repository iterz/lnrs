package lnrs.search.api;

import lnrs.search.generated.api.CompaniesApiDelegate;
import lnrs.search.generated.api.model.CompaniesResource;
import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyApiDelegate implements CompaniesApiDelegate {

    private final CompanyService service;

    @Override
    public ResponseEntity<CompaniesResource> searchCompanies(CompanyRequest request, Boolean active) {
        if (request.getCompanyName() == null && request.getCompanyNumber() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.searchCompanies(request, active));
    }
    
}
