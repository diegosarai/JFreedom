package br.web.jfreedom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Essa annotation deve ser colocada em um atributo que será considerado um FORM BEAN,
 *  ou seja, todos os campos do formulário que passarão por validação estarão dentro dele.
 *  Essa annotation só deverá ser utilizada quando o usuário não realizar o mapeamento dos campos.
 * @author Diego
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface NotMappedValidator {

}
