package br.web.jfreedom.exception;

/**
 * Essa exceção será lançada caso ocorre IOException na apresentação das custom tags Messages e Message na JSP
 * @author Diego
 *
 */
public class JspMessageException extends RuntimeException{
	
	public JspMessageException(){
		super("Some failure occurred on the JSP validation message print");
	}
}
