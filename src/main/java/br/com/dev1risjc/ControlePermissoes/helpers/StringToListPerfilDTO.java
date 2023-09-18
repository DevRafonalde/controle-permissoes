package br.com.dev1risjc.ControlePermissoes.helpers;

import br.com.dev1risjc.ControlePermissoes.models.entities.dto.PerfilDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StringToListPerfilDTO implements Converter<String, List<PerfilDTO>> {
    @Override
    public List<PerfilDTO> convert(String source) {
        source = source.trim();
        List<PerfilDTO> perfilDTOList = new ArrayList<>();

        // Divida a string com base no separador (neste exemplo, usamos vírgula)
        String[] elements = source.split(",");

        for (String element : elements) {
            // Crie uma instância vazia de PerfilDTO
            PerfilDTO perfilDTO = new PerfilDTO();

            // Preencha os campos do objeto PerfilDTO conforme necessário
            // Neste exemplo, vamos supor que PerfilDTO tenha um método setNome
            perfilDTO.setId(Integer.valueOf(element.trim()));

            perfilDTOList.add(perfilDTO);
        }

        return perfilDTOList;
    }
}
