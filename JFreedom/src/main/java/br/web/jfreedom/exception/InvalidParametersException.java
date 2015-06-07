package br.web.jfreedom.exception;


/**
 * Essa exception ocorrerá em caso do usuário não ter colocado os parâmetros request e response no 
 * método http mapeado em seu controller
 * 
 * @author diego.sarai
 *
 */
public class InvalidParametersException extends RuntimeException{

	public InvalidParametersException(){
		super("Methods mapped with @RequestDefinition must have HttpServletRequest and HttpServletResponse parameters");
	}
	
}
