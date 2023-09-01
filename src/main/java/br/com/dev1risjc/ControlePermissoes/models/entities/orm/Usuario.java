package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "tbl_Usuario")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @NotBlank(message = "Insira o nome completo do usuário")
    @Column(name = "NomeCompleto")
    @Getter @Setter
    private String nomeCompleto;

    @NotBlank(message = "Insira um nome amigável para o usuário")
    @Column(name = "NomeAmigavel")
    @Getter @Setter
    private String NomeAmigavel;

    @NotBlank(message = "Insira o nome de usuário pelo qual o funcionário irá se logar")
    @Column(name = "NomeUser")
    @Getter @Setter
    private String nomeUser;

    @NotBlank(message = "Insira uma senha")
    @Column(name = "SenhaUser")
    @Getter @Setter
    private String senhaUser;

//    @Transient
//    @Getter @Setter
//    private String senhaUser;

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
    private Boolean ativo = true;

    @Column(name = "CaixaVirtual")
    @Getter @Setter
    private Boolean caixaVirtual = false;

    @Column(name = "Observacao")
    @Getter @Setter
    private String observacao;

}
