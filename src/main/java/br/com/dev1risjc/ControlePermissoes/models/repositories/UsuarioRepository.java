package br.com.dev1risjc.ControlePermissoes.models.repositories;

import br.com.dev1risjc.ControlePermissoes.models.entities.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
    @Query("Select x from Usuario x where x.NomeAmigavel like %:NomeAmigavel%")
    List<Usuario> findByNomeAmigavel(String NomeAmigavel);
//
//    @Query("Select x from Funcionario x where x.cargo.id = :cargoId")
//    List<Usuario> findByCargoId(int cargoId);
//
//    @Query("Select x from Funcionario x where x.dataEntrada >= :dataEntrada order by x.dataEntrada asc")
//    List<Usuario> findByDataEntrada(LocalDate dataEntrada);
//
//    @Query("Select x from Funcionario x where x.dataSaida <= :dataSaida order by x.dataEntrada asc")
//    List<Usuario> findByDataSaida(LocalDate dataSaida);
//
//    @Query("Select x from Funcionario x where x.dataEntrada >= :dataEntrada and x.dataSaida <= :dataSaida order by x.dataEntrada asc")
//    List<Usuario> findByDataEntradaDataSaida(LocalDate dataEntrada, LocalDate dataSaida);
}
