package br.com.dev1risjc.ControlePermissoes.services;

import br.com.dev1risjc.ControlePermissoes.exceptions.AtributoJaUtilizadoException;
import br.com.dev1risjc.ControlePermissoes.exceptions.ElementoNaoEncontradoException;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PerfilDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.UsuarioDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.UsuarioPerfilDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.UsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroUsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioPerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPerfilRepository usuarioPerfilRepository;
    private final PerfilRepository perfilRepository;
    private ModelMapper mapper = new ModelMapper();

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioPerfilRepository usuarioPerfilRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.perfilRepository = perfilRepository;
    }

    public List<UsuarioDTO> listarTodos() {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        List<UsuarioDTO> perfisAmigaveis = usuarios.stream()
                .filter(usuario -> usuario.getNomeAmigavel() != null && usuario.getAtivo())
                .map(usuario -> mapper.map(usuario, UsuarioDTO.class))
                .toList();
        perfisAmigaveis.forEach(
                usuario -> usuario.setNomeAmigavel(
                        usuario.getNomeAmigavel()
                                .substring(0, 1)
                                .toUpperCase()
                                .concat(usuario.getNomeAmigavel()
                                        .substring(1)
                                )
                )
        );

        return perfisAmigaveis;
    }

    public ModeloCadastroUsuarioPerfil listarEspecifico(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados"));

        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = new ModeloCadastroUsuarioPerfil();
        modeloCadastroUsuarioPerfil.setUsuario(mapper.map(usuario, UsuarioDTO.class));
        List<PerfilDTO> perfis = usuarioPerfilRepository.findByUsuario(usuario).stream()
                .map(UsuarioPerfil::getPerfil)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(perfil -> perfil.getSistema().getNome()))
                .map(perfil -> mapper.map(perfil, PerfilDTO.class))
                .toList();

        if (perfis.isEmpty()) {
            modeloCadastroUsuarioPerfil.setPerfisUsuario(new ArrayList<>());
        } else {
            modeloCadastroUsuarioPerfil.setPerfisUsuario(perfis);
        }

        return modeloCadastroUsuarioPerfil;
    }

    public ModeloCadastroUsuarioPerfil clonar(Integer id) {
        ModeloCadastroUsuarioPerfil usuarioExistente = listarEspecifico(id);
        usuarioExistente.setUsuario(null);
        return usuarioExistente;
    }

    public ModeloCadastroUsuarioPerfil preEditar(Integer id) {
        Usuario usuarioBanco = usuarioRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados"));

        List<PerfilDTO> perfisUsuario = usuarioPerfilRepository.findByUsuario(usuarioBanco).stream()
                .map(UsuarioPerfil::getPerfil)
                .map(perfil -> mapper.map(perfil, PerfilDTO.class))
                .toList();

        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = new ModeloCadastroUsuarioPerfil();
        modeloCadastroUsuarioPerfil.setUsuario(mapper.map(usuarioBanco, UsuarioDTO.class));
        modeloCadastroUsuarioPerfil.setPerfisUsuario(perfisUsuario);

        return modeloCadastroUsuarioPerfil;
    }

    public void deletar(Integer id) {
        Usuario usuarioDelete = usuarioRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados"));

        List<UsuarioPerfil> vinculosUsuario = usuarioPerfilRepository.findByUsuario(usuarioDelete);
        if (!vinculosUsuario.isEmpty()) {
            usuarioPerfilRepository.deleteAll(vinculosUsuario);
        }
        usuarioRepository.delete(usuarioDelete);
    }

    public int novoUsuario(ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil) {
        Usuario usuarioExistente = usuarioRepository.findByNomeUser(modeloCadastroUsuarioPerfil.getUsuario().getNomeUser());

        if (Objects.nonNull(usuarioExistente)) {
            throw new AtributoJaUtilizadoException("Nome de Usuário já está sendo utilizado");
        }

        Usuario usuarioRecebido = mapper.map(modeloCadastroUsuarioPerfil.getUsuario(), Usuario.class);

//        Possível implementação para gravação de um hash da senha na tbl_Usuario ao invés da senha em si por questões de segurança e LGPD
//        Só não implementei pq o campo é pequeno demais para aceitar o hash
//        MessageDigest criptografia;
//        byte[] messageDigest;
//        try {
//            System.out.println(usuarioRecebido.getSenhaUser());
//            criptografia = MessageDigest.getInstance("SHA-256");
//            messageDigest = criptografia.digest(usuarioRecebido.getSenhaUser().getBytes(StandardCharsets.UTF_8));
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : messageDigest) {
//            hexString.append(String.format("%02X", 0xFF & b));
//        }
//        String senhahex = hexString.toString();
//        System.out.println(senhahex);
//        usuarioRecebido.setHashSenhaUser(senhahex);

        Usuario usuarioCadastrado = usuarioRepository.save(usuarioRecebido);

        List<Perfil> perfis = modeloCadastroUsuarioPerfil.getPerfisUsuario()
                .stream()
                .map(perfilDTO -> mapper.map(perfilDTO, Perfil.class))
                .toList();
        for (Perfil perfil : perfis) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(usuarioCadastrado);
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setPerfil(perfil);
            usuarioPerfilRepository.save(usuarioPerfil);
        }

        return usuarioCadastrado.getId();
    }

    public UsuarioDTO editar(ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil) {
        Usuario usuarioMexido = mapper.map(modeloCadastroUsuarioPerfil.getUsuario(), Usuario.class);

        Usuario usuarioBanco = usuarioRepository.findById(usuarioMexido.getId()).orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados"));

        if (!usuarioMexido.getNomeUser().equalsIgnoreCase(usuarioBanco.getNomeUser())){
            Usuario usuarioExistente = usuarioRepository.findByNomeUser(usuarioMexido.getNomeUser());

            if (Objects.nonNull(usuarioExistente)) {
                throw new AtributoJaUtilizadoException("Nome de usuário já está sendo utilizado");
            }
        }

        usuarioMexido.setSenhaUser(usuarioBanco.getSenhaUser());

        Usuario usuarioSalvo = usuarioRepository.save(usuarioMexido);
        List<UsuarioPerfil> registrosExistentes = usuarioPerfilRepository.findByUsuario(usuarioMexido);
        usuarioPerfilRepository.deleteAll(registrosExistentes);

        List<Perfil> perfis = modeloCadastroUsuarioPerfil.getPerfisUsuario()
                .stream()
                .map(perfilDTO -> mapper.map(perfilDTO, Perfil.class))
                .toList();

        for (Perfil perfil : perfis) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(usuarioMexido);
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setPerfil(perfil);
            usuarioPerfilRepository.save(usuarioPerfil);
        }


        return mapper.map(usuarioSalvo, UsuarioDTO.class);
    }

    public List<PerfilDTO> getPerfis() {
//        if (Objects.nonNull(usuarioPerfil.getId())){
//            Usuario usuario = usuarioRepository.findById(usuarioPerfil.getId()).orElse(null);
//            List<Integer> idsPerfis = usuarioPerfilRepository.findByUsuario(usuario).stream().filter(u -> Objects.nonNull(u.getPerfil())).map(u -> u.getPerfil().getId()).toList();
//            List<Perfil> perfis = (List<Perfil>) perfilRepository.findAllById(idsPerfis);
//            return perfis.stream()
//                    .sorted(Comparator.comparing(perfil -> perfil.getSistema().getNome()))
//                    .map(perfil -> mapper.map(perfil, PerfilDTO.class))
//                    .toList();
//        }
        List<Perfil> todosPerfis = (List<Perfil>) perfilRepository.findAll();
        return todosPerfis.stream()
                .sorted(Comparator.comparing(Perfil::getNome))
                .map(perfil -> mapper.map(perfil, PerfilDTO.class))
                .toList();
    }

    public List<Integer> getPerfisVinculadosId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ElementoNaoEncontradoException("Usuário não encontrado no banco de dados"));
        UsuarioDTO usuarioDTO = mapper.map(usuario, UsuarioDTO.class);

        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = listarEspecifico(id);


        return modeloCadastroUsuarioPerfil.getPerfisUsuario()
                .stream()
                .map(PerfilDTO::getId)
                .toList();
    }
}
