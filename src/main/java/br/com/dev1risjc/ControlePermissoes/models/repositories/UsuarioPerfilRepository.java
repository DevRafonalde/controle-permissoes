package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.UsuarioPerfil;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioPerfilRepository extends CrudRepository<UsuarioPerfil, Integer> {

    public List<UsuarioPerfil> findByUsuario(Usuario usuario);
    public List<UsuarioPerfil> findByPerfil(Perfil perfil);
}
