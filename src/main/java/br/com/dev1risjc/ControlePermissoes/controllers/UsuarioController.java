package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.UsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroUsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioPerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPerfilRepository usuarioPerfilRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioPerfilRepository usuarioPerfilRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
        this.perfilRepository = perfilRepository;
    }
    //    Adiciona o meu validador personalizado
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.addValidators(new FuncionarioValidator());
//    }

    @GetMapping("/cadastrar")
    public String cadastrar(ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil) {
        return "usuarios/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao, @RequestParam("atributo") Optional<String> atributo) {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        int paginaAtual = pagina.orElse(1);
        String ordem = direcao.orElse("normal");
        String baseOrdenacao = atributo.orElse("nome");

        Paginacao<Usuario> paginaUsuarios;

        if (baseOrdenacao.equalsIgnoreCase("id")) {
            paginaUsuarios = buscaPaginadaId(paginaAtual, ordem, usuarios);
        } else {
            paginaUsuarios = buscaPaginadaNome(paginaAtual, ordem, usuarios);
        }


        modelMap.addAttribute("paginaUsuarios", paginaUsuarios);
        return "usuarios/lista";
    }

    @GetMapping("/listar-especifico/{id}")
    public String preListarEspecifico(@PathVariable Integer id, ModelMap modelMap) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
        usuarioPerfil.setUsuario(usuario);
        modelMap.addAttribute("usuarioPerfil", usuarioPerfil);
        return "usuarios/lista-especifica";
    }

    @PostMapping("/novo-usuario")
    public String novoFuncionario(@Valid ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }
        if (Objects.nonNull(modeloCadastroUsuarioPerfil.getUsuario().getId())) {
            List<UsuarioPerfil> registrosExistentes = usuarioPerfilRepository.findByUsuario(modeloCadastroUsuarioPerfil.getUsuario());
            for (UsuarioPerfil usuarioPerfil : registrosExistentes) {
                usuarioPerfilRepository.delete(usuarioPerfil);
            }
        }

        List<Perfil> perfis = modeloCadastroUsuarioPerfil.getPerfisUsuario();
        for (Perfil perfil : perfis) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(modeloCadastroUsuarioPerfil.getUsuario());
            usuarioPerfil.getUsuario().setAtivo(true);
            usuarioPerfil.setNegacao(false);
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setExcluido(false);
            usuarioPerfil.setPerfil(perfil);
            System.out.println(perfil.getNome());
            usuarioPerfilRepository.save(usuarioPerfil);
            System.out.println("Salvou");
        }

        attributes.addFlashAttribute("sucesso", "Funcionário inserido com sucesso");
        return "redirect:/usuarios/cadastrar";
    }

    @RequestMapping(value="/novo-usuario", params={"addPerfil"})
    public String addRowCadastro(final ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroUsuarioPerfil.getPerfisUsuario())) {
            modeloCadastroUsuarioPerfil.setPerfisUsuario(new ArrayList<>());
        }
        modeloCadastroUsuarioPerfil.getPerfisUsuario().add(new Perfil());
        return "usuarios/cadastro";
    }

    @RequestMapping(value="/novo-usuario", params={"removePerfil"})
    public String removeRowCadastro(final ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer perfilUsuarioId = Integer.valueOf(req.getParameter("removePerfil"));
        modeloCadastroUsuarioPerfil.getPerfisUsuario().remove(perfilUsuarioId.intValue());
        return "usuarios/cadastro";
    }

    @RequestMapping(value="/editar", params={"addPerfil"})
    public String addRowEdicao(final ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroUsuarioPerfil.getPerfisUsuario())) {
            modeloCadastroUsuarioPerfil.setPerfisUsuario(new ArrayList<>());
        }
        modeloCadastroUsuarioPerfil.getPerfisUsuario().add(new Perfil());
        return "usuarios/cadastro";
    }

    @RequestMapping(value="/editar", params={"removePerfil"})
    public String removeRowEdicao(final ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer perfilUsuarioId = Integer.valueOf(req.getParameter("removePerfil"));
        modeloCadastroUsuarioPerfil.getPerfisUsuario().remove(perfilUsuarioId.intValue());
        return "usuarios/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }
        Usuario usuarioMexido = modeloCadastroUsuarioPerfil.getUsuario();
        usuarioMexido.setAtivo(true);
        usuarioRepository.save(usuarioMexido);
        List<UsuarioPerfil> registrosExistentes = usuarioPerfilRepository.findByUsuario(modeloCadastroUsuarioPerfil.getUsuario());
        for (UsuarioPerfil usuarioPerfil : registrosExistentes) {
            usuarioPerfilRepository.delete(usuarioPerfil);
        }

        List<Perfil> perfis = modeloCadastroUsuarioPerfil.getPerfisUsuario();
        for (Perfil perfil : perfis) {
            UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
            usuarioPerfil.setUsuario(modeloCadastroUsuarioPerfil.getUsuario());
            usuarioPerfil.getUsuario().setAtivo(true);
            usuarioPerfil.setNegacao(false);
            usuarioPerfil.setDataHora(LocalDateTime.now());
            usuarioPerfil.setExcluido(false);
            usuarioPerfil.setPerfil(perfil);
            usuarioPerfilRepository.save(usuarioPerfil);
        }

        attributes.addFlashAttribute("sucesso", "Funcionário editado com sucesso");
        return "redirect:/usuarios/listar-especifico/"+ usuarioMexido.getId();
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable Integer id, ModelMap modelMap) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        List<Perfil> perfisUsuario = usuarioPerfilRepository.findByUsuario(usuario).stream().map(UsuarioPerfil::getPerfil).toList();
        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = new ModeloCadastroUsuarioPerfil();
        modeloCadastroUsuarioPerfil.setUsuario(usuario);
        modeloCadastroUsuarioPerfil.setPerfisUsuario(perfisUsuario);
        modelMap.addAttribute("modeloCadastroUsuarioPerfil", modeloCadastroUsuarioPerfil);
        return "usuarios/cadastro";
    }

    @Transactional(readOnly = true)
    @GetMapping("/buscarPorId/{id}")
    public Optional<Usuario> buscarPorId(@PathVariable Integer id) {
        return usuarioRepository.findById(id);
    }

    @GetMapping("/buscarPorNome")
    public String buscarPorNome(@RequestParam("nome") String nome, ModelMap modelMap) {
        int paginaAtual = 1;
        String ordem = "normal";
        List<Usuario> listaExibicao;

        if (Objects.isNull(nome) || nome.equals("")) {
            listaExibicao = (List<Usuario>) usuarioRepository.findAll();
        } else {
            listaExibicao = usuarioRepository.findByNomeAmigavel(nome);
        }

        Paginacao<Usuario> paginaUsuarios = buscaPaginadaNome(paginaAtual, ordem, listaExibicao);

        modelMap.addAttribute("paginaUsuarios", paginaUsuarios);
        return "usuarios/lista";
    }

    @Transactional(readOnly = true)
    @GetMapping("/buscarTodos")
    public Iterable<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, ModelMap modelMap) {
        Usuario usuarioDelete = usuarioRepository.findById(id).orElse(null);
        List<UsuarioPerfil> vinculosUsuario = usuarioPerfilRepository.findByUsuario(usuarioDelete);
        if (vinculosUsuario.size() >= 1) {
            usuarioPerfilRepository.deleteAll(vinculosUsuario);
        }
        usuarioRepository.delete(usuarioDelete);
        modelMap.addAttribute("sucesso", "Usuario deletado com sucesso.");

        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        Paginacao<Usuario> paginaUsuarios = buscaPaginadaNome(1, "normal", usuarios);

        modelMap.addAttribute("paginaUsuarios", paginaUsuarios);

        return "usuarios/lista";
    }

    public Paginacao<Usuario> buscaPaginadaNome(int pagina, String direcao, List<Usuario> usuarios) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        List<Usuario> perfisAmigaveis = new java.util.ArrayList<>(usuarios.stream().filter(usuario -> usuario.getNomeAmigavel() != null && usuario.getAtivo()).toList());
        perfisAmigaveis.forEach(usuario -> usuario.setNomeAmigavel(usuario.getNomeAmigavel().substring(0 , 1).toUpperCase().concat(usuario.getNomeAmigavel().substring(1))));
        if (direcao.equalsIgnoreCase("normal")) {
            perfisAmigaveis.sort(Comparator.comparing(usuario -> usuario.getNomeUser().toLowerCase()));
        } else {
            perfisAmigaveis.sort(Collections.reverseOrder(Comparator.comparing(usuario -> usuario.getNomeUser().toLowerCase())));
        }
        List<Usuario> paginaUsuarios = perfisAmigaveis.stream().filter(usuario -> perfisAmigaveis.indexOf(usuario) >= inicio).limit(tamanho).toList();

        int totalPaginas = (perfisAmigaveis.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaUsuarios);
    }

    public Paginacao<Usuario> buscaPaginadaId(int pagina, String direcao, List<Usuario> usuarios) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        List<Usuario> perfisAmigaveis = new java.util.ArrayList<>(usuarios.stream().filter(usuario -> usuario.getNomeAmigavel() != null && usuario.getAtivo()).toList());
        perfisAmigaveis.forEach(usuario -> usuario.setNomeAmigavel(usuario.getNomeAmigavel().substring(0 , 1).toUpperCase().concat(usuario.getNomeAmigavel().substring(1))));
        if (direcao.equalsIgnoreCase("normal")) {
            perfisAmigaveis.sort(Comparator.comparing(Usuario::getId));
        } else {
            perfisAmigaveis.sort(Collections.reverseOrder(Comparator.comparing(Usuario::getId)));
        }
        List<Usuario> paginaUsuarios = perfisAmigaveis.stream().filter(usuario -> perfisAmigaveis.indexOf(usuario) >= inicio).limit(tamanho).toList();

        int totalPaginas = (perfisAmigaveis.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaUsuarios);
    }

    @ModelAttribute("perfis")
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

//    public List<Usuario> buscarPorDatas(LocalDate dataEntrada, LocalDate dataSaida) {
//        if (dataEntrada != null && dataSaida != null) {
//            return usuarioRepository.findByDataEntradaDataSaida(dataEntrada, dataSaida);
//        } else if (dataEntrada != null) {
//            return usuarioRepository.findByDataEntrada(dataEntrada);
//        } else if (dataSaida != null) {
//            return usuarioRepository.findByDataSaida(dataSaida);
//        } else {
//            return new ArrayList<>();
//        }
//    }
}
