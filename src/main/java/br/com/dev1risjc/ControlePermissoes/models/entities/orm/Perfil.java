package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "tbl_Perfil")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido;

    public Sistema getSistema() {
        if (Objects.isNull(sistema)) {
            Sistema sistemaVazio = new Sistema();
            sistemaVazio.setNome("");
            return sistemaVazio;
        }
        return sistema;
    }
}
