package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
    @Query("Select x from Usuario x where x.NomeAmigavel like %:NomeAmigavel%")
    Usuario findByNomeAmigavel(String NomeAmigavel);

    Usuario findByNomeUser(String NomeUser);
}
