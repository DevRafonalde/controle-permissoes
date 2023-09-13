package br.com.dev1risjc.ControlePermissoes.models.entities.view;

import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PerfilDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ModeloCadastroPerfilUsuarioId {

    @Valid
    @Getter @Setter
    private PerfilDTO perfil;

    @Getter @Setter
    private List<Integer> usuariosPerfilId;
}
