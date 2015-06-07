package br.web.jfreedom.exception;

/**
 * Essa exce��o ocorrer� quando ocorrer algum erro de valida��o nas fases:
 * - SingleValidator
 * - GroupValidator
 * - NotMappingValidator
 * 
 * Por�m essa exce��o s� ocorrer� quando o usu�rio n�o mapear o atributo errorValidatorPath
 * na annotation @RequestDefinition. Nesses cen�rios o JFreedom precisa redirecionar para a p�gina de erro do usu�rio,
 * mas se o usu�rio n�o mapear n�o ser� poss�vel identificar a JSP de erro que dever� ser redirecionada.
 * 
 * @author Diego
 *
 */
public class ErrorValidatorPathRequiredException extends RuntimeException{

	public ErrorValidatorPathRequiredException(){
		super("When SingleValidator, GroupValidator or NotMappingValidator occurs you must explicitly use errorValidatorPath attribute"
				+ "on annotation @RequestDefinition");
	}
	
}
