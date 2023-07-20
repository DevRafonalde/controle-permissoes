package br.com.dev1risjc.ControlePermissoes.helpers.validators;

import br.com.dev1risjc.ControlePermissoes.models.entities.Usuario;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class FuncionarioValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Usuario.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Usuario f = (Usuario) object;
//        if (f.getDataSaida() != null) {
//            if (f.getDataSaida().isBefore(f.getDataEntrada())) {
//                errors.rejectValue("dataSaida", "PosteriorDataEntrada.funcionario.dataSaida");
//            }
//        }
    }
}
