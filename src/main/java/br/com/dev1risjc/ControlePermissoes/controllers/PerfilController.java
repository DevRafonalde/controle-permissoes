package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilPermissaoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/perfis")
public class PerfilController {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PerfilPermissaoRepository perfilPermissaoRepository;

    @GetMapping("/cadastrar")
    public String cadastrar(Perfil perfil) {
        return "perfis/cadastro";
    }

    @ModelAttribute("perfisPermissao")
    public List<PerfilPermissao> listaPerfisPermissoes() {
        return (List<PerfilPermissao>) perfilPermissaoRepository.findAll();
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao) {
        int paginaAtual = pagina.orElse(1);
        String ordem = direcao.orElse("normal");
        Paginacao<Perfil> paginaPerfis = buscaPaginada(paginaAtual, ordem);

        modelMap.addAttribute("paginaPerfis", paginaPerfis);
        return "perfis/lista";
    }

    @PostMapping("/novoPerfil")
    public String novoPerfil(@Valid Perfil perfil, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "perfis/cadastro";
        }
        perfilRepository.save(perfil);
        attributes.addFlashAttribute("sucesso", "Perfil inserido com sucesso.");
        return "redirect:/perfis/cadastrar";
    }

    @PostMapping("/editar")
    public String editar(Perfil perfil, RedirectAttributes attributes) {
        perfilRepository.save(perfil);
        attributes.addFlashAttribute("sucesso", "Perfil editado com sucesso.");
        return "redirect:/perfis/listar";
    }

    @GetMapping("/preEditar/{id}")
    public String preEditar(@PathVariable int id, ModelMap modelMap) {
        modelMap.addAttribute("perfil", perfilRepository.findById(id).orElse(null));
        return "perfis/cadastro";
    }

    @Transactional(readOnly = true)
    @GetMapping("/buscarPorId/{id}")
    public Optional<Perfil> buscarPorId(@PathVariable int id) {
        return perfilRepository.findById(id);
    }

    public Paginacao<Perfil> buscaPaginada(int pagina, String direcao) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        List<Perfil> perfis = (List<Perfil>) perfilRepository.findAll();
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
        System.out.println("veio pro path de delete");
//        if (cargoVazio(id)) {
//            cargoRepository.deleteById(id);
//            modelMap.addAttribute("sucesso", "Cargo deletado com sucesso.");
//        } else {
//            modelMap.addAttribute("falha", "Cargo não removido. Possui funcionário(s) vinculado(s).");
//        }
        return "perfis/lista";
    }

}
