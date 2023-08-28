package br.com.dev1risjc.ControlePermissoes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.CONFLICT)
public class AtributoJaUtilizadoException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AtributoJaUtilizadoException(String mensagem) {
        super(mensagem);
    }
}
