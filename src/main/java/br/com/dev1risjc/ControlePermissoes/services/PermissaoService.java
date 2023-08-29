package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilPermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.SistemasRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class PermissaoService {

    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private Integer ultimoId;

    public PermissaoService(PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, PerfilPermissaoRepository perfilPermissaoRepository) {
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    public List<Permissao> listar() {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        ultimoId = permissoes.get(permissoes.size() - 1).getId();
        return permissoes;
    }

    public void novaPermissao(Permissao permissao) {
        if (Objects.isNull(ultimoId)) {
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }

        ultimoId++;
        permissao.setId(ultimoId);
        permissaoRepository.save(permissao);
    }

    public void editar(Permissao permissao) {
        Permissao permissaoBanco = permissaoRepository.findById(permissao.getId()).orElse(null);

        if (Objects.isNull(permissaoBanco)) {
            throw new ElementoNaoEncontradoException("Permissão não encontrada no banco de dados");
        }

        if (Objects.isNull(ultimoId)) {
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
            permissoes.sort(Comparator.comparingInt(Permissao::getId));
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }

        ultimoId++;
        permissao.setId(ultimoId);
        permissaoRepository.save(permissao);
    }

    public Permissao preEditar(int id) {
        Permissao permissaoBanco = permissaoRepository.findById(id).orElse(null);

        if (Objects.isNull(permissaoBanco)) {
            throw new ElementoNaoEncontradoException("Permissão não encontrada no banco de dados");
        }

        return permissaoBanco;
    }

    public void deletar(int id) {
        Permissao permissaoDelete = permissaoRepository.findById(id).orElse(null);

        if (Objects.isNull(permissaoDelete)) {
            throw new ElementoNaoEncontradoException("Permissão não encontrada no banco de dados");
        }

        List<PerfilPermissao> usosPermissao = perfilPermissaoRepository.findByPermissao(permissaoDelete);
        if (!usosPermissao.isEmpty()) {
            perfilPermissaoRepository.deleteAll(usosPermissao);
        }

        permissaoRepository.delete(permissaoDelete);
    }

    public List<Sistema> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();

        sistemas.sort(Comparator.comparing(Sistema::getNome));
        return sistemas;
    }

    public List<Permissao> getPermissoesCadastro() {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        permissoes.sort(Comparator.comparing(Permissao::getNome));
        return permissoes;
    }
}
