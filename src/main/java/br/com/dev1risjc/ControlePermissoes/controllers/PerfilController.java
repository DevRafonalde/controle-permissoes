package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroPerfilUsuario;
import br.com.dev1risjc.ControlePermissoes.services.PerfilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
        List<Perfil> perfis = perfilService.listarTodos();
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

    @PostMapping("/vincular-usuarios-em-lote")
    public String vincularUsuariosEmLotePost(ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario, RedirectAttributes attributes) {
        ModeloCadastroPerfilUsuario modeloRetorno = perfilService.vincularUsuariosEmLote(modeloCadastroPerfilUsuario);
        attributes.addFlashAttribute("sucesso", "Usuários vinculados com sucesso");
        return "redirect:/perfis/listar-usuarios-vinculados/" + modeloRetorno.getPerfil().getId();
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
    public String editar(ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, BindingResult result, ModelMap modelMap) {
        if (result.hasErrors()) {
            return "perfis/edicao";
        }

        ModeloCadastroPerfilPermissao perfilPermissao = perfilService.editar(modeloCadastroPerfilPermissao);
        modelMap.addAttribute("sucesso", "Perfil salvo com sucesso");
        modelMap.addAttribute("perfilPermissao", perfilPermissao);
        return "perfis/lista-especifica";
    }

    @RequestMapping(value="/novo-perfil", params={"addPermissao"})
    public String addRowCadastro(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPerfilPermissao.getPermissoesPerfil())) {
            modeloCadastroPerfilPermissao.setPermissoesPerfil(new ArrayList<>());
        }
        modeloCadastroPerfilPermissao.getPermissoesPerfil().add(0, new Permissao());
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
        modeloCadastroPerfilPermissao.getPermissoesPerfil().add(0, new Permissao());
        return "perfis/edicao";
    }

    @RequestMapping(value="/editar", params={"removePermissao"})
    public String removeRowEdicao(final ModeloCadastroPerfilPermissao modeloCadastroPerfilPermissao, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer permissaoPerfilId = Integer.valueOf(req.getParameter("removePermissao"));
        modeloCadastroPerfilPermissao.getPermissoesPerfil().remove(permissaoPerfilId.intValue());
        return "perfis/edicao";
    }

    @RequestMapping(value="/vincular-usuarios-em-lote", params={"addUsuario"})
    public String addRowVinculoUsuario(final ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario, final BindingResult bindingResult) {
        if (Objects.isNull(modeloCadastroPerfilUsuario.getUsuariosPerfil())) {
            modeloCadastroPerfilUsuario.setUsuariosPerfil(new ArrayList<>());
        }
        modeloCadastroPerfilUsuario.getUsuariosPerfil().add(0, new Usuario());
        return "perfis/usuarios-em-lote";
    }

    @RequestMapping(value="/vincular-usuarios-em-lote", params={"removeUsuario"})
    public String removeRowVinculoUsuario(final ModeloCadastroPerfilUsuario modeloCadastroPerfilUsuario, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer usuarioPerfilId = Integer.valueOf(req.getParameter("removeUsuario"));
        modeloCadastroPerfilUsuario.getUsuariosPerfil().remove(usuarioPerfilId.intValue());
        return "perfis/usuarios-em-lote";
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

    @ModelAttribute("permissoes")
    public List<Permissao> getPermissoes() {
        return perfilService.getPermissoes();
    }

    @ModelAttribute("sistemas")
    public List<Sistema> getSistemas() {
        return perfilService.getSistemas();
    }

    @ModelAttribute("usuarios")
    public List<Usuario> getUsuarios() {
        return perfilService.getUsuarios();
    }

}
