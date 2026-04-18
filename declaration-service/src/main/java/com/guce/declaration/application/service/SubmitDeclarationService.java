package com.guce.declaration.application.service;

import com.guce.declaration.application.port.in.SubmitDeclarationCommand;
import com.guce.declaration.application.port.in.SubmitDeclarationUseCase;
import com.guce.declaration.application.port.out.DauSoumisePublisher;
import com.guce.declaration.application.port.out.SaveDeclarationPort;
import com.guce.declaration.domain.Declaration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class SubmitDeclarationService implements SubmitDeclarationUseCase {

    private final SaveDeclarationPort saveDeclarationPort;
    private final DauSoumisePublisher dauSoumisePublisher;

    public SubmitDeclarationService(
            SaveDeclarationPort saveDeclarationPort,
            DauSoumisePublisher dauSoumisePublisher) {
        this.saveDeclarationPort = saveDeclarationPort;
        this.dauSoumisePublisher = dauSoumisePublisher;
    }

    @Override
    @Transactional
    public Declaration submit(SubmitDeclarationCommand command) {
        Declaration declaration = Declaration.newSubmission(
                command.correlationId(),
                command.declarantId(),
                command.customsOfficeCode(),
                command.referenceNumber(),
                command.payloadJson());
        Declaration saved = saveDeclarationPort.save(declaration);
        dauSoumisePublisher.publish(saved);
        return saved;
    }
}
