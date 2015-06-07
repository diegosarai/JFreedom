package br.web.jfreedom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.web.jfreedom.enumerator.RequestType;

@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestDefinition {

	//Path que deverá ser ouvido pela requisição
	String path();
	//Tipo da requisição que será feita. GET ou POST
	RequestType requestType();
	//Caso ocorra algum erro de validação, esse caminho deve ser passado para que o JFreedom redirecione a requisição do usuáiro para esse caminho
	String errorValidatorPath() default "";
	//Se o usuário deseja pular os ciclos de vida SingleValidator, GroupValidator e NotMappingValidator deverá ser false, caso contrário true.
	boolean validator() default false;
}
