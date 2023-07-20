package br.com.dev1risjc.ControlePermissoes.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_PerfilPermissao")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PerfilPermissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Getter @Setter
    private Integer id;

//    @ManyToMany
//    @JoinTable(
//            name = "tbl_PerfilPermissao",
//            joinColumns = @JoinColumn(name = "ID_Perfil"),
//            inverseJoinColumns = @JoinColumn(name = "ID_Permissao")
//    )
    @ManyToOne
    @JoinColumn(name = "ID_Perfil")
    private Perfil perfil;

//    @ManyToMany
//    @JoinTable(
//            name = "tbl_PerfilPermissao",
//            joinColumns = @JoinColumn(name = "ID_Permissao"),
//            inverseJoinColumns = @JoinColumn(name = "ID_Perfil")
//    )
    @ManyToOne
    @JoinColumn(name = "ID_Permissao")
    private Permissao permissoes;

    @Column(name = "DataHora", columnDefinition = "DATETIME")
    @DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @Getter @Setter
    private LocalDateTime dataHora;

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido;

}
