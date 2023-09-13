package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PermissaoDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.SistemaDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilPermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.SistemasRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class PermissaoService {

    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private Integer ultimoId;
    private ModelMapper mapper = new ModelMapper();

    public PermissaoService(PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, PerfilPermissaoRepository perfilPermissaoRepository) {
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    public List<PermissaoDTO> listar() {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        ultimoId = permissoes.get(permissoes.size() - 1).getId();
        return permissoes.stream()
                .map(permissao -> mapper.map(permissao, PermissaoDTO.class))
                .toList();
    }

    public PermissaoDTO preEditar(int id) {
        Permissao permissaoBanco = permissaoRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Permissão não encontrada no banco de dados"));
        return mapper.map(permissaoBanco, PermissaoDTO.class);
    }

    public void deletar(int id) {
        Permissao permissaoDelete = permissaoRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Permissão não encontrada no banco de dados"));

        List<PerfilPermissao> usosPermissao = perfilPermissaoRepository.findByPermissao(permissaoDelete);
        if (!usosPermissao.isEmpty()) {
            perfilPermissaoRepository.deleteAll(usosPermissao);
        }

        permissaoRepository.delete(permissaoDelete);
    }

    public void novaPermissao(PermissaoDTO permissao) {
        if (Objects.isNull(ultimoId)) {
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }
        Permissao permissaoRecebida = mapper.map(permissao, Permissao.class);

        ultimoId++;
        permissao.setId(ultimoId);
        permissaoRepository.save(permissaoRecebida);
    }

    public void editar(PermissaoDTO permissao) {
        permissaoRepository.findById(permissao.getId()).orElseThrow(() -> new ElementoNaoEncontradoException("Permissão não encontrada no banco de dados"));

        if (Objects.isNull(ultimoId)) {
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
            permissoes.sort(Comparator.comparingInt(Permissao::getId));
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }

        Permissao permissaoRecebida = mapper.map(permissao, Permissao.class);

        permissaoRepository.save(permissaoRecebida);
    }

    public List<SistemaDTO> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();
        return sistemas.stream()
                .sorted(Comparator.comparing(Sistema::getNome))
                .map(sistema -> mapper.map(sistema, SistemaDTO.class))
                .toList();
    }

    public List<PermissaoDTO> getPermissoesCadastro() {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        return permissoes.stream()
                .sorted(Comparator.comparing(Permissao::getNome))
                .map(permissao -> mapper.map(permissao, PermissaoDTO.class))
                .toList();
    }
}
