package br.com.dev1risjc.ControlePermissoes.exceptions;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ExceptionResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private LocalDateTime dataHora;

    @Getter
    private String mensagem;

    @Getter
    private String detalhes;

    public ExceptionResponse(LocalDateTime dataHora, String mensagem, String detalhes) {
        this.dataHora = dataHora;
        this.mensagem = mensagem;
        this.detalhes = detalhes;
    }
}
