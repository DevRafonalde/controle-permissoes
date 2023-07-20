package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.Permissao;
import org.springframework.data.repository.CrudRepository;

public interface PermissaoRepository extends CrudRepository<Permissao, Integer> {
}
