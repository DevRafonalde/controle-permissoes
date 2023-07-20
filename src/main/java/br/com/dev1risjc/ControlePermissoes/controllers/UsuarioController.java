package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.entities.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.UsuarioPermissao;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioPermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String cadastrar(UsuarioPermissao usuarioPermissao) {
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
    public String preListarEspecifico(@PathVariable Integer id, ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        modelMap.addAttribute("usuario", usuario);
        return "usuarios/lista-especifica";
    }

    @PostMapping("/novo-usuario")
    public String novoFuncionario(@Valid UsuarioPermissao usuarioPermissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }

        System.out.println(usuarioPermissao.getPerfil());

        usuarioPermissao.getUsuario().setAtivo(true);
        usuarioPermissao.setNegacao(false);
        usuarioPermissao.setDataHora(LocalDateTime.now());
        usuarioPermissao.setExcluido(false);
        usuarioPermissaoRepository.save(usuarioPermissao);
        attributes.addFlashAttribute("sucesso", "Funcionário inserido com sucesso");
        return "redirect:/usuarios/cadastrar";
    }

    @PostMapping("/editar")
    public String editar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }
        usuarioRepository.save(usuario);
        attributes.addFlashAttribute("sucesso", "Funcionario editado com sucesso.");
        return "redirect:/usuarios/cadastrar";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable Integer id, ModelMap modelMap) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        modelMap.addAttribute("usuario", usuario);
        return "usuarios/edicao";
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
    public List<Perfil> getPerfis(Usuario usuario) {
        if (Objects.nonNull(usuario.getId())){
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
