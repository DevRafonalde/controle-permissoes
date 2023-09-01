package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "tbl_Permissao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Permissao {

    @Id
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @NotNull(message = "Selecione o sistema relativo à permissão")
    @ManyToOne
    @JoinColumn(name = "ID_Sistema")
    @Setter
    private Sistema sistema;

    @NotBlank(message = "O nome da permissão é obrigatório")
    @Column(name = "Nome")
    @Getter @Setter
    private String Nome;

    @NotBlank(message = "Insira uma breve descrição sobre o funcionamento da permissão")
    @Column(name = "Descricao")
    @Getter @Setter
    private String descricao;

    @Column(name = "GerarLog")
    @Getter @Setter
    private Boolean gerarLog;

    @Column(name = "ID_Permissao_Superior")
    @Getter @Setter
    private Integer idPermissaoSuperior;

    @Column(name = "Desabilitado")
    @Getter @Setter
    private Boolean desabilitado = false;

    @NotBlank(message = "Insira um mnemônico para essa permissão")
    @Column(name = "Mnemonico")
    @Getter @Setter
    private String mnemonico;

    public Sistema getSistema() {
        if (Objects.isNull(sistema)) {
            Sistema sistemaVazio = new Sistema();
            sistemaVazio.setNome("");
            return sistemaVazio;
        }
        return sistema;
    }
}
