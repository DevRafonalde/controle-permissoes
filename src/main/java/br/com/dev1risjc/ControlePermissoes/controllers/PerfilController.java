package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.*;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.repositories.*;
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
@RequestMapping("/perfis")
public class PerfilController {

    private PerfilRepository perfilRepository;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private UsuarioPerfilRepository usuarioPerfilRepository;

    public PerfilController(PerfilRepository perfilRepository, PerfilPermissaoRepository perfilPermissaoRepository, PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, UsuarioPerfilRepository usuarioPerfilRepository) {
        this.perfilRepository = perfilRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.usuarioPerfilRepository = usuarioPerfilRepository;
    }

    @GetMapping("/cadastrar")
    public String cadastrar(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
        return "perfis/cadastro";
    }

//    @ModelAttribute("perfisPermissao")
//    public List<PerfilPermissao> listaPerfisPermissoes() {
//        return (List<PerfilPermissao>) perfilPermissaoRepository.findAll();
//    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao) {
        List<Perfil> perfis = (List<Perfil>) perfilRepository.findAll();
        int paginaAtual = pagina.orElse(1);
        String ordem = direcao.orElse("normal");
        Paginacao<Perfil> paginaPerfis = buscaPaginada(paginaAtual, ordem, perfis);

        modelMap.addAttribute("paginaPerfis", paginaPerfis);
        return "perfis/lista";
    }

    @GetMapping("/listar-especifico/{id}")
    public String listarEspecifico(@PathVariable Integer id, ModelMap modelMap) {
        Perfil perfil = perfilRepository.findById(id).orElse(null);
        PerfilPermissao perfilPermissao = new PerfilPermissao();
        perfilPermissao.setPerfil(perfil);
        modelMap.addAttribute("perfilPermissao", perfilPermissao);
        return "perfis/lista-especifica";
    }

    @PostMapping("/novo-perfil")
    public String novoPerfil(@Valid ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "perfis/cadastro";
        }

        List<Permissao> permissoes = modeloCadastroPerfilPermissao.getPermissoesPerfil();
        for (Permissao permissao : permissoes) {
            PerfilPermissao perfilPermissao = new PerfilPermissao();
            perfilPermissao.setPerfil(modeloCadastroPerfilPermissao.getPerfil());
            perfilPermissao.getPerfil().setExcluido(false);
            perfilPermissao.setDataHora(LocalDateTime.now());
            perfilPermissao.setExcluido(false);
            perfilPermissao.setPermissao(permissao);
            System.out.println(permissao.getNome());
            perfilPermissaoRepository.save(perfilPermissao);
            System.out.println("Salvou");
        }

        attributes.addFlashAttribute("sucesso", "Perfil criado com sucesso");
        return "redirect:/perfis/listar-especifico/"+ modeloCadastroPerfilPermissao.getPerfil().getId();
    }

    @PostMapping("/editar")
    public String editar(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "perfis/cadastro";
        }
        Perfil perfilMexido = modeloCadastroPerfilPermissao.getPerfil();
        perfilMexido.setExcluido(false);
        perfilRepository.save(perfilMexido);
        List<PerfilPermissao> registrosExistentes = perfilPermissaoRepository.findByPerfil(modeloCadastroPerfilPermissao.getPerfil());
        for (PerfilPermissao perfilPermissao : registrosExistentes) {
            perfilPermissaoRepository.delete(perfilPermissao);
        }

        List<Permissao> permissoes = modeloCadastroPerfilPermissao.getPermissoesPerfil();
        for (Permissao permissao : permissoes) {
            PerfilPermissao perfilPermissao = new PerfilPermissao();
            perfilPermissao.setPerfil(modeloCadastroPerfilPermissao.getPerfil());
            perfilPermissao.getPerfil().setExcluido(false);
            perfilPermissao.setDataHora(LocalDateTime.now());
            perfilPermissao.setExcluido(false);
            perfilPermissao.setPermissao(permissao);
            perfilPermissaoRepository.save(perfilPermissao);
        }

        attributes.addFlashAttribute("sucesso", "Perfil salvo com sucesso");
        return "redirect:/perfis/listar-especifico/"+ perfilMexido.getId();
    }

    @RequestMapping(value="/novo-perfil", params={"addPermissao"})
    public String addRowCadastro(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPerfilPermissao.getPermissoesPerfil())) {
            modeloCadastroPerfilPermissao.setPermissoesPerfil(new ArrayList<>());
        }
        modeloCadastroPerfilPermissao.getPermissoesPerfil().add(new Permissao());
        return "perfis/cadastro";
    }

    @RequestMapping(value="/novo-perfil", params={"removePermissao"})
    public String removeRowCadastro(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer permissaoPerfilId = Integer.valueOf(req.getParameter("removePermissao"));
        modeloCadastroPerfilPermissao.getPermissoesPerfil().remove(permissaoPerfilId.intValue());
        return "perfis/cadastro";
    }

    @RequestMapping(value="/editar", params={"addPermissao"})
    public String addRowEdicao(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPerfilPermissao.getPermissoesPerfil())) {
            modeloCadastroPerfilPermissao.setPermissoesPerfil(new ArrayList<>());
        }
        modeloCadastroPerfilPermissao.getPermissoesPerfil().add(new Permissao());
        return "perfis/cadastro";
    }

    @RequestMapping(value="/editar", params={"removePermissao"})
    public String removeRowEdicao(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer permissaoPerfilId = Integer.valueOf(req.getParameter("removePermissao"));
        modeloCadastroPerfilPermissao.getPermissoesPerfil().remove(permissaoPerfilId.intValue());
        return "perfis/cadastro";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable int id, ModelMap modelMap) {
        Perfil perfil = perfilRepository.findById(id).orElse(null);
        List<Permissao> permissoesPerfil = perfilPermissaoRepository.findByPerfil(perfil).stream().map(PerfilPermissao::getPermissao).toList();
        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = new ModeloCadastroPerfilPermissao();
        modeloCadastroPerfilPermissao.setPerfil(perfil);
        modeloCadastroPerfilPermissao.setPermissoesPerfil(permissoesPerfil);
        modelMap.addAttribute("modeloCadastroPerfilPermissao", modeloCadastroPerfilPermissao);
        return "perfis/cadastro";
    }

    @Transactional(readOnly = true)
    @GetMapping("/buscarPorId/{id}")
    public Optional<Perfil> buscarPorId(@PathVariable int id) {
        return perfilRepository.findById(id);
    }

    public Paginacao<Perfil> buscaPaginada(int pagina, String direcao, List<Perfil> perfis) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        if (direcao.equalsIgnoreCase("normal")) {
            perfis.sort(Comparator.comparing(Perfil::getNome));
        } else {
            perfis.sort(Collections.reverseOrder(Comparator.comparing(Perfil::getNome)));
        }
        List<Perfil> paginaPerfis = perfis.stream().filter(perfil -> perfis.indexOf(perfil) >= inicio).limit(tamanho).toList();

        int totalPaginas = (perfis.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaPerfis);
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, ModelMap modelMap) {
        Perfil perfilDelete = perfilRepository.findById(id).orElse(null);
        List<UsuarioPerfil> usuariosPerfil = usuarioPerfilRepository.findByPerfil(perfilDelete);
        List<PerfilPermissao> perfisPermissao = perfilPermissaoRepository.findByPerfil(perfilDelete);
        if (usuariosPerfil.size() >=1) {
            usuarioPerfilRepository.deleteAll(usuariosPerfil);
        }
        if (perfisPermissao.size() >= 1) {
            perfilPermissaoRepository.deleteAll(perfisPermissao);
        }
        perfilRepository.delete(perfilDelete);
        modelMap.addAttribute("sucesso", "Perfil deletado com sucesso.");

        List<Perfil> perfis = (List<Perfil>) perfilRepository.findAll();
        Paginacao<Perfil> paginaPerfis = buscaPaginada(1, "normal", perfis);

        modelMap.addAttribute("paginaPerfis", paginaPerfis);

        return "perfis/lista";
    }

    @ModelAttribute("permissoes")
    public List<Permissao> getPermissoes(PerfilPermissao perfilPermissao) {
        if (Objects.nonNull(perfilPermissao.getId())){
            Perfil perfil = perfilRepository.findById(perfilPermissao.getId()).orElse(null);
            List<Integer> idsPermissoes = perfilPermissaoRepository.findByPerfil(perfil).stream().filter(p -> Objects.nonNull(p.getPerfil())).map(u -> u.getPermissao().getId()).toList();
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAllById(idsPermissoes);
            for (Permissao permissao : permissoes) {
                if (Objects.isNull(permissao.getSistema())){
                    Sistema sistemaVazio = new Sistema();
                    sistemaVazio.setNome("");
                    permissao.setSistema(sistemaVazio);
                }
            }
            return permissoes.stream().sorted(Comparator.comparing(Permissao::getNome)).toList();
        }
        List<Permissao> todasPermissoes = (List<Permissao>) permissaoRepository.findAll();
        todasPermissoes.sort(Comparator.comparing(Permissao::getNome));
        return todasPermissoes;
    }

    @ModelAttribute("sistemas")
    public List<Sistema> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();
        sistemas.sort(Comparator.comparing(Sistema::getNome));
        return sistemas;
    }

}
