package com.guce.declaration.application.port.out;

import com.guce.declaration.domain.Declaration;

public interface SaveDeclarationPort {

    Declaration save(Declaration declaration);
}
