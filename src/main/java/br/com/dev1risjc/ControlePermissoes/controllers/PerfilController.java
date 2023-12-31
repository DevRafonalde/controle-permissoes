package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PerfilDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PermissaoDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.SistemaDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.dto.UsuarioDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuarioId;
import br.com.dev1risjc.ControlePermissoes.services.PerfilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/perfis")
public class PerfilController {
    private PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/cadastrar")
    public String cadastrar(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao) {
        perfilService.setClonar(null);
        return "perfis/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap) {
        List<PerfilDTO> perfis = perfilService.listarTodos();
        modelMap.addAttribute("perfis", perfis);
        return "perfis/lista";
    }

    @GetMapping("/listar-usuarios-vinculados/{id}")
    public String listarUsuariosVinculados(@PathVariable Integer id, ModelMap modelMap) {
        ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario = perfilService.listarUsuariosVinculados(id);
        modelMap.addAttribute("modeloCadastroPerfilUsuario", modeloCadastroPerfilUsuario);
        return "perfis/lista-usuarios-vinculados";
    }

    @GetMapping("/vincular-usuarios-em-lote/{id}")
    public String vincularUsuariosEmLote(@PathVariable Integer id, ModelMap modelMap) {
        ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario = perfilService.listarUsuariosVinculados(id);
        modelMap.addAttribute("modeloCadastroPerfilUsuario", modeloCadastroPerfilUsuario);
        return "perfis/usuarios-em-lote";
    }

    @GetMapping("/listar-especifico/{id}")
    public String listarEspecifico(@PathVariable Integer id, ModelMap modelMap) {
        ModeloCadastroPerfilPermissao perfilPermissao = perfilService.listarEspecifico(id);
        modelMap.addAttribute("perfilPermissao", perfilPermissao);
        return "perfis/lista-especifica";
    }

    @GetMapping("/clonar/{id}")
    public String clonar(@PathVariable Integer id, ModelMap modelMap) {
        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = perfilService.clonar(id);
        modelMap.addAttribute("modeloCadastroPerfilPermissao", modeloCadastroPerfilPermissao);
        return "perfis/cadastro";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable int id, ModelMap modelMap) {
        ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao = perfilService.preEditar(id);
        modelMap.addAttribute("modeloCadastroPerfilPermissao", modeloCadastroPerfilPermissao);
        return "perfis/edicao";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable int id, RedirectAttributes attributes) {
        perfilService.deletar(id);
        attributes.addFlashAttribute("sucesso", "Perfil deletado com sucesso.");
        return "redirect:/perfis/listar";
    }

    @GetMapping("/get-usuarios")
    public ResponseEntity<List<UsuarioDTO>> getUsuarios() {
        return new ResponseEntity<>(perfilService.getUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/get-usuarios-vinculados/{id}")
    public ResponseEntity<List<Integer>> getUsuariosVinculados(@PathVariable int id) {
        List<Integer> usuariosVinculadosIds = perfilService.listarUsuariosVinculados(id)
                .getUsuariosPerfil()
                .stream()
                .map(UsuarioDTO::getId)
                .toList();
        return new ResponseEntity<>(usuariosVinculadosIds, HttpStatus.OK);
    }

    @GetMapping("/get-todas-permissoes")
    public ResponseEntity<List<PermissaoDTO>> getPerfis() {
        return new ResponseEntity<>(perfilService.getPermissoes(), HttpStatus.OK);
    }

    @GetMapping("/get-permissoes-vinculadas/{id}")
    public ResponseEntity<List<Integer>> getPerfisVinculados(@PathVariable Integer id) {
        return new ResponseEntity<>(perfilService.getPermissoesVinculadasId(id), HttpStatus.OK);
    }

    @PostMapping("/novo-perfil")
    public String novoPerfil(@Valid ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "perfis/cadastro";
        }

        int idPerfilCadastrado = perfilService.novoPerfil(modeloCadastroPerfilPermissao);
        attributes.addFlashAttribute("sucesso", "Perfil criado com sucesso");
        return "redirect:/perfis/listar-especifico/" + idPerfilCadastrado;
    }

    @PostMapping("/editar")
    public String editar(@Valid ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, BindingResult result, ModelMap modelMap) {
        if (result.hasErrors()) {
            return "perfis/edicao";
        }

        ModeloCadastroPerfilPermissao perfilPermissao = perfilService.editar(modeloCadastroPerfilPermissao);
        modelMap.addAttribute("sucesso", "Perfil salvo com sucesso");
        modelMap.addAttribute("perfilPermissao", perfilPermissao);
        return "perfis/lista-especifica";
    }

    // Esse método é utilizado via Javascript na página "perfis/usuarios-em-lote" ao invés do próprio Thymeleaf
    @PostMapping("/vincular-usuarios-em-lote")
    public ResponseEntity<Integer> vincularUsuariosEmLotePost(@RequestBody ModeloCadastroPerfilUsuarioId modeloCadastroPerfilUsuarioId, RedirectAttributes attributes) {
        ModeloCadastroPerfilUsuarioId modeloRetorno = perfilService.vincularUsuariosEmLote(modeloCadastroPerfilUsuarioId);
        attributes.addFlashAttribute("sucesso", "Usuários vinculados com sucesso");
        return new ResponseEntity<>(modeloRetorno.getPerfil().getId(), HttpStatus.OK);
    }

    @RequestMapping(value="/novo-perfil", params={"addPermissao"})
    public String addRowCadastro(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPerfilPermissao.getPermissoesPerfil())) {
            modeloCadastroPerfilPermissao.setPermissoesPerfil(new ArrayList<>());
        }
        modeloCadastroPerfilPermissao.getPermissoesPerfil().add(0, new PermissaoDTO());
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
        modeloCadastroPerfilPermissao.getPermissoesPerfil().add(0, new PermissaoDTO());
        return "perfis/edicao";
    }

    @RequestMapping(value="/editar", params={"removePermissao"})
    public String removeRowEdicao(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer permissaoPerfilId = Integer.valueOf(req.getParameter("removePermissao"));
        modeloCadastroPerfilPermissao.getPermissoesPerfil().remove(permissaoPerfilId.intValue());
        return "perfis/edicao";
    }

    @ModelAttribute("sistemas")
    public List<SistemaDTO> getSistemas() {
        return perfilService.getSistemas();
    }

}
