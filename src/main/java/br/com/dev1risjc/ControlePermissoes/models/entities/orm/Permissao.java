package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.Objects;

@Entity
@Table(name = "tbl_Permissao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Permissao {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ID_Sistema")
    @Setter
    private Sistema sistema;

    @Column(name = "Nome")
    @Getter @Setter
    private String Nome;

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
    private Boolean desabilitado;

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
