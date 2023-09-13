package br.com.dev1risjc.ControlePermissoes.helpers;

import br.com.dev1risjc.ControlePermissoes.models.entities.dto.SistemaDTO;
import br.com.dev1risjc.ControlePermissoes.models.entities.orm.Sistema;
import br.com.dev1risjc.ControlePermissoes.models.repositories.SistemasRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSistema implements Converter<String, SistemaDTO> {
    @Autowired
    private SistemasRepository sistemasRepository;

    @Override
    public SistemaDTO convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        ModelMapper mapper = new ModelMapper();
        int id = Integer.parseInt(source);
        Sistema sistema = sistemasRepository.findById(id).orElse(null);
        return mapper.map(sistema, SistemaDTO.class);
    }
}
