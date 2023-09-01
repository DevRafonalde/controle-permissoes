package br.com.dev1risjc.ControlePermissoes.models.entities.view;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ModeloCadastroPerfilPermissao {
    @Valid
    @Getter @Setter
    private Perfil perfil;

    @Getter @Setter
    private List<Permissao> permissoesPerfil;
}
