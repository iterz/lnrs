package lnrs.search.gateway;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.generated.api.model.CompanyResource;
import lnrs.search.generated.api.model.OfficerResource;
import lnrs.search.upstream.client.TruProxyCompaniesClient;
import lnrs.search.upstream.model.CompaniesResponse;
import lnrs.search.upstream.model.CompanyResponse;
import lnrs.search.upstream.model.OfficerResponse;
import lnrs.search.upstream.model.OfficersResponse;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class CompanyGatewayTest {

    private static final String CO_NAME = "Co Name";
    private static final String CO_NUM = "101";

    @Mock
    private TruProxyCompaniesClient client;
    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private CompanyGateway underTest;

    private final EasyRandom generator = new EasyRandom();

    @DisplayName("Should retrieve companies by name")
    @Test
    void getCompaniesByName() {
        // Given
        CompanyRequest request = new CompanyRequest().companyName(CO_NAME);
        String query = CO_NAME;
        int resultSize = 2;
        List<CompanyResponse> externalResponses = generator.objects(CompanyResponse.class, resultSize).toList();
        List<CompanyResource> expected = generator.objects(CompanyResource.class, resultSize).toList();
        CompaniesResponse externalResponse = new CompaniesResponse().setItems(externalResponses);
        when(client.getCompanies(query)).thenReturn(externalResponse);
        when(mapper.map(any(CompanyResponse.class), eq(CompanyResource.class))).thenReturn(expected.get(0), expected.get(1));

        // When
        List<CompanyResource> response = underTest.getCompanies(request);

        // Then
        assertThat(response).isEqualTo(expected);
        externalResponse.getItems().forEach(e -> {
            verify(mapper).map(e, CompanyResource.class);
        });
    }

    @DisplayName("Should retrieve companies by number")
    @Test
    void getCompaniesByNumber() {
        // Given
        String coName = CO_NAME;
        String coNum = CO_NUM;
        String query = CO_NUM;
        int resultSize = 2;
        CompanyRequest request = new CompanyRequest().companyName(coName).companyNumber(coNum);
        List<CompanyResponse> externalResponses = generator.objects(CompanyResponse.class, resultSize).toList();
        List<CompanyResource> expected = generator.objects(CompanyResource.class, resultSize).toList();
        CompaniesResponse externalResponse = new CompaniesResponse().setItems(externalResponses);
        when(client.getCompanies(query)).thenReturn(externalResponse);
        when(mapper.map(any(CompanyResponse.class), eq(CompanyResource.class))).thenReturn(expected.get(0), expected.get(1));

        // When
        List<CompanyResource> response = underTest.getCompanies(request);

        // Then
        assertThat(response).isEqualTo(expected);
        externalResponse.getItems().forEach(e -> {
            verify(mapper).map(e, CompanyResource.class);
        });
    }

    @DisplayName("Should retrieve active officers by company number")
    @Test
    void getActiveOfficersByCoNum() {
        // Given
        int resultSize = 2;
        List<OfficerResponse> externalResponses =
            generator.objects(OfficerResponse.class, resultSize).map(o -> o.setResignedOn(null)).toList();
        List<OfficerResource> expected = generator.objects(OfficerResource.class, resultSize).toList();
        OfficersResponse externalResponse = new OfficersResponse().setItems(externalResponses);
        when(client.getOfficers(CO_NUM)).thenReturn(externalResponse);
        when(mapper.map(any(OfficerResponse.class), eq(OfficerResource.class))).thenReturn(expected.get(0), expected.get(1));

        // When
        List<OfficerResource> response = underTest.getActiveOfficers(CO_NUM);

        // Then
        assertThat(response).isEqualTo(expected);
        externalResponse.getItems().forEach(e -> {
            verify(mapper).map(e, OfficerResource.class);
        });
    }

    @DisplayName("Should filter inactive officers")
    @Test
    void shouldFilterInActiveOfficers() {
        // Given
        int resultSize = 2;
        List<OfficerResponse> externalResponses =
            generator.objects(OfficerResponse.class, resultSize).map(o -> o.setResignedOn(now())).toList();
        List<OfficerResource> expected = List.of();
        OfficersResponse externalResponse = new OfficersResponse().setItems(externalResponses);
        when(client.getOfficers(CO_NUM)).thenReturn(externalResponse);

        // When
        List<OfficerResource> response = underTest.getActiveOfficers(CO_NUM);

        // Then
        assertThat(response).isEqualTo(expected);
        externalResponse.getItems().forEach(e -> {
            verify(mapper, never()).map(e, OfficerResource.class);
        });
    }

}
