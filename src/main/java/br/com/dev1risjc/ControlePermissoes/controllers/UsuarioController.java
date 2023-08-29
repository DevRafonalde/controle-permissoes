package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Usuario;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.UsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.view.ModeloCadastroUsuarioPerfil;
import br.com.dev1risjc.ControlePermissoes.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
    public String listar(ModelMap modelMap) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        modelMap.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @GetMapping("/listar-especifico/{id}")
    public String listarEspecifico(@PathVariable Integer id, ModelMap modelMap) {
        ModeloCadastroUsuarioPerfil usuario = usuarioService.listarEspecifico(id);
        modelMap.addAttribute("usuario", usuario);
        return "usuarios/lista-especifica";
    }

    @PostMapping("/novo-usuario")
    public String novoUsuario(@Valid ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, BindingResult result, ModelMap modelMap) {
        if (result.hasErrors()) {
            return "usuarios/cadastro";
        }

        ModeloCadastroUsuarioPerfil usuarioNovo = usuarioService.novoUsuario(modeloCadastroUsuarioPerfil);

        modelMap.addAttribute("sucesso", "Usuário inserido com sucesso");
        modelMap.addAttribute("usuario", usuarioNovo);
        return "usuarios/lista-especifica";
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
        return "usuarios/edicao";
    }

    @RequestMapping(value="/editar", params={"removePerfil"})
    public String removeRowEdicao(final ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, final BindingResult bindingResult, final HttpServletRequest req) {
        final Integer perfilUsuarioId = Integer.valueOf(req.getParameter("removePerfil"));
        modeloCadastroUsuarioPerfil.getPerfisUsuario().remove(perfilUsuarioId.intValue());
        return "usuarios/edicao";
    }

    @PostMapping("/editar")
    public String editar(@Valid ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil, BindingResult result, ModelMap modelMap) {
        if (result.hasErrors()) {
            return "usuarios/edicao";
        }

        ModeloCadastroUsuarioPerfil modeloRetorno = usuarioService.editar(modeloCadastroUsuarioPerfil);

        modelMap.addAttribute("sucesso", "Funcionário editado com sucesso");
        modelMap.addAttribute("usuario", modeloRetorno);
        return "usuarios/lista-especifica";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable Integer id, ModelMap modelMap) {
        ModeloCadastroUsuarioPerfil modeloCadastroUsuarioPerfil = usuarioService.preEditar(id);
        modelMap.addAttribute("modeloCadastroUsuarioPerfil", modeloCadastroUsuarioPerfil);
        return "usuarios/edicao";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes attributes) {

        usuarioService.deletar(id);

        attributes.addFlashAttribute("sucesso", "Usuario deletado com sucesso.");

        return "redirect:/usuarios/listar";
    }

    @ModelAttribute("perfis")
    public List<Perfil> getPerfis(UsuarioPerfil usuarioPerfil) {
        return usuarioService.getPerfis(usuarioPerfil);
    }
}
