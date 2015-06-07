package br.web.jfreedom.exception;

public class GroupValidatorInterfaceException extends RuntimeException{

	public GroupValidatorInterfaceException(){
		super("GroupValidator class must implement GroupValidator interface");
	}
}
