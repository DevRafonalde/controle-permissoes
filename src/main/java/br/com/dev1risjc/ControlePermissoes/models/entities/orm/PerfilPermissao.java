package br.com.dev1risjc.ControlePermissoes.models.entities.orm;

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

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_Perfil", referencedColumnName = "ID")
    @Getter @Setter
    private Perfil perfil;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_Permissao", referencedColumnName = "ID")
    @Getter @Setter
    private Permissao permissao;

    @Column(name = "DataHora", columnDefinition = "DATETIME")
    @DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    @Getter @Setter
    private LocalDateTime dataHora;

    @Column(name = "Excluido")
    @Getter @Setter
    private Boolean excluido;

}
