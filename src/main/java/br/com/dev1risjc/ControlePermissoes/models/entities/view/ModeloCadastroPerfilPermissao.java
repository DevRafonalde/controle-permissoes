package br.com.dev1risjc.ControlePermissoes.models.entities.view;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ModeloCadastroPerfilPermissao {
    @Getter @Setter
    private Perfil perfil;

    @Getter @Setter
    private List<Permissao> permissoesPerfil;
}
