package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_Sistema")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Sistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @Column(name = "Nome")
    @Getter @Setter
    private String nome;

    @Column(name = "Prefixo")
    @Getter @Setter
    private String prefixo;

    @Column(name = "Descricao")
    @Getter @Setter
    private String descricao;

    @Column(name = "VersaoBanco")
    @Getter @Setter
    private Integer versaoBanco;
}
