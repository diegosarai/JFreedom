package br.web.jfreedom.exception;


/**
 * Essa exception ocorrer� em caso do usu�rio n�o ter colocado os par�metros request e response no 
 * m�todo http mapeado em seu controller
 * 
 * @author diego.sarai
 *
 */
public class InvalidParametersException extends RuntimeException{

	public InvalidParametersException(){
		super("Methods mapped with @RequestDefinition must have HttpServletRequest and HttpServletResponse parameters");
	}
	
}
