package br.com.dev1risjc.ControlePermissoes.helpers.paginacao;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class Paginacao<T> {
    @Getter
    private int tamanho;

    @Getter
    private int pagina;

    @Getter
    private int totalPaginas;

    @Getter
    private String direcao;

    @Getter
    private List<T> registros;
}
