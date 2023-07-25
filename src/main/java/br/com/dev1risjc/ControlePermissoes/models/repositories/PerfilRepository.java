package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PerfilRepository extends CrudRepository<Perfil, Integer> {

    @Query("Select x from Perfil x where x.Nome like %:Nome%")
    List<Perfil> findByNome(String Nome);

//    @Query("Select x from Perfil x where (x.ID_Sistema = :ID_Sistema)")
//    List<Perfil> findBySistema(Integer ID_Sistema);
}
