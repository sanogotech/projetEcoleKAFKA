package com.guce.declaration.infrastructure.persistence;

import com.guce.declaration.domain.Declaration;
import com.guce.declaration.infrastructure.persistence.entity.DeclarationEntity;
import org.springframework.stereotype.Component;

@Component
public class DeclarationMapper {

    public DeclarationEntity toEntity(Declaration domain) {
        DeclarationEntity e = new DeclarationEntity();
        e.setId(domain.getId());
        e.setCorrelationId(domain.getCorrelationId());
        e.setDeclarantId(domain.getDeclarantId());
        e.setCustomsOfficeCode(domain.getCustomsOfficeCode());
        e.setReferenceNumber(domain.getReferenceNumber());
        e.setPayloadJson(domain.getPayloadJson());
        e.setStatus(domain.getStatus());
        e.setCreatedAt(domain.getCreatedAt());
        return e;
    }

    public Declaration toDomain(DeclarationEntity e) {
        return new Declaration(
                e.getId(),
                e.getCorrelationId(),
                e.getDeclarantId(),
                e.getCustomsOfficeCode(),
                e.getReferenceNumber(),
                e.getPayloadJson(),
                e.getStatus(),
                e.getCreatedAt());
    }
}
