package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PerfilPermissaoRepository extends CrudRepository<PerfilPermissao, Integer> {
    public List<PerfilPermissao> findByPerfil(Perfil perfil);
    public List<PerfilPermissao> findByPermissao(Permissao permissao);
}
