package br.web.jfreedom.exception;

/**
 * Essa exce��o ser� lan�ada caso ocorre IOException na apresenta��o das custom tags Messages e Message na JSP
 * @author Diego
 *
 */
public class JspMessageException extends RuntimeException{
	
	public JspMessageException(){
		super("Some failure occurred on the JSP validation message print");
	}
}
