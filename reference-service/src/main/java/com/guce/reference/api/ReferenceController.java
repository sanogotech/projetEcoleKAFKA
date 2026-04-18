package com.guce.reference.api;

import com.guce.reference.api.dto.CustomsOfficeDto;
import com.guce.reference.api.dto.ShCodeDto;
import com.guce.reference.api.dto.TariffDto;
import com.guce.reference.service.ReferenceCatalog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/references")
@Tag(name = "Référentiels")
public class ReferenceController {

    private final ReferenceCatalog catalog;

    public ReferenceController(ReferenceCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/sh-codes")
    @Operation(summary = "Codes SH (recherche optionnelle)")
    public List<ShCodeDto> shCodes(@RequestParam(required = false) String q) {
        return catalog.searchShCodes(q);
    }

    @GetMapping("/customs-offices")
    @Operation(summary = "Bureaux de douane")
    public List<CustomsOfficeDto> customsOffices() {
        return catalog.listCustomsOffices();
    }

    @GetMapping("/tariffs/{shCode}")
    @Operation(summary = "Tarif douanier par code SH")
    public ResponseEntity<TariffDto> tariff(@PathVariable String shCode) {
        return catalog.findTariff(shCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
