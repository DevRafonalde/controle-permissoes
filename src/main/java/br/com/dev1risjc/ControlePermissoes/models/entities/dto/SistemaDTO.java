package br.com.dev1risjc.ControlePermissoes.models.entities.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SistemaDTO {

    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private String nome;

    @Getter @Setter
    private String prefixo;

    @Getter @Setter
    private String descricao;

    @Getter @Setter
    private String versaoBanco;
}
