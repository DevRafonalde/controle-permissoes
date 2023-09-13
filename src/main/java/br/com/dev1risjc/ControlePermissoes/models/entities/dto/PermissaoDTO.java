package br.com.dev1risjc.ControlePermissoes.models.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PermissaoDTO {

    @Getter @Setter
    private Integer id;

    @NotNull(message = "Selecione o sistema relativo à permissão")
    @Setter
    private SistemaDTO sistema;

    @NotBlank(message = "O nome da permissão é obrigatório")
    @Getter @Setter
    private String Nome;

    @NotBlank(message = "Insira uma breve descrição sobre o funcionamento da permissão")
    @Getter @Setter
    private String descricao;

    @Getter @Setter
    private Boolean gerarLog;

    @Getter @Setter
    private Integer idPermissaoSuperior;

    @Getter @Setter
    private Boolean desabilitado = false;

    @NotBlank(message = "Insira um mnemônico para essa permissão")
    @Getter @Setter
    private String mnemonico;

    public SistemaDTO getSistema() {
        if (Objects.isNull(sistema)) {
            SistemaDTO sistemaVazio = new SistemaDTO();
            sistemaVazio.setNome("");
            return sistemaVazio;
        }
        return sistema;
    }
}
