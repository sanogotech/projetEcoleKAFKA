package com.guce.portal.web;

import com.guce.portal.config.PortalProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class DashboardController {

    private final RestTemplate restTemplate;
    private final PortalProperties urls;

    public DashboardController(RestTemplate restTemplate, PortalProperties urls) {
        this.restTemplate = restTemplate;
        this.urls = urls;
    }

    @GetMapping("/")
    public String dashboard(
            Model model,
            @RequestParam(required = false) Boolean loadRefs,
            @RequestParam(required = false) String tab) {
        if (Boolean.TRUE.equals(loadRefs)) {
            try {
                ResponseEntity<List<Map<String, Object>>> sh = restTemplate.exchange(
                        urls.getReference() + "/api/v1/references/sh-codes",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });
                model.addAttribute("shCodes", sh.getBody());
                ResponseEntity<List<Map<String, Object>>> offices = restTemplate.exchange(
                        urls.getReference() + "/api/v1/references/customs-offices",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });
                model.addAttribute("customsOffices", offices.getBody());
            } catch (Exception e) {
                model.addAttribute("refsError", "Service référentiels (8086) : " + shortErr(e));
            }
        }
        if (tab != null && !tab.isBlank()) {
            model.addAttribute("activeTab", tab);
        }
        if (!model.containsAttribute("activeTab")) {
            model.addAttribute("activeTab", "overview");
        }
        return "dashboard";
    }

    @PostMapping("/submit/declaration")
    public String submitDeclaration(
            @RequestParam String correlationId,
            @RequestParam String declarantId,
            @RequestParam String customsOfficeCode,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam String payloadJson,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("correlationId", correlationId);
        body.put("declarantId", declarantId);
        body.put("customsOfficeCode", customsOfficeCode);
        body.put("referenceNumber", referenceNumber);
        body.put("payloadJson", payloadJson);
        return postJson(urls.getDeclaration() + "/api/v1/declarations", body, ra, "DAU soumise avec succès.", "declaration");
    }

    @PostMapping("/submit/manifest")
    public String submitManifest(
            @RequestParam String manifestNumber,
            @RequestParam String vesselName,
            @RequestParam(required = false) String arrivalDate,
            @RequestParam String linesJson,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("manifestNumber", manifestNumber);
        body.put("vesselName", vesselName);
        body.put("arrivalDate", arrivalDate);
        body.put("linesJson", linesJson);
        return postJson(urls.getManifest() + "/api/v1/manifests", body, ra, "Manifeste enregistré.", "manifest");
    }

    @PostMapping("/submit/authorization")
    public String submitAuthorization(
            @RequestParam String applicantId,
            @RequestParam String goodsDescription,
            @RequestParam(required = false) String expiresAt,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("applicantId", applicantId);
        body.put("goodsDescription", goodsDescription);
        body.put("expiresAt", expiresAt);
        return postJson(urls.getAuthorization() + "/api/v1/authorizations", body, ra, "Demande d'autorisation créée.", "authorization");
    }

    @PostMapping("/submit/taxes")
    public String submitTaxes(
            @RequestParam String declarationId,
            @RequestParam(required = false) String amountXof,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (amountXof != null && !amountXof.isBlank()) {
            body.put("amountXof", new BigDecimal(amountXof));
        }
        body.put("declarationId", UUID.fromString(declarationId.trim()));
        return postJson(urls.getPayment() + "/api/v1/payments/taxes/calculate", body, ra, "Taxes calculées (événement dau.taxee).", "payment");
    }

    @PostMapping("/submit/payment-confirm")
    public String submitPaymentConfirm(
            @RequestParam String declarationId,
            @RequestParam String amount,
            @RequestParam String channel,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("amount", new BigDecimal(amount));
        body.put("channel", channel);
        String url = urls.getPayment() + "/api/v1/payments/" + declarationId + "/confirm";
        return postJson(url, body, ra, "Paiement confirmé (événement paiement.confirme).", "payment");
    }

    @PostMapping("/submit/inspection-orient")
    public String inspectionOrient(@RequestParam String declarationId, RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("declarationId", UUID.fromString(declarationId.trim()));
        return postJson(urls.getInspection() + "/api/v1/inspections/orient", body, ra, "Orientation canal calculée.", "inspection");
    }

    @PostMapping("/submit/inspection-schedule")
    public String inspectionSchedule(
            @RequestParam String declarationId,
            @RequestParam String channel,
            @RequestParam(required = false) String scheduledAt,
            @RequestParam(required = false) String notes,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("declarationId", UUID.fromString(declarationId.trim()));
        body.put("channel", channel);
        body.put("scheduledAt", scheduledAt);
        body.put("notes", notes);
        return postJson(urls.getInspection() + "/api/v1/inspections", body, ra, "Inspection planifiée.", "inspection");
    }

    @PostMapping("/submit/pcs-movement")
    public String pcsMovement(
            @RequestParam String containerNumber,
            @RequestParam(required = false) String declarationId,
            @RequestParam String movementType,
            @RequestParam(required = false) String occurredAt,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("containerNumber", containerNumber);
        if (declarationId != null && !declarationId.isBlank()) {
            body.put("declarationId", UUID.fromString(declarationId.trim()));
        }
        body.put("movementType", movementType);
        body.put("occurredAt", occurredAt);
        return postJson(urls.getPcs() + "/api/v1/pcs/container-movements", body, ra, "Mouvement conteneur enregistré.", "pcs");
    }

    @PostMapping("/submit/pcs-mainlevee")
    public String pcsMainlevee(
            @RequestParam String declarationId,
            @RequestParam String mainleveeId,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("declarationId", UUID.fromString(declarationId.trim()));
        body.put("mainleveeId", UUID.fromString(mainleveeId.trim()));
        return postJson(urls.getPcs() + "/api/v1/pcs/notifications/mainlevee", body, ra, "Accusé mainlevée envoyé au port.", "pcs");
    }

    @PostMapping("/submit/operator")
    public String submitOperator(
            @RequestParam String legalName,
            @RequestParam String email,
            @RequestParam String role,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("legalName", legalName);
        body.put("email", email);
        body.put("role", role);
        return postJson(urls.getOperator() + "/api/v1/operators", body, ra, "Opérateur enregistré.", "operator");
    }

    @PostMapping("/submit/notification")
    public String submitNotification(
            @RequestParam String channel,
            @RequestParam String target,
            @RequestParam String bodyText,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("channel", channel);
        body.put("target", target);
        body.put("body", bodyText);
        return postJson(urls.getNotification() + "/api/v1/notifications/dispatch", body, ra, "Notification demandée (topic notification.demande).", "notification");
    }

    @PostMapping("/submit/audit")
    public String submitAudit(
            @RequestParam String declarationId,
            @RequestParam String eventType,
            @RequestParam String actor,
            @RequestParam String payloadJson,
            @RequestParam(required = false) String occurredAt,
            RedirectAttributes ra) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("declarationId", UUID.fromString(declarationId.trim()));
        body.put("eventType", eventType);
        body.put("actor", actor);
        body.put("payloadJson", payloadJson);
        body.put("occurredAt", occurredAt);
        return postJson(urls.getAudit() + "/api/v1/audit/records", body, ra, "Événement d'audit enregistré.", "audit");
    }

    private String postJson(String url, Map<String, Object> body, RedirectAttributes ra, String okMsg, String tab) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);
            ra.addFlashAttribute("flashSuccess", okMsg);
            if (resp.getBody() != null && resp.getBody().get("id") != null) {
                ra.addFlashAttribute("lastEntityId", String.valueOf(resp.getBody().get("id")));
            }
        } catch (ResourceAccessException e) {
            ra.addFlashAttribute("flashError", "Service injoignable : " + shortErr(e));
        } catch (HttpStatusCodeException e) {
            ra.addFlashAttribute("flashError", "Erreur API (" + e.getStatusCode() + ") : " + e.getResponseBodyAsString());
        } catch (Exception e) {
            ra.addFlashAttribute("flashError", shortErr(e));
        }
        ra.addFlashAttribute("activeTab", tab);
        return "redirect:/";
    }

    private static String shortErr(Exception e) {
        String m = e.getMessage();
        return m != null && m.length() > 200 ? m.substring(0, 200) + "…" : (m != null ? m : e.getClass().getSimpleName());
    }
}
