package br.com.dev1risjc.ControlePermissoes.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "tbl_Perfil")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Perfil {

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
    private String nome;

    @Column(name = "Descricao")
    @Getter @Setter
    private String descricao;

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido;
}
