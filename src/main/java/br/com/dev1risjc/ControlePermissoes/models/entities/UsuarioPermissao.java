package br.com.dev1risjc.ControlePermissoes.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tbl_UsuarioPermissao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioPermissao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ID_Usuario", referencedColumnName = "ID")
    @Getter @Setter
    private Usuario usuario;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ID_Perfil", referencedColumnName = "ID")
    @Getter @Setter
    private Perfil perfil;

    @Column(name = "Negacao")
    @Getter @Setter
    private Boolean negacao;

    @Column(name = "DataHora", columnDefinition = "DATETIME")
    @DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @Getter @Setter
    private LocalDateTime dataHora;

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido;
}
