package com.guce.declaration.application.port.in;

import com.guce.declaration.domain.Declaration;

public interface SubmitDeclarationUseCase {

    Declaration submit(SubmitDeclarationCommand command);
}
