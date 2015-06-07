package br.web.jfreedom.exception;

/**
 * Essa exce��o ser� lan�ada quando o usu�rio n�o implementar a interface SingleValidator na classe de valida��o 
 * individual.
 * @author Diego
 *
 */
public class SingleValidatorInterfaceException extends RuntimeException{

	public SingleValidatorInterfaceException(){
		super("SingleValidator class must implement SingleValidator interface");
	}
}
