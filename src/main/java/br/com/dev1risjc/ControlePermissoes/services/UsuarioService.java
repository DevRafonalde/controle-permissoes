package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.AtributoJaUtilizadoException;
import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.UsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroUsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioPerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPerfilRepository usuarioPerfilRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioPerfilRepository usuarioPerfilRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.perfilRepository = perfilRepository;
    }
    //    Adiciona o meu validador personalizado
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.addValidators(new FuncionarioValidator());
//    }
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        List<Usuario> perfisAmigaveis = usuarios.stream()
                .filter(usuario -> usuario.getNomeAmigavel() != null && usuario.getAtivo())
                .toList();
        perfisAmigaveis.forEach(
                usuario -> usuario.setNomeAmigavel(
                        usuario.getNomeAmigavel()
                                .substring(0 , 1)
                                .toUpperCase()
                                .concat(usuario.getNomeAmigavel()
                                        .substring(1)
                                )
                )
        );

        return perfisAmigaveis;
    }

    public ModeloCadastroUsuarioPerfil listarEspecifico(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (Objects.isNull(usuario)) {
            throw new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados");
        }

        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = new ModeloCadastroUsuarioPerfil();
        modeloCadastroUsuarioPerfil.setUsuario(usuario);
        List<Perfil> perfis = usuarioPerfilRepository.findByUsuario(usuario).stream()
                .map(UsuarioPerfil::getPerfil)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(perfil -> perfil.getSistema().getNome()))
                .toList();

        modeloCadastroUsuarioPerfil.setPerfisUsuario(perfis);

        return modeloCadastroUsuarioPerfil;
    }

    public ModeloCadastroUsuarioPerfil novoUsuario(ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil) {
        Usuario usuarioExistente = usuarioRepository.findByNomeUser(modeloCadastroUsuarioPerfil.getUsuario().getNomeUser());

        if (Objects.nonNull(usuarioExistente)) {
            throw new AtributoJaUtilizadoException("Nome de Usuário já está sendo utilizado");
        }

        Usuario usuarioRecebido = modeloCadastroUsuarioPerfil.getUsuario();
        Usuario usuarioCadastrado = usuarioRepository.save(usuarioRecebido);

        List<Perfil> perfis = modeloCadastroUsuarioPerfil.getPerfisUsuario();
        for (Perfil perfil : perfis) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(usuarioCadastrado);
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setPerfil(perfil);
            usuarioPerfilRepository.save(usuarioPerfil);
        }

        List<Perfil> perfisVinculados = usuarioPerfilRepository.findByUsuario(usuarioCadastrado).stream()
                .map(UsuarioPerfil::getPerfil)
                .toList();

        ModeloCadastroUsuarioPerfil modeloRetorno = new ModeloCadastroUsuarioPerfil();
        modeloRetorno.setUsuario(usuarioCadastrado);
        modeloRetorno.setPerfisUsuario(perfisVinculados);

        return modeloRetorno;
    }

    public ModeloCadastroUsuarioPerfil editar(ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil) {
        Usuario usuarioMexido = modeloCadastroUsuarioPerfil.getUsuario();

        Usuario usuarioBanco = usuarioRepository.findById(usuarioMexido.getId()).orElse(null);

        if (Objects.isNull(usuarioBanco)) {
            throw new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados");
        }

        if (!usuarioMexido.getNomeUser().equalsIgnoreCase(usuarioBanco.getNomeUser())){
            Usuario usuarioExistente = usuarioRepository.findByNomeUser(usuarioMexido.getNomeUser());

            if (Objects.nonNull(usuarioExistente)) {
                throw new AtributoJaUtilizadoException("Nome de usuário já está sendo utilizado");
            }
        }

        Usuario usuarioSalvo = usuarioRepository.save(usuarioMexido);
        List<UsuarioPerfil> registrosExistentes = usuarioPerfilRepository.findByUsuario(modeloCadastroUsuarioPerfil.getUsuario());
        usuarioPerfilRepository.deleteAll(registrosExistentes);

        List<Perfil> perfis = modeloCadastroUsuarioPerfil.getPerfisUsuario();
        for (Perfil perfil : perfis) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(modeloCadastroUsuarioPerfil.getUsuario());
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setPerfil(perfil);
            usuarioPerfilRepository.save(usuarioPerfil);
        }

        List<Perfil> perfisVinculados = usuarioPerfilRepository.findByUsuario(usuarioSalvo).stream()
                .map(UsuarioPerfil::getPerfil)
                .toList();

        ModeloCadastroUsuarioPerfil modeloRetorno = new ModeloCadastroUsuarioPerfil();
        modeloRetorno.setUsuario(usuarioSalvo);
        modeloRetorno.setPerfisUsuario(perfisVinculados);

        return modeloRetorno;
    }

    public ModeloCadastroUsuarioPerfil preEditar(Integer id) {
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);

        if (Objects.isNull(usuarioBanco)) {
            throw new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados");
        }

        List<Perfil> perfisUsuario = usuarioPerfilRepository.findByUsuario(usuarioBanco).stream()
                .map(UsuarioPerfil::getPerfil)
                .toList();

        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = new ModeloCadastroUsuarioPerfil();
        modeloCadastroUsuarioPerfil.setUsuario(usuarioBanco);
        modeloCadastroUsuarioPerfil.setPerfisUsuario(perfisUsuario);

        return modeloCadastroUsuarioPerfil;
    }

    public void deletar(Integer id) {
        Usuario usuarioDelete = usuarioRepository.findById(id).orElse(null);

        if (Objects.isNull(usuarioDelete)) {
            throw new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados");
        }

        List<UsuarioPerfil> vinculosUsuario = usuarioPerfilRepository.findByUsuario(usuarioDelete);
        if (!vinculosUsuario.isEmpty()) {
            usuarioPerfilRepository.deleteAll(vinculosUsuario);
        }
        usuarioRepository.delete(usuarioDelete);
    }

    public List<Perfil> getPerfis(UsuarioPerfil usuarioPerfil) {
        if (Objects.nonNull(usuarioPerfil.getId())){
            Usuario usuario = usuarioRepository.findById(usuarioPerfil.getId()).orElse(null);
            List<Integer> idsPerfis = usuarioPerfilRepository.findByUsuario(usuario).stream().filter(u -> Objects.nonNull(u.getPerfil())).map(u -> u.getPerfil().getId()).toList();
            List<Perfil> perfis = (List<Perfil>) perfilRepository.findAllById(idsPerfis);
            return perfis.stream().sorted(Comparator.comparing(perfil -> perfil.getSistema().getNome())).toList();
        }
        List<Perfil> todosPerfis = (List<Perfil>) perfilRepository.findAll();
        todosPerfis.sort(Comparator.comparing(Perfil::getNome));
        return todosPerfis;
    }
}