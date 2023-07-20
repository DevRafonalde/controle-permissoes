package br.com.dev1risjc.ControlePermissoes.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "tbl_Permissao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_Sistema")
    @Getter @Setter
    private Sistema sistema;

    @Column(name = "Nome")
    @Getter @Setter
    private String Nome;

    @NotBlank
    @Column(name = "Descricao")
    @Getter @Setter
    private String descricao;

    @Column(name = "GerarLog")
    @Getter @Setter
    private Boolean gerarLog;

//    @Column(name = "ID_Permissao_Superior")
//    @Getter @Setter
//    private Permissao permissao;

    @Column(name = "ID_Permissao_Superior")
    @Getter @Setter
    private Integer idPermissaoSuperior;

    @Column(name = "Desabilitado")
    @Getter @Setter
    private Boolean desabilitado;

    @Column(name = "Mnemonico")
    @Getter @Setter
    private String mnemonico;
}
