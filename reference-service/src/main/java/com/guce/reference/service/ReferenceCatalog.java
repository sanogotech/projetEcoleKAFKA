package com.guce.reference.service;

import com.guce.reference.api.dto.CustomsOfficeDto;
import com.guce.reference.api.dto.ShCodeDto;
import com.guce.reference.api.dto.TariffDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
@Service
public class ReferenceCatalog {

    private final List<ShCodeDto> shCodes = List.of(
            new ShCodeDto("100630", "Blé dur", "10"),
            new ShCodeDto("270900", "Huiles de pétrole", "27"),
            new ShCodeDto("870323", "Véhicules < 1500 cm³", "87"));

    private final List<CustomsOfficeDto> offices = List.of(
            new CustomsOfficeDto("CIAB1", "Abidjan — Bureau principal", "Lagunes"),
            new CustomsOfficeDto("CISN2", "San-Pédro", "Bas-Sassandra"),
            new CustomsOfficeDto("CIBO1", "Bouaké", "Gontougo"));

    private final Map<String, TariffDto> tariffs = Map.of(
            "100630", new TariffDto("100630", BigDecimal.valueOf(5), "XOF"),
            "270900", new TariffDto("270900", BigDecimal.valueOf(10), "XOF"),
            "870323", new TariffDto("870323", BigDecimal.valueOf(20), "XOF"));

    public List<ShCodeDto> searchShCodes(String query) {
        if (query == null || query.isBlank()) {
            return shCodes;
        }
        String q = query.toLowerCase(Locale.ROOT);
        return shCodes.stream()
                .filter(s -> s.code().contains(q) || s.description().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    public List<CustomsOfficeDto> listCustomsOffices() {
        return offices;
    }

    public Optional<TariffDto> findTariff(String shCode) {
        return Optional.ofNullable(tariffs.get(shCode));
    }
}
