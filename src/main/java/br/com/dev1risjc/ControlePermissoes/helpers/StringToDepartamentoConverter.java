package br.com.dev1risjc.ControlePermissoes.helpers;

import br.com.dev1risjc.ControlePermissoes.models.entities.PerfilPermissao;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilPermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDepartamentoConverter implements Converter<String, PerfilPermissao> {

    @Autowired
    private PerfilPermissaoRepository perfilPermissaoRepository;

    @Override
    public PerfilPermissao convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        int id = Integer.parseInt(source);
        return perfilPermissaoRepository.findById(id).orElse(null);
    }
}
