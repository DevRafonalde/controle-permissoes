package br.com.dev1risjc.ControlePermissoes.models.entities.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioPerfilDTO {
    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private UsuarioDTO usuario;

    @Getter @Setter
    private PerfilDTO perfil;

    @Getter @Setter
    private Boolean negacao = false;

    @Getter @Setter
    private LocalDateTime dataHora;

    @Getter @Setter
    private Boolean excluido = false;
}
