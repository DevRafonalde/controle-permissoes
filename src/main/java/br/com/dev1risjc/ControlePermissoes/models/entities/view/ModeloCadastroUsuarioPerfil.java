package br.com.dev1risjc.ControlePermissoes.models.entities.view;

import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PerfilDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.UsuarioDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
public class ModeloCadastroUsuarioPerfil {
    @Valid
    @Getter @Setter
    private UsuarioDTO usuario;

    @Getter @Setter
    private List<PerfilDTO> perfisUsuario;

}
