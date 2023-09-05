package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.*;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuario;
import br.com.dev1risjc.ControlePermissoes.models.repositories.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class PerfilService {

    private PerfilRepository perfilRepository;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private UsuarioPerfilRepository usuarioPerfilRepository;
    private UsuarioRepository usuarioRepository;

    @Getter @Setter
    private Integer clonar;

    public PerfilService(PerfilRepository perfilRepository, PerfilPermissaoRepository perfilPermissaoRepository, PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, UsuarioPerfilRepository usuarioPerfilRepository, UsuarioRepository usuarioRepository) {
        this.perfilRepository = perfilRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Perfil> listarTodos() {
        return (List<Perfil>) perfilRepository.findAll();
    }

    public ModeloCadastroPerfilPermissao listarEspecifico(Integer id) {
        Perfil perfil = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));

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

    public int novoPerfil(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
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

        if (Objects.nonNull(clonar)) {
            List<Usuario> usuariosVinculados = listarUsuariosVinculados(clonar).getUsuariosPerfil();

            for (Usuario usuario : usuariosVinculados) {
                UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
                usuarioPerfil.setPerfil(perfilNovo);
                usuarioPerfil.setDataHora(LocalDateTime.now());
                usuarioPerfil.setUsuario(usuario);
                usuarioPerfilRepository.save(usuarioPerfil);
            }
        }

        setClonar(null);
        return perfilNovo.getId();

    }

    public ModeloCadastroPerfilPermissao editar(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
        Perfil perfilMexido = modeloCadastroPerfilPermissao.getPerfil();

        perfilRepository.findById(perfilMexido.getId()).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));

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
        Perfil perfilBanco = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));

        List<Permissao> permissoesPerfil = perfilPermissaoRepository.findByPerfil(perfilBanco).stream()
                .map(PerfilPermissao::getPermissao)
                .toList();

        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = new ModeloCadastroPerfilPermissao();
        modeloCadastroPerfilPermissao.setPerfil(perfilBanco);
        modeloCadastroPerfilPermissao.setPermissoesPerfil(permissoesPerfil);

        return modeloCadastroPerfilPermissao;
    }

    public void deletar(int id) {
        Perfil perfilDelete = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));

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

    public List<Permissao> getPermissoes() {
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
//        TODO Copiar relação de usuários vinculados ao salvar
        ModeloCadastroPerfilPermissao perfilExistente = listarEspecifico(id);
        setClonar(perfilExistente.getPerfil().getId());
        perfilExistente.getPerfil().setNome(null);
        perfilExistente.getPerfil().setDescricao(null);
        perfilExistente.getPerfil().setId(null);
        perfilExistente.getPerfil().setExcluido(null);
        return perfilExistente;
    }

    public ModeloCadastroPerfilUsuario listarUsuariosVinculados(Integer id) {
        Perfil perfil = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));

        ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario = new ModeloCadastroPerfilUsuario();
        modeloCadastroPerfilUsuario.setPerfil(perfil);
        List<Usuario> permissoes = usuarioPerfilRepository.findByPerfil(perfil).stream()
                .map(UsuarioPerfil::getUsuario)
                .filter(Objects::nonNull)
                .filter(Usuario::getAtivo)
                .sorted(Comparator.comparing(Usuario::getNomeAmigavel))
                .toList();

        modeloCadastroPerfilUsuario.setUsuariosPerfil(permissoes);

        return modeloCadastroPerfilUsuario;
    }

    public List<Usuario> getUsuarios() {
        List<Usuario> usuariosBanco = (List<Usuario>) usuarioRepository.findAll();
        return usuariosBanco.stream()
                .filter(usuario -> Objects.nonNull(usuario.getNomeAmigavel()) && usuario.getAtivo())
                .sorted(Comparator.comparing(Usuario::getNomeAmigavel))
                .toList();
    }

    public ModeloCadastroPerfilUsuario vincularUsuariosEmLote(ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario) {
        Perfil perfilRecebido = modeloCadastroPerfilUsuario.getPerfil();
        List<Usuario> usuarios = modeloCadastroPerfilUsuario.getUsuariosPerfil();

        List<UsuarioPerfil> registrosExistentes = usuarioPerfilRepository.findByPerfil(perfilRecebido);
        usuarioPerfilRepository.deleteAll(registrosExistentes);

            for (Usuario usuario : usuarios) {
                UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
                usuarioPerfil.setPerfil(perfilRecebido);
                usuarioPerfil.setDataHora(LocalDateTime.now());
                usuarioPerfil.setUsuario(usuario);
                usuarioPerfilRepository.save(usuarioPerfil);
            }

        return modeloCadastroPerfilUsuario;
    }
}
