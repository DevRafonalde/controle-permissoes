package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.entities.Permissao;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PermissaoRepository;
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
@RequestMapping("/permissoes")
public class PermissaoController {

    @Autowired
    private PermissaoRepository permissaoRepository;

    @GetMapping("/cadastrar")
    //Esse objeto Permissao colocado como parâmetro do método é para que a página receba uma instância dele para conseguir abrir
    public String cadastrar(Permissao Permissao) {
        return "permissoes/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao) {
        int paginaAtual = pagina.orElse(1);
        String ordem = direcao.orElse("normal");
        Paginacao<Permissao> paginaPermissoes = buscaPaginada(paginaAtual, ordem);

        modelMap.addAttribute("paginaPermissoes", paginaPermissoes);
        return "permissoes/lista";
    }

    @PostMapping("/novaPermissao")
    public String novoPermissao(@Valid Permissao Permissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "permissoes/cadastro";
        }
        permissaoRepository.save(Permissao);
        attributes.addFlashAttribute("sucesso", "Permissao criada com sucesso.");
        return "redirect:/permissoes/cadastrar";
    }

    @PostMapping("/editar")
    public String editar(Permissao Permissao, RedirectAttributes attributes) {
        permissaoRepository.save(Permissao);
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
        System.out.println("Entrou no path de delete");
//        if (PermissaoVazio(id)) {
//            PermissaoRepository.deleteById(id);
//            modelMap.addAttribute("sucesso", "Permissao deletado com sucesso.");
//        } else {
//            modelMap.addAttribute("falha", "Permissao não removido. Possui cargo(s) vinculado(s).");
//        }
        return "permissoes/lista";
    }

    public Paginacao<Permissao> buscaPaginada(int pagina, String direcao) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        List<Permissao> perfis = (List<Permissao>) permissaoRepository.findAll();
        if (direcao.equalsIgnoreCase("normal")) {
            perfis.sort(Comparator.comparing(Permissao::getNome));
        } else {
            perfis.sort(Collections.reverseOrder(Comparator.comparing(Permissao::getNome)));
        }
        List<Permissao> paginaPerfis = perfis.stream().filter(permissao -> perfis.indexOf(permissao) >= inicio).limit(tamanho).toList();

        int totalPaginas = (perfis.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaPerfis);
    }
}
