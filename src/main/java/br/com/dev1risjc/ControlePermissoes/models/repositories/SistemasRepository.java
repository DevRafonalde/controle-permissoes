package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.Sistema;
import org.springframework.data.repository.CrudRepository;

public interface SistemasRepository extends CrudRepository<Sistema, Integer> {
}
