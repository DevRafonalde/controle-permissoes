package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilPermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.SistemasRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/permissoes")
public class PermissaoController {

    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private Integer ultimoId;

    public PermissaoController(PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, PerfilPermissaoRepository perfilPermissaoRepository) {
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    @GetMapping("/cadastrar")
    public String cadastrar(Permissao permissao) {
        return "permissoes/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap) {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        ultimoId = permissoes.get(permissoes.size() - 1).getId();
        modelMap.addAttribute("permissoes", permissoes);
        return "permissoes/lista";
    }

    @PostMapping("/nova-permissao")
    public String novoPermissao(@Valid Permissao permissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "permissoes/cadastro";
        }
        if (Objects.isNull(ultimoId)) {
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }
        permissao.setDesabilitado(false);
        permissao.setGerarLog(false);
        permissao.setId(ultimoId);
        ultimoId++;
        permissaoRepository.save(permissao);
        attributes.addFlashAttribute("sucesso", "Permissao criada com sucesso.");
        return "redirect:/permissoes/cadastrar";
    }

    @PostMapping("/editar")
    public String editar(@Valid Permissao permissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "permissoes/cadastro";
        }
        if (Objects.isNull(ultimoId)) {
            List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
            permissoes.sort(Comparator.comparingInt(Permissao::getId));
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }
        ultimoId++;
        permissao.setDesabilitado(false);
        permissao.setGerarLog(false);
        permissao.setId(ultimoId);
        permissaoRepository.save(permissao);
        attributes.addFlashAttribute("sucesso", "Permissao editada com sucesso.");
        return "redirect:/permissoes/listar";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable int id, ModelMap modelMap) {
        modelMap.addAttribute("Permissao", permissaoRepository.findById(id).orElse(null));
        return "permissoes/cadastro";
    }

    @Transactional(readOnly = true)
    @GetMapping("/buscarPorId/{id}")
    public Optional<Permissao> buscarPorId(@PathVariable int id) {
        return permissaoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @GetMapping("/buscarTodos")
    public Iterable<Permissao> buscarTodos() {
        return permissaoRepository.findAll();
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, ModelMap modelMap) {

        Permissao permissaoDelete = permissaoRepository.findById(id).orElse(null);
        List<PerfilPermissao> usosPermissao = perfilPermissaoRepository.findByPermissao(permissaoDelete);
        if (!usosPermissao.isEmpty()) {
            perfilPermissaoRepository.deleteAll(usosPermissao);
        }
        permissaoRepository.delete(permissaoDelete);
        modelMap.addAttribute("sucesso", "Permissão deletada com sucesso.");

        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        modelMap.addAttribute("permissoes", permissoes);
        return "permissoes/lista";
    }

    @ModelAttribute("sistemas")
    public List<Sistema> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();

        sistemas.sort(Comparator.comparing(Sistema::getNome));
        return sistemas;
    }

    @ModelAttribute("listaPermissoesCadastro")
    public List<Permissao> getPermissoesCadastro() {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        permissoes.sort(Comparator.comparing(Permissao::getNome));
        return permissoes;
    }
}
