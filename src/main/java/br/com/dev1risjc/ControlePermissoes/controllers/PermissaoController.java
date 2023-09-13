package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PermissaoDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.SistemaDTO;
import br.com.dev1risjc.ControlePermissoes.services.PermissaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/permissoes")
public class PermissaoController {
    private PermissaoService permissaoService;

    public PermissaoController(PermissaoService permissaoService) {
        this.permissaoService = permissaoService;
    }

    @GetMapping("/cadastrar")
    public String cadastrar(PermissaoDTO permissaoDTO) {
        return "permissoes/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap) {
        List<PermissaoDTO> permissoes = permissaoService.listar();
        modelMap.addAttribute("permissoes", permissoes);
        return "permissoes/lista";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable int id, ModelMap modelMap) {
        modelMap.addAttribute("permissaoDTO", permissaoService.preEditar(id));
        return "permissoes/edicao";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes attributes) {
        permissaoService.deletar(id);
        attributes.addFlashAttribute("sucesso", "Permissao deletada com sucesso.");
        return "redirect:/permissoes/listar";
    }

    @PostMapping("/nova-permissao")
    public String novaPermissao(@Valid PermissaoDTO permissaoDTO, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "permissoes/cadastro";
        }

        permissaoService.novaPermissao(permissaoDTO);

        attributes.addFlashAttribute("sucesso", "Permissao criada com sucesso.");
        return "redirect:/permissoes/listar";
    }

    @PostMapping("/editar")
    public String editar(@Valid PermissaoDTO permissaoDTO, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "permissoes/edicao";
        }

        permissaoService.editar(permissaoDTO);

        attributes.addFlashAttribute("sucesso", "Permissao editada com sucesso.");
        return "redirect:/permissoes/listar";
    }

    @ModelAttribute("sistemas")
    public List<SistemaDTO> getSistemas() {
        return permissaoService.getSistemas();
    }

    @ModelAttribute("listaPermissoesCadastro")
    public List<PermissaoDTO> getPermissoesCadastro() {
        return permissaoService.getPermissoesCadastro();
    }
}
