package br.com.dev1risjc.ControlePermissoes.models.entities.view;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ModeloCadastroUsuarioPerfil {
    @Valid
    @Getter @Setter
    private Usuario usuario;

    @Getter @Setter
    private List<Perfil> perfisUsuario;

}
