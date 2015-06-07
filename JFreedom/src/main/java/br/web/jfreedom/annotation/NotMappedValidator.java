package br.web.jfreedom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Essa annotation deve ser colocada em um atributo que ser� considerado um FORM BEAN,
 *  ou seja, todos os campos do formul�rio que passar�o por valida��o estar�o dentro dele.
 *  Essa annotation s� dever� ser utilizada quando o usu�rio n�o realizar o mapeamento dos campos.
 * @author Diego
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface NotMappedValidator {

}
