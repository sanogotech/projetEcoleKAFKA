package com.guce.declaration.infrastructure.persistence;

import com.guce.declaration.application.port.out.LoadDeclarationPort;
import com.guce.declaration.application.port.out.SaveDeclarationPort;
import com.guce.declaration.domain.Declaration;
import com.guce.declaration.infrastructure.persistence.entity.DeclarationEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class DeclarationPersistenceAdapter implements SaveDeclarationPort, LoadDeclarationPort {

    private final DeclarationJpaRepository repository;
    private final DeclarationMapper mapper;

    public DeclarationPersistenceAdapter(DeclarationJpaRepository repository, DeclarationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Declaration save(Declaration declaration) {
        DeclarationEntity entity = mapper.toEntity(declaration);
        DeclarationEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Declaration> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
