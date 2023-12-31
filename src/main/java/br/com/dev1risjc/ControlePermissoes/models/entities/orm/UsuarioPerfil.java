package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_UsuarioPermissao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioPerfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_Usuario", referencedColumnName = "ID")
    @Getter @Setter
    private Usuario usuario;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_Perfil", referencedColumnName = "ID")
    @Getter @Setter
    private Perfil perfil;

    @Column(name = "Negacao")
    @Getter @Setter
    private Boolean negacao = false;

    @Column(name = "DataHora", columnDefinition = "DATETIME")
    @DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @Getter @Setter
    private LocalDateTime dataHora;

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido = false;
}
