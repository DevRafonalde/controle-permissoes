package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.*;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroUsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PerfilService {

    private PerfilRepository perfilRepository;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private UsuarioPerfilRepository usuarioPerfilRepository;

    public PerfilService(PerfilRepository perfilRepository, PerfilPermissaoRepository perfilPermissaoRepository, PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, UsuarioPerfilRepository usuarioPerfilRepository) {
        this.perfilRepository = perfilRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
    }

    public List<Perfil> listarTodos() {
        return (List<Perfil>) perfilRepository.findAll();
    }

    public ModeloCadastroPerfilPermissao listarEspecifico(Integer id) {
        Perfil perfil = perfilRepository.findById(id).orElse(null);

        if (Objects.isNull(perfil)) {
            throw new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados");
        }

        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = new ModeloCadastroPerfilPermissao();
        modeloCadastroPerfilPermissao.setPerfil(perfil);
        List<Permissao> permissoes = perfilPermissaoRepository.findByPerfil(perfil).stream()
                .map(PerfilPermissao::getPermissao)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(permissao -> permissao.getSistema().getNome()))
                .toList();

        modeloCadastroPerfilPermissao.setPermissoesPerfil(permissoes);

        return modeloCadastroPerfilPermissao;

    }

    public ModeloCadastroPerfilPermissao novoPerfil(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
        Perfil perfilRecebido = modeloCadastroPerfilPermissao.getPerfil();
        Perfil perfilNovo = perfilRepository.save(perfilRecebido);

        List<Permissao> permissoes = modeloCadastroPerfilPermissao.getPermissoesPerfil();

        for (Permissao permissao : permissoes) {
            PerfilPermissao perfilPermissao = new PerfilPermissao();
            perfilPermissao.setPerfil(perfilNovo);
            perfilPermissao.setDataHora(LocalDateTime.now());
            perfilPermissao.setPermissao(permissao);
            perfilPermissaoRepository.save(perfilPermissao);
        }

        List<Permissao> permissoesVinculadas = perfilPermissaoRepository.findByPerfil(perfilNovo).stream()
                .map(PerfilPermissao::getPermissao)
                .toList();

        ModeloCadastroPerfilPermissao modeloRetorno = new ModeloCadastroPerfilPermissao();
        modeloRetorno.setPerfil(perfilNovo);
        modeloRetorno.setPermissoesPerfil(permissoesVinculadas);

        return modeloRetorno;

    }

    public ModeloCadastroPerfilPermissao editar(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
        Perfil perfilMexido = modeloCadastroPerfilPermissao.getPerfil();

        Perfil perfilBanco = perfilRepository.findById(perfilMexido.getId()).orElse(null);

        if (Objects.isNull(perfilBanco)) {
            throw new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados");
        }

        Perfil perfilSalvo = perfilRepository.save(perfilMexido);
        List<PerfilPermissao> registrosExistentes = perfilPermissaoRepository.findByPerfil(modeloCadastroPerfilPermissao.getPerfil());
        perfilPermissaoRepository.deleteAll(registrosExistentes);

        List<Permissao> permissoes = modeloCadastroPerfilPermissao.getPermissoesPerfil();
        for (Permissao permissao : permissoes) {
            PerfilPermissao perfilPermissao = new PerfilPermissao();
            perfilPermissao.setPerfil(modeloCadastroPerfilPermissao.getPerfil());
            perfilPermissao.setDataHora(LocalDateTime.now());
            perfilPermissao.setPermissao(permissao);
            perfilPermissaoRepository.save(perfilPermissao);
        }

        List<Permissao> permissoesVinculadas = perfilPermissaoRepository.findByPerfil(perfilSalvo).stream()
                .map(PerfilPermissao::getPermissao)
                .toList();

        ModeloCadastroPerfilPermissao modeloRetorno = new ModeloCadastroPerfilPermissao();
        modeloRetorno.setPerfil(perfilSalvo);
        modeloRetorno.setPermissoesPerfil(permissoesVinculadas);

        return modeloRetorno;
    }

    public ModeloCadastroPerfilPermissao preEditar(int id) {
        Perfil perfilBanco = perfilRepository.findById(id).orElse(null);

        if (Objects.isNull(perfilBanco)) {
            throw new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados");
        }

        List<Permissao> permissoesPerfil = perfilPermissaoRepository.findByPerfil(perfilBanco).stream()
                .map(PerfilPermissao::getPermissao)
                .toList();

        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = new ModeloCadastroPerfilPermissao();
        modeloCadastroPerfilPermissao.setPerfil(perfilBanco);
        modeloCadastroPerfilPermissao.setPermissoesPerfil(permissoesPerfil);

        return modeloCadastroPerfilPermissao;
    }

    public void deletar(int id) {
        Perfil perfilDelete = perfilRepository.findById(id).orElse(null);

        if (Objects.isNull(perfilDelete)) {
            throw new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados");
        }

        List<UsuarioPerfil> usuariosPerfil = usuarioPerfilRepository.findByPerfil(perfilDelete);
        if (!usuariosPerfil.isEmpty()) {
            usuarioPerfilRepository.deleteAll(usuariosPerfil);
        }

        List<PerfilPermissao> perfisPermissao = perfilPermissaoRepository.findByPerfil(perfilDelete);
        if (!perfisPermissao.isEmpty()) {
            perfilPermissaoRepository.deleteAll(perfisPermissao);
        }

        perfilRepository.delete(perfilDelete);
    }

    public List<Permissao> getPermissoes(PerfilPermissao perfilPermissao) {
        if (Objects.nonNull(perfilPermissao.getId())){
            Perfil perfil = perfilRepository.findById(perfilPermissao.getId()).orElse(null);
            List<Integer> idsPermissoes = perfilPermissaoRepository.findByPerfil(perfil).stream().filter(p -> Objects.nonNull(p.getPerfil())).map(u -> u.getPermissao().getId()).toList();
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAllById(idsPermissoes);
            return permissoes.stream().sorted(Comparator.comparing(Permissao::getNome)).toList();
        }
        List<Permissao> todasPermissoes = (List<Permissao>) permissaoRepository.findAll();
        todasPermissoes.sort(Comparator.comparing(Permissao::getNome));
        return todasPermissoes;
    }

    public List<Sistema> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();
        sistemas.sort(Comparator.comparing(Sistema::getNome));
        return sistemas;
    }

    public ModeloCadastroPerfilPermissao clonar(Integer id) {
        ModeloCadastroPerfilPermissao perfilExistente = listarEspecifico(id);
        perfilExistente.setPerfil(null);
        return perfilExistente;
    }
}
