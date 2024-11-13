package lnrs.search.gateway;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import lnrs.search.generated.api.model.CompanyRequest;
import lnrs.search.generated.api.model.CompanyResource;
import lnrs.search.generated.api.model.OfficerResource;
import lnrs.search.upstream.client.TruProxyCompaniesClient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyGateway {

    private final TruProxyCompaniesClient client;
    private final ModelMapper modelMapper;

    public List<CompanyResource> getCompanies(CompanyRequest request) {
        String query = Optional.ofNullable(request.getCompanyNumber()).orElse(request.getCompanyName());
        return client.getCompanies(query).getItems().stream().map(i -> modelMapper.map(i, CompanyResource.class)).toList();
    }

    public List<OfficerResource> getActiveOfficers(String companyNo) {
        return client.getOfficers(companyNo)
                     .getItems()
                     .stream()
                     .filter(o -> ofNullable(o.getResignedOn()).isEmpty())
                     .map(i -> modelMapper.map(i, OfficerResource.class))
                     .toList();
    }

}
