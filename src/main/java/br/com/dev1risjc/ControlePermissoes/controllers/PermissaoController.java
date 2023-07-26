package br.com.dev1risjc.ControlePermissoes.controllers;

import br.com.dev1risjc.ControlePermissoes.helpers.paginacao.Paginacao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Permissao;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema2;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilPermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PermissaoRepository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.Sistemas2Repository;
import br.com.dev1risjc.ControlePermissoes.models.repositories.SistemasRepository;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/permissoes")
public class PermissaoController {

    private PermissaoRepository permissaoRepository;
    private SistemasRepository sistemasRepository;
    private Sistemas2Repository sistemasRepository2;
    private PerfilPermissaoRepository perfilPermissaoRepository;
    private Integer ultimoId;

    public PermissaoController(PermissaoRepository permissaoRepository, SistemasRepository sistemasRepository, @Lazy Sistemas2Repository sistemasRepository2, PerfilPermissaoRepository perfilPermissaoRepository) {
        this.permissaoRepository = permissaoRepository;
        this.sistemasRepository = sistemasRepository;
        this.sistemasRepository2 = sistemasRepository2;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    @GetMapping("/teste")
    public ResponseEntity teste() {
        Sistema2 teste = sistemasRepository2.findById(15).orElse(null);

//        teste.setNome("SI Integrador");

        sistemasRepository2.delete(teste);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/cadastrar")
    //Esse objeto Permissao colocado como parâmetro do método é para que a página receba uma instância dele para conseguir abrir
    public String cadastrar(Permissao permissao) {
        return "permissoes/cadastro";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelMap, @RequestParam("pagina") Optional<Integer> pagina, @RequestParam("direcao") Optional<String> direcao, @RequestParam("atributo") Optional<String> atributo) {
        int paginaAtual = pagina.orElse(1);
        String ordem = direcao.orElse("normal");
        String baseOrdenacao = atributo.orElse("nome");

        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        ultimoId = permissoes.get(permissoes.size() - 1).getId();

        Paginacao<Permissao> paginaPermissoes;

        if (baseOrdenacao.equalsIgnoreCase("id")) {
            paginaPermissoes = buscaPaginadaId(paginaAtual, ordem, permissoes);
        } else if (baseOrdenacao.equalsIgnoreCase("sistema")) {
            paginaPermissoes = buscaPaginadaSistema(paginaAtual, ordem, permissoes);
        } else {
            paginaPermissoes = buscaPaginadaNome(paginaAtual, ordem, permissoes);
        }

        modelMap.addAttribute("paginaPermissoes", paginaPermissoes);
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
            ultimoId = permissoes.get(permissoes.size() - 1).getId();
        }
        ultimoId++;
//        permissao.setSistema(sistemasRepository.save(permissao.getSistema()));
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
        if (usosPermissao.size() >= 1) {
            perfilPermissaoRepository.deleteAll(usosPermissao);
        }
        permissaoRepository.delete(permissaoDelete);
        modelMap.addAttribute("sucesso", "Permissão deletada com sucesso.");

        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        Paginacao<Permissao> paginaPermissoes = buscaPaginadaNome(1, "normal", permissoes);

        modelMap.addAttribute("paginaPermissoes", paginaPermissoes);
        return "permissoes/lista";
    }

    public Paginacao<Permissao> buscaPaginadaNome(int pagina, String direcao, List<Permissao> permissoes) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        if (direcao.equalsIgnoreCase("normal")) {
            permissoes.sort(Comparator.comparing(Permissao::getNome));
        } else {
            permissoes.sort(Collections.reverseOrder(Comparator.comparing(Permissao::getNome)));
        }
        List<Permissao> paginaPerfis = permissoes.stream().filter(permissao -> permissoes.indexOf(permissao) >= inicio).limit(tamanho).toList();

        int totalPaginas = (permissoes.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaPerfis);
    }

    public Paginacao<Permissao> buscaPaginadaId(int pagina, String direcao, List<Permissao> permissoes) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        if (direcao.equalsIgnoreCase("normal")) {
            permissoes.sort(Comparator.comparing(Permissao::getId));
        } else {
            permissoes.sort(Collections.reverseOrder(Comparator.comparing(Permissao::getId)));
        }
        List<Permissao> paginaPerfis = permissoes.stream().filter(permissao -> permissoes.indexOf(permissao) >= inicio).limit(tamanho).toList();

        int totalPaginas = (permissoes.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaPerfis);
    }

    public Paginacao<Permissao> buscaPaginadaSistema(int pagina, String direcao, List<Permissao> permissoes) {
        int tamanho = 30;
        int inicio = (pagina - 1) * tamanho;
        if (direcao.equalsIgnoreCase("normal")) {
            permissoes.sort(Comparator.comparing(permissao -> permissao.getSistema().getNome()));
        } else {
            permissoes.sort(Collections.reverseOrder(Comparator.comparing(permissao -> permissao.getSistema().getNome())));
        }
        List<Permissao> paginaPerfis = permissoes.stream().filter(permissao -> permissoes.indexOf(permissao) >= inicio).limit(tamanho).toList();


        int totalPaginas = (permissoes.size() + (tamanho - 1)) / tamanho;

        return new Paginacao<>(tamanho, pagina, totalPaginas, direcao, paginaPerfis);
    }

    @ModelAttribute("sistemas")
    public List<Sistema> getSistemas() {
        List<Sistema> sistemas = (List<Sistema>) sistemasRepository.findAll();

        sistemas.sort(Comparator.comparing(Sistema::getNome));
        return sistemas;
    }

//    @ModelAttribute("listaPermissoes")
//    public List<Permissao> getPermissoes() {
//        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
//        for (Permissao permissao : permissoes) {
//            if (Objects.isNull(permissao.getSistema())){
//                Sistema sistemaVazio = new Sistema();
//                sistemaVazio.setNome("");
//                permissao.setSistema(sistemaVazio);
//            }
//        }
//        permissoes.sort(Comparator.comparing(Permissao::getNome));
//        return permissoes;
//    }

    @ModelAttribute("listaPermissoesCadastro")
    public List<Permissao> getPermissoesCadastro() {
        List<Permissao> permissoes = (List<Permissao>) permissaoRepository.findAll();
        permissoes.sort(Comparator.comparing(Permissao::getNome));
        return permissoes;
    }
}
