package lnrs.search.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;

import lnrs.search.gateway.CompanyGateway;
import lnrs.search.generated.api.model.CompaniesResource;
import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.generated.api.model.CompanyResource;
import lnrs.search.generated.api.model.OfficerResource;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyGateway gateway;

    @InjectMocks
    private CompanyService underTest;

    private final EasyRandom generator = new EasyRandom();

    @DisplayName("Should respond with list of companies")
    @Test
    void searchCompanies() {
        // Given
        CompanyRequest request = generator.nextObject(CompanyRequest.class);
        boolean active = false;
        int resultsSize = 2;
        List<OfficerResource> officers = generator.objects(OfficerResource.class, 1).toList();
        List<CompanyResource> companies = generator.objects(CompanyResource.class, resultsSize).toList();
        CompaniesResource expected = new CompaniesResource().items(companies).totalResults(BigDecimal.valueOf(resultsSize));
        given(gateway.getCompanies(request)).willReturn(companies);
        given(gateway.getActiveOfficers(anyString())).willReturn(officers);

        // When
        CompaniesResource response = underTest.searchCompanies(request, active);

        // Then
        assertThat(response).isEqualTo(expected);
        verify(gateway, times(2)).getActiveOfficers(anyString());
    }

    @DisplayName("Should filter only ACTIVE companies")
    @Test
    void searchActiveCompanies() {
        // Given
        CompanyRequest request = generator.nextObject(CompanyRequest.class);
        boolean active = true;
        List<CompanyResource> companies =
            generator.objects(CompanyResource.class, 2).map(c -> c.companyStatus("dissolved")).toList();
        CompaniesResource expected = new CompaniesResource().items(List.of()).totalResults(BigDecimal.valueOf(0));
        given(gateway.getCompanies(request)).willReturn(companies);

        // When
        CompaniesResource response = underTest.searchCompanies(request, active);

        // Then
        assertThat(response).isEqualTo(expected);
        verify(gateway, never()).getActiveOfficers(anyString());
    }

}
