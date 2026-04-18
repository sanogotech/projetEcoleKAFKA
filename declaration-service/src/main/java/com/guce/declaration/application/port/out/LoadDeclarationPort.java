package com.guce.declaration.application.port.out;

import com.guce.declaration.domain.Declaration;

import java.util.Optional;
import java.util.UUID;

public interface LoadDeclarationPort {

    Optional<Declaration> findById(UUID id);
}
