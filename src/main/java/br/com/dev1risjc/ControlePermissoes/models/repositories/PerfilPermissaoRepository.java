package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.PerfilPermissao;
import org.springframework.data.repository.CrudRepository;

public interface PerfilPermissaoRepository extends CrudRepository<PerfilPermissao, Integer> {
}
