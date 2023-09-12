package br.com.dev1risjc.ControlePermissoes.models.entities.view;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ModeloCadastroPerfilUsuarioId {

    @Getter @Setter
    private Perfil perfil;

    @Getter @Setter
    private List<Integer> usuariosPerfilId;
}
