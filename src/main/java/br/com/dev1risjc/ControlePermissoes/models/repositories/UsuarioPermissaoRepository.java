package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.UsuarioPermissao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioPermissaoRepository extends CrudRepository<UsuarioPermissao, Integer> {

    public List<UsuarioPermissao> findByUsuario(Usuario usuario);
}
