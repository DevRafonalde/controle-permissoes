package br.com.dev1risjc.ControlePermissoes.models.entities.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PerfilPermissaoDTO {

    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private PerfilDTO perfil;

    @Getter @Setter
    private PermissaoDTO permissao;

    @Getter @Setter
    private LocalDateTime dataHora;

    @Getter @Setter
    private Boolean excluido = false;

}
