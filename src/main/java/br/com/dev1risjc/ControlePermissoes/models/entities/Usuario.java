package br.com.dev1risjc.ControlePermissoes.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_Usuario")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter @Setter
    private Integer id;

    // https://stackoverflow.com/questions/51268935/set-thvalue-in-thymeleaf-date-input
    @Column(name = "NomeCompleto")
    @Getter @Setter
    private String nomeCompleto;

    @Column(name = "NomeAmigavel")
    @Getter @Setter
    private String NomeAmigavel;

    @Column(name = "NomeUser")
    @Getter @Setter
    private String nomeUser;

    @Column(name = "SenhaUser")
    @Getter @Setter
    private String senhaUser;

    @Column(name = "ID_WebRI")
    @Getter @Setter
    private Integer idWebRi;

    @Column(name = "ID_WebTD")
    @Getter @Setter
    private Integer idWebTd;

    @Column(name = "ID_WebRI_Caixa")
    @Getter @Setter
    private Integer idWebRiCaixa;

    @Column(name = "ID_WebTD_Caixa")
    @Getter @Setter
    private Integer idWebTdCaixa;

    @Column(name = "Ativo")
    @Getter @Setter
    private Boolean ativo;

    @Column(name = "CaixaVirtual")
    @Getter @Setter
    private Boolean caixaVirtual;

    @Column(name = "Observacao")
    @Getter @Setter
    private String observacao;

}
