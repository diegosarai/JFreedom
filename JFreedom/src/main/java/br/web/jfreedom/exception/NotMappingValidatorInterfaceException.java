package br.web.jfreedom.exception;

public class NotMappingValidatorInterfaceException extends RuntimeException{

	public NotMappingValidatorInterfaceException(){
		super("NotMappingValidator class must implement NotMappingValidator interface");
	}
}
