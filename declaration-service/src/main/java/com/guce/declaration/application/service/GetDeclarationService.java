package com.guce.declaration.application.service;

import com.guce.declaration.application.port.out.LoadDeclarationPort;
import com.guce.declaration.domain.Declaration;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetDeclarationService {

    private final LoadDeclarationPort loadDeclarationPort;

    public GetDeclarationService(LoadDeclarationPort loadDeclarationPort) {
        this.loadDeclarationPort = loadDeclarationPort;
    }

    public Optional<Declaration> getById(UUID id) {
        return loadDeclarationPort.findById(id);
    }
}
