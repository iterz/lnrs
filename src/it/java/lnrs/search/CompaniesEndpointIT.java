package lnrs.search;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static lnrs.search.config.UpstreamClientConfig.HEADER_API_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import lnrs.search.generated.api.model.CompaniesResource;
import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.upstream.model.CompaniesResponse;
import lnrs.search.upstream.model.CompanyResponse;
import lnrs.search.upstream.model.OfficerResponse;
import lnrs.search.upstream.model.OfficersResponse;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
class CompaniesEndpointIT {

    private static final String API_KEY = "api-key";
    private static final String UPSTREAM_COMPANIES_URL_PATH = "/Companies/v1/Search";// "/TruProxyAPI/rest/Companies/v1/Search";
    private static final String UPSTREAM_OFFICERS_URL_PATH = "/Companies/v1/Officers";
    private static final String API_COMPANIES_PATH = "/api/companies";

    protected static final WireMockServer truProxyApiMock;
    private static final String CO_NUM = "101";

    static {
        truProxyApiMock = new WireMockServer(wireMockConfig().dynamicPort());
        truProxyApiMock.start();
    }

    @DynamicPropertySource
    protected static void wireUpWiremockEndpoints(DynamicPropertyRegistry registry) {
        registry.add("app.upstream.client.base-endpoint", () -> truProxyApiMock.baseUrl());
        registry.add("app.upstream.client.api-key", () -> API_KEY);
    }

    private final EasyRandom generator = new EasyRandom();
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper marshaller;

    @Test
    void searchCompanies() throws Exception {
        // Given
        List<CompanyResponse> upstreamCompanies =
            generator.objects(CompanyResponse.class, 1).map(c -> c.setCompanyNumber(CO_NUM)).toList();
        List<OfficerResponse> upstreamOfficers =
            generator.objects(OfficerResponse.class, 2).map(o -> o.setResignedOn(null)).toList();
        CompaniesResponse upstreamCompaniesResponse = new CompaniesResponse().setItems(upstreamCompanies);
        OfficersResponse upstreamOfficersResponse = new OfficersResponse().setItems(upstreamOfficers);
        truProxyApiMock.stubFor(upstreamGet(urlPathTemplate(UPSTREAM_COMPANIES_URL_PATH)).withQueryParam("Query",
                                                                                                         WireMock.equalTo(CO_NUM.toString()))
                                                                                         .willReturn(okJson(toJson(upstreamCompaniesResponse))));
        truProxyApiMock.stubFor(upstreamGet(urlPathTemplate(UPSTREAM_OFFICERS_URL_PATH)).withQueryParam("CompanyNumber",
                                                                                                        WireMock.equalTo(CO_NUM.toString()))
                                                                                        .willReturn(okJson(toJson(upstreamOfficersResponse))));
        CompanyRequest request = new CompanyRequest().companyNumber(CO_NUM.toString());

        // When
        CompaniesResource response =
            fromJson(mockMvc.perform(post(API_COMPANIES_PATH).content(toJson(request)).contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString(),
                     CompaniesResource.class);

        // Then
        assertThat(response).satisfies(r -> {
            assertThat(r.getItems()).hasSameSizeAs(upstreamCompaniesResponse.getItems());
            assertThat(r.getTotalResults().longValue()).isEqualTo(upstreamCompaniesResponse.getItems().size());
        });
        assertThat(response.getItems()).zipSatisfy(upstreamCompaniesResponse.getItems(), (r, src) -> {
            assertThat(r.getAddress().getPostalCode()).isEqualTo(src.getAddress().getPostalCode());
            assertThat(r.getCompanyNumber()).isEqualTo(src.getCompanyNumber());
            assertThat(r.getTitle()).isEqualTo(src.getTitle());
            assertThat(r.getCompanyStatus()).isEqualTo(src.getCompanyStatus());
        });
        response.getItems().forEach(c -> {
            assertThat(c.getOfficers()).zipSatisfy(upstreamOfficersResponse.getItems(), (r, src) -> {
                assertThat(r.getAddress().getPostalCode()).isEqualTo(src.getAddress().getPostalCode());
                assertThat(r.getName()).isEqualTo(src.getName());
                assertThat(r.getOfficerRole()).isEqualTo(src.getOfficerRole());
                assertThat(r.getAppointedOn()).isEqualTo(src.getAppointedOn());
            });
            ;
        });
    }

    protected final MappingBuilder upstreamGet(UrlPattern urlPattern) {
        return WireMock.get(urlPattern).withHeader(HEADER_API_KEY, WireMock.equalTo(API_KEY));
    }

    protected String toJson(Object payload) {
        try {
            return marshaller.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T fromJson(String json, Class<T> type) {
        try {
            return marshaller.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
