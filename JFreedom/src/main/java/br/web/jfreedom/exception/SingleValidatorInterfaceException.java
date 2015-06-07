package br.web.jfreedom.exception;

/**
 * Essa exceção será lançada quando o usuário não implementar a interface SingleValidator na classe de validação 
 * individual.
 * @author Diego
 *
 */
public class SingleValidatorInterfaceException extends RuntimeException{

	public SingleValidatorInterfaceException(){
		super("SingleValidator class must implement SingleValidator interface");
	}
}
