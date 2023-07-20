package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.Perfil;
import org.springframework.data.repository.CrudRepository;

public interface PerfilRepository extends CrudRepository<Perfil, Integer> {
}
