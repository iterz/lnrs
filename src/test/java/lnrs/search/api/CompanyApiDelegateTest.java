package lnrs.search.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lnrs.search.generated.api.model.CompaniesResource;
import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.service.CompanyService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class CompanyApiDelegateTest {

    @Mock
    private CompanyService service;

    @InjectMocks
    private CompanyApiDelegate underTest;

    private final EasyRandom generator = new EasyRandom();

    @DisplayName("Should delegate to service")
    @Test
    void shouldDelegateToService() {
        // Given
        CompanyRequest request = generator.nextObject(CompanyRequest.class);
        boolean active = false;
        CompaniesResource expected = generator.nextObject(CompaniesResource.class);
        when(service.searchCompanies(request, active)).thenReturn(expected);

        // When
        var response = underTest.searchCompanies(request, active);

        // Then
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @DisplayName("Should validate request")
    @Test
    void shouldValidateRequest() {
        // Given
        CompanyRequest request = new CompanyRequest();
        boolean active = false;

        // When
        var response = underTest.searchCompanies(request, active);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(service, never()).searchCompanies(request, active);
    }

}
