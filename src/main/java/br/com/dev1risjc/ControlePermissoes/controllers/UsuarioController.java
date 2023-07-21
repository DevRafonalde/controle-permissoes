package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.UsuarioPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPermissao;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioPermissaoRepository;
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
    private final UsuarioPermissaoRepository usuarioPermissaoRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, UsuarioPermissaoRepository usuarioPermissaoRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioPermissaoRepository = usuarioPermissaoRepository;
        this.perfilRepository = perfilRepository;
    }
    //    Adiciona o meu validador personalizado
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.addValidators(new FuncionarioValidator());
//    }

//    @GetMapping("/cadastrar")
//    public String cadastrar(Usuario usuario) {
//        return "usuarios/cadastro";
//    }

    @GetMapping("/cadastrar")
    public String cadastrar(ModeloCadastroPermissao modeloCadastroPermissao) {
        return "usuarios/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao) {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        int paginaAtual = pagina.orElse(1);
        String ordem = direcao.orElse("normal");
        Paginacao<Usuario> paginaUsuarios = buscaPaginada(paginaAtual, ordem, usuarios);

        modelMap.addAttribute("paginaUsuarios", paginaUsuarios);
        return "usuarios/lista";
    }

    @GetMapping("/listar-especifico/{id}")
    public String preListarEspecifico(@PathVariable Integer id, ModelMap modelMap) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        UsuarioPermissao usuarioPermissao = new UsuarioPermissao();
        usuarioPermissao.setUsuario(usuario);
        modelMap.addAttribute("usuarioPermissao", usuarioPermissao);
        return "usuarios/lista-especifica";
    }

    @PostMapping("/novo-usuario")
    public String novoFuncionario(@Valid ModeloCadastroPermissao modeloCadastroPermissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }
        if (Objects.nonNull(modeloCadastroPermissao.getUsuario().getId())) {
            List<UsuarioPermissao> registrosExistentes = usuarioPermissaoRepository.findByUsuario(modeloCadastroPermissao.getUsuario());
            for (UsuarioPermissao usuarioPermissao : registrosExistentes) {
                usuarioPermissaoRepository.delete(usuarioPermissao);
            }
        }


        List<Perfil> perfis = modeloCadastroPermissao.getPerfisUsuario();
        for (Perfil perfil : perfis) {
            UsuarioPermissao usuarioPermissao = new UsuarioPermissao();
            usuarioPermissao.setUsuario(modeloCadastroPermissao.getUsuario());
            usuarioPermissao.getUsuario().setAtivo(true);
            usuarioPermissao.setNegacao(false);
            usuarioPermissao.setDataHora(LocalDateTime.now());
            usuarioPermissao.setExcluido(false);
            usuarioPermissao.setPerfil(perfil);
            System.out.println(perfil.getNome());
            usuarioPermissaoRepository.save(usuarioPermissao);
            System.out.println("Salvou");
        }

        attributes.addFlashAttribute("sucesso", "Funcionário inserido com sucesso");
        return "redirect:/usuarios/cadastrar";
    }

    @RequestMapping(value="/novo-usuario", params={"addPerfil"})
    public String addRowCadastro(final ModeloCadastroPermissao modeloCadastroPermissao, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPermissao.getPerfisUsuario())) {
            modeloCadastroPermissao.setPerfisUsuario(new ArrayList<>());
        }
        modeloCadastroPermissao.getPerfisUsuario().add(new Perfil());
        return "usuarios/cadastro";
    }

    @RequestMapping(value="/novo-usuario", params={"removePerfil"})
    public String removeRowCadastro(final ModeloCadastroPermissao modeloCadastroPermissao, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer perfilUsuarioId = Integer.valueOf(req.getParameter("removePerfil"));
        modeloCadastroPermissao.getPerfisUsuario().remove(perfilUsuarioId.intValue());
        return "usuarios/cadastro";
    }

    @RequestMapping(value="/editar", params={"addPerfil"})
    public String addRowEdicao(final ModeloCadastroPermissao modeloCadastroPermissao, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPermissao.getPerfisUsuario())) {
            modeloCadastroPermissao.setPerfisUsuario(new ArrayList<>());
        }
        modeloCadastroPermissao.getPerfisUsuario().add(new Perfil());
        return "usuarios/cadastro";
    }

    @RequestMapping(value="/editar", params={"removePerfil"})
    public String removeRowEdicao(final ModeloCadastroPermissao modeloCadastroPermissao, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer perfilUsuarioId = Integer.valueOf(req.getParameter("removePerfil"));
        modeloCadastroPermissao.getPerfisUsuario().remove(perfilUsuarioId.intValue());
        return "usuarios/cadastro";
    }

    @PostMapping("/editar")
    public String editar(@Valid ModeloCadastroPermissao modeloCadastroPermissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }
        Usuario usuarioMexido = modeloCadastroPermissao.getUsuario();
        if (Objects.nonNull(usuarioMexido.getId())) {
            usuarioMexido.setAtivo(true);
            usuarioRepository.save(usuarioMexido);
            List<UsuarioPermissao> registrosExistentes = usuarioPermissaoRepository.findByUsuario(modeloCadastroPermissao.getUsuario());
            for (UsuarioPermissao usuarioPermissao : registrosExistentes) {
                usuarioPermissaoRepository.delete(usuarioPermissao);
            }
        }


        List<Perfil> perfis = modeloCadastroPermissao.getPerfisUsuario();
        for (Perfil perfil : perfis) {
            UsuarioPermissao usuarioPermissao = new UsuarioPermissao();
            usuarioPermissao.setUsuario(modeloCadastroPermissao.getUsuario());
            usuarioPermissao.getUsuario().setAtivo(true);
            usuarioPermissao.setNegacao(false);
            usuarioPermissao.setDataHora(LocalDateTime.now());
            usuarioPermissao.setExcluido(false);
            usuarioPermissao.setPerfil(perfil);
            System.out.println(perfil.getNome());
            usuarioPermissaoRepository.save(usuarioPermissao);
            System.out.println("Salvou");
        }

        attributes.addFlashAttribute("sucesso", "Funcionário editado com sucesso");
        return "redirect:/usuarios/listar-especifico/"+ usuarioMexido.getId();
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable Integer id, ModelMap modelMap) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        List<Perfil> perfisUsuario = usuarioPermissaoRepository.findByUsuario(usuario).stream().map(UsuarioPermissao::getPerfil).toList();
        ModeloCadastroPermissao modeloCadastroPermissao = new ModeloCadastroPermissao();
        modeloCadastroPermissao.setUsuario(usuario);
        modeloCadastroPermissao.setPerfisUsuario(perfisUsuario);
        modelMap.addAttribute("modeloCadastroPermissao", modeloCadastroPermissao);
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
        Paginacao<Usuario> paginaUsuarios = buscaPaginada(paginaAtual, ordem, listaExibicao);

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
        usuarioRepository.deleteById(id);
        modelMap.addAttribute("sucesso", "Funcionario deletado com sucesso.");
        return "perfis/lista";
    }

    public Paginacao<Usuario> buscaPaginada(int pagina, String direcao, List<Usuario> perfis) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        List<Usuario> perfisAmigaveis = new java.util.ArrayList<>(perfis.stream().filter(usuario -> usuario.getNomeAmigavel() != null && usuario.getAtivo()).toList());
        if (direcao.equalsIgnoreCase("normal")) {
            perfisAmigaveis.sort(Comparator.comparing(Usuario::getNomeAmigavel));
        } else {
            perfisAmigaveis.sort(Collections.reverseOrder(Comparator.comparing(Usuario::getNomeAmigavel)));
        }
        List<Usuario> paginaUsuarios = perfisAmigaveis.stream().filter(usuario -> perfisAmigaveis.indexOf(usuario) >= inicio).limit(tamanho).toList();

        int totalPaginas = (perfisAmigaveis.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaUsuarios);
    }

    @ModelAttribute("perfis")
    public List<Perfil> getPerfis(UsuarioPermissao usuarioPermissao) {
        if (Objects.nonNull(usuarioPermissao.getId())){
            Usuario usuario = usuarioRepository.findById(usuarioPermissao.getId()).orElse(null);
            List<Integer> idsPerfis = usuarioPermissaoRepository.findByUsuario(usuario).stream().filter(u -> Objects.nonNull(u.getPerfil())).map(u -> u.getPerfil().getId()).toList();
            List<Perfil> perfis = (List<Perfil>) perfilRepository.findAllById(idsPerfis);
            for (Perfil perfil : perfis) {
                if (Objects.isNull(perfil.getSistema())){
                    Sistema sistemaVazio = new Sistema();
                    sistemaVazio.setNome("");
                    perfil.setSistema(sistemaVazio);
                }
            }
            return perfis.stream().sorted(Comparator.comparing(perfil -> perfil.getSistema().getNome())).toList();
        }
        return (List<Perfil>) perfilRepository.findAll();
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
