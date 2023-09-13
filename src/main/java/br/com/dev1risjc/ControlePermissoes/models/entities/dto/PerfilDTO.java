package br.com.dev1risjc.ControlePermissoes.models.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PerfilDTO {

    @Getter @Setter
    private Integer id;

    @NotNull(message = "Selecione o sistema relativo ao perfil")
    @Setter
    private SistemaDTO sistema;

    @NotBlank(message = "O nome do perfil é obrigatório")
    @Getter @Setter
    private String Nome;

    @NotBlank(message = "Insira uma breve descrição sobre o funcionamento do perfil")
    @Getter @Setter
    private String descricao;

    @Getter @Setter
    private Boolean excluido = false;

    public SistemaDTO getSistema() {
        if (Objects.isNull(sistema)) {
            SistemaDTO sistemaVazio = new SistemaDTO();
            sistemaVazio.setNome("");
            return sistemaVazio;
        }
        return sistema;
    }
}
