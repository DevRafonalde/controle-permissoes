package br.com.dev1risjc.ControlePermissoes.models.entities.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioDTO {

    @Getter @Setter
    private Integer id;

    @NotBlank(message = "Insira o nome completo do usuário")
    @Getter @Setter
    private String nomeCompleto;

    @NotBlank(message = "Insira um nome amigável para o usuário")
    @Getter @Setter
    private String NomeAmigavel;

    @NotBlank(message = "Insira o nome de usuário pelo qual o funcionário irá se logar")
    @Getter @Setter
    private String nomeUser;

    @NotBlank(message = "Insira uma senha")
    @Getter @Setter
    private String senhaUser;

//    @Transient
//    @Getter @Setter
//    private String senhaUser;

    @Getter @Setter
    private Integer idWebRi;

    @Getter @Setter
    private Integer idWebTd;

    @Getter @Setter
    private Integer idWebRiCaixa;

    @Getter @Setter
    private Integer idWebTdCaixa;

    @Getter @Setter
    private Boolean ativo = true;

    @Getter @Setter
    private Boolean caixaVirtual = false;

    @Getter @Setter
    private String observacao;
}
