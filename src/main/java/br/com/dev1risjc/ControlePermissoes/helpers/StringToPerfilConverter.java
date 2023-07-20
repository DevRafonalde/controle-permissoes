package br.com.dev1risjc.ControlePermissoes.helpers;

import br.com.dev1risjc.ControlePermissoes.models.entities.Perfil;
import br.com.dev1risjc.ControlePermissoes.models.repositories.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPerfilConverter implements Converter<String, Perfil> {

    @Autowired
    private PerfilRepository perfilRepository;

    @Override
    public Perfil convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        int id = Integer.parseInt(source);
        return perfilRepository.findById(id).orElse(null);
    }
}
