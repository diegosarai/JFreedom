package br.web.jfreedom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.web.jfreedom.enumerator.RequestType;

@Target(value=ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestDefinition {

	//Path que dever� ser ouvido pela requisi��o
	String path();
	//Tipo da requisi��o que ser� feita. GET ou POST
	RequestType requestType();
	//Caso ocorra algum erro de valida��o, esse caminho deve ser passado para que o JFreedom redirecione a requisi��o do usu�iro para esse caminho
	String errorValidatorPath() default "";
	//Se o usu�rio deseja pular os ciclos de vida SingleValidator, GroupValidator e NotMappingValidator dever� ser false, caso contr�rio true.
	boolean validator() default false;
}
