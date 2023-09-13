package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PerfilDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PermissaoDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.SistemaDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.UsuarioDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.*;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuarioId;
import br.com.dev1risjc.ControlePermissoes.models.repositories.*;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private ModelMapper mapper = new ModelMapper();

    public PerfilService(PerfilRepository perfilRepository, PerfilPermissaoRepository perfilPermissaoRepository, PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, UsuarioPerfilRepository usuarioPerfilRepository, UsuarioRepository usuarioRepository) {
        this.perfilRepository = perfilRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<PerfilDTO> listarTodos() {
        List<Perfil> perfisBanco = (List<Perfil>) perfilRepository.findAll();
        return perfisBanco.stream()
                .map(perfil -> mapper.map(perfil, PerfilDTO.class))
                .toList();
    }

    public ModeloCadastroPerfilUsuario listarUsuariosVinculados(Integer id) {
        Perfil perfil = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));
        PerfilDTO perfilDTO = mapper.map(perfil, PerfilDTO.class);

        ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario = new ModeloCadastroPerfilUsuario();
        modeloCadastroPerfilUsuario.setPerfil(perfilDTO);
        List<UsuarioDTO> usuarios = usuarioPerfilRepository.findByPerfil(perfil).stream()
                .map(UsuarioPerfil::getUsuario)
                .filter(Objects::nonNull)
                .filter(Usuario::getAtivo)
                .sorted(Comparator.comparing(Usuario::getNomeAmigavel))
                .map(usuario -> mapper.map(usuario, UsuarioDTO.class))
                .toList();

        if (usuarios.isEmpty()) {
            modeloCadastroPerfilUsuario.setUsuariosPerfil(new ArrayList<>());
        } else {
            modeloCadastroPerfilUsuario.setUsuariosPerfil(usuarios);
        }

        return modeloCadastroPerfilUsuario;
    }

    public ModeloCadastroPerfilPermissao listarEspecifico(Integer id) {
        Perfil perfil = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));
        PerfilDTO perfilDTO = mapper.map(perfil, PerfilDTO.class);

        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = new ModeloCadastroPerfilPermissao();
        modeloCadastroPerfilPermissao.setPerfil(perfilDTO);
        List<PermissaoDTO> permissoes = perfilPermissaoRepository.findByPerfil(perfil).stream()
                .map(PerfilPermissao::getPermissao)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(permissao -> permissao.getSistema().getNome()))
                .map(permissao -> mapper.map(permissao, PermissaoDTO.class))
                .toList();

        modeloCadastroPerfilPermissao.setPermissoesPerfil(permissoes);

        return modeloCadastroPerfilPermissao;
    }

    public ModeloCadastroPerfilPermissao clonar(Integer id) {
        ModeloCadastroPerfilPermissao perfilExistente = listarEspecifico(id);
        setClonar(perfilExistente.getPerfil().getId());
        perfilExistente.getPerfil().setNome(null);
        perfilExistente.getPerfil().setDescricao(null);
        perfilExistente.getPerfil().setId(null);
        perfilExistente.getPerfil().setExcluido(null);
        return perfilExistente;
    }

    public ModeloCadastroPerfilPermissao preEditar(int id) {
        Perfil perfilBanco = perfilRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));
        PerfilDTO perfilDTO = mapper.map(perfilBanco, PerfilDTO.class);

        List<PermissaoDTO> permissoesPerfil = perfilPermissaoRepository.findByPerfil(perfilBanco).stream()
                .map(PerfilPermissao::getPermissao)
                .map(permissao -> mapper.map(permissao, PermissaoDTO.class))
                .toList();

        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = new ModeloCadastroPerfilPermissao();
        modeloCadastroPerfilPermissao.setPerfil(perfilDTO);
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

    public List<UsuarioDTO> getUsuarios() {
        List<Usuario> usuariosBanco = (List<Usuario>) usuarioRepository.findAll();
        return usuariosBanco.stream()
                .filter(usuario -> Objects.nonNull(usuario.getNomeAmigavel()) && usuario.getAtivo())
                .sorted(Comparator.comparing(usuario -> usuario.getNomeUser().toLowerCase()))
                .map(usuario -> mapper.map(usuario, UsuarioDTO.class))
                .toList();
    }

    public int novoPerfil(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
        Perfil perfilRecebido = mapper.map(modeloCadastroPerfilPermissao.getPerfil(), Perfil.class);
        Perfil perfilNovo = perfilRepository.save(perfilRecebido);

        List<Permissao> permissoes = modeloCadastroPerfilPermissao.getPermissoesPerfil().stream()
                .map(permissaoDTO -> mapper.map(permissaoDTO, Permissao.class))
                .toList();

        for (Permissao permissao : permissoes) {
            PerfilPermissao perfilPermissao = new PerfilPermissao();
            perfilPermissao.setPerfil(perfilNovo);
            perfilPermissao.setDataHora(LocalDateTime.now());
            perfilPermissao.setPermissao(permissao);
            perfilPermissaoRepository.save(perfilPermissao);
        }

        if (Objects.nonNull(clonar)) {
            List<Usuario> usuariosVinculados = listarUsuariosVinculados(clonar).getUsuariosPerfil()
                    .stream()
                    .map(usuarioDTO -> mapper.map(usuarioDTO, Usuario.class))
                    .toList();

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
        Perfil perfilMexido = mapper.map(modeloCadastroPerfilPermissao.getPerfil(), Perfil.class);

        perfilRepository.findById(perfilMexido.getId()).orElseThrow(() -> new ElementoNaoEncontradoException("Perfil não encontrado no banco de dados"));

        Perfil perfilSalvo = perfilRepository.save(perfilMexido);
        List<PerfilPermissao> registrosExistentes = perfilPermissaoRepository.findByPerfil(perfilMexido);
        perfilPermissaoRepository.deleteAll(registrosExistentes);

        List<Permissao> permissoes = modeloCadastroPerfilPermissao.getPermissoesPerfil()
                .stream()
                .map(permissaoDTO -> mapper.map(permissaoDTO, Permissao.class))
                .toList();

        for (Permissao permissao : permissoes) {
            PerfilPermissao perfilPermissao = new PerfilPermissao();
            perfilPermissao.setPerfil(perfilMexido);
            perfilPermissao.setDataHora(LocalDateTime.now());
            perfilPermissao.setPermissao(permissao);
            perfilPermissaoRepository.save(perfilPermissao);
        }

        List<PermissaoDTO> permissoesVinculadas = perfilPermissaoRepository.findByPerfil(perfilSalvo).stream()
                .map(PerfilPermissao::getPermissao)
                .map(permissao -> mapper.map(permissao, PermissaoDTO.class))
                .toList();

        ModeloCadastroPerfilPermissao modeloRetorno = new ModeloCadastroPerfilPermissao();
        modeloRetorno.setPerfil(mapper.map(perfilSalvo, PerfilDTO.class));
        modeloRetorno.setPermissoesPerfil(permissoesVinculadas);

        return modeloRetorno;
    }

    public ModeloCadastroPerfilUsuarioId vincularUsuariosEmLote(ModeloCadastroPerfilUsuarioId modeloCadastroPerfilUsuarioId) {
        Perfil perfilRecebido = mapper.map(modeloCadastroPerfilUsuarioId.getPerfil(), Perfil.class);
        List<Integer> usuariosId = modeloCadastroPerfilUsuarioId.getUsuariosPerfilId();
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAllById(usuariosId);

        List<UsuarioPerfil> registrosExistentes = usuarioPerfilRepository.findByPerfil(perfilRecebido);
        usuarioPerfilRepository.deleteAll(registrosExistentes);

        for (Usuario usuario : usuarios) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setPerfil(perfilRecebido);
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setUsuario(usuario);
            usuarioPerfilRepository.save(usuarioPerfil);
        }

        return modeloCadastroPerfilUsuarioId;
    }

    public List<PermissaoDTO> getPermissoes() {
        List<Permissao> todasPermissoes = (List<Permissao>) permissaoRepository.findAll();

        return todasPermissoes.stream()
                .sorted(Comparator.comparing(Permissao::getNome))
                .map(permissao -> mapper.map(permissao, PermissaoDTO.class))
                .toList();
    }

    public List<SistemaDTO> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();
        sistemas.sort(Comparator.comparing(Sistema::getNome));
        return sistemas.stream()
                .sorted(Comparator.comparing(Sistema::getNome))
                .map(sistema -> mapper.map(sistema, SistemaDTO.class))
                .toList();
    }
}
