package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Selecione o sistema relativo ao perfil")
    @ManyToOne
    @JoinColumn(name = "ID_Sistema")
    @Setter
    private Sistema sistema;

    @NotBlank(message = "O nome do perfil é obrigatório")
    @Column(name = "Nome")
    @Getter @Setter
    private String Nome;

    @NotBlank(message = "Insira uma breve descrição sobre o funcionamento do perfil")
    @Column(name = "Descricao")
    @Getter @Setter
    private String descricao;

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido = false;

    public Sistema getSistema() {
        if (Objects.isNull(sistema)) {
            Sistema sistemaVazio = new Sistema();
            sistemaVazio.setNome("");
            return sistemaVazio;
        }
        return sistema;
    }
}
